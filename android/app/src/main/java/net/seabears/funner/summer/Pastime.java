package net.seabears.funner.summer;

import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.seabears.funner.ContextualDateFormatter;
import net.seabears.funner.db.ActionInsertTask;
import net.seabears.funner.db.FunnerDbHelper;
import net.seabears.funner.db.SQLiteCursorLoader;
import net.seabears.funner.db.SelectionMethod;
import net.seabears.funner.summer.suggest.PastimeActionArgs;
import net.seabears.funner.weather.WeatherService;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

public class Pastime extends FragmentActivity
    implements LoaderManager.LoaderCallbacks<Cursor>
{
  public static final String ARG_PASTIME_ARGS = "pastime_arguments";

  public static final String ARG_PARENT = "parent";

  public static final String ARG_PASTIME_ID = "pastime_id";

  private static final int MAX_ROWS = 10;

  private static final Set<Class<?>> PARENTS;

  private static final Map<Class<?>, Long> SELECTION_METHODS;

  static
  {
    SELECTION_METHODS = new HashMap<Class<?>, Long>(5);
    SELECTION_METHODS.put(Ideas.class, SelectionMethod.HISTORICAL.getId());
    SELECTION_METHODS.put(RandomPastimes.class, SelectionMethod.RANDOM.getId());
    SELECTION_METHODS.put(PastimeEditor.class, SelectionMethod.MANUAL.getId());
    SELECTION_METHODS.put(Pastimes.class, SelectionMethod.MANUAL.getId());
    SELECTION_METHODS.put(History.class, SelectionMethod.MANUAL.getId());
    PARENTS = new HashSet<Class<?>>(SELECTION_METHODS.keySet());
  }

  private long id;

  private boolean custom;

  private PastimeActionArgs pastimeArgs;

  private Class<?> parent;

  private FunnerDbHelper dbHelper;

  // This is the Adapter being used to display the list's data
  private SimpleCursorAdapter mAdapter;

  private final WeatherService weatherService = new WeatherService();

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_pastime_detail);

    // Show the Up button in the action bar.
    getActionBar().setDisplayHomeAsUpEnabled(true);

    // get arguments from intent
    readArguments();

    // get pastime from database
    dbHelper = new FunnerDbHelper(this);
    Cursor cursor = dbHelper.getReadableDatabase()
        .query("pastime", new String[] { "action_name", "active", "custom" },
            "_id = ?", new String[] { String.valueOf(id) },
            null, null, null);
    cursor.moveToFirst();
    final String action = cursor.getString(cursor.getColumnIndexOrThrow("action_name"));
    final boolean active = cursor.getInt(cursor.getColumnIndexOrThrow("active")) != 0;
    custom = cursor.getInt(cursor.getColumnIndexOrThrow("custom")) != 0;
    cursor.close();

    // pastime details
    setTitle(action);
    TextView actionView = (TextView) findViewById(R.id.pastime_action);
    actionView.setText(action);

    // history
    final ContextualDateFormatter formatter = new ContextualDateFormatter(this);
    mAdapter = new SimpleCursorAdapter(this,
        android.R.layout.simple_list_item_1, null,
        new String[] { "performed" }, new int[] { android.R.id.text1 }, 0)
    {
      @Override
      public void setViewText(TextView v, String text)
      {
        try
        {
          super.setViewText(v, formatter.format(text));
        }
        catch (ParseException e)
        {
          Log.e(getClass().getSimpleName(), e.getMessage(), e);
          super.setViewText(v, text);
        }
      }
    };
    LoaderManager.getInstance(this).initLoader(0, null, this);

    // settings
    LinearLayout settingsView = (LinearLayout) findViewById(R.id.pastime_settings);
    CheckBox setting = new CheckBox(this);
    settingsView.addView(setting);
    setting.setText(R.string.pastime_include);
    setting.setChecked(active);
    setting.setOnCheckedChangeListener(new OnCheckedChangeListener()
    {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
      {
        final ContentValues values = new ContentValues(1);
        values.put("active", isChecked);
        dbHelper.getWritableDatabase().update("pastime", values,
            "_id = ?", new String[] { String.valueOf(Pastime.this.id) });
      }
    });

    // ok button
    Button button = (Button) findViewById(R.id.button_pastime_do);
    button.setOnClickListener(new OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final long pastimeId = Pastime.this.id;
        final long selectionMethodId = SELECTION_METHODS.get(parent);
        final ActionInsertTask task = new ActionInsertTask(db, pastimeId, selectionMethodId);
        final ActionInsertInBackgroundTask backgroundTask = new ActionInsertInBackgroundTask(new ActivityProvider() {
          @Override
          public Activity getActivity() {
            return Pastime.this;
          }
        }, parent, task, weatherService);

        if (pastimeArgs == null)
        {
          // prompt for crowd setting
          new ManualPastimeDialogFragment(backgroundTask).show(getSupportFragmentManager(), null);
        }
        else
        {
          // insert into database in background
          backgroundTask.execute(pastimeArgs.getCrowd());
        }
      }
    });
  }

  private void readArguments()
  {
    Intent intent = getIntent();
    id = intent.getLongExtra(ARG_PASTIME_ID, 1);
    parent = (Class<?>) intent.getSerializableExtra(ARG_PARENT);
    pastimeArgs = (PastimeActionArgs) intent.getSerializableExtra(ARG_PASTIME_ARGS);

    if (!PARENTS.contains(parent))
    {
      throw new IllegalStateException(getClass() + " " + ARG_PARENT + " intent extra " + parent + " is invalid.");
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    getMenuInflater().inflate(R.menu.pastime, menu);
    return custom;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    final int id = item.getItemId();
    if (id == android.R.id.home)
    {
      // This ID represents the Home or Up button. In the case of this
      // activity, the Up button is shown. For
      // more details, see the Navigation pattern on Android Design:
      //
      // http://developer.android.com/design/patterns/navigation.html#up-vs-back
      //
      Intent intent = new Intent(this, parent);
      if (PastimeEditor.class.equals(parent))
      {
        intent.putExtra(PastimeEditor.ARG_PASTIME_ID, this.id);
        intent.putExtra(PastimeEditor.ARG_PARENT, Pastime.class);
      }
      navigateUpTo(intent);
      return true;
    }
    else if (id == R.id.action_pastime_edit && custom)
    {
      Intent intent = new Intent(this, PastimeEditor.class);
      intent.putExtra(PastimeEditor.ARG_PASTIME_ID, this.id);
      intent.putExtra(PastimeEditor.ARG_PARENT, Pastime.class);
      startActivity(intent);
    }
    return super.onOptionsItemSelected(item);
  }

  @NonNull
  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args)
  {
    return new SQLiteCursorLoader(this, dbHelper,
        "select a._id as _id, datetime(a.performed, 'localtime') as performed "
            + "from action a "
            + "join selection_method sm on a.method_id = sm._id "
            + "where a.pastime_id = ? and sm.name <> 'ballast' "
            + "order by a.performed desc "
            + "limit ?",
        new String[] { String.valueOf(Pastime.this.id), String.valueOf(MAX_ROWS) });
  }

  @Override
  public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data)
  {
    final LinearLayout historyView = (LinearLayout) findViewById(R.id.pastime_history);
    final Resources resources = getResources();

    // hide "loading" view
    ((TextView) findViewById(R.id.pastime_history_loading)).setVisibility(View.GONE);

    // remove all child views, else we might add duplicate entries
    historyView.removeAllViews();

    // add data views
    mAdapter.swapCursor(data);
    final int count = data.getCount();
    for (int i = 0; i < count; ++i)
    {
      historyView.addView(mAdapter.getView(i, null, historyView));
      if (i != count - 1)
      {
        historyView.addView(createDivider(resources));
      }
    }

    // show "empty" view
    if (data.getCount() == 0)
    {
      ((TextView) findViewById(R.id.pastime_history_empty)).setVisibility(View.VISIBLE);
    }
  }

  @Override
  public void onLoaderReset(@NonNull Loader<Cursor> loader)
  {
    Cursor oldCursor = mAdapter.swapCursor(null);
    if (oldCursor != null) {
      oldCursor.close();
    }
  }

  private View createDivider(Resources resources)
  {
    View divider = new View(this);
    divider.setLayoutParams(new ViewGroup.LayoutParams(
        LayoutParams.MATCH_PARENT,
        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, resources.getDisplayMetrics())
        ));
    divider.setBackgroundColor(Color.LTGRAY);
    return divider;
  }
}

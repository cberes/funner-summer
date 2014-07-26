package net.seabears.funner.summer;

import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.seabears.funner.ContextualDateFormatter;
import net.seabears.funner.db.ActionInsertTask;
import net.seabears.funner.db.FunnerDbHelper;
import net.seabears.funner.db.SelectionMethod;
import net.seabears.funner.summer.suggest.PastimeActionArgs;
import net.seabears.funner.weather.BlockingWeatherReceiver;
import net.seabears.funner.weather.WeatherPullService;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.commonsware.cwac.loaderex.SQLiteCursorLoader;

public class Pastime extends Activity
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
    SELECTION_METHODS = new HashMap<Class<?>, Long>(3);
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

  private List<String> settings;

  // This is the Adapter being used to display the list's data
  private SimpleCursorAdapter mAdapter;

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
    final String action = cursor.getString(cursor.getColumnIndex("action_name"));
    final boolean active = cursor.getInt(cursor.getColumnIndex("active")) != 0;
    custom = cursor.getInt(cursor.getColumnIndex("custom")) != 0;

    // pastime details
    setTitle(action);
    TextView actionView = (TextView) findViewById(R.id.pastime_action);
    actionView.setText(action);

    // history
    ListView historyView = (ListView) findViewById(R.id.pastime_history);
    historyView.setChoiceMode(ListView.CHOICE_MODE_NONE);
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
    historyView.setAdapter(mAdapter);
    getLoaderManager().initLoader(0, null, this);

    // settings
    settings = Arrays.asList(getResources().getText(R.string.pastime_include).toString());

    ListView settingsView = (ListView) findViewById(R.id.pastime_settings);
    settingsView.setAdapter(new ArrayAdapter<String>(this,
        android.R.layout.simple_list_item_multiple_choice,
        android.R.id.text1,
        settings));
    settingsView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    settingsView.setOnItemClickListener(new OnItemClickListener()
    {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id)
      {
        CheckedTextView item = (CheckedTextView) view;
        ContentValues values = new ContentValues(1);
        values.put("active", item.isChecked());
        dbHelper.getWritableDatabase().update("pastime", values, "_id = ?", new String[] { String.valueOf(Pastime.this.id) });
      }
    });
    settingsView.setItemChecked(0, active);

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
        final BlockingWeatherReceiver weatherReceiver = new BlockingWeatherReceiver();
        final ActionInsertInBackgroundTask backgroundTask = new ActionInsertInBackgroundTask(Pastime.this, parent, task, weatherReceiver);
        WeatherPullService.observeWeather(Pastime.this, weatherReceiver);

        if (pastimeArgs == null)
        {
          // prompt for crowd setting
          new ManualPastimeDialogFragment(backgroundTask).show(getFragmentManager(), null);
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
      navigateUpTo(new Intent(this, parent));
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
  public void onLoadFinished(Loader<Cursor> loader, Cursor data)
  {
    ((TextView) findViewById(R.id.pastime_history_loading)).setVisibility(View.GONE);
    mAdapter.swapCursor(data);
    if (data.getCount() == 0)
    {
      ((TextView) findViewById(R.id.pastime_history_empty)).setVisibility(View.VISIBLE);
    }
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader)
  {
    mAdapter.swapCursor(null);
  }
}

package net.seabears.funner.summer;

import java.util.Arrays;
import java.util.List;

import net.seabears.funner.db.FunnerDbHelper;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.commonsware.cwac.loaderex.SQLiteCursorLoader;

public class Pastime extends Activity
    implements LoaderManager.LoaderCallbacks<Cursor>
{
  public static final String ARG_PASTIME_ID = "pastime_id";

  private static final int MAX_ROWS = 10;

  private long id;

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

    // get pastime from database
    id = getIntent().getLongExtra(ARG_PASTIME_ID, 1);
    dbHelper = new FunnerDbHelper(this);
    Cursor cursor = dbHelper.getReadableDatabase()
        .query("pastime", new String[] { "action_name", "active" },
            "_id = ?", new String[] { String.valueOf(id) },
            null, null, null);
    cursor.moveToFirst();
    final String action = cursor.getString(cursor.getColumnIndex("action_name"));
    final boolean active = cursor.getInt(cursor.getColumnIndex("active")) != 0;

    // pastime details
    setTitle(action);
    TextView actionView = (TextView) findViewById(R.id.pastime_action);
    actionView.setText(action);

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

    // history
    ListView historyView = (ListView) findViewById(R.id.pastime_history);
    historyView.setChoiceMode(ListView.CHOICE_MODE_NONE);
    mAdapter = new SimpleCursorAdapter(this,
        android.R.layout.simple_list_item_1, null,
        new String[] { "performed" }, new int[] { android.R.id.text1 }, 0);
    historyView.setAdapter(mAdapter);
    getLoaderManager().initLoader(0, null, this);
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
      navigateUpTo(new Intent(this, Ideas.class));
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args)
  {
    return new SQLiteCursorLoader(this, dbHelper,
        "select a._id as _id, datetime(a.performed) as performed "
            + "from action a "
            + "join selection_method sm on a.method_id = sm._id "
            + "where a.pastime_id = ? and sm.name <> 'ballast' "
            + "order by a.performed desc "
            + "limit ?",
        new String[] { String.valueOf(id), String.valueOf(MAX_ROWS) });
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

package net.seabears.funner.summer;

import net.seabears.funner.db.FunnerDbHelper;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.commonsware.cwac.loaderex.SQLiteCursorLoader;

public class PastimesFragment extends ProgressListFragment
    implements LoaderManager.LoaderCallbacks<Cursor>
{
  // This is the Adapter being used to display the list's data
  private SimpleCursorAdapter mAdapter;

  @Override
  public void onActivityCreated(Bundle savedInstanceState)
  {
    super.onActivityCreated(savedInstanceState);

    // For the cursor adapter, specify which columns go into which views
    // SimpleCursorAdapter can have more columns than resource IDs
    final String[] fromColumns = { "action", "active" };
    // The TextView in simple_list_item_1
    final int[] toViews = { android.R.id.text1 };

    // Create an empty adapter we will use to display the loaded data.
    // We pass null for the cursor, then update it in onLoadFinished()
    mAdapter = new SimpleCursorAdapter(getActivity(),
        android.R.layout.simple_list_item_1, null,
        fromColumns, toViews, 0)
    {
      @Override
      public void bindView(View view, Context context, Cursor cursor)
      {
        super.bindView(view, context, cursor);

        // apply a different style to inactive items
        final View v = view.findViewById(toViews[0]);
        final boolean active = cursor.getInt(cursor.getColumnIndex(fromColumns[1])) != 0;
        if (v instanceof TextView && !active)
        {
          ((TextView) v).setTextAppearance(context, R.style.textAppearanceInactiveListItemSmall);
        }
      }
    };
    setListAdapter(mAdapter);

    // Prepare the loader. Either re-connect with an existing one,
    // or start a new one.
    getLoaderManager().initLoader(0, getArguments(), this);
  }

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
  {
    inflater.inflate(R.menu.pastimes, menu);
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    final int id = item.getItemId();
    if (id == R.id.action_pastime_create)
    {
      Intent intent = new Intent(getActivity(), PastimeEditor.class);
      intent.putExtra(PastimeEditor.ARG_PARENT, Pastimes.class);
      startActivity(intent);
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args)
  {
    // Now create and return a CursorLoader that will take care of
    // creating a Cursor for the data being displayed.
    return new SQLiteCursorLoader(getActivity().getApplicationContext(),
        new FunnerDbHelper(getActivity().getApplicationContext()),
        "select _id, action_name as action, active from pastime order by name",
        new String[0]);
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data)
  {
    // Swap the new cursor in. (The framework will take care of closing the
    // old cursor once we return.)
    mAdapter.swapCursor(data);
    if (data.getCount() == 0)
    {
      clear(R.string.empty);
    }
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader)
  {
    // This is called when the last Cursor provided to onLoadFinished()
    // above is about to be closed. We need to make sure we are no
    // longer using it.
    mAdapter.swapCursor(null);
  }

  @Override
  public void onListItemClick(ListView l, View v, int position, long id)
  {
    Intent intent = new Intent(this.getActivity(), Pastime.class);
    intent.putExtra(Pastime.ARG_PASTIME_ID, id);
    intent.putExtra(Pastime.ARG_PARENT, Pastimes.class);
    startActivity(intent);
  }
}

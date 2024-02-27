package net.seabears.funner.summer;

import java.text.ParseException;

import net.seabears.funner.ContextualDateFormatter;
import net.seabears.funner.db.FunnerDbHelper;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

public class HistoryFragment extends ProgressListFragment
    implements LoaderManager.LoaderCallbacks<Cursor>
{
  private static final int MAX_ROWS = 50;

  // This is the Adapter being used to display the list's data
  private SimpleCursorAdapter mAdapter;

  @Override
  public void onActivityCreated(Bundle savedInstanceState)
  {
    super.onActivityCreated(savedInstanceState);

    // For the cursor adapter, specify which columns go into which views
    final String[] fromColumns = { "action", "performed" };
    // The TextView in simple_list_item_1
    final int[] toViews = { android.R.id.text1, android.R.id.text2 };

    // Create an empty adapter we will use to display the loaded data.
    // We pass null for the cursor, then update it in onLoadFinished()
    final ContextualDateFormatter formatter = new ContextualDateFormatter(getActivity());
    mAdapter = new SimpleCursorAdapter(getActivity(),
        android.R.layout.simple_list_item_2, null,
        fromColumns, toViews, 0)
    {
      @Override
      public void setViewText(TextView v, String text)
      {
        if (v.getId() == android.R.id.text2)
        {
          try
          {
            super.setViewText(v, formatter.format(text));
            return;
          }
          catch (ParseException e)
          {
            Log.e(getClass().getSimpleName(), e.getMessage(), e);
          }
        }
        super.setViewText(v, text);
      }
    };
    setListAdapter(mAdapter);

    // Prepare the loader. Either re-connect with an existing one,
    // or start a new one.
    LoaderManager.getInstance(this).initLoader(0, getArguments(), this);
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args)
  {
    // Now create and return a CursorLoader that will take care of
    // creating a Cursor for the data being displayed.
    return new SQLiteCursorLoader(getActivity().getApplicationContext(),
        new FunnerDbHelper(getActivity().getApplicationContext()),
        "select p._id as _id, p.action_name as action, datetime(a.performed, 'localtime') as performed "
            + "from action a "
            + "join pastime p on a.pastime_id = p._id "
            + "join selection_method sm on a.method_id = sm._id "
            + "where sm.name <> 'ballast' "
            + "order by a.performed desc "
            + "limit ?",
        new String[] { String.valueOf(MAX_ROWS) });
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
    Cursor oldCursor = mAdapter.swapCursor(null);
    if (oldCursor != null) {
      oldCursor.close();
    }
  }

  @Override
  public void onListItemClick(ListView l, View v, int position, long id)
  {
    Intent intent = new Intent(this.getActivity(), Pastime.class);
    intent.putExtra(Pastime.ARG_PASTIME_ID, id);
    intent.putExtra(Pastime.ARG_PARENT, History.class);
    startActivity(intent);
  }
}

package net.seabears.funner.summer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.seabears.funner.db.FunnerDbHelper;
import net.seabears.funner.summer.suggest.RandomSqlQueryFactory;
import net.seabears.funner.summer.suggest.SuggestArgs;
import net.seabears.funner.summer.suggest.SuggestionSqlQueryFactory;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ListView;

import com.commonsware.cwac.loaderex.SQLiteCursorLoader;

public class IdeasFragment extends ProgressListFragment
    implements LoaderManager.LoaderCallbacks<Cursor>
{
  public static final String ARG_PARENT = "parent";

  public static final String ARG_QUERY_OPTIONS = "query_options";

  /**
   * The fragment argument representing the section number for this fragment.
   */
  public static final String ARG_SECTION_NUMBER = "section_number";

  private static final Set<Class<?>> PARENTS = new HashSet<Class<?>>(Arrays.asList(Ideas.class, RandomPastimes.class));

  private Class<?> parent;

  // This is the Adapter being used to display the list's data
  private SimpleCursorAdapter mAdapter;

  private SuggestArgs suggestArgs;

  @Override
  public void onActivityCreated(Bundle savedInstanceState)
  {
    super.onActivityCreated(savedInstanceState);

    parent = (Class<?>) getArguments().getSerializable(ARG_PARENT);

    if (!PARENTS.contains(parent))
    {
      throw new IllegalStateException(getClass() + " " + ARG_PARENT + " argument " + parent + " is invalid.");
    }

    // For the cursor adapter, specify which columns go into which views
    final String[] fromColumns = { "action" };
    // The TextView in simple_list_item_1
    final int[] toViews = { android.R.id.text1 };

    // Create an empty adapter we will use to display the loaded data.
    // We pass null for the cursor, then update it in onLoadFinished()
    mAdapter = new SimpleCursorAdapter(getActivity(),
        android.R.layout.simple_list_item_1, null,
        fromColumns, toViews, 0);
    setListAdapter(mAdapter);

    // Prepare the loader. Either re-connect with an existing one,
    // or start a new one.
    getLoaderManager().initLoader(0, getArguments(), this);
  }

  // Called when a new Loader needs to be created
  public Loader<Cursor> onCreateLoader(int id, Bundle args)
  {
    // Now create and return a CursorLoader that will take care of
    // creating a Cursor for the data being displayed.
    suggestArgs = SuggestArgs.fromBundle(args.getBundle(ARG_QUERY_OPTIONS));
    final Context context = getActivity().getApplicationContext();
    return new SQLiteCursorLoader(
        context,
        new FunnerDbHelper(context),
        Ideas.class.equals(parent) ? SuggestionSqlQueryFactory.query(context)
            : RandomSqlQueryFactory.query(context),
        Ideas.class.equals(parent) ? SuggestionSqlQueryFactory.args(suggestArgs)
            : RandomSqlQueryFactory.args(suggestArgs));
  }

  // Called when a previously created loader has finished loading
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

  // Called when a previously created loader is reset, making the data
  // unavailable
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
    intent.putExtra(Pastime.ARG_PARENT, parent);
    intent.putExtra(Pastime.ARG_PASTIME_ARGS, suggestArgs);
    startActivity(intent);
  }
}
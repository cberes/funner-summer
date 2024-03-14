package net.seabears.funner.summer;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import net.seabears.funner.db.FunnerDbHelper;
import net.seabears.funner.db.SQLiteCursorLoader;
import net.seabears.funner.summer.suggest.RandomSqlQueryFactory;
import net.seabears.funner.summer.suggest.SuggestArgs;
import net.seabears.funner.summer.suggest.SuggestionSqlQueryFactory;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.loader.app.LoaderManager;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.loader.content.Loader;

public class IdeasFragment extends ProgressListFragment
    implements LoaderManager.LoaderCallbacks<Cursor>
{
  public static final int LIST_COUNT_DEFAULT = 7;

  public static final String ARG_PARENT = "parent";

  /**
   * The fragment argument representing the section number for this fragment.
   */
  public static final String ARG_SECTION_NUMBER = "section_number";

  private static final Set<Class<?>> PARENTS = new HashSet<Class<?>>(Arrays.<Class<?>> asList(Ideas.class, RandomPastimes.class));

  private Class<?> parent;

  // This is the Adapter being used to display the list's data
  private SimpleCursorAdapter mAdapter;

  private Bundle updatedArgs;

  private Date lastRefreshed;

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

    // set list adapter for suggestions after adding ad view
    setListAdapter(mAdapter);

    LoaderManager.getInstance(this).initLoader(0, argumentsWithUpdates(), this);
    lastRefreshed = new Date();
  }

  private Bundle argumentsWithUpdates()
  {
    Bundle initial = getArguments();
    Bundle withUpdates = new Bundle();
    withUpdates.putAll(initial);
    if (updatedArgs != null) {
      withUpdates.putAll(updatedArgs);
    }
    return withUpdates;
  }

  // Called when a new Loader needs to be created
  @NonNull
  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args)
  {
    // Now create and return a CursorLoader that will take care of
    // creating a Cursor for the data being displayed.
    SuggestArgs suggestArgs = SuggestArgs.fromBundle(args);
    final Context context = getActivity().getApplicationContext();
    return new SQLiteCursorLoader(
            context,
            new FunnerDbHelper(context),
            query(context),
            queryArgs(suggestArgs));
  }

  private String query(Context context)
  {
    return Ideas.class.equals(parent)
            ? SuggestionSqlQueryFactory.query(context)
            : RandomSqlQueryFactory.query(context);
  }

  private String[] queryArgs(SuggestArgs suggestArgs)
  {
    return Ideas.class.equals(parent)
            ? SuggestionSqlQueryFactory.args(suggestArgs)
            : RandomSqlQueryFactory.args(suggestArgs);
  }

  // Called when a previously created loader is reset, making the data
  // unavailable
  public void onLoaderReset(@NonNull Loader<Cursor> loader)
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
  public void onLoadFinished(@NonNull final Loader<Cursor> loader, final Cursor data) {
    // Swap the new cursor in. (The framework will take care of closing the
    // old cursor once we return.)
    mAdapter.swapCursor(data);
    if (data.getCount() == 0)
    {
      clear(R.string.empty);
    }
  }

  @Override
  public void onListItemClick(ListView l, View v, int position, long id)
  {
    Intent intent = new Intent(this.getActivity(), Pastime.class);
    intent.putExtra(Pastime.ARG_PASTIME_ID, id);
    intent.putExtra(Pastime.ARG_PARENT, parent);
    intent.putExtra(Pastime.ARG_PASTIME_ARGS, SuggestArgs.fromBundle(argumentsWithUpdates()));
    startActivity(intent);
  }

  public void refresh(Bundle update)
  {
    updatedArgs = update;
    LoaderManager.getInstance(this).destroyLoader(0);
    LoaderManager.getInstance(this).initLoader(0, argumentsWithUpdates(), this);
    lastRefreshed = new Date();
  }

  public Date getLastRefreshed()
  {
    return lastRefreshed;
  }
}
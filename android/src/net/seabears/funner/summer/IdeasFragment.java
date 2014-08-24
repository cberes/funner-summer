package net.seabears.funner.summer;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import net.seabears.funner.Weather;
import net.seabears.funner.db.FunnerDbHelper;
import net.seabears.funner.location.LocationErrorReceiver;
import net.seabears.funner.location.LocationUtils;
import net.seabears.funner.summer.license.License;
import net.seabears.funner.summer.suggest.RandomSqlQueryFactory;
import net.seabears.funner.summer.suggest.SuggestArgs;
import net.seabears.funner.summer.suggest.SuggestionSqlQueryFactory;
import net.seabears.funner.weather.BlockOnWeatherSQLiteCursorLoader;
import net.seabears.funner.weather.BlockingWeatherReceiver;
import net.seabears.funner.weather.WeatherPullService;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class IdeasFragment extends ProgressListFragment
    implements LoaderManager.LoaderCallbacks<Cursor>
{
  public static final int LIST_COUNT_DEFAULT = 7;

  public static final String ARG_PARENT = "parent";

  public static final String ARG_QUERY_OPTIONS = "query_options";

  /**
   * The fragment argument representing the section number for this fragment.
   */
  public static final String ARG_SECTION_NUMBER = "section_number";

  private static final Set<Class<?>> PARENTS = new HashSet<Class<?>>(Arrays.<Class<?>> asList(Ideas.class, RandomPastimes.class));

  /* Your ad unit id. Replace with your actual ad unit id. */
  private static final String AD_UNIT_ID = "ca-app-pub-3190866209318496/8163233208";

  private AdView adView;

  private Class<?> parent;

  // This is the Adapter being used to display the list's data
  private SimpleCursorAdapter mAdapter;

  private Date lastRefreshed;

  private SuggestArgs suggestArgs;

  private final BlockingWeatherReceiver weatherReceiver = new BlockingWeatherReceiver();

  private boolean firstAttemptToGetWeather = true;

  private final LocationErrorReceiver errorReceiver = new LocationErrorReceiver()
  {
    @Override
    protected Activity getActivity()
    {
      return IdeasFragment.this.getActivity();
    }
  };

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

    if (License.getInstance().isAdsEnabled())
    {
      // Create an ad.
      adView = new AdView(getActivity());
      adView.setAdSize(AdSize.BANNER);
      adView.setAdUnitId(AD_UNIT_ID);

      // Add the AdView to the view hierarchy. The view will have no size
      // until the ad is loaded.
      getListView().addHeaderView(adView);

      // Create an ad request. Check logcat output for the hashed device ID to
      // get test ads on a physical device.
      final AdRequest.Builder builder = new AdRequest.Builder()
          .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
          .addTestDevice("B07B09DBA65355FD3B655A02BC1732A2");
      Location location = weatherReceiver.getLocation();
      if (location != null)
      {
        builder.setLocation(location);
      }
      final AdRequest adRequest = builder.build();

      // Start loading the ad in the background.
      adView.loadAd(adRequest);
    }
  }

  // Called when a new Loader needs to be created
  public Loader<Cursor> onCreateLoader(int id, Bundle args)
  {
    // Now create and return a CursorLoader that will take care of
    // creating a Cursor for the data being displayed.
    suggestArgs = SuggestArgs.fromBundle(args.getBundle(ARG_QUERY_OPTIONS));
    final Context context = getActivity().getApplicationContext();
    return new BlockOnWeatherSQLiteCursorLoader(
        context,
        new FunnerDbHelper(context),
        Ideas.class.equals(parent)
            ? SuggestionSqlQueryFactory.query(context)
            : RandomSqlQueryFactory.query(context),
        weatherReceiver)
    {
      @Override
      protected String[] getArgs(Weather weather)
      {
        suggestArgs = new SuggestArgs(suggestArgs.getCount(),
            suggestArgs.getCrowd(), (int) weather.getTemperature(), weather.getCondition());
        return Ideas.class.equals(parent)
            ? SuggestionSqlQueryFactory.args(suggestArgs)
            : RandomSqlQueryFactory.args(suggestArgs);
      }
    };
  }

  // Called when a previously created loader has finished loading
  public void onLoadFinished(Loader<Cursor> loader, Cursor data)
  {
    // Swap the new cursor in. (The framework will take care of closing the
    // old cursor once we return.)
    lastRefreshed = new Date();
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

  public void refresh()
  {
    getLoaderManager().destroyLoader(0);
    getLoaderManager().initLoader(0, getArguments(), this);
  }

  public Date getLastRefreshed()
  {
    return lastRefreshed;
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    if (requestCode == LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST)
    {
      // try to get the weather regardless of whether the error was resolved
      final boolean ignoreErrors = true;
      WeatherPullService.observeWeather(getActivity(), weatherReceiver, errorReceiver, ignoreErrors);
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public void onStart()
  {
    super.onStart();
    final boolean localFirstAttemptToGetWeather = firstAttemptToGetWeather;
    firstAttemptToGetWeather = false;
    WeatherPullService.observeWeather(getActivity(), weatherReceiver, errorReceiver, !localFirstAttemptToGetWeather);
  }

  @Override
  public void onResume()
  {
    super.onResume();
    if (adView != null)
    {
      adView.resume();
    }
  }

  @Override
  public void onPause()
  {
    if (adView != null)
    {
      adView.pause();
    }
    super.onPause();
  }

  @Override
  public void onDestroy()
  {
    // Destroy the AdView.
    if (adView != null)
    {
      adView.destroy();
    }
    super.onDestroy();
  }
}
package net.seabears.funner.summer;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.seabears.funner.Weather;
import android.app.Activity;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;

public class WeatherPullService extends IntentService implements
    GooglePlayServicesClient.ConnectionCallbacks,
    GooglePlayServicesClient.OnConnectionFailedListener
{
  private static final String WEATHER_URL = "http://ilium/funner/api/weather/summary?lat=%f&lng=%f";

  private static final String PREF_KEY_WEATHER_CONDITION = "weather_condition";

  private static final String PREF_KEY_WEATHER_TEMPERATURE = "weather_temperature";

  private static final String PREF_KEY_WEATHER_EXPIRATION = "weather_temperature";

  private LocationClient mLocationClient;

  private FutureTask<Location> locationTask;

  private final ExecutorService executor;

  private final ObjectMapper mapper;

  /**
   * Defines a custom Intent action
   */
  public static final String BROADCAST_ACTION = WeatherPullService.class.getName() + ".BROADCAST";

  /**
   * Defines the key for the status "extra" in an Intent
   */
  public static final String EXTENDED_DATA_STATUS = WeatherPullService.class.getName() + ".STATUS";

  /**
   * Defines a custom Intent action
   */
  public static final String RESULT_ACTION = WeatherPullService.class.getName() + ".RESULT";

  /**
   * Defines the key for the status "extra" in an Intent
   */
  public static final String EXTENDED_DATA_RESULT = WeatherPullService.class.getName() + ".RESULT";

  public static void observeWeather(Activity activity)
  {
    Weather weather = readWeatherFromPreferences(activity);
    if (weather != null)
    {
      broadcastResult(activity, weather, 0, TimeUnit.SECONDS);
      return;
    }

    activity.startService(new Intent(activity, WeatherPullService.class));
  }

  public WeatherPullService()
  {
    super(WeatherPullService.class.getSimpleName());
    executor = Executors.newSingleThreadExecutor();
    mapper = new ObjectMapper();
  }

  @Override
  protected void onHandleIntent(Intent intent)
  {
    Location location = findLocationWithFallback(getDefaultLocation(), 10, TimeUnit.SECONDS);
    broadcastStatus(this, 50);
    Weather weather = findWeatherWithFallback(location, null, 10, TimeUnit.SECONDS);
    broadcastStatus(this, 99);
    if (weather == null)
    {
      // shorter cache time because the result was invalid
      broadcastResult(this, getDefaultWeather(), 15, TimeUnit.MINUTES);
    }
    else
    {
      // longer cache time because the result was valid
      broadcastResult(this, weather, 45, TimeUnit.MINUTES);
    }
    broadcastStatus(this, 100);
  }

  private static Weather readWeatherFromPreferences(Activity activity)
  {
    final SharedPreferences prefs = activity.getPreferences(Activity.MODE_PRIVATE);
    final String condition = prefs.getString(PREF_KEY_WEATHER_CONDITION, null);
    final float temperature = prefs.getFloat(PREF_KEY_WEATHER_TEMPERATURE, Float.NaN);
    if (System.currentTimeMillis() <= prefs.getLong(PREF_KEY_WEATHER_EXPIRATION, 0)
        && condition != null && temperature != Float.NaN)
    {
      return new Weather(condition, temperature);
    }
    return null;
  }

  public static void writeWeatherToPreferences(Activity activity, Weather weather, long ttl, TimeUnit unit)
  {
    activity.getPreferences(Activity.MODE_PRIVATE).edit()
        .putString(PREF_KEY_WEATHER_CONDITION, weather.getCondition())
        .putFloat(PREF_KEY_WEATHER_TEMPERATURE, (float) weather.getTemperature())
        .putLong(PREF_KEY_WEATHER_EXPIRATION, unit.toMillis(ttl) + System.currentTimeMillis())
        .commit();
  }

  private static void broadcastStatus(Context context, int status)
  {
    // Creates a new Intent containing a URI object BROADCAST_ACTION is a custom
    // Intent action
    Intent localIntent = new Intent(BROADCAST_ACTION)
        .putExtra(EXTENDED_DATA_STATUS, status);
    // Broadcasts the Intent to receivers in this app.
    LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
  }

  private static void broadcastResult(Context context, Weather result, long ttl, TimeUnit unit)
  {
    // Creates a new Intent containing a URI object BROADCAST_ACTION is a custom
    // Intent action
    Intent localIntent = new Intent(RESULT_ACTION)
        .putExtra(EXTENDED_DATA_RESULT, result)
        .putExtra(EXTENDED_DATA_STATUS, unit.toMillis(ttl));
    // Broadcasts the Intent to receivers in this app.
    LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
  }

  private static Weather getDefaultWeather()
  {
    return new Weather("clouds", 70);
  }

  private Weather findWeatherWithFallback(Location location, Weather fallback, long timeout, TimeUnit unit)
  {
    try
    {
      return findWeather(location, timeout, unit);
    } catch (IOException e)
    {
      Log.e(getClass().getSimpleName(), e.getMessage(), e);
      return fallback;
    }
  }

  private Weather findWeather(Location location, long timeout, TimeUnit unit) throws IOException
  {
    URL url = new URL(String.format(WEATHER_URL, location.getLatitude(), location.getLongitude()));
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setConnectTimeout((int) unit.toMillis(timeout) / 3);
    connection.setReadTimeout((int) unit.toMillis(timeout) - (int) unit.toMillis(timeout) / 3);
    try
    {
      InputStream in = new BufferedInputStream(connection.getInputStream());
      return mapper.readValue(in, Weather.class);
    } finally
    {
      connection.disconnect();
    }
  }

  private static Location getDefaultLocation()
  {
    final Location location = new Location(WeatherPullService.class.getName());
    location.setLatitude(41.8500300);
    location.setLongitude(-87.6500500);
    return location;
  }

  private Location findLocationWithFallback(Location fallback, long timeout, TimeUnit unit)
  {
    try
    {
      mLocationClient.connect();
      return locationTask.get(timeout, unit);
    } catch (InterruptedException | ExecutionException | TimeoutException e)
    {
      Log.e(getClass().getSimpleName(), e.getMessage(), e);
    } finally
    {
      mLocationClient.disconnect();
    }
    return fallback;
  }

  private boolean servicesConnected(Context context)
  {
    return GooglePlayServicesUtil.isGooglePlayServicesAvailable(context)
        == ConnectionResult.SUCCESS;
  }

  @Override
  public void onCreate()
  {
    super.onCreate();
    mLocationClient = new LocationClient(this, this, this);
    locationTask = new FutureTask<Location>(new LocationCallable(mLocationClient));
  }

  @Override
  public void onDestroy()
  {
    super.onDestroy();
  }

  @Override
  public void onConnectionFailed(ConnectionResult result)
  {
    // TODO handle error
  }

  @Override
  public void onConnected(Bundle connectionHint)
  {
    executor.execute(locationTask);
  }

  @Override
  public void onDisconnected()
  {
    // do nothing
  }

  private static class LocationCallable implements Callable<Location>
  {
    private LocationClient mLocationClient;

    public LocationCallable(LocationClient mLocationClient)
    {
      this.mLocationClient = mLocationClient;
    }

    @Override
    public Location call() throws Exception
    {
      return mLocationClient.getLastLocation();
    }
  }

  public static abstract class WeatherReceiver extends BroadcastReceiver
  {
    @Override
    public void onReceive(Context context, Intent intent)
    {
      // get result from intent
      final Weather weather = (Weather) intent.getSerializableExtra(EXTENDED_DATA_RESULT);

      // if activity is not dead, use it to cache the result
      // IDK if the destroyed-check is necessary
      Activity activity = getActivity();
      if (!activity.isDestroyed())
      {
        final long cacheTime = intent.getLongExtra(EXTENDED_DATA_STATUS, 0L);
        if (cacheTime > 0)
        {
          writeWeatherToPreferences(activity, weather, System.currentTimeMillis() + cacheTime, TimeUnit.MILLISECONDS);
        }
      }

      // call the more specific handler
      onReceiveWeather(context, weather);
    }

    protected abstract Activity getActivity();

    public abstract void onReceiveWeather(Context context, Weather weather);
  }
}

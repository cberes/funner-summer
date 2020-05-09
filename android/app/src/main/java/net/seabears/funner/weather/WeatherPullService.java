package net.seabears.funner.weather;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import net.seabears.funner.location.LocationErrorReceiver;
import net.seabears.funner.location.LocationUtils;
import android.app.Activity;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

public class WeatherPullService extends IntentService implements
    GooglePlayServicesClient.ConnectionCallbacks,
    GooglePlayServicesClient.OnConnectionFailedListener
{
  private static final String WEATHER_URL = "https://btcvd69xdc.execute-api.us-east-1.amazonaws.com/v1/api/weather?lat=%s&lng=%s&summary=true";

  private static final String WEATHER_API_KEY = "TODO_REPLACE_ME";

  private static final String PREF_KEY_WEATHER_CONDITION = "weather_condition";

  private static final String PREF_KEY_WEATHER_TEMPERATURE = "weather_temperature";

  private static final String PREF_KEY_WEATHER_EXPIRATION = "weather_expiration";

  private static final String PREF_KEY_LOCATION_LATITUDE = "location_latitude";

  private static final String PREF_KEY_LOCATION_LONGITUDE = "location_longitude";

  private static final String ARG_IGNORE_ERRORS = "ignore_errors";

  private final ExecutorService executor;

  private final ObjectMapper mapper;

  private LocationClient mLocationClient;

  private FutureTask<Location> locationTask;

  private boolean ignoreErrors;

  private boolean locationError;

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
  public static final String EXTENDED_DATA_LOCATION = WeatherPullService.class.getName() + ".LOCATION";

  /**
   * Defines the key for the status "extra" in an Intent
   */
  public static final String EXTENDED_DATA_WEATHER = WeatherPullService.class.getName() + ".WEATHER";

  /**
   * Defines a custom Intent action
   */
  public static final String ERROR_ACTION = WeatherPullService.class.getName() + ".ERROR";

  public static void observeWeather(Activity activity, WeatherReceiver weatherReceiver, LocationErrorReceiver errorReceiver,
      boolean ignoreErors)
  {
    // register a receiver
    // regardless of how the weather is retrieved, we'll send it via broadcast
    // register for error broadcasts as well
    LocalBroadcastManager.getInstance(activity).registerReceiver(weatherReceiver, new IntentFilter(RESULT_ACTION));
    LocalBroadcastManager.getInstance(activity).registerReceiver(errorReceiver, new IntentFilter(ERROR_ACTION));

    Location location = readLocationFromPreferences(activity);
    Weather weather = readWeatherFromPreferences(activity);
    if (weather != null)
    {
      // weather was cached
      if (location != null)
      {
        broadcastResult(activity, location);
      }
      broadcastResult(activity, weather);
    }
    else if (ignoreErors || LocationUtils.servicesConnected(activity, true))
    {
      // weather needs to be retrieved
      Intent intent = new Intent(activity, WeatherPullService.class);
      intent.putExtra(ARG_IGNORE_ERRORS, ignoreErors);
      activity.startService(intent);
    }
  }

  public WeatherPullService()
  {
    super(WeatherPullService.class.getSimpleName());
    executor = Executors.newSingleThreadExecutor();
    mapper = new ObjectMapper();
    locationError = false;
  }

  @Override
  protected void onHandleIntent(Intent intent)
  {
    Weather weather = readWeatherFromPreferences(this);
    if (weather == null)
    {
      ignoreErrors = intent.getBooleanExtra(ARG_IGNORE_ERRORS, true);
      Location location = findLocationWithFallback(getDefaultLocation(), 10, TimeUnit.SECONDS);
      if (!locationError)
      {
        Log.d(getClass().getSimpleName(), "Got location without error");
        broadcastStatus(this, 50);
        weather = findWeatherWithFallback(location, null, 10, TimeUnit.SECONDS);
        boolean weatherValid = weather != null && weather.getTemperature() != null;
        Log.d(getClass().getSimpleName(),
                (weatherValid ? "Got weather without error" : "Got default weather") + ": " + weather);
        broadcastStatus(this, 99);
        if (weatherValid)
        {
          // shorter cache time because the result was invalid
          weather = getDefaultWeather();
          writeWeatherToPreferences(this, weather, 15, TimeUnit.MINUTES);
        }
        else
        {
          writeWeatherToPreferences(this, weather, 45, TimeUnit.MINUTES);
        }
      }
    }

    // weather should be null only if there was a resolvable error getting the
    // location and ignoreErrors was false
    if (weather != null)
    {
      broadcastResult(this, weather);
    }
    broadcastStatus(this, 100);
  }

  private static Location readLocationFromPreferences(Context context)
  {
    final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    final float latitude = prefs.getFloat(PREF_KEY_LOCATION_LATITUDE, Float.NaN);
    final float longitude = prefs.getFloat(PREF_KEY_LOCATION_LONGITUDE, Float.NaN);
    if (latitude != Float.NaN && longitude != Float.NaN)
    {
      final Location location = new Location(WeatherPullService.class.getName());
      location.setLatitude(latitude);
      location.setLongitude(longitude);
      return location;
    }
    return null;
  }

  private static Weather readWeatherFromPreferences(Context context)
  {
    final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    final String condition = prefs.getString(PREF_KEY_WEATHER_CONDITION, null);
    final String temperature = prefs.getString(PREF_KEY_WEATHER_TEMPERATURE, null);
    if (System.currentTimeMillis() <= prefs.getLong(PREF_KEY_WEATHER_EXPIRATION, 0)
        && condition != null && temperature != null)
    {
      return new Weather(condition, new BigDecimal(temperature));
    }
    return null;
  }

  private static void writeWeatherToPreferences(Context context, Weather weather, long ttl, TimeUnit unit)
  {
    PreferenceManager.getDefaultSharedPreferences(context).edit()
        .putString(PREF_KEY_WEATHER_CONDITION, weather.getCondition())
        .putString(PREF_KEY_WEATHER_TEMPERATURE, weather.getTemperature().toString())
        .putLong(PREF_KEY_WEATHER_EXPIRATION, unit.toMillis(ttl) + System.currentTimeMillis())
        .commit();
  }

  private static void writeLocationToPreferences(Context context, Location location)
  {
    PreferenceManager.getDefaultSharedPreferences(context).edit()
        .putFloat(PREF_KEY_LOCATION_LATITUDE, (float) location.getLatitude())
        .putFloat(PREF_KEY_LOCATION_LONGITUDE, (float) location.getLongitude())
        .commit();
  }

  private static void broadcastStatus(Context context, int status)
  {
    Intent localIntent = new Intent(BROADCAST_ACTION)
        .putExtra(EXTENDED_DATA_STATUS, status);
    LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
  }

  private static void broadcastResult(Context context, Weather result)
  {
    Intent localIntent = new Intent(RESULT_ACTION)
        .putExtra(EXTENDED_DATA_WEATHER, result);
    LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
  }

  private static void broadcastResult(Context context, Location result)
  {
    Intent localIntent = new Intent(RESULT_ACTION)
        .putExtra(EXTENDED_DATA_LOCATION, result);
    LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
  }

  private static void broadcastError(Context context, ConnectionResult result)
  {
    Intent localIntent = new Intent(ERROR_ACTION)
        .putExtra(LocationErrorReceiver.ARG_PENDING_INTENT, result.getResolution())
        .putExtra(LocationErrorReceiver.ARG_STATUS_CODE, result.getErrorCode());
    LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
  }

  private static Weather getDefaultWeather()
  {
    return new Weather("clouds", BigDecimal.valueOf(70));
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
    URL url = new URL(String.format(WEATHER_URL, roundCoord(location.getLatitude()), roundCoord(location.getLongitude())));
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");
    connection.setRequestProperty("Accept", "application/json");
    connection.setRequestProperty("X-API-Key", WEATHER_API_KEY);
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

  private static String roundCoord(final double coord)
  {
    BigDecimal precision = BigDecimal.valueOf(20);
    return BigDecimal.valueOf(coord).multiply(precision)
      .setScale(0, RoundingMode.HALF_EVEN)
      .divide(precision, 2, RoundingMode.HALF_EVEN)
      .toString();
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
    if (LocationUtils.servicesConnected(this))
    {
      try
      {
        mLocationClient.connect();
        final Location location = locationTask.get(timeout, unit);
        Log.d(getClass().getSimpleName(), location != null ? "Found location" : "Could not find location");
        writeLocationToPreferences(this, location);
        broadcastResult(this, location);
        return location;
      } catch (InterruptedException e)
      {
        Log.e(getClass().getSimpleName(), e.getMessage(), e);
      } catch (ExecutionException e)
      {
        Log.e(getClass().getSimpleName(), e.getMessage(), e);
      } catch (TimeoutException e)
      {
        Log.e(getClass().getSimpleName(), e.getMessage(), e);
      } finally
      {
        mLocationClient.disconnect();
      }
    }
    Log.d(getClass().getSimpleName(), "Using fallback location for weather");
    return fallback;
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
    if (!ignoreErrors)
    {
      broadcastError(this, result);
      if (result.hasResolution())
      {
        locationError = true;
      }
    }
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
      final Object location = intent.getParcelableExtra(EXTENDED_DATA_LOCATION);
      final Object weather = intent.getSerializableExtra(EXTENDED_DATA_WEATHER);
      if (location != null)
      {
        onReceiveLocation(context, (Location) location);
      }
      if (weather != null)
      {
        onReceiveWeather(context, (Weather) weather);
      }
    }

    protected abstract void onReceiveLocation(Context context, Location location);

    protected abstract void onReceiveWeather(Context context, Weather weather);
  }
}

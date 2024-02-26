package net.seabears.funner.weather;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import net.seabears.funner.Weather;
import net.seabears.funner.weather.WeatherPullService.WeatherReceiver;
import android.content.Context;
import android.location.Location;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

public class BlockingWeatherReceiver extends WeatherReceiver
{
  private final CountDownLatch received = new CountDownLatch(1);

  private Location location;

  private Weather weather;

  @Override
  protected synchronized void onReceiveLocation(Context context, Location location)
  {
    this.location = location;
  }

  @Override
  protected void onReceiveWeather(Context context, Weather weather)
  {
    Log.d(getClass().getSimpleName(), "Received weather: " + weather);
    LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
    this.weather = weather;
    this.received.countDown();
  }

  public Weather get() throws InterruptedException
  {
    received.await();
    return weather;
  }

  public Weather get(long timeout, TimeUnit unit) throws InterruptedException
  {
    received.await(timeout, unit);
    return weather;
  }

  public synchronized Location getLocation()
  {
    return location;
  }
}

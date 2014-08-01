package net.seabears.funner.weather.commands;

import net.seabears.funner.cache.CachedValue;
import net.seabears.funner.cache.IWeatherCacheRemote;
import net.seabears.funner.weather.GeographicCoordinate;
import net.seabears.funner.weather.openweathermap.Weather;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

public class WriteWeatherToRemoteCacheCommand extends HystrixCommand<Void>
{
  private IWeatherCacheRemote cache;
  private GeographicCoordinate key;
  private CachedValue<Weather> value;

  public WriteWeatherToRemoteCacheCommand(IWeatherCacheRemote cache, GeographicCoordinate key, CachedValue<Weather> value)
  {
    super(HystrixCommandGroupKey.Factory.asKey(WriteWeatherToRemoteCacheCommand.class.getSimpleName()));
    this.cache = cache;
    this.key = key;
    this.value = value;
  }

  @Override
  protected Void run()
  {
    cache.write(key, value);
    return null;
  }
}

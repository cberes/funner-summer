package net.seabears.funner.weather.commands;

import net.seabears.funner.cache.CachedValue;
import net.seabears.funner.cache.IWeatherCacheRemote;
import net.seabears.funner.weather.GeographicCoordinate;
import net.seabears.funner.weather.openweathermap.Weather;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

@Scope("request")
@Component
public class ReadWeatherFromRemoteCacheCommand extends HystrixCommand<Weather> implements IWeatherReadCommand
{
  private GeographicCoordinate key;
  private boolean valid;
  private Weather defaultValue;
  private IWeatherCacheRemote cache;

  public ReadWeatherFromRemoteCacheCommand(IWeatherCacheRemote cache)
  {
    super(HystrixCommandGroupKey.Factory.asKey(ReadWeatherFromRemoteCacheCommand.class.getSimpleName()));
    this.cache = cache;
    this.valid = false;
  }

  @Override
  public void setDefault(Weather value)
  {
    this.defaultValue = value;
  }

  @Override
  public boolean isValueValid()
  {
    return valid;
  }

  public void setKey(GeographicCoordinate key)
  {
    this.key = key;
  }

  @Override
  protected Weather run()
  {
    final CachedValue<Weather> value = cache.read(key);
    if (value == null)
    {
      return null;
    }
    else
    {
      final Weather weather = value.getValue();
      valid = weather != null && value.isAlive();
      return weather;
    }
  }

  @Override
  protected Weather getFallback()
  {
    return defaultValue;
  }

  @Override
  public Weather getValue()
  {
    return execute();
  }

  @Override
  public boolean cacheValue()
  {
    return false;
  }
}

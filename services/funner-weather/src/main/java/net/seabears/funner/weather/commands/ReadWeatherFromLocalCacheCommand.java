package net.seabears.funner.weather.commands;

import net.seabears.funner.cache.IWeatherCacheLocal;
import net.seabears.funner.weather.GeographicCoordinate;
import net.seabears.funner.weather.openweathermap.Weather;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("request")
@Component
public class ReadWeatherFromLocalCacheCommand implements IWeatherReadCommand
{
  private GeographicCoordinate key;
  private boolean valid;
  private IWeatherCacheLocal cache;

  public ReadWeatherFromLocalCacheCommand(IWeatherCacheLocal cache)
  {
    this.cache = cache;
    this.valid = false;
  }

  @Override
  public void setDefault(Weather value)
  {
    // no default value
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
  public Weather getValue()
  {
    final Weather value = cache.read(key);
    valid = value != null;
    return value;
  }

  @Override
  public boolean cacheValue()
  {
    return false;
  }
}

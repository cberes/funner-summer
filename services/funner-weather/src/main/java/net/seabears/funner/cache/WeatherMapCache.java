package net.seabears.funner.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.seabears.funner.weather.GeographicCoordinate;
import net.seabears.funner.weather.openweathermap.Weather;

import org.springframework.stereotype.Component;

@Component
public class WeatherMapCache implements IWeatherCache
{
  private final Map<GeographicCoordinate, CachedValue<Weather>> CACHE = new ConcurrentHashMap<GeographicCoordinate, CachedValue<Weather>>();

  @Override
  public CachedValue<Weather> read(GeographicCoordinate key)
  {
    return CACHE.get(key);
  }

  @Override
  public void write(GeographicCoordinate key, CachedValue<Weather> value)
  {
    CACHE.put(key, value);
  }

}

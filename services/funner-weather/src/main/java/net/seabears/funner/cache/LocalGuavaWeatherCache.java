package net.seabears.funner.cache;

import java.util.concurrent.TimeUnit;

import net.seabears.funner.weather.GeographicCoordinate;
import net.seabears.funner.weather.openweathermap.Weather;

import org.springframework.stereotype.Component;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Component
public class LocalGuavaWeatherCache implements IWeatherCacheLocal
{
  private final LoadingCache<GeographicCoordinate, Weather> cache = CacheBuilder.newBuilder()
      .maximumSize(10000)
      .expireAfterWrite(1, TimeUnit.HOURS)
      .build(new CacheLoader<GeographicCoordinate, Weather>()
      {
        @Override
        public Weather load(GeographicCoordinate key)
        {
          return null;
        }
      });

  @Override
  public Weather read(GeographicCoordinate key)
  {
    return cache.getIfPresent(key);
  }

  @Override
  public void write(GeographicCoordinate key, Weather value)
  {
    cache.put(key, value);
  }

}

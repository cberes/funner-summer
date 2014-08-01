package net.seabears.funner.cache;

import net.seabears.funner.weather.GeographicCoordinate;
import net.seabears.funner.weather.openweathermap.Weather;

public interface IWeatherCacheLocal extends ILocalCache<GeographicCoordinate, Weather>
{
  Weather read(GeographicCoordinate key);

  void write(GeographicCoordinate key, Weather value);
}

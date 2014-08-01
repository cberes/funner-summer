package net.seabears.funner.cache;

import net.seabears.funner.weather.GeographicCoordinate;
import net.seabears.funner.weather.openweathermap.Weather;

public interface IWeatherCacheRemote extends IRemoteCache<GeographicCoordinate, Weather>
{
  CachedValue<Weather> read(GeographicCoordinate key);

  void write(GeographicCoordinate key, CachedValue<Weather> value);
}

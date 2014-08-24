package net.seabears.funner.weather.commands;

import net.seabears.funner.weather.GeographicCoordinate;
import net.seabears.funner.weather.openweathermap.Weather;

public interface IWeatherReadCommand
{
  void setKey(GeographicCoordinate key);

  void setDefault(Weather value);

  boolean isValueValid();

  Weather getValue();

  boolean cacheValue();
}

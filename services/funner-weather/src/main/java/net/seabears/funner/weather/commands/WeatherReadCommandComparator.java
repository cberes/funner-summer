package net.seabears.funner.weather.commands;

import java.io.Serializable;
import java.util.Comparator;

public class WeatherReadCommandComparator implements Comparator<IWeatherReadCommand>, Serializable
{
  private static final long serialVersionUID = -1497990211381420593L;

  @Override
  public int compare(IWeatherReadCommand a, IWeatherReadCommand b)
  {
    return Integer.valueOf(a.getPriority()).compareTo(b.getPriority());
  }
}

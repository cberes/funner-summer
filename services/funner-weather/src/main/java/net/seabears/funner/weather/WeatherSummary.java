package net.seabears.funner.weather;

import java.io.Serializable;
import java.util.Date;

public class WeatherSummary implements Serializable
{
  private static final long serialVersionUID = 6905387227455544028L;

  private String condition;
  private double temperature;
  private GeographicCoordinate location;
  private Date time;

  public WeatherSummary()
  {}

  public WeatherSummary(String condition, double temperature, GeographicCoordinate location, Date time)
  {
    this.condition = condition;
    this.temperature = temperature;
    this.location = location;
    this.time = time;
  }

  public static double kelvinsToFahrenheit(double kelvins)
  {
    return (kelvins - 273.15) * 1.8 + 32.0;
  }

  public String getCondition()
  {
    return condition;
  }

  public void setCondition(String condition)
  {
    this.condition = condition;
  }

  public double getTemperature()
  {
    return temperature;
  }

  public void setTemperature(double temperature)
  {
    this.temperature = temperature;
  }

  public GeographicCoordinate getLocation()
  {
    return location;
  }

  public void setLocation(GeographicCoordinate location)
  {
    this.location = location;
  }

  public Date getTime()
  {
    return time;
  }

  public void setTime(Date time)
  {
    this.time = time;
  }
}

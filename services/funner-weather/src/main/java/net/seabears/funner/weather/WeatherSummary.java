package net.seabears.funner.weather;

import java.io.Serializable;

public class WeatherSummary implements Serializable
{
  private static final long serialVersionUID = 6905387227455544028L;

  private String condition;
  private double temperature;

  public WeatherSummary()
  {}

  public WeatherSummary(String condition, double temperature)
  {
    this.condition = condition;
    this.temperature = temperature;
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
}

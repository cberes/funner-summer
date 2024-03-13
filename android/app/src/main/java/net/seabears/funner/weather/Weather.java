package net.seabears.funner.weather;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Weather implements Serializable
{
  private static final long serialVersionUID = -7091862077507499417L;

  private String condition;
  private int temperature;
  private TemperatureUnit temperatureUnit;

  public Weather()
  {}

  public Weather(String condition, int temperature, TemperatureUnit temperatureUnit) {
    this.condition = condition;
    this.temperature = temperature;
    this.temperatureUnit = temperatureUnit;
  }

  public String getCondition()
  {
    return condition;
  }

  public void setCondition(String condition)
  {
    this.condition = condition;
  }

  public int getTemperatureAsF()
  {
    return temperatureUnit.toFahrenheit(temperature);
  }

  public int getTemperature()
  {
    return temperature;
  }

  public void setTemperature(int temperature)
  {
    this.temperature = temperature;
  }

  public TemperatureUnit getTemperatureUnit() {
    return temperatureUnit;
  }

  public void setTemperatureUnit(TemperatureUnit temperatureUnit) {
    this.temperatureUnit = temperatureUnit;
  }

  @NonNull
  @Override
  public String toString() {
    return "Weather{" +
            "condition='" + condition + '\'' +
            ", temperature=" + temperature +
            ", temperatureUnit=" + temperatureUnit +
            '}';
  }
}

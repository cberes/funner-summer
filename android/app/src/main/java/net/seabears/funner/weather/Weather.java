package net.seabears.funner.weather;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Weather implements Serializable
{
  private static final long serialVersionUID = -7091862077507499417L;

  private String condition;
  private int temperature;

  public Weather()
  {}

  public Weather(String condition, int temperature)
  {
    this.condition = condition;
    this.temperature = temperature;
  }

  public String getCondition()
  {
    return condition;
  }

  public void setCondition(String condition)
  {
    this.condition = condition;
  }

  public int getTemperature()
  {
    return temperature;
  }

  public void setTemperature(int temperature)
  {
    this.temperature = temperature;
  }

  @NonNull
  @Override
  public String toString()
  {
    return "Weather [" +
            "condition=" + condition +
            ", temperature=" + temperature +
            "]";
  }
}

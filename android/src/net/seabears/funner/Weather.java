package net.seabears.funner;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Weather implements Serializable
{
  private static final long serialVersionUID = -7091862077507499417L;

  private String condition;
  private double temperature;

  public Weather()
  {}

  public Weather(String condition, double temperature)
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

  public double getTemperature()
  {
    return temperature;
  }

  public void setTemperature(double temperature)
  {
    this.temperature = temperature;
  }
}

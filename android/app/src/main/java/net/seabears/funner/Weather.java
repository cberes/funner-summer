package net.seabears.funner;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Weather implements Serializable
{
  private static final long serialVersionUID = -7091862077507499417L;

  private String condition;
  private BigDecimal temperature;

  public Weather()
  {}

  public Weather(String condition, BigDecimal temperature)
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

  public BigDecimal getTemperature()
  {
    return temperature;
  }

  public void setTemperature(BigDecimal temperature)
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

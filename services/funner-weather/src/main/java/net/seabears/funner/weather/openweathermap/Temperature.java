package net.seabears.funner.weather.openweathermap;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Temperature implements Serializable
{
  private static final long serialVersionUID = -151507311354380453L;

  private double temp;
  private int pressure;
  private double temp_min;
  private double temp_max;
  private int humidity;

  public Temperature()
  {}

  public Temperature(double temp, int pressure, double temp_min, double temp_max, int humidity)
  {
    this.temp = temp;
    this.pressure = pressure;
    this.temp_min = temp_min;
    this.temp_max = temp_max;
    this.humidity = humidity;
  }

  public double getTemp()
  {
    return temp;
  }

  public void setTemp(double temp)
  {
    this.temp = temp;
  }

  public int getPressure()
  {
    return pressure;
  }

  public void setPressure(int pressure)
  {
    this.pressure = pressure;
  }

  public double getTemp_min()
  {
    return temp_min;
  }

  public void setTemp_min(double temp_min)
  {
    this.temp_min = temp_min;
  }

  public double getTemp_max()
  {
    return temp_max;
  }

  public void setTemp_max(double temp_max)
  {
    this.temp_max = temp_max;
  }

  public int getHumidity()
  {
    return humidity;
  }

  public void setHumidity(int humidity)
  {
    this.humidity = humidity;
  }
}

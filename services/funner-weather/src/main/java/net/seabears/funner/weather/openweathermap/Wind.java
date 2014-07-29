package net.seabears.funner.weather.openweathermap;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Wind implements Serializable
{
  private static final long serialVersionUID = -151507311354380453L;

  private double speed;
  private double deg;

  public Wind()
  {}

  public Wind(double speed, double deg)
  {
    this.speed = speed;
    this.deg = deg;
  }

  public double getSpeed()
  {
    return speed;
  }

  public void setSpeed(double speed)
  {
    this.speed = speed;
  }

  public double getDeg()
  {
    return deg;
  }

  public void setDeg(double deg)
  {
    this.deg = deg;
  }
}

package net.seabears.funner.weather.openweathermap;

import java.io.Serializable;

public class GeographicCoordinate implements Serializable
{
  private static final long serialVersionUID = -5618721060326725645L;

  private double lat;
  private double lon;

  public GeographicCoordinate()
  {}

  public GeographicCoordinate(double lat, double lon)
  {
    this.lat = lat;
    this.lon = lon;
  }

  public double getLat()
  {
    return lat;
  }

  public void setLat(double lat)
  {
    this.lat = lat;
  }

  public double getLon()
  {
    return lon;
  }

  public void setLon(double lon)
  {
    this.lon = lon;
  }
}

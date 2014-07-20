package net.seabears.funner.weather.openweathermap;

import java.io.Serializable;
import java.util.List;

public class Weather implements Serializable
{
  private static final long serialVersionUID = -2631058119788403403L;

  private GeographicCoordinate coord;
  private List<Condition> weather;
  private Temperature main;
  private long dt;

  public Weather()
  {}

  public Weather(GeographicCoordinate coord, List<Condition> weather, Temperature main, long dt)
  {
    this.coord = coord;
    this.weather = weather;
    this.main = main;
    this.dt = dt;
  }

  public GeographicCoordinate getCoord()
  {
    return coord;
  }

  public void setCoord(GeographicCoordinate coord)
  {
    this.coord = coord;
  }

  public List<Condition> getWeather()
  {
    return weather;
  }

  public void setWeather(List<Condition> weather)
  {
    this.weather = weather;
  }

  public Temperature getMain()
  {
    return main;
  }

  public void setMain(Temperature main)
  {
    this.main = main;
  }

  public long getDt()
  {
    return dt;
  }

  public void setDt(long dt)
  {
    this.dt = dt;
  }
}

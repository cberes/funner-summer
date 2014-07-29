package net.seabears.funner.weather.openweathermap;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Weather implements Serializable
{
  private static final long serialVersionUID = -2631058119788403403L;

  private GeographicCoordinate coord;
  private List<Condition> weather;
  private Temperature main;
  private Wind wind;
  private Map<String, Object> sys;
  private Map<String, Object> clouds;
  private long dt;
  private long id;
  private String name;
  private int cod;
  private String base;

  public Weather()
  {}

  public Weather(long dt, long id, String name, int cod, String base)
  {
    this.dt = dt;
    this.id = id;
    this.name = name;
    this.cod = cod;
    this.base = base;
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

  public Wind getWind()
  {
    return wind;
  }

  public void setWind(Wind wind)
  {
    this.wind = wind;
  }

  public Map<String, Object> getSys()
  {
    return sys;
  }

  public void setSys(Map<String, Object> sys)
  {
    this.sys = sys;
  }

  public Map<String, Object> getClouds()
  {
    return clouds;
  }

  public void setClouds(Map<String, Object> clouds)
  {
    this.clouds = clouds;
  }

  public long getDt()
  {
    return dt;
  }

  public void setDt(long dt)
  {
    this.dt = dt;
  }

  public long getId()
  {
    return id;
  }

  public void setId(long id)
  {
    this.id = id;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public int getCod()
  {
    return cod;
  }

  public void setCod(int cod)
  {
    this.cod = cod;
  }

  public String getBase()
  {
    return base;
  }

  public void setBase(String base)
  {
    this.base = base;
  }
}

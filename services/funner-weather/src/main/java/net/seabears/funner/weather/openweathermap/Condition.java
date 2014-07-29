package net.seabears.funner.weather.openweathermap;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Condition implements Serializable
{
  private static final long serialVersionUID = 9130581359441830789L;

  private int id;
  private String main;
  private String description;
  private String icon;

  public Condition()
  {}

  public Condition(int id, String main, String description, String icon)
  {
    this.id = id;
    this.main = main;
    this.description = description;
    this.icon = icon;
  }

  public int getId()
  {
    return id;
  }

  public void setId(int id)
  {
    this.id = id;
  }

  public String getMain()
  {
    return main;
  }

  public void setMain(String main)
  {
    this.main = main;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public String getIcon()
  {
    return icon;
  }

  public void setIcon(String icon)
  {
    this.icon = icon;
  }
}

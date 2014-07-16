package net.seabears.funner.summer.suggest;

import java.io.Serializable;

import android.os.Bundle;

public class PastimeActionArgs implements Serializable
{
  private static final long serialVersionUID = -1873078141883313938L;

  private static final String KEY_GROUP = "group";
  private static final String KEY_SINGLE = "single";
  private static final String KEY_TEMPERATURE = "temperature";
  private static final String KEY_WEATHER = "weather";

  private final boolean group;
  private final boolean single;
  private final int temperature;
  private final String weather;

  public PastimeActionArgs(boolean group, boolean single, int temperature, String weather)
  {
    this.group = group;
    this.single = single;
    this.temperature = temperature;
    this.weather = weather;
  }

  public static PastimeActionArgs fromBundle(Bundle bundle)
  {
    return new PastimeActionArgs(
        bundle.getBoolean(KEY_GROUP),
        bundle.getBoolean(KEY_SINGLE),
        bundle.getInt(KEY_TEMPERATURE),
        bundle.getString(KEY_WEATHER));
  }

  public Bundle toBundle()
  {
    Bundle bundle = new Bundle();
    bundle.putBoolean(KEY_GROUP, group);
    bundle.putBoolean(KEY_SINGLE, single);
    bundle.putInt(KEY_TEMPERATURE, temperature);
    bundle.putString(KEY_WEATHER, weather);
    return bundle;
  }

  public boolean isGroup()
  {
    return group;
  }

  public boolean isSingle()
  {
    return single;
  }

  public int getTemperature()
  {
    return temperature;
  }

  public String getWeather()
  {
    return weather;
  }
}

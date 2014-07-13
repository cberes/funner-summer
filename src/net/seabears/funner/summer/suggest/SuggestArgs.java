package net.seabears.funner.summer.suggest;

import android.os.Bundle;

public class SuggestArgs
{
  private static final String KEY_COUNT = "count";
  private static final String KEY_GROUP = "group";
  private static final String KEY_SINGLE = "single";
  private static final String KEY_TEMPERATURE = "temperature";
  private static final String KEY_WEATHER = "weather";

  private final int count;
  private final boolean group;
  private final boolean single;
  private final int temperature;
  private final String weather;

  public SuggestArgs(int count, boolean group, boolean single, int temperature, String weather)
  {
    this.count = count;
    this.group = group;
    this.single = single;
    this.temperature = temperature;
    this.weather = weather;
  }

  public static SuggestArgs fromBundle(Bundle bundle)
  {
    return new SuggestArgs(
        bundle.getInt(KEY_COUNT),
        bundle.getBoolean(KEY_GROUP),
        bundle.getBoolean(KEY_SINGLE),
        bundle.getInt(KEY_TEMPERATURE),
        bundle.getString(KEY_WEATHER));
  }

  public Bundle toBundle()
  {
    Bundle bundle = new Bundle();
    bundle.putInt(KEY_COUNT, count);
    bundle.putBoolean(KEY_GROUP, group);
    bundle.putBoolean(KEY_SINGLE, single);
    bundle.putInt(KEY_TEMPERATURE, temperature);
    bundle.putString(KEY_WEATHER, weather);
    return bundle;
  }

  public int getCount()
  {
    return count;
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

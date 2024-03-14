package net.seabears.funner.summer.suggest;

import java.io.Serializable;

import net.seabears.funner.db.Crowd;
import android.os.Bundle;

public class PastimeActionArgs implements Serializable
{
  private static final long serialVersionUID = -1873078141883313938L;

  private static final String KEY_CROWD = "crowd";
  public static final String KEY_TEMPERATURE = "temperature";
  public static final String KEY_WEATHER = "weather";

  private final Crowd crowd;
  private final int temperature;
  private final String weather;

  public PastimeActionArgs(Crowd crowd, int temperature, String weather)
  {
    this.crowd = crowd;
    this.temperature = temperature;
    this.weather = weather;
  }

  public static PastimeActionArgs fromBundle(Bundle bundle)
  {
    return new PastimeActionArgs(
        Crowd.fromString(bundle.getString(KEY_CROWD)),
        bundle.getInt(KEY_TEMPERATURE),
        bundle.getString(KEY_WEATHER));
  }

  public Bundle toBundle()
  {
    Bundle bundle = new Bundle();
    bundle.putString(KEY_CROWD, crowd.getCode());
    bundle.putInt(KEY_TEMPERATURE, temperature);
    bundle.putString(KEY_WEATHER, weather);
    return bundle;
  }

  public Crowd getCrowd()
  {
    return crowd;
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

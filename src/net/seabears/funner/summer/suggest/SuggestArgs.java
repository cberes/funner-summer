package net.seabears.funner.summer.suggest;

import android.os.Bundle;

public class SuggestArgs extends PastimeActionArgs
{
  private static final long serialVersionUID = -2973451813006973928L;

  private static final String KEY_COUNT = "count";

  private final int count;

  public SuggestArgs(int count, boolean group, boolean single, int temperature, String weather)
  {
    super(group, single, temperature, weather);
    this.count = count;
  }

  public static SuggestArgs fromBundle(Bundle bundle)
  {
    PastimeActionArgs base = PastimeActionArgs.fromBundle(bundle);
    return new SuggestArgs(
        bundle.getInt(KEY_COUNT),
        base.isGroup(),
        base.isSingle(),
        base.getTemperature(),
        base.getWeather());
  }

  @Override
  public Bundle toBundle()
  {
    Bundle bundle = super.toBundle();
    bundle.putInt(KEY_COUNT, count);
    return bundle;
  }

  public int getCount()
  {
    return count;
  }
}

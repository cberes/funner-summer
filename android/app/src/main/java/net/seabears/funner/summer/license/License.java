package net.seabears.funner.summer.license;

public class License
{
  private static License instance;

  private boolean adsEnabled;

  private License()
  {
    this.adsEnabled = true;
  }

  public static License getInstance()
  {
    if (instance == null)
    {
      instance = new License();
    }
    return instance;
  }

  public boolean isAdsEnabled()
  {
    return adsEnabled;
  }

  public void setAdsEnabled(boolean adsEnabled)
  {
    this.adsEnabled = adsEnabled;
  }
}

package net.seabears.funner.weather.commands;

import net.seabears.funner.weather.GeographicCoordinate;
import net.seabears.funner.weather.openweathermap.IWeatherClient;
import net.seabears.funner.weather.openweathermap.Weather;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;

public class ReadWeatherFromServiceCommand extends HystrixCommand<Weather> implements IWeatherReadCommand
{
  private static final int TIMEOUT_MS = 5000;

  private GeographicCoordinate key;
  private boolean valid;
  private Weather defaultValue;
  private IWeatherClient client;

  public ReadWeatherFromServiceCommand(IWeatherClient client)
  {
    super(Setter
        .withGroupKey(HystrixCommandGroupKey.Factory.asKey(ReadWeatherFromServiceCommand.class.getSimpleName()))
        .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
            .withExecutionIsolationThreadTimeoutInMilliseconds(TIMEOUT_MS)));
    this.client = client;
    this.valid = false;
  }

  @Override
  public void setDefault(Weather value)
  {
    this.defaultValue = value;
  }

  @Override
  public boolean isValueValid()
  {
    return valid;
  }

  public void setKey(GeographicCoordinate key)
  {
    this.key = key;
  }

  @Override
  protected Weather run()
  {
    final Weather value = client.getWeather(key.getLatitude(), key.getLongitude(), "imperial");
    valid = value != null;
    return value;
  }

  @Override
  protected Weather getFallback()
  {
    return defaultValue;
  }

  @Override
  public Weather getValue()
  {
    return execute();
  }

  @Override
  public boolean cacheValue()
  {
    return valid;
  }
}

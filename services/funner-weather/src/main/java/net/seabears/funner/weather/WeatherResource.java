package net.seabears.funner.weather;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import net.seabears.funner.cache.CachedValue;
import net.seabears.funner.cache.IWeatherCacheLocal;
import net.seabears.funner.cache.IWeatherCacheRemote;
import net.seabears.funner.weather.commands.IWeatherReadCommand;
import net.seabears.funner.weather.commands.WeatherReadCommandComparator;
import net.seabears.funner.weather.commands.WriteWeatherToRemoteCacheCommand;
import net.seabears.funner.weather.openweathermap.IWeatherClient;
import net.seabears.funner.weather.openweathermap.Weather;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Root resource (exposed at "api" path)
 */
@Path("/")
public class WeatherResource
{
  private static final Comparator<IWeatherReadCommand> SORT_BY_PRIORITY = new WeatherReadCommandComparator();

  @Autowired
  private IWeatherClient client;

  @Autowired
  private IWeatherCacheLocal localCache;

  @Autowired(required = false)
  private IWeatherCacheRemote remoteCache;

  private List<IWeatherReadCommand> weatherCommands;

  @Autowired
  private void setWeatherCommands(Collection<IWeatherReadCommand> commands)
  {
    this.weatherCommands = new ArrayList<IWeatherReadCommand>(commands);
    Collections.sort(this.weatherCommands, SORT_BY_PRIORITY);
  }

  /**
   * Method handling HTTP GET requests. The returned object will be sent to the
   * client as "text/plain" media type.
   * 
   * @return String that will be returned as a text/plain response.
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("all")
  public Weather getWeather(@QueryParam("lat") double latitude, @QueryParam("lng") double longitude)
  {
    final GeographicCoordinate location = new GeographicCoordinate(round(latitude), round(longitude));
    if (!location.isValid())
    {
      throw new IllegalArgumentException("coordinates are invalid");
    }

    Weather value = null;
    for (IWeatherReadCommand command : weatherCommands)
    {
      command.setKey(location);
      command.setDefault(value);
      value = command.getValue();
      if (command.isValueValid())
      {
        if (command.cacheValue())
        {
          localCache.write(location, value);
          if (remoteCache != null)
          {
            new WriteWeatherToRemoteCacheCommand(remoteCache, location,
                new CachedValue<Weather>(value, 1, TimeUnit.HOURS)).queue();
          }
        }
        break;
      }
    }
    return value;
  }

  /**
   * Round the value to the nearest of 0.00, 0.05, or 0.10.
   * 
   * @param d
   *          double to round
   * @return rounded value
   */
  private static double round(double d)
  {
    return Math.rint(d * 20.0) / 20.0;
  }

  /**
   * Method handling HTTP GET requests. The returned object will be sent to the
   * client as "text/plain" media type.
   * 
   * @return String that will be returned as a text/plain response.
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("summary")
  public WeatherSummary getWeatherSummary(@QueryParam("lat") double latitude, @QueryParam("lng") double longitude)
  {
    final Weather weather = getWeather(latitude, longitude);
    return new WeatherSummary(
        weather.getWeather().get(0).getMain(),
        weather.getMain().getTemp());
  }
}

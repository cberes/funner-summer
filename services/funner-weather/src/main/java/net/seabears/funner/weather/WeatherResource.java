package net.seabears.funner.weather;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import net.seabears.funner.CachedValue;
import net.seabears.funner.weather.openweathermap.Weather;

/**
 * Root resource (exposed at "api" path)
 */
@Path("api")
public class WeatherResource
{
  private static final String APPID = "TODO add OpenWeatherMap key";

  private static final Map<GeographicCoordinate, CachedValue<Weather>> WEATHER_CACHE = new ConcurrentHashMap<GeographicCoordinate, CachedValue<Weather>>();

  /**
   * Method handling HTTP GET requests. The returned object will be sent to the
   * client as "text/plain" media type.
   * 
   * @return String that will be returned as a text/plain response.
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("weather/summary")
  public WeatherSummary getWeatherSummary(@QueryParam("lat") double latitude, @QueryParam("lng") double longitude)
  {
    Weather weather = getWeather(latitude, longitude);
    WeatherSummary summary = new WeatherSummary(
        weather.getWeather().get(0).getMain(),
        weather.getMain().getTemp(),
        convert(weather.getCoord()),
        new Date(weather.getDt() * 1000)
        );
    return summary;
  }

  /**
   * Method handling HTTP GET requests. The returned object will be sent to the
   * client as "text/plain" media type.
   * 
   * @return String that will be returned as a text/plain response.
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("weather")
  public Weather getWeather(@QueryParam("lat") double latitude, @QueryParam("lng") double longitude)
  {
    // try to return cached value
    final GeographicCoordinate location = new GeographicCoordinate(round(latitude), round(longitude));
    final CachedValue<Weather> cached = WEATHER_CACHE.get(location);
    if (cached != null && cached.isAlive())
    {
      return cached.getValue();
    }

    // create client for external weather service
    WebTarget target = ClientBuilder.newClient()
        .target("http://api.openweathermap.org/data/2.5").path("weather")
        .queryParam("lat", latitude)
        .queryParam("lon", longitude)
        .queryParam("units", "imperial");

    // call external weather service
    Invocation.Builder builder = target.request();
    Weather weather = builder.header("x-api-key", APPID).get(Weather.class);

    // cache value and return
    WEATHER_CACHE.put(location, new CachedValue<Weather>(weather, 1, TimeUnit.HOURS));
    return weather;
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
   * Convert OpenWeatherMap geocode to this API's geocode.
   * 
   * @param other
   *          geocode to convert
   * @return converted geocode
   */
  private static GeographicCoordinate convert(net.seabears.funner.weather.openweathermap.GeographicCoordinate other)
  {
    return other == null ? null : new GeographicCoordinate(other.getLat(), other.getLon());
  }
}

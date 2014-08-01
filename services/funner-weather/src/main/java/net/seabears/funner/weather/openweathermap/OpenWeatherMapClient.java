package net.seabears.funner.weather.openweathermap;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;

import org.springframework.stereotype.Component;

@Component
public class OpenWeatherMapClient implements IWeatherClient
{
  private static final String APPID = "TODO add OpenWeatherMap key";

  @Override
  public Weather getWeather(double latitude, double longitude, String units)
  {
    // create client for external weather service
    WebTarget target = ClientBuilder.newClient()
        .target("http://api.openweathermap.org/data/2.5").path("weather")
        .queryParam("lat", latitude)
        .queryParam("lon", longitude)
        .queryParam("units", units);

    // call external weather service
    Invocation.Builder builder = target.request();
    return builder.header("x-api-key", APPID).get(Weather.class);
  }
}

package net.seabears.funner.weather;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;

public class WeatherApplication extends ResourceConfig
{
  /**
   * Register JAX-RS application components.
   */
  public WeatherApplication()
  {
    register(RequestContextFilter.class);
    register(WeatherResource.class);
    register(JacksonFeature.class);
  }
}

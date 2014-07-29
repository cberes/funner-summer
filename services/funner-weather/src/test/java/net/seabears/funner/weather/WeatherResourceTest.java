package net.seabears.funner.weather;

import static org.junit.Assert.assertNotNull;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class WeatherResourceTest
{
  private WebTarget target;

  @Before
  public void setUp() throws Exception
  {
    // create the client
    Client c = ClientBuilder.newClient();
    target = c.target("foo");
  }

  @After
  public void tearDown() throws Exception
  {}

  @Test
  public void testGetIt()
  {
    WeatherSummary weather = target.path("api").path("weather")
        .queryParam("lat", 42.9047)
        .queryParam("lng", -78.8494).request()
        .get(WeatherSummary.class);
    assertNotNull("Got it!", weather);
  }
}

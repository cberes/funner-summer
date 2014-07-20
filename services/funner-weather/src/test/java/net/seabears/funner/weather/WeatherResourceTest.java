package net.seabears.funner.weather;

import static org.junit.Assert.assertNotNull;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WeatherResourceTest
{

  private HttpServer server;
  private WebTarget target;

  @Before
  public void setUp() throws Exception
  {
    // start the server
    server = Main.startServer();
    // create the client
    Client c = ClientBuilder.newClient();

    target = c.target(Main.BASE_URI);
  }

  @After
  public void tearDown() throws Exception
  {
    server.shutdownNow();
  }

  /**
   * Test to see that the message "Got it!" is sent in the response.
   */
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

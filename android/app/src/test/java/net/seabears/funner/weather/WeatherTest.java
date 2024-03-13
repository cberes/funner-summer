package net.seabears.funner.weather;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class WeatherTest {
    @Test
    public void testGetTemperatureAsFWhenCelsius() {
        Weather weather = new Weather("clouds", 0, TemperatureUnit.CELSIUS);
        assertEquals(0, weather.getTemperature());
        assertEquals(32, weather.getTemperatureAsF());

        weather.setTemperature(100);
        assertEquals(100, weather.getTemperature());
        assertEquals(212, weather.getTemperatureAsF());
    }

    @Test
    public void testGetTemperatureAsFWhenFahrenheit() {
        Weather weather = new Weather("clouds", 0, TemperatureUnit.FAHRENHEIT);
        assertEquals(0, weather.getTemperature());
        assertEquals(0, weather.getTemperatureAsF());

        weather.setTemperature(100);
        assertEquals(100, weather.getTemperature());
        assertEquals(100, weather.getTemperatureAsF());
    }
}

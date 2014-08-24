package net.seabears.funner.weather;

import net.seabears.funner.cache.LocalGuavaWeatherCache;
import net.seabears.funner.weather.openweathermap.OpenWeatherMapClient;

import org.springframework.context.annotation.Import;

@org.springframework.context.annotation.Configuration
@Import({
    LocalGuavaWeatherCache.class,
    OpenWeatherMapClient.class
})
public class Configuration
{}

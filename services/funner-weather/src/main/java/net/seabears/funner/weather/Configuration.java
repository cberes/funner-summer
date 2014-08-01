package net.seabears.funner.weather;

import net.seabears.funner.cache.LocalGuavaWeatherCache;
import net.seabears.funner.cache.WeatherMapCache;
import net.seabears.funner.weather.commands.ReadWeatherFromLocalCacheCommand;
import net.seabears.funner.weather.commands.ReadWeatherFromRemoteCacheCommand;
import net.seabears.funner.weather.commands.ReadWeatherFromServiceCommand;
import net.seabears.funner.weather.openweathermap.OpenWeatherMapClient;

import org.springframework.context.annotation.Import;

@org.springframework.context.annotation.Configuration
@Import({
    WeatherMapCache.class,
    LocalGuavaWeatherCache.class,
    ReadWeatherFromLocalCacheCommand.class,
    ReadWeatherFromRemoteCacheCommand.class,
    ReadWeatherFromServiceCommand.class,
    OpenWeatherMapClient.class
})
public class Configuration
{}

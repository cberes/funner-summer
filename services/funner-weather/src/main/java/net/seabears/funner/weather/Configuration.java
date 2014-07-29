package net.seabears.funner.weather;

import net.seabears.funner.cache.WeatherMapCache;

import org.springframework.context.annotation.Import;

@org.springframework.context.annotation.Configuration
@Import({
    WeatherMapCache.class
})
public class Configuration
{}

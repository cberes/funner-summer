package net.seabears.funner.weather.openweathermap;

public interface IWeatherClient
{
  Weather getWeather(double latitude, double longitude, String units);
}

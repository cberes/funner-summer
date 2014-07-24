package net.seabears.funner.weather;

import net.seabears.funner.Weather;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;

public abstract class BlockOnWeatherSQLiteCursorLoader extends WeatherDependentSQLiteCursorLoader
{
  private final BlockingWeatherReceiver weatherReceiver;

  public BlockOnWeatherSQLiteCursorLoader(Context context, SQLiteOpenHelper db, String rawQuery, BlockingWeatherReceiver weatherReceiver)
  {
    super(context, db, rawQuery);
    this.weatherReceiver = weatherReceiver;
  }

  @Override
  protected Cursor buildCursor()
  {
    final Weather weather = getWeather();
    super.args = getArgs(weather);
    return super.buildCursor();
  }

  @Override
  protected Weather getWeather()
  {
    try
    {
      return weatherReceiver.get();
    } catch (InterruptedException e)
    {
      Thread.currentThread().interrupt();
      return null;
    }
  }

  protected abstract String[] getArgs(Weather weather);
}

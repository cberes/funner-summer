package net.seabears.funner.weather;

import net.seabears.funner.Weather;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;

import com.commonsware.cwac.loaderex.SQLiteCursorLoader;

public abstract class WeatherDependentSQLiteCursorLoader extends SQLiteCursorLoader
{
  public WeatherDependentSQLiteCursorLoader(Context context, SQLiteOpenHelper db, String rawQuery)
  {
    super(context, db, rawQuery, null);
  }

  @Override
  protected Cursor buildCursor()
  {
    final Weather weather = getWeather();
    super.args = getArgs(weather);
    return super.buildCursor();
  }

  protected abstract Weather getWeather();

  protected abstract String[] getArgs(Weather weather);
}

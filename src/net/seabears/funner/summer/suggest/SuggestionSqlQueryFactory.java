package net.seabears.funner.summer.suggest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.seabears.funner.summer.R;
import android.content.Context;
import android.util.Log;

public final class SuggestionSqlQueryFactory
{
  private SuggestionSqlQueryFactory()
  {
    throw new UnsupportedOperationException("cannot instantiate " + getClass());
  }

  public static String query(Context context)
  {
    InputStream is = context.getResources().openRawResource(R.raw.suggest);
    BufferedReader br = new BufferedReader(new InputStreamReader(is));
    StringBuilder sqlQueryBuilder = new StringBuilder();

    String readLine = null;
    try
    {
      while ((readLine = br.readLine()) != null)
      {
        sqlQueryBuilder.append(readLine).append(System.lineSeparator());
      }
    } catch (IOException e)
    {
      Log.e(SuggestionSqlQueryFactory.class.getSimpleName(), e.getMessage(), e);
    }

    return sqlQueryBuilder.toString();
  }

  public static String[] args(SuggestArgs args)
  {
    final String groupArg = args.isGroup() ? "1" : "0";
    final String soloArg = args.isSingle() ? "1" : "0";
    final String tempArg = String.valueOf(args.getTemperature());
    return new String[]
    {
        groupArg,
        groupArg,
        soloArg,
        soloArg,
        args.getWeather(),
        args.getWeather(),
        args.getWeather(),
        args.getWeather(),
        tempArg,
        tempArg,
        tempArg,
        tempArg,
        String.valueOf(args.getCount())
    };
  }
}

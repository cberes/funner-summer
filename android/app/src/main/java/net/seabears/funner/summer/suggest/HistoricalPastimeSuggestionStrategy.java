package net.seabears.funner.summer.suggest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.seabears.funner.summer.R;
import android.content.Context;
import android.util.Log;

public class HistoricalPastimeSuggestionStrategy implements PastimeSuggestionStrategy
{
  @Override
  public String getQuery(Context context)
  {
    InputStream is = context.getResources().openRawResource(R.raw.suggest);
    BufferedReader br = new BufferedReader(new InputStreamReader(is));
    StringBuilder sqlQueryBuilder = new StringBuilder();

    String readLine = null;
    try
    {
      while ((readLine = br.readLine()) != null)
      {
        sqlQueryBuilder.append(readLine).append(System.getProperty("line.separator"));
      }
    } catch (IOException e)
    {
      Log.e(HistoricalPastimeSuggestionStrategy.class.getSimpleName(), e.getMessage(), e);
    }

    return sqlQueryBuilder.toString();
  }

  @Override
  public String[] getArguments(SuggestArgs args)
  {
    final String crowdArg = args.getCrowd().getCode();
    final String tempArg = String.valueOf(args.getTemperature());
    return new String[]
    {
        crowdArg,
        crowdArg,
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

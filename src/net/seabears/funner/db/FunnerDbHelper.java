package net.seabears.funner.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import net.seabears.funner.summer.R;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class FunnerDbHelper extends SQLiteOpenHelper
{
  // If you change the database schema, you must increment the database version.
  public static final int DATABASE_VERSION = 1;
  public static final String DATABASE_NAME = "Funner.db";
  public final List<String> sqlQueries;

  public FunnerDbHelper(Context context)
  {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);

    sqlQueries = new LinkedList<String>();
    InputStream is = context.getResources().openRawResource(R.raw.db);
    BufferedReader br = new BufferedReader(new InputStreamReader(is));
    StringBuilder sqlQueryBuilder = new StringBuilder();

    String readLine = null;
    try
    {
      while ((readLine = br.readLine()) != null)
      {
        if (readLine.trim().isEmpty())
        {
          addSqlQueryString(sqlQueryBuilder.toString());
          sqlQueryBuilder = new StringBuilder();
        }
        else
        {
          sqlQueryBuilder.append(readLine).append(System.lineSeparator());
        }
      }
      addSqlQueryString(sqlQueryBuilder.toString());
    } catch (IOException e)
    {
      Log.e(getClass().getSimpleName(), e.getMessage(), e);
    }
  }

  private void addSqlQueryString(String sqlQuery)
  {
    if (!sqlQuery.isEmpty())
    {
      sqlQueries.add(sqlQuery);
    }
  }

  public void onCreate(SQLiteDatabase db)
  {
    for (String sqlQuery : sqlQueries)
    {
      db.execSQL(sqlQuery);
    }
  }

  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
  {
    // nothing
  }

  public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
  {
    onUpgrade(db, oldVersion, newVersion);
  }

}

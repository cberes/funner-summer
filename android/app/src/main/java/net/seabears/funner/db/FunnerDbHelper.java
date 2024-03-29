package net.seabears.funner.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
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
  private final List<String> sqlQueries;
  private final Context context;

  public FunnerDbHelper(Context context)
  {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
    this.context = context;

    sqlQueries = new LinkedList<>();
    InputStream is = context.getResources().openRawResource(R.raw.db);
    BufferedReader br = new BufferedReader(new InputStreamReader(is));
    StringBuilder sqlQueryBuilder = new StringBuilder();

    String readLine;
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
          sqlQueryBuilder.append(readLine).append(System.getProperty("line.separator"));
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

  @Override
  public void onCreate(SQLiteDatabase db)
  {
    Log.i(getClass().getSimpleName(), "Creating databases....");
    for (String sqlQuery : sqlQueries)
    {
      db.execSQL(sqlQuery);
    }
    Log.i(getClass().getSimpleName(), "Finished creating databases.");

    try
    {
      Log.i(getClass().getSimpleName(), "Importing data....");
      new CsvDataImporter(context, db, R.raw.data).importData();
      Log.i(getClass().getSimpleName(), "Finished importing data.");
    } catch (IOException | ParseException e)
    {
      Log.e(getClass().getSimpleName(), e.getMessage(), e);
    }
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
  {
    // nothing
  }

  @Override
  public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
  {
    onUpgrade(db, oldVersion, newVersion);
  }

}

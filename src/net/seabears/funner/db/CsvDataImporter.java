package net.seabears.funner.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class CsvDataImporter
{
  private Context context;

  private SQLiteDatabase db;

  private int resourceId;

  public CsvDataImporter(Context context, SQLiteDatabase db, int resourceId)
  {
    this.context = context;
    this.db = db;
    this.resourceId = resourceId;
  }

  private static enum DataColumn
  {
    NAME(0),
    ACTION(1),
    ALIASES(2),
    TIMES(3),
    SINGLE(4),
    COUPLE(5),
    GROUP(6),
    TEMPERATURES(7),
    WEATHER(8);

    private final int index;

    private DataColumn(int index)
    {
      this.index = index;
    }

    public int index()
    {
      return index;
    }
  }

  private static Map<DataColumn, String> parseRow(final String row, final int offset)
  {
    final String[] columns = row.split(",");
    for (int i = 0; i < columns.length; ++i)
    {
      columns[i] = columns[i].trim();
    }

    final Map<DataColumn, String> map = new HashMap<DataColumn, String>(DataColumn.values().length);
    for (DataColumn column : DataColumn.values())
    {
      map.put(column, columns[column.index() + offset]);
    }
    return map;
  }

  public void importData() throws IOException
  {
    final int offset = 0;
    InputStream is = context.getResources().openRawResource(resourceId);
    BufferedReader br = new BufferedReader(new InputStreamReader(is));

    String readLine = null;
    while ((readLine = br.readLine()) != null)
    {
      if (readLine.trim().isEmpty())
      {
        continue;
      }

      Map<DataColumn, String> rows = parseRow(readLine, offset);
    }
  }
}

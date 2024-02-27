package net.seabears.funner.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.google.common.base.Preconditions;

public class CsvDataImporter
{
  private static final boolean HEADER_ROW = true;
  private final Date day = new Date();
  private final Context context;
  private final SQLiteDatabase db;
  private final int resourceId;

  public CsvDataImporter(Context context, SQLiteDatabase db, int resourceId)
  {
    this.context = context;
    this.db = db;
    this.resourceId = resourceId;
  }

  private enum DataColumn
  {
    NAME(0),
    ACTION(1),
    ALIASES(2)
    {
      @Override
      public Object parse(String s)
      {
        return s.isEmpty() ? new String[0] : s.split("\\s*;+\\s*");
      }
    },
    TIMES(3)
    {
      @Override
      public Object parse(String s)
      {
        return s.isEmpty() ? new String[0] : s.split("\\s+");
      }
    },
    SINGLE(4)
    {
      @Override
      public Object parse(String s)
      {
        return Integer.parseInt(s) != 0;
      }
    },
    COUPLE(5)
    {
      @Override
      public Object parse(String s)
      {
        return Integer.parseInt(s) != 0;
      }
    },
    GROUP(6)
    {
      @Override
      public Object parse(String s)
      {
        return Integer.parseInt(s) != 0;
      }
    },
    TEMPERATURES(7)
    {
      @Override
      public Object parse(String s)
      {
        return s.isEmpty() ? new String[0] : s.split("\\s+");
      }
    },
    WEATHER(8)
    {
      @Override
      public Object parse(String s)
      {
        return s.isEmpty() ? new String[0] : s.split("\\s+");
      }
    };

    private final int index;

    private DataColumn(int index)
    {
      this.index = index;
    }

    public int index()
    {
      return index;
    }

    public Object parse(String s)
    {
      return s;
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

  private interface RawDataConverter<T>
  {
    T convert(String s) throws ParseException;
  }

  private static <T> List<T> convert(String[] raw, RawDataConverter<T> converter) throws ParseException
  {
    List<T> converted = new ArrayList<T>(raw.length);
    for (String s : raw)
    {
      converted.add(converter.convert(s));
    }
    return converted;
  }

  public void importData() throws IOException, ParseException
  {
    final int offset = 0;
    InputStream is = context.getResources().openRawResource(resourceId);
    BufferedReader br = new BufferedReader(new InputStreamReader(is));

    String readLine = null;
    boolean skippedHeaderRow = false;
    while ((readLine = br.readLine()) != null)
    {
      if (readLine.trim().isEmpty())
      {
        continue;
      }

      if (HEADER_ROW && !skippedHeaderRow)
      {
        skippedHeaderRow = true;
        continue;
      }

      Map<DataColumn, String> columns = parseRow(readLine, offset);
      Map<DataColumn, Object> data = new HashMap<DataColumn, Object>(columns.size());
      for (Map.Entry<DataColumn, String> column : columns.entrySet())
      {
        data.put(column.getKey(), column.getKey().parse(column.getValue()));
      }
      insertData(data);
    }
  }

  @SuppressLint("SimpleDateFormat")
  private void insertData(Map<DataColumn, Object> data) throws ParseException
  {
    // get each datum
    String name = (String) data.get(DataColumn.NAME);
    String action = (String) data.get(DataColumn.ACTION);
    boolean single = (Boolean) data.get(DataColumn.SINGLE);
    boolean couple = (Boolean) data.get(DataColumn.COUPLE);
    boolean group = (Boolean) data.get(DataColumn.GROUP);
    final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    List<Date> times = convert((String[]) data.get(DataColumn.TIMES), new RawDataConverter<Date>()
    {
      @Override
      public Date convert(String s) throws ParseException
      {
        return timeFormat.parse(s);
      }
    });
    List<Integer> temps = convert((String[]) data.get(DataColumn.TEMPERATURES), new RawDataConverter<Integer>()
    {
      @Override
      public Integer convert(String s)
      {
        return Integer.parseInt(s);
      }
    });
    List<String> weather = Arrays.asList((String[]) data.get(DataColumn.WEATHER));
    List<String> aliases = Arrays.asList((String[]) data.get(DataColumn.ALIASES));

    // validate data
    Preconditions.checkState(!name.isEmpty());
    Preconditions.checkState(!action.isEmpty());
    Preconditions.checkState(!times.isEmpty());
    Preconditions.checkState(!temps.isEmpty());
    Preconditions.checkState(!weather.isEmpty());

    // insert pastime
    final ContentValues values = new ContentValues();
    values.put("name", name);
    values.put("action_name", action);
    values.put("custom", 0);
    long pastimeId = insert("pastime", values);

    // insert aliases
    for (String alias : aliases)
    {
      values.clear();
      values.put("name", alias);
      values.put("pastime_id", pastimeId);
      insert("pastime_alias", values);
    }

    // insert action rows
    final SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    long actionId = 0;
    for (Date time : times)
    {
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
      calendar.setTime(day);
      calendar.clear(Calendar.HOUR);
      calendar.clear(Calendar.MINUTE);
      calendar.clear(Calendar.SECOND);
      calendar.clear(Calendar.MILLISECOND);
      calendar.setTime(new Date(calendar.getTime().getTime() + time.getTime()));

      values.clear();
      values.put("performed", datetimeFormat.format(calendar.getTime()));
      values.put("pastime_id", pastimeId);
      values.put("method_id", SelectionMethod.BALLAST.getId());
      actionId = insert("action", values);
    }

    // insert temperatures
    for (int temp : temps)
    {
      values.clear();
      values.put("stat_id", Statistic.TEMPERATURE.getId());
      values.put("action_id", actionId);
      values.put("value_integer", temp);
      insert("measurement", values);
    }

    // insert weather conditions
    for (String condition : weather)
    {
      values.clear();
      values.put("stat_id", Statistic.WEATHER.getId());
      values.put("action_id", actionId);
      values.put("value_text", condition);
      insert("measurement", values);
    }

    // insert crowd conditions
    values.clear();
    values.put("stat_id", Statistic.CROWD.getId());
    values.put("action_id", actionId);
    if (single)
    {
      values.put("value_text", Crowd.SINGLE.getCode());
      insert("measurement", values);
    }
    if (couple)
    {
      values.put("value_text", Crowd.COUPLE.getCode());
      insert("measurement", values);
    }
    if (group)
    {
      values.put("value_text", Crowd.GROUP.getCode());
      insert("measurement", values);
    }
  }

  private long insert(String table, ContentValues values)
  {
    return db.insert(table, null, values);
  }
}

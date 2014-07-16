package net.seabears.funner.db;

import net.seabears.funner.summer.suggest.PastimeActionArgs;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class ActionInsertTask
{
  private final SQLiteDatabase db;
  private final long pastimeId;
  private final long selectionMethodId;
  private PastimeActionArgs pastimeArgs;

  public ActionInsertTask(SQLiteDatabase db, long pastimeId, long selectionMethodId)
  {
    this(db, pastimeId, selectionMethodId, null);
  }

  public ActionInsertTask(SQLiteDatabase db, long pastimeId, long selectionMethodId, PastimeActionArgs pastimeArgs)
  {
    this.db = db;
    this.pastimeId = pastimeId;
    this.selectionMethodId = selectionMethodId;
    this.pastimeArgs = pastimeArgs;
  }

  public long getPastimeId()
  {
    return pastimeId;
  }

  public long getSelectionMethodId()
  {
    return selectionMethodId;
  }

  public PastimeActionArgs getPastimeArgs()
  {
    return pastimeArgs;
  }

  public void setPastimeArgs(PastimeActionArgs pastimeArgs)
  {
    this.pastimeArgs = pastimeArgs;
  }

  public void insert()
  {
    // add action row
    ContentValues values = new ContentValues();
    values.put("pastime_id", pastimeId);
    values.put("method_id", selectionMethodId);
    long id = db.insert("action", null, values);

    // add group measurement
    values.clear();
    values.put("action_id", id);
    values.put("stat_id", Statistic.GROUP.getId());
    values.put("value_integer", pastimeArgs.isGroup() ? 1 : 0);
    db.insert("measurement", null, values);

    // add single measurement
    values.clear();
    values.put("action_id", id);
    values.put("stat_id", Statistic.SINGLE.getId());
    values.put("value_integer", pastimeArgs.isSingle() ? 1 : 0);
    db.insert("measurement", null, values);

    // add temperature measurement
    values.clear();
    values.put("action_id", id);
    values.put("stat_id", Statistic.TEMPERATURE.getId());
    values.put("value_integer", 70);
    db.insert("measurement", null, values);

    // add weather measurement
    values.clear();
    values.put("action_id", id);
    values.put("stat_id", Statistic.WEATHER.getId());
    values.put("value_text", "rainy");
    db.insert("measurement", null, values);
  }
}

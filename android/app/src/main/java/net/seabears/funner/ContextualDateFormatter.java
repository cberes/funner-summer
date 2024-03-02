package net.seabears.funner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import net.seabears.funner.summer.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateFormat;

public class ContextualDateFormatter
{
  @SuppressLint("SimpleDateFormat")
  private final SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private final java.text.DateFormat dateFormatThisYear;
  private final java.text.DateFormat dateFormat;
  private final java.text.DateFormat timeFormat;
  private final String dateTimeSep;
  private final String yesterdayStr;
  private final Calendar today;
  private final Calendar yesterday;
  private final Calendar first;

  public ContextualDateFormatter(Context context) {
    this(DateFormat.getDateFormat(context), DateFormat.getTimeFormat(context),
            ' ' + context.getResources().getString(R.string.at) + ' ',
            context.getResources().getString(R.string.yesterday));
  }

  public ContextualDateFormatter(java.text.DateFormat dateFormat, java.text.DateFormat timeFormat,
                                 String dateTimeSep, String yesterdayStr) {
    this.dateFormat = dateFormat;
    this.timeFormat = timeFormat;
    dateFormatThisYear = new SimpleDateFormat("MMMM d");
    this.dateTimeSep = dateTimeSep;
    this.yesterdayStr = yesterdayStr;

    today = Calendar.getInstance();
    today.set(Calendar.HOUR, 0);
    today.set(Calendar.MINUTE, 0);
    today.set(Calendar.SECOND, 0);
    today.set(Calendar.MILLISECOND, 0);

    yesterday = Calendar.getInstance();
    yesterday.setTime(today.getTime());
    yesterday.roll(Calendar.DATE, false);

    first = Calendar.getInstance();
    first.setTime(today.getTime());
    first.set(Calendar.MONTH, Calendar.JANUARY);
    first.set(Calendar.DATE, 1);
  }

  public String format(String sqlDateTime) throws ParseException
  {
    return format(inputFormat.parse(sqlDateTime));
  }

  public String format(Date dateTime)
  {
    final Calendar then = Calendar.getInstance();
    then.setTime(dateTime);

    if (!then.before(today))
    {
      return timeFormat.format(dateTime);
    }
    else if (!then.before(yesterday))
    {
      return yesterdayStr + dateTimeSep + timeFormat.format(dateTime);
    }
    else if (!then.before(first))
    {
      return dateFormatThisYear.format(dateTime) + dateTimeSep + timeFormat.format(dateTime);
    }
    else
    {
      return dateFormat.format(dateTime) + dateTimeSep + timeFormat.format(dateTime);
    }
  }
}

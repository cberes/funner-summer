package net.seabears.funner;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeFalse;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;

@RunWith(MockitoJUnitRunner.class)
public class ContextualDateFormatterTest {
    @Mock(answer = RETURNS_DEEP_STUBS)
    private Context context;

    private ContextualDateFormatter dateFormatter;

    @Before
    public void setup() {
        context.getResources().getConfiguration().locale = Locale.US;
        dateFormatter = new ContextualDateFormatter(
                new SimpleDateFormat("MM/dd/yyyy", Locale.US),
                new SimpleDateFormat("HH:mm:ss", Locale.US),
                " at ", "yesterday");
    }

    @Test
    public void testToday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 34);
        cal.set(Calendar.SECOND, 56);
        assertEquals("12:34:56", format(cal));
    }

    @Test
    public void testFormatSqlTimeToday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 34);
        cal.set(Calendar.SECOND, 56);
        assertEquals("12:34:56", formatSql(cal));
    }

    @Test
    public void testYesterday() {
        assumeFalse("this test can't run on the first of the month", isFirstOfMonth());

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 34);
        cal.set(Calendar.SECOND, 56);
        assertEquals("yesterday at 12:34:56", format(cal));
    }

    @Test
    public void testFormatSqlTimeYesterday() {
        assumeFalse("this test can't run on the first of the month", isFirstOfMonth());

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 34);
        cal.set(Calendar.SECOND, 56);
        assertEquals("yesterday at 12:34:56", formatSql(cal));
    }

    @Test
    public void testFormatThisYear() {
        assumeFalse("this test can't run on January 1 or 2", isJanuary1or2());

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        cal.set(year, 0, 1, 12, 34, 56);
        assertEquals("January 1 at 12:34:56", format(cal));
    }

    @Test
    public void testFormatSqlTimeThisYear() {
        assumeFalse("this test can't run on January 1 or 2", isJanuary1or2());

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        cal.set(year, 0, 1, 12, 34, 56);
        assertEquals("January 1 at 12:34:56", formatSql(cal));
    }

    @Test
    public void testFormatLastYearOrEarlier() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        cal.set(year - 1, 11, 1, 12, 34, 56);
        assertEquals("12/01/2023 at 12:34:56", format(cal));
    }

    @Test
    public void testFormatSqlTimeLastYearOrEarlier() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        cal.set(year - 1, 11, 1, 12, 34, 56);
        assertEquals("12/01/2023 at 12:34:56", formatSql(cal));
    }

    private String format(Calendar cal) {
        return dateFormatter.format(cal.getTime());
    }

    private String formatSql(Calendar cal) {
        return dateFormatter.format(sql(cal.getTime()));
    }

    private static java.sql.Date sql(Date d) {
        return new java.sql.Date(d.getTime());
    }

    private static boolean isFirstOfMonth() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.DATE) == 1;
    }

    private static boolean isJanuary1or2() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.DAY_OF_YEAR) <= 2;
    }
}

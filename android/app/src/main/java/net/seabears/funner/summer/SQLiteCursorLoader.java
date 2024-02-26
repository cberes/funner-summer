package net.seabears.funner.summer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.NonNull;
import androidx.loader.content.CursorLoader;

public class SQLiteCursorLoader extends CursorLoader {
    private final SQLiteOpenHelper db;
    private final String query;
    private final String[] args;

    public SQLiteCursorLoader(@NonNull final Context context,
                              @NonNull final SQLiteOpenHelper db,
                              @NonNull final String query,
                              @NonNull final String[] args) {
        super(context);
        this.db = db;
        this.query = query;
        this.args = args;
    }

    @Override
    public Cursor loadInBackground()
    {
        return db.getReadableDatabase().rawQuery(query, args);
    }
}

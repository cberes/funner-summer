package net.seabears.funner.summer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import net.seabears.funner.db.FunnerDbHelper;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import androidx.fragment.app.FragmentActivity;

public class PastimeEditor extends FragmentActivity
{
  public static final String ARG_PARENT = "parent";

  public static final String ARG_PASTIME_ID = "pastime_id";

  private static final Set<Class<?>> PARENTS;

  static
  {
    PARENTS = new HashSet<Class<?>>(Arrays.<Class<?>> asList(Pastime.class, Pastimes.class, Ideas.class, RandomPastimes.class));
  }

  private long id;

  private Class<?> parent;

  private FunnerDbHelper dbHelper;

  private Button button;
  private EditText textAction;
  private EditText textName;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_pastime_edit);

    // Show the Up button in the action bar.
    getActionBar().setDisplayHomeAsUpEnabled(true);

    // get arguments from intent
    readArguments();

    // get pastime from database
    dbHelper = new FunnerDbHelper(this);
    Cursor cursor = dbHelper.getReadableDatabase()
        .query("pastime", new String[] { "_id", "name", "action_name" },
            "_id = ? and custom = 1", new String[] { String.valueOf(id) },
            null, null, null);

    button = (Button) findViewById(R.id.button_pastime_save);
    textAction = (EditText) findViewById(R.id.text_pastime_prompt);
    textName = (EditText) findViewById(R.id.text_pastime_name);

    if (cursor.getCount() > 0)
    {
      setTitle(getText(R.string.action_pastime_edit));
      button.setText(R.string.pastime_update);

      cursor.moveToFirst();
      textAction.setText(cursor.getString(cursor.getColumnIndexOrThrow("action_name")));
      textName.setText(cursor.getString(cursor.getColumnIndexOrThrow("name")));
    }
    else
    {
      // be safe, reset id to default
      id = 0;
    }

    textName.setOnEditorActionListener(new OnEditorActionListener()
    {
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
      {
        if (actionId == EditorInfo.IME_ACTION_DONE)
        {
          submit();
          return true;
        }
        return false;
      }
    });

    // ok button
    button.setOnClickListener(new OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        submit();
      }
    });
  }

  private void readArguments()
  {
    Intent intent = getIntent();
    id = intent.getLongExtra(ARG_PASTIME_ID, 1);
    parent = (Class<?>) intent.getSerializableExtra(ARG_PARENT);

    if (!PARENTS.contains(parent))
    {
      throw new IllegalStateException(getClass() + " " + ARG_PARENT + " intent extra " + parent + " is invalid.");
    }
  }

  private void submit()
  {
    LinkedList<Integer> errorResourceIds = new LinkedList<Integer>();
    final String name = textName.getText().toString().trim();
    final String action = textAction.getText().toString().trim();

    // check for duplicate name
    if (dbHelper.getReadableDatabase()
        .query("pastime", new String[] { "_id" },
            "name = ? and _id <> ?", new String[] { name, String.valueOf(id) },
            null, null, null).getCount() > 0)
    {
      errorResourceIds.add(R.string.pastime_edit_error_duplicate_name);
    }

    // check for duplicate action name
    if (dbHelper.getReadableDatabase()
        .query("pastime", new String[] { "_id" },
            "action_name = ? and _id <> ? collate nocase", new String[] { action, String.valueOf(id) },
            null, null, null).getCount() > 0)
    {
      errorResourceIds.add(R.string.pastime_edit_error_duplicate_action);
    }

    // validate name
    if (name.isEmpty())
    {
      errorResourceIds.add(R.string.pastime_edit_error_empty_name);
    }

    // validate action
    if (action.isEmpty())
    {
      errorResourceIds.add(R.string.pastime_edit_error_empty_action);
    }

    if (!errorResourceIds.isEmpty())
    {
      errorResourceIds.addFirst(R.string.pastime_edit_error);
      ErrorReasonsDialogFragment fragment = new ErrorReasonsDialogFragment();
      Bundle bundle = new Bundle();
      bundle.putIntegerArrayList("resourceIds", new ArrayList<>(errorResourceIds));
      fragment.setArguments(bundle);
      fragment.show(getSupportFragmentManager(), null);
      return;
    }

    // insert pastime
    final SQLiteDatabase db = dbHelper.getWritableDatabase();
    final String table = "pastime";
    final ContentValues values = new ContentValues();
    values.put("name", name);
    values.put("action_name", action);
    if (id == 0)
    {
      id = db.insert(table, null, values);
    }
    else
    {
      db.update(table, values, "_id = ?", new String[] { String.valueOf(id) });
    }

    // go to pastime activity
    Intent intent = new Intent(PastimeEditor.this, Pastime.class);
    intent.putExtra(Pastime.ARG_PASTIME_ID, id);
    intent.putExtra(Pastime.ARG_PARENT, PastimeEditor.class);
    startActivity(intent);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    final int id = item.getItemId();
    if (id == android.R.id.home)
    {
      Intent intent = new Intent(this, parent);
      if (Pastime.class.equals(parent))
      {
        intent.putExtra(Pastime.ARG_PASTIME_ID, this.id);
        intent.putExtra(Pastime.ARG_PARENT, PastimeEditor.class);
      }
      navigateUpTo(intent);
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}

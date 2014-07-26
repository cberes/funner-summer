package net.seabears.funner.summer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import net.seabears.funner.db.FunnerDbHelper;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
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

public class PastimeEditor extends Activity
{
  public static final String ARG_PARENT = "parent";

  public static final String ARG_PASTIME_ID = "pastime_id";

  private static final Set<Class<?>> PARENTS;

  static
  {
    PARENTS = new HashSet<Class<?>>(Arrays.asList(Pastime.class, Pastimes.class, Ideas.class, RandomPastimes.class));
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
      textAction.setText(cursor.getString(cursor.getColumnIndex("action_name")));
      textName.setText(cursor.getString(cursor.getColumnIndex("name")));
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
      new ErrorReasonsDialogFragment(errorResourceIds).show(getFragmentManager(), null);
      return;
    }

    // insert pastime
    final ContentValues values = new ContentValues();
    values.put("name", name);
    values.put("action_name", action);
    id = dbHelper.getWritableDatabase().insert("pastime", null, values);

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
      navigateUpTo(new Intent(this, parent));
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}

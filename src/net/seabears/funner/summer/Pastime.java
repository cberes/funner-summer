package net.seabears.funner.summer;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Pastime extends Activity
{
  private List<String> settings;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_pastime_detail);

    // Show the Up button in the action bar.
    getActionBar().setDisplayHomeAsUpEnabled(true);

    // pastime details
    setTitle("Walking");
    TextView actionView = (TextView) findViewById(R.id.pastime_action);
    actionView.setText("Enjoy a walk");

    // settings
    settings = Arrays.asList(getResources().getText(R.string.pastime_include).toString());

    ListView settingsView = (ListView) findViewById(R.id.pastime_settings);
    settingsView.setAdapter(new ArrayAdapter<String>(this,
        android.R.layout.simple_list_item_multiple_choice,
        android.R.id.text1,
        settings));
    settingsView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    settingsView.setOnItemClickListener(new OnItemClickListener()
    {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id)
      {
        Log.d(Pastime.this.getClass().getSimpleName(), "selected item " + position);
        CheckedTextView item = (CheckedTextView) view;
        Toast.makeText(Pastime.this,
            item.isChecked() ? "checked" : "not checked",
            Toast.LENGTH_SHORT).show();
      }
    });
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    final int id = item.getItemId();
    if (id == android.R.id.home)
    {
      // This ID represents the Home or Up button. In the case of this
      // activity, the Up button is shown. For
      // more details, see the Navigation pattern on Android Design:
      //
      // http://developer.android.com/design/patterns/navigation.html#up-vs-back
      //
      navigateUpTo(new Intent(this, Ideas.class));
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}

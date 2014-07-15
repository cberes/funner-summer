package net.seabears.funner.summer;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

public abstract class FragmentHostActivity extends Activity
{
  private Fragment fragment;

  public FragmentHostActivity(Fragment fragment)
  {
    this.fragment = fragment;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_fragment_host);

    // Show the Up button in the action bar.
    getActionBar().setDisplayHomeAsUpEnabled(true);

    // savedInstanceState is non-null when there is fragment state
    // saved from previous configurations of this activity
    // (e.g. when rotating the screen from portrait to landscape).
    // In this case, the fragment will automatically be re-added
    // to its container so we don't need to manually add it.
    // For more information, see the Fragments API guide at:
    //
    // http://developer.android.com/guide/components/fragments.html
    //
    if (savedInstanceState == null)
    {
      getFragmentManager().beginTransaction()
          .add(R.id.fragment, fragment)
          .commit();
    }
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

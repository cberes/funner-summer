package net.seabears.funner.summer;

import java.util.Locale;

import net.seabears.funner.db.Crowd;
import net.seabears.funner.summer.suggest.SuggestArgs;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

public class Ideas extends Activity implements ActionBar.TabListener
{
  /**
   * The {@link android.support.v4.view.PagerAdapter} that will provide
   * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
   * derivative, which will keep every loaded fragment in memory. If this
   * becomes too memory intensive, it may be best to switch to a
   * {@link android.support.v13.app.FragmentStatePagerAdapter}.
   */
  private SectionsPagerAdapter mSectionsPagerAdapter;

  /**
   * The {@link ViewPager} that will host the section contents.
   */
  private ViewPager mViewPager;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_ideas);

    // Set up the action bar.
    final ActionBar actionBar = getActionBar();
    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

    // Create the adapter that will return a fragment for each of the three
    // primary sections of the activity.
    mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

    // Set up the ViewPager with the sections adapter.
    mViewPager = (ViewPager) findViewById(R.id.pager);
    mViewPager.setAdapter(mSectionsPagerAdapter);

    // When swiping between different sections, select the corresponding
    // tab. We can also use ActionBar.Tab#select() to do this if we have
    // a reference to the Tab.
    mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
    {
      @Override
      public void onPageSelected(int position)
      {
        actionBar.setSelectedNavigationItem(position);
      }
    });

    // For each of the sections in the app, add a tab to the action bar.
    for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++)
    {
      // Create a tab with text corresponding to the page title defined by
      // the adapter. Also specify this Activity object, which implements
      // the TabListener interface, as the callback (listener) for when
      // this tab is selected.
      actionBar.addTab(
          actionBar.newTab()
              .setText(mSectionsPagerAdapter.getPageTitle(i))
              .setTabListener(this));
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.ideas, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    if (id == R.id.action_history)
    {
      startActivity(new Intent(this, History.class));
      return true;
    }
    else if (id == R.id.action_pastimes)
    {
      startActivity(new Intent(this, Pastimes.class));
      return true;
    }
    else if (id == R.id.action_random)
    {
      startActivity(new Intent(this, RandomPastimes.class));
      return true;
    }
    else if (id == R.id.action_pastime_create)
    {
      Intent intent = new Intent(this, PastimeEditor.class);
      intent.putExtra(PastimeEditor.ARG_PARENT, Ideas.class);
      startActivity(intent);
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
  {
    // When the given tab is selected, switch to the corresponding page in
    // the ViewPager.
    mViewPager.setCurrentItem(tab.getPosition());
  }

  @Override
  public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
  {}

  @Override
  public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
  {}

  /**
   * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one
   * of the sections/tabs/pages.
   */
  public class SectionsPagerAdapter extends FragmentPagerAdapter
  {
    public SectionsPagerAdapter(FragmentManager fm)
    {
      super(fm);
    }

    @Override
    public Fragment getItem(int position)
    {
      final int count = IdeasFragment.LIST_COUNT_DEFAULT;
      final IdeasFragment fragment = new IdeasFragment();
      final Bundle args = new Bundle();

      args.putInt(IdeasFragment.ARG_SECTION_NUMBER, position);
      args.putSerializable(IdeasFragment.ARG_PARENT, Ideas.class);
      switch (position)
      {
      case 0:
        args.putBundle(IdeasFragment.ARG_QUERY_OPTIONS,
            new SuggestArgs(count, Crowd.SINGLE, 0, null).toBundle());
        break;
      case 1:
        args.putBundle(IdeasFragment.ARG_QUERY_OPTIONS,
            new SuggestArgs(count, Crowd.COUPLE, 0, null).toBundle());
        break;
      case 2:
        args.putBundle(IdeasFragment.ARG_QUERY_OPTIONS,
            new SuggestArgs(count, Crowd.GROUP, 0, null).toBundle());
        break;
      }
      fragment.setArguments(args);
      return fragment;
    }

    @Override
    public int getCount()
    {
      return 3;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
      Locale l = Locale.getDefault();
      switch (position)
      {
      case 0:
        return getString(R.string.title_section_single).toUpperCase(l);
      case 1:
        return getString(R.string.title_section_couple).toUpperCase(l);
      case 2:
        return getString(R.string.title_section_group).toUpperCase(l);
      }
      return null;
    }
  }
}

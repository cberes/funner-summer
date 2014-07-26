package net.seabears.funner.summer;

import java.lang.reflect.Method;
import java.util.Date;
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
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

public class RandomPastimes extends Activity implements ActionBar.TabListener
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
    actionBar.setDisplayHomeAsUpEnabled(true);
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
    getMenuInflater().inflate(R.menu.random, menu);
    return true;
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
    else if (id == R.id.action_refresh)
    {
      mSectionsPagerAdapter.refreshFragments();
      return true;
    }
    else if (id == R.id.action_pastime_create)
    {
      Intent intent = new Intent(this, PastimeEditor.class);
      intent.putExtra(PastimeEditor.ARG_PARENT, RandomPastimes.class);
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
    private SparseArray<Date> fragmentsToRefresh = new SparseArray<Date>(getCount());

    private Fragment primary;

    // start with an invalid index
    private int primaryPosition = -1;

    public SectionsPagerAdapter(FragmentManager fm)
    {
      super(fm);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object)
    {
      super.setPrimaryItem(container, position, object);

      // get the primary item so we can refresh it if we need to
      primary = (Fragment) object;
      primaryPosition = position;
    }

    @Override
    public Fragment getItem(int position)
    {
      final int count = IdeasFragment.LIST_COUNT_DEFAULT;
      final IdeasFragment fragment = new IdeasFragment();
      final Bundle args = new Bundle();

      args.putInt(IdeasFragment.ARG_SECTION_NUMBER, position);
      args.putSerializable(IdeasFragment.ARG_PARENT, RandomPastimes.class);
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

    public void refreshFragments()
    {
      final Date date = new Date();
      for (int i = 0; i < getCount(); ++i)
      {
        if (i != primaryPosition)
        {
          fragmentsToRefresh.put(i, date);
        }
      }

      if (primary instanceof IdeasFragment)
      {
        ((IdeasFragment) primary).refresh();
      }
    }

    private Fragment getFragment(int viewId, int position)
    {
      try
      {
        // the tag name is created by a private method
        Method method = FragmentPagerAdapter.class.getDeclaredMethod("makeFragmentName", Integer.TYPE, Long.TYPE);
        if (method != null)
        {
          method.setAccessible(true);
          String tag = (String) method.invoke(null, viewId, position);
          return getFragmentManager().findFragmentByTag(tag);
        }
      } catch (Exception e)
      {
        // log the error and show other methods
        Log.e(getClass().getSimpleName(), e.getMessage(), e);
        Log.d(getClass().getSimpleName(), "Available methods:");
        for (Method m : FragmentPagerAdapter.class.getDeclaredMethods())
        {
          Log.d(getClass().getSimpleName(), m.toGenericString());
        }
      }
      return null;
    }

    @Override
    public void startUpdate(ViewGroup container)
    {
      super.startUpdate(container);
      for (int i = 0; i < getCount(); ++i)
      {
        Fragment fragment = getFragment(container.getId(), i);
        if (fragment instanceof IdeasFragment)
        {
          // refresh the fragment if it exists and is out of date
          IdeasFragment ideasFragment = (IdeasFragment) fragment;
          final Date refreshed = ideasFragment.getLastRefreshed();
          final Date date = fragmentsToRefresh.get(i);
          if (date != null && refreshed != null && date.after(refreshed))
          {
            Log.d(getClass().getSimpleName(), "Refreshing fragment at position " + i);
            ideasFragment.refresh();
          }
        }
      }
    }
  }
}

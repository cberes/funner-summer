package net.seabears.funner.summer;

import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import net.seabears.funner.db.Crowd;
import net.seabears.funner.summer.suggest.SuggestArgs;
import net.seabears.funner.weather.Weather;
import net.seabears.funner.weather.WeatherService;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Locale;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one
 * of the sections/tabs/pages.
 */
class SectionsPagerAdapter extends FragmentPagerAdapter {
    private static class RefreshData {
        final Date requested;
        final Bundle data;

        RefreshData(Date requested, Bundle data) {
            this.requested = requested;
            this.data = data;
        }
    }

    private final SparseArray<RefreshData> fragmentsToRefresh = new SparseArray<>(getCount());

    private final FragmentManager fragmentManager;

    private final Class<?> parent;

    private final ContextProvider contextProvider;

    private final WeatherService weatherService;

    private Fragment primary;

    // start with an invalid index
    private int primaryPosition = -1;

    public SectionsPagerAdapter(FragmentManager fragmentManager, Class<?> parent, ContextProvider contextProvider, WeatherService weatherService) {
        super(fragmentManager);
        this.fragmentManager = fragmentManager;
        this.parent = parent;
        this.contextProvider = contextProvider;
        this.weatherService = weatherService;
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container, position, object);

        // get the primary item so we can refresh it if we need to
        primary = (Fragment) object;
        primaryPosition = position;
    }

    @Override
    public Fragment getItem(int position) {
        Crowd crowd;
        switch (position) {
            case 0:
                crowd = Crowd.SINGLE;
                break;
            case 1:
                crowd = Crowd.COUPLE;
                break;
            case 2:
            default:
                crowd = Crowd.GROUP;
                break;
        }

        final int count = IdeasFragment.LIST_COUNT_DEFAULT;
        final Weather weather = weatherService.getWeather(contextProvider.getContext());

        final Bundle args = new Bundle();
        args.putInt(IdeasFragment.ARG_SECTION_NUMBER, position);
        args.putSerializable(IdeasFragment.ARG_PARENT, parent);
        args.putAll(new SuggestArgs(count, crowd, weather.getTemperatureAsF(), weather.getCondition()).toBundle());

        final IdeasFragment fragment = new IdeasFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return contextProvider.getContext().getString(R.string.title_section_single).toUpperCase(l);
            case 1:
                return contextProvider.getContext().getString(R.string.title_section_couple).toUpperCase(l);
            case 2:
                return contextProvider.getContext().getString(R.string.title_section_group).toUpperCase(l);
        }
        return null;
    }

    public void refreshFragments() {
        refreshFragments(new Bundle());
    }

    public void refreshFragments(Bundle data) {
        Date now = new Date();
        for (int i = 0; i < getCount(); ++i) {
            if (i != primaryPosition) {
                fragmentsToRefresh.put(i, new RefreshData(now, data));
            }
        }

        if (primary instanceof IdeasFragment) {
            ((IdeasFragment) primary).refresh(data);
        }
    }

    @Override
    public void startUpdate(@NonNull ViewGroup container) {
        super.startUpdate(container);
        for (int i = 0; i < getCount(); ++i) {
            int viewId = container.getId();
            Fragment fragment = fragmentManager.findFragmentByTag(getFragmentTag(viewId, i));
            if (!(fragment instanceof IdeasFragment)) {
                continue;
            }

            // refresh the fragment if it exists and is out of date
            IdeasFragment ideasFragment = (IdeasFragment) fragment;
            final RefreshData data = fragmentsToRefresh.get(i);
            if (data == null) {
                continue;
            }

            if (data.requested.after(ideasFragment.getLastRefreshed())) {
                ideasFragment.refresh(data.data);
            }
        }
    }

    private String getFragmentTag(int viewId, int position) {
        try {
            // the tag name is created by a private method
            // TODO this seems dumb...do I really need to do it this way?
            Method method = FragmentPagerAdapter.class.getDeclaredMethod("makeFragmentName", Integer.TYPE, Long.TYPE);
            if (method != null) {
                method.setAccessible(true);
                return (String) method.invoke(null, viewId, position);
            }
        } catch (Exception e) {
            // log the error and show other methods
            Log.e(getClass().getSimpleName(), e.getMessage(), e);
            Log.d(getClass().getSimpleName(), "Available methods:");
            for (Method m : FragmentPagerAdapter.class.getDeclaredMethods()) {
                Log.d(getClass().getSimpleName(), m.toGenericString());
            }
        }
        return "";
    }
}

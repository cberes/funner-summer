package net.seabears.funner.summer;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;

public abstract class ProgressListFragment extends ListFragment
{
  private ProgressBar progressBar;

  @Override
  public void onActivityCreated(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    // Create a progress bar to display while the list loads
    progressBar = new ProgressBar(getActivity());
    progressBar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
        LayoutParams.WRAP_CONTENT));
    progressBar.setIndeterminate(true);
    getListView().setEmptyView(progressBar);

    // Must add the progress bar to the root of the layout
    ViewGroup root = (ViewGroup) getActivity().findViewById(android.R.id.content);
    root.addView(progressBar);
  }

  protected void clear(int resourceId)
  {
    // TODO is this right? should we save progressBar as a member?
    getListView().setEmptyView(null);
    ViewGroup root = (ViewGroup) getActivity().findViewById(android.R.id.content);
    root.removeView(progressBar);
    setEmptyText(getActivity().getResources().getText(resourceId));
  }
}
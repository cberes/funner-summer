package net.seabears.funner.summer;

import net.seabears.funner.Weather;
import net.seabears.funner.db.ActionInsertTask;
import net.seabears.funner.db.Crowd;
import net.seabears.funner.summer.suggest.PastimeActionArgs;
import net.seabears.funner.weather.BlockingWeatherReceiver;
import net.seabears.funner.weather.WeatherPullService;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class ManualPastimeDialogFragment extends DialogFragment
{
  private final Activity activity;
  private final Class<?> parent;
  private final ActionInsertInBackgroundTask task;

  public ManualPastimeDialogFragment(Activity activity, Class<?> parent, ActionInsertTask task)
  {
    this.activity = activity;
    this.parent = parent;
    BlockingWeatherReceiver weatherReceiver = new BlockingWeatherReceiver();
    this.task = new ActionInsertInBackgroundTask(task, weatherReceiver);
    WeatherPullService.observeWeather(this.activity, weatherReceiver);
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState)
  {
    // Use the Builder class for convenient dialog construction
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setCancelable(false)
        .setMessage(R.string.pastime_manual_prompt)
        .setPositiveButton(R.string.title_section_group, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface dialog, int id)
          {
            runInsert(Crowd.GROUP);
          }
        })
        .setNeutralButton(R.string.title_section_couple, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface dialog, int id)
          {
            runInsert(Crowd.COUPLE);
          }
        })
        .setNegativeButton(R.string.title_section_single, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface dialog, int id)
          {
            runInsert(Crowd.SINGLE);
          }
        });
    // Create the AlertDialog object and return it
    return builder.create();
  }

  private void runInsert(Crowd crowd)
  {
    task.execute(crowd);
    Toast.makeText(activity, R.string.pastime_recorded, Toast.LENGTH_LONG).show();
    activity.navigateUpTo(new Intent(activity, parent));
  }

  private static class ActionInsertInBackgroundTask extends AsyncTask<Crowd, Void, Void>
  {
    private final ActionInsertTask task;
    private final BlockingWeatherReceiver weatherReceiver;

    public ActionInsertInBackgroundTask(ActionInsertTask task, BlockingWeatherReceiver weatherReceiver)
    {
      this.task = task;
      this.weatherReceiver = weatherReceiver;
    }

    @Override
    protected Void doInBackground(Crowd... params)
    {
      try
      {
        // block until weather is known
        Weather weather = weatherReceiver.get();
        // set arguments now that all are known
        task.setPastimeArgs(new PastimeActionArgs(params[0], (int) weather.getTemperature(), weather.getCondition()));
        // insert into database
        task.insert();
      } catch (InterruptedException e)
      {
        // do nothing; thread will exit
        Log.e(getClass().getSimpleName(), e.getMessage(), e);
      }
      return null;
    }
  }
}
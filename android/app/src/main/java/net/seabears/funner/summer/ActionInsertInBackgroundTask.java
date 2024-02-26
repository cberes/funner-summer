package net.seabears.funner.summer;

import net.seabears.funner.Weather;
import net.seabears.funner.db.ActionInsertTask;
import net.seabears.funner.db.Crowd;
import net.seabears.funner.summer.suggest.PastimeActionArgs;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import java.math.BigDecimal;

public class ActionInsertInBackgroundTask extends AsyncTask<Crowd, Integer, Void>
{
  private final Activity activity;
  private final Class<?> parent;
  private final ActionInsertTask task;
  private ProgressDialog progressDialog;

  public ActionInsertInBackgroundTask(Activity activity, Class<?> parent, ActionInsertTask task)
  {
    this.activity = activity;
    this.parent = parent;
    this.task = task;
  }

  @Override
  protected void onPreExecute()
  {
    progressDialog = ProgressDialog.show(activity,
        activity.getText(R.string.pastime_progress_title),
        activity.getText(R.string.pastime_progress),
        false);
    super.onPreExecute();
  };

  @Override
  protected Void doInBackground(Crowd... params)
  {
    // TODO get weather somehow
    Weather weather = new Weather("cloudy", BigDecimal.valueOf(70));
    publishProgress((progressDialog.getMax() / 2) - 1);
    // set arguments now that all are known
    task.setPastimeArgs(new PastimeActionArgs(params[0], weather.getTemperature().intValue(), weather.getCondition()));
    publishProgress(progressDialog.getMax() / 2);
    // insert into database
    task.insert();
    publishProgress(progressDialog.getMax());
    return null;
  }

  @Override
  protected void onProgressUpdate(Integer... values)
  {
    super.onProgressUpdate(values);
    if (progressDialog.isShowing())
    {
      progressDialog.setProgress(values[0]);
    }
  };

  @Override
  protected void onPostExecute(Void result)
  {
    super.onPostExecute(result);
    progressDialog.dismiss();
    Toast.makeText(activity, R.string.pastime_recorded, Toast.LENGTH_LONG).show();
    Intent intent = new Intent(activity, parent);
    if (PastimeEditor.class.equals(parent))
    {
      intent.putExtra(PastimeEditor.ARG_PASTIME_ID, task.getPastimeId());
      intent.putExtra(PastimeEditor.ARG_PARENT, activity.getClass());
    }
    activity.navigateUpTo(intent);
  }
}
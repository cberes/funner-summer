package net.seabears.funner.summer;

import net.seabears.funner.db.ActionInsertTask;
import net.seabears.funner.db.Crowd;
import net.seabears.funner.summer.suggest.PastimeActionArgs;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class ManualPastimeDialogFragment extends DialogFragment
{
  private final Activity activity;
  private final Class<?> parent;
  private final ActionInsertTask task;

  public ManualPastimeDialogFragment(Activity activity, Class<?> parent, ActionInsertTask task)
  {
    this.activity = activity;
    this.parent = parent;
    this.task = task;
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState)
  {
    // Use the Builder class for convenient dialog construction
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setCancelable(false)
        .setMessage(R.string.pastime_manual_prompt)
        .setPositiveButton(R.string.title_section3, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface dialog, int id)
          {
            runInsert(new PastimeActionArgs(Crowd.GROUP, 0, null));
          }
        })
        .setNeutralButton(R.string.title_section2, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface dialog, int id)
          {
            runInsert(new PastimeActionArgs(Crowd.COUPLE, 0, null));
          }
        })
        .setNegativeButton(R.string.title_section1, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface dialog, int id)
          {
            runInsert(new PastimeActionArgs(Crowd.SINGLE, 0, null));
          }
        });
    // Create the AlertDialog object and return it
    return builder.create();
  }

  private void runInsert(PastimeActionArgs args)
  {
    task.setPastimeArgs(args);
    task.insert();
    Toast.makeText(activity, R.string.pastime_recorded, Toast.LENGTH_LONG).show();
    activity.navigateUpTo(new Intent(activity, parent));
  }
}
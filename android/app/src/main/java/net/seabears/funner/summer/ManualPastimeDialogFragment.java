package net.seabears.funner.summer;

import net.seabears.funner.db.Crowd;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class ManualPastimeDialogFragment extends DialogFragment
{
  private final ActionInsertInBackgroundTask task;

  public ManualPastimeDialogFragment(ActionInsertInBackgroundTask task)
  {
    this.task = task;
  }

  @NonNull
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
    // insert into database in background
    task.execute(crowd);
    // close dialog
    dismiss();
  }
}
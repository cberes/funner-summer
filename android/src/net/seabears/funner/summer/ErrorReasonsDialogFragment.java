package net.seabears.funner.summer;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class ErrorReasonsDialogFragment extends DialogFragment
{
  private final List<Integer> resourceIds;

  public ErrorReasonsDialogFragment(List<Integer> resourceIds)
  {
    this.resourceIds = new ArrayList<Integer>(resourceIds);
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState)
  {
    return new AlertDialog.Builder(getActivity())
        .setMessage(buildMessage())
        .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface dialog, int id)
          {
            dismiss();
          }
        }).create();
  }

  private String buildMessage()
  {
    StringBuilder message = new StringBuilder(getText(resourceIds.get(0)));
    for (int resourceId : resourceIds.subList(1, resourceIds.size()))
    {
      message.append(System.lineSeparator()).append(getText(resourceId));
    }
    return message.toString();
  }
}
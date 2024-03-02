package net.seabears.funner.summer;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class ErrorReasonsDialogFragment extends DialogFragment
{
  @NonNull
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
    Bundle arguments = getArguments();
    List<Integer> resourceIds = arguments == null ? null : arguments.getIntegerArrayList("resourceIds");
    return resourceIds == null || resourceIds.isEmpty() ? "" : buildMessageNotEmpty(resourceIds);
  }

  private String buildMessageNotEmpty(List<Integer> resourceIds) {
    StringBuilder message = new StringBuilder(getText(resourceIds.get(0)));
    for (int resourceId : resourceIds.subList(1, resourceIds.size()))
    {
      message.append(System.getProperty("line.separator")).append(getText(resourceId));
    }
    return message.toString();
  }
}
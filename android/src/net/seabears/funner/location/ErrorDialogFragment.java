package net.seabears.funner.location;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

public class ErrorDialogFragment extends DialogFragment
{
  // Global field to contain the error dialog
  private Dialog mDialog;

  public ErrorDialogFragment()
  {}

  // Set the dialog to display
  public void setDialog(Dialog dialog)
  {
    mDialog = dialog;
  }

  // Return a Dialog to the DialogFragment.
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState)
  {
    return mDialog;
  }
}
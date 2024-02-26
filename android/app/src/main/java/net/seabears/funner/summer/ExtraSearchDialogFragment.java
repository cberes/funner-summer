package net.seabears.funner.summer;

import java.util.Locale;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;

public class ExtraSearchDialogFragment extends DialogFragment
{
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState)
  {
    String action = getArguments().getString("action");

    // Use the Builder class for convenient dialog construction
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    final String message = String.format("%s %s?",
        getActivity().getResources().getText(R.string.pastime_search_prompt),
        action.toLowerCase(Locale.ENGLISH));
    builder.setMessage(message)
        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface dialog, int id)
          {
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra(SearchManager.QUERY, action);
            startActivity(intent);
          }
        })
        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface dialog, int id)
          {
            // User cancelled the dialog
          }
        });
    // Create the AlertDialog object and return it
    return builder.create();
  }
}
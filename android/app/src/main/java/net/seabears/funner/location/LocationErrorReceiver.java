package net.seabears.funner.location;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;

public abstract class LocationErrorReceiver extends BroadcastReceiver
{
  public static final String ARG_PENDING_INTENT = "pending_intent";
  public static final String ARG_STATUS_CODE = "status_code";

  @Override
  public void onReceive(Context context, Intent intent)
  {
    LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
    final PendingIntent pendingIntent = intent.getParcelableExtra(ARG_PENDING_INTENT);
    final int statusCode = intent.getIntExtra(ARG_STATUS_CODE, 0);
    Log.i(getClass().getSimpleName(), "Received result: " + statusCode + ", " + pendingIntent);
    if (pendingIntent != null)
    {
      final Activity activity = getActivity();
      final ConnectionResult result = new ConnectionResult(statusCode, pendingIntent);
      if (result.hasResolution())
      {
        try
        {
          // Start an Activity that tries to resolve the error
          result.startResolutionForResult(activity,
              LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);
        } catch (IntentSender.SendIntentException e)
        {
          // Thrown if Google Play services canceled the original PendingIntent
          Log.e(getClass().getSimpleName(), e.getMessage(), e);
        }
      } else
      {
        // If no resolution is available, display a dialog to the user with the
        // error.
        LocationUtils.showErrorDialog(activity, result.getErrorCode(), LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);
      }
    }
  }

  protected abstract Activity getActivity();
}
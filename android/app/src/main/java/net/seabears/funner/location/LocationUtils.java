package net.seabears.funner.location;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public final class LocationUtils
{
  // Debugging tag for the application
  public static final String APPTAG = "LocationSample";

  /*
   * Define a request code to send to Google Play services This code is returned
   * in Activity.onActivityResult
   */
  public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

  private LocationUtils()
  {
    throw new UnsupportedOperationException("cannot instantiate " + getClass());
  }

  /**
   * Verify that Google Play services is available before making a request.
   * 
   * @return true if Google Play services is available, otherwise false
   */
  public static boolean servicesConnected(Activity activity, boolean showDialog)
  {
    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
    Log.d(LocationUtils.class.getSimpleName(), "services result is " + resultCode);
    if (resultCode == ConnectionResult.SUCCESS)
    {
      return true;
    } else if (showDialog)
    {
      // Google Play services was not available for some reason
      // Display an error dialog
      // request code can be LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST
      showErrorDialog(activity, resultCode, 0);
    }
    return false;
  }

  /**
   * Verify that Google Play services is available before making a request.
   * 
   * @return true if Google Play services is available, otherwise false
   */
  public static boolean servicesConnected(Context context)
  {
    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
    Log.d(LocationUtils.class.getSimpleName(), "services result is " + resultCode);
    return resultCode == ConnectionResult.SUCCESS;
  }

  /**
   * Show a dialog returned by Google Play services for the connection error
   * code
   * 
   * @param errorCode
   *          An error code returned from onConnectionFailed
   */
  public static void showErrorDialog(Activity activity, int errorCode, int requestCode)
  {
    // Get the error dialog from Google Play services
    Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode, activity, requestCode);

    // Show error dialog if Google Play services provided one
    if (errorDialog != null)
    {
      ErrorDialogFragment errorFragment = new ErrorDialogFragment();
      errorFragment.setDialog(errorDialog);
      errorFragment.show(activity.getFragmentManager(), LocationUtils.APPTAG);
    }
  }
}

package net.seabears.funner.summer;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

final class PermissionChecker {
    private static final int REQUEST_CODE = 595759908;

    private PermissionChecker() {
        throw new UnsupportedOperationException("cannot instantiate " + getClass());
    }

    static boolean isPermissionRequestNecessary(final Context context)
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                !isLocationPermissionGranted(context);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private static boolean isLocationPermissionGranted(final Context context)
    {
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;
    }

    @TargetApi(Build.VERSION_CODES.M)
    static void requestLocationPermission(final Activity activity)
    {
        ActivityCompat.requestPermissions(activity,
                new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
                REQUEST_CODE);
    }

    @TargetApi(Build.VERSION_CODES.M)
    static void requestLocationPermission(final Fragment fragment)
    {
        fragment.requestPermissions(
                new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
                REQUEST_CODE);
    }

    static boolean isLocationPermissionResponse(final int requestCode)
    {
        return requestCode == REQUEST_CODE;
    }

    static boolean isLocationPermissionGranted(final int[] grantResults)
    {
        return grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }
}

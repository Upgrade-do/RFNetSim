package ntv.upgrade.rfnetworksimulator.tools;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Created by Paulino on 9/16/2015.
 */
public class CheckServices {

    /**
     * Verifies that Google Play services is installed and enabled on
     * this device, and that the version installed on this device is
     * no older than the one required by this client.
     */
    public static boolean checkPlayServices(final Activity activity) {
        final int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity.getApplicationContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {

                int PLAY_SERVICES_RESOLUTION_REQUEST = 10;
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        PLAY_SERVICES_RESOLUTION_REQUEST);
                if (dialog != null) {
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        public void onDismiss(DialogInterface dialog) {
                            activity.finish();
                        }
                    });
                    dialog.show();
                    return false;
                }
            }
            return false;
        }
        return true;
    }

    public static boolean isGoogleMapsInstalled(final Activity activity) {
        try {
            activity.getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}

package de.lmu.ifi.bouncingbash.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;

/**
 * Created by Leon on 29.12.2015.
 */
public class Utils {

    public static void showConnectionErrorDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("Connection Error")
                .setMessage("An error occured while connecting to the server. Try again later.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public static boolean enableBluetooth(Activity activity) {

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Log.v("Utils#enableBluetooth", "Device doesn't support Bluetooth.");
            return false;
        }
        // enable Bluetooth
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, 1);
        }
        return true;
    }

    public static void enableLocation(final Activity activity) {

        LocationManager l = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        if(l.isProviderEnabled(LocationManager.GPS_PROVIDER)
                && l.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            return;
        }

        final AlertDialog.Builder builder =
                new AlertDialog.Builder(activity);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String title = "Enable Location Services";
        final String message = "Please enable high accuracy location"
                + " services for BuncingBash to work properly. Click OK to go to"
                + " location services settings to let you do so.";

        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                activity.startActivity(new Intent(action));
                                d.dismiss();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });
        builder.create().show();
    }
}

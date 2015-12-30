package de.lmu.ifi.bouncingbash.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

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
}

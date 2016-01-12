package de.lmu.ifi.bouncingbash.app;

import android.util.Log;

/**
 * Created by Leon on 02.01.2016.
 */
public class Data {

    private final static String TAG ="Data";

    public static String userId = null;
    public static String password = null;

    public static Session currentSession = null;
    //public static boolean isHost = false;


    public static void setCredentials(String id, String pw) {
        userId = id;
        password = pw;
        Log.i(TAG, "set credentials to: " + userId + ", " + password);
    }
}

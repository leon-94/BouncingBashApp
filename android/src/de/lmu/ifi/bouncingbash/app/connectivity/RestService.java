package de.lmu.ifi.bouncingbash.app.connectivity;

import android.os.Handler;
import android.util.Log;

import com.eclipsesource.json.JsonObject;

import de.lmu.ifi.bouncingbash.app.Data;

/**
 * Created by Leon on 01.12.2015.
 */
public class RestService {

    private final String TAG = "RestService";

    public static final int MESSAGE_SERVER_RESPONSE = 0;
    public static final int MESSAGE_ERROR = 1;

    private final String IP =/** "192.168.2.104"**/"192.168.100.160";
    private final String BASE_URL = "http://"+ IP +":8080/BouncingBashServer/service/";
    //private String userId;
    //private String password;

    private static RestService restService;

    public static RestService getRestService() {
        if(restService == null) restService = new RestService();
        return restService;
    }

    private void performHttpRequest(String url, String method, String data, Handler handler) {
        new WebTask(url, method, data, handler).execute();
    }

    public void ping(Handler handler) {

        performHttpRequest(BASE_URL + "ping", "GET", null, handler);
    }


    public void testAuthentication(String id, String pw, Handler handler) {

        JsonObject credentials = new JsonObject();
        credentials.add("userId", id);
        credentials.add("password", pw);

        JsonObject message = new JsonObject();
        message.add("credentials", credentials);

        performHttpRequest(BASE_URL + "testauth", "POST", message.toString(), handler);
    }
    public void testAuthentication(Handler handler) {

        JsonObject message = createAuthenticatedMessage();

        performHttpRequest(BASE_URL + "testauth", "POST", message.toString(), handler);
    }

    public void signUp(Handler handler) {

        JsonObject message = new JsonObject();
        message.add("userId", Data.userId);
        message.add("password", Data.password);

        performHttpRequest(BASE_URL + "signup", "POST", message.toString(), handler);
    }

    public void signUp (String id, String pw, Handler handler) {

        JsonObject message = new JsonObject();
        message.add("userId", id);
        message.add("password", pw);

        performHttpRequest(BASE_URL + "signup", "POST", message.toString(), handler);
    }

    private JsonObject createAuthenticatedMessage() {

        JsonObject credentials = new JsonObject();
        credentials.add("userId", Data.userId);
        credentials.add("password", Data.password);

        JsonObject message = new JsonObject();
        message.add("credentials", credentials);

        return message;
    }

    public void postLocation(double lat, double lng, Handler handler) {

        JsonObject message = createAuthenticatedMessage();
        message.add("lat", lat);
        message.add("lng", lng);
        message.add("mac", BluetoothService.BT_MAC_ADDRESS);

        performHttpRequest(BASE_URL + "postlocation", "POST", message.toString(), handler);
    }

    public void postSession(double lat, double lng, Handler handler) {

        JsonObject message = createAuthenticatedMessage();
        message.add("lat", lat);
        message.add("lng", lng);
        message.add("mac", BluetoothService.BT_MAC_ADDRESS);

        performHttpRequest(BASE_URL + "postsession", "POST", message.toString(), handler);
    }

    public void getSessions(Handler handler) {

        JsonObject message = createAuthenticatedMessage();

        performHttpRequest(BASE_URL + "getsessions", "POST", message.toString(), handler);
    }

    public void openSessionPolling(Handler handler) {

        JsonObject message = createAuthenticatedMessage();

        performHttpRequest(BASE_URL + "opensessionpolling", "POST", message.toString(), handler);
    }

    public void joinSession(String hostId, Handler handler) {

        JsonObject message = createAuthenticatedMessage();
        message.add("hostId", hostId);
        message.add("mac", BluetoothService.BT_MAC_ADDRESS);

        performHttpRequest(BASE_URL + "joinsession", "POST", message.toString(), handler);
    }
}

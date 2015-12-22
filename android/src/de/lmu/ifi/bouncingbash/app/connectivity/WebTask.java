package de.lmu.ifi.bouncingbash.app.connectivity;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Leon on 01.12.2015.
 */
public class WebTask extends AsyncTask<String, String, String> {

    private final String TAG = "WebTask";

    private Handler handler;
    private String url;
    private String data;
    private String method;

    public WebTask(String u, String m, String d, Handler h) {
        super();

        url = u;
        data = d;
        handler = h;
        method = m;
    }

    protected String doInBackground(String... params) {

        return performHttpRequest(method, url, data);
    }

    @Override
    protected void onPreExecute() {

        Log.v("DEBUG", "WebTask started");
    }

    @Override
    protected void onProgressUpdate(String... values) {
    }

    @Override
    protected void onPostExecute(String result) {

        Log.v(TAG, "server response (raw): " + result);
        handler.obtainMessage(RestService.MESSAGE_SERVER_RESPONSE, result).sendToTarget();
        Log.v(TAG, "WebTask done");
    }

    public String performHttpRequest(String method, String urlString, String data) {

        StringBuffer response = new StringBuffer();

        try {
            URL url = new URL(urlString);
            if(method != "POST" && method != "PUT" && method != "GET" && method != "DELETE")
                throw new IllegalArgumentException("\""+ method +"\" is not a valid request type. POST, PUT, GET and DELETE are valid types.");

            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            //Log.v("DEBUG", "Response Message: " + connection.getResponseMessage());
            //connection.setRequestProperty("Accept", "application/xml");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestMethod(method);
            if(method == "POST" || method == "PUT") {
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                out.write(data);
                out.close();
            }

            Log.v("DEBUG", "Response Message: " + connection.getResponseMessage());

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String decodedString;
            while ((decodedString = in.readLine()) != null) {
                response.append(decodedString + "\n");
            }
            in.close();
            connection.disconnect();

        } catch(Exception e) { e.printStackTrace(); }

        return response.toString();
    }
}

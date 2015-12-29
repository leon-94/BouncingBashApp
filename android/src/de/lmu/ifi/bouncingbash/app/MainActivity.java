package de.lmu.ifi.bouncingbash.app;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

import de.lmu.ifi.bouncingbash.app.android.R;
import de.lmu.ifi.bouncingbash.app.connectivity.*;


public class MainActivity extends ActionBarActivity {

    private final String TAG = "MainActivity";

    private TextView textView;
    private EditText macEdit;

    private BluetoothAdapter bluetoothAdapter;

    private BluetoothService bluetoothService;

    private RestService restService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize system services
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Log.v(TAG, "Device doesn't support Bluetooth.");
        }
        // enable Bluetooth
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }


        // initialize custom services
        bluetoothService = BluetoothService.initBluetoothService(btHandler);
        restService = RestService.getRestService();

        textView = (TextView)findViewById(R.id.textView);

        // log MAC address of BluetoothAdapter
        Log.i(TAG, "your MAC: " + bluetoothAdapter.getAddress());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // button methods
    public void onButtonOpen(View view) {

        bluetoothService.openServer();
    }
    public void onButtonConnect(View view) {

        bluetoothService.connectToServer(BluetoothService.MAC_LEON);
    }
    public void onButtonPing(View view) {

        bluetoothService.write("ping");
    }
    public void onButtonStartGame(View view) {

    }

    public void onButtonTestAuth(View view) {

        restService.testAuthentication(testRestHandler);
    }

    public void onButtonMap(View view) {

        Intent i = new Intent(this, MapActivity.class);
        startActivity(i);
    }

    /*@Override
    public void onLocationChanged(Location location) {

        double lat = location.getLatitude();
        double lng = location.getLongitude();
        Log.d(TAG, "onLocationChanged   lat: "+lat+", lng: "+lng);

        restService.postLocation(lat, lng, testRestHandler);
    }*/

    // handlers
    private final Handler testRestHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            String stringMessage = (String) msg.obj;
            JsonObject message = (JsonObject) Json.parse(stringMessage);
            Log.d(TAG, "server response: " + message.toString());
        }
    };

    private final Handler btHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case BluetoothService.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    textView.setText(readMessage+"\n"+textView.getText());
                    break;
                case BluetoothService.MESSAGE_STATE_CHANGE:
                    break;
                case BluetoothService.MESSAGE_DEVICE_NAME:
                    break;
                case BluetoothService.MESSAGE_CONN_FAILED:
                    break;
                case BluetoothService.MESSAGE_CONN_LOST:
                    break;
                default:
                    Log.e(TAG, "btHandler received UNKNOWN message");
                    break;
            }
        }
    };

    private final Handler taskHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case RestService.MESSAGE_SERVER_RESPONSE:
                    String messageString = (String)msg.obj;
                    JsonObject message = (JsonObject) Json.parse(messageString);
                    String type = message.getString("type", "undefined");
                    Log.d(TAG, "message type: " + type);
                    switch(type) {
                        case "undefined":
                            Log.e(TAG, "message type is UNDEFINED");
                            break;
                        case "OPEN_SERVER_SOCKET":
                            bluetoothService.openServer();
                            break;
                        case "CONNECT_TO_SERVER":
                            String mac = (String) message.getString("MAC", null);
                            if(mac == null) Log.e(TAG, "no MAC address");
                            else bluetoothService.connectToServer(mac);
                            break;
                        case "GAME":
                            break;
                        default:
                            break;
                    }
                default:
                    break;
            }
        }
    };
}
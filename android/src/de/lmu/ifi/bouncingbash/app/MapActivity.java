package de.lmu.ifi.bouncingbash.app;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import de.lmu.ifi.bouncingbash.app.android.AndroidLauncher;
import de.lmu.ifi.bouncingbash.app.android.R;
import de.lmu.ifi.bouncingbash.app.connectivity.BluetoothService;
import de.lmu.ifi.bouncingbash.app.connectivity.RestService;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, LocationListener {

    private final String TAG = "MapActivity";

    public final long LOC_UPDATE_MIN_TIME = 10000;
    public final long LOC_UPDATE_MIN_DIST = 5;

    private LocationManager locationManager;
    private BluetoothService bluetoothService;
    private RestService restService;
    private GoogleMap mMap;

    private LatLng position;

    Marker positionMarker;
    HashMap<String, Marker> sessionMarkers;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        bluetoothService = BluetoothService.initBluetoothService(btHandler);

        sessionMarkers = new HashMap<>();

        restService = RestService.getRestService();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMarkerClickListener(this);

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOC_UPDATE_MIN_TIME, LOC_UPDATE_MIN_DIST, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOC_UPDATE_MIN_TIME, LOC_UPDATE_MIN_DIST, this);
        } catch(SecurityException e) { e.printStackTrace(); }

        position = null;
        try {
            Location l = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (l == null) position = new LatLng(48.1550547, 11.4017505);
            else position = new LatLng(l.getLatitude(), l.getLongitude());
        } catch(SecurityException e) { e.printStackTrace(); }

        positionMarker = mMap.addMarker(new MarkerOptions()
                .position(position)
                .title("Your Position")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_position))
                .visible(false));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));

        restService.getSessions(restHandler);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        Log.d(TAG, "onMarkerClick");
        if(marker.getTitle().equals("Your Position")) {

        }
        else {
            String snippet = marker.getSnippet();
            final String hostId = snippet.split("hosted by ")[1];
            Log.d(TAG, hostId);
            new AlertDialog.Builder(this)
                    .setTitle("Session Details")
                    .setMessage("Do you want to join "+ hostId +"'s game?")
                    .setPositiveButton("Join", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            restService.joinSession(hostId, restHandler);
                            progress = ProgressDialog.show(MapActivity.this, "Connecting",
                                    "Receiving data from the server ...", true);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_menu_myplaces)
                    .show();
        }
        return true;
    }

    public void onButtonRefresh(View view) {
        restService.getSessions(restHandler);
    }

    public void onButtonPostSession(View view) {
        restService.postSession(position.latitude, position.longitude, restHandler);
    }

    @Override
    public void onLocationChanged(Location location) {

        position = new LatLng(location.getLatitude(), location.getLongitude());
        positionMarker.setPosition(position);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));

        restService.postLocation(position.latitude, position.longitude, restHandler);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void updateSessions(ArrayList<Session> sessions) {

        for(Session session : sessions) {
            String hostId = session.getHostId();
            LatLng location = new LatLng(session.getLat(), session.getLng());
            Marker m = sessionMarkers.get(hostId);
            if(m == null) {
                sessionMarkers.put(hostId, mMap.addMarker(new MarkerOptions()
                        .position(location)
                        .title("An open session")
                        .snippet("hosted by " + session.getHostId())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_session))));
            }
            else {
                m.setPosition(location);
            }
        }
    }

    private ArrayList<Session> convertSessionArray(JsonArray jsonSessions) {

        Iterator it = jsonSessions.iterator();
        ArrayList<Session> sessions = new ArrayList<>();
        while(it.hasNext()) {
            JsonObject j = (JsonObject)it.next();
            Session s = new Session(j.getString("hostId", null),
                    j.getString("hostMac", null),
                    j.getDouble("lat", 1000),
                    j.getDouble("lng", 1000));
            sessions.add(s);
        }
        return sessions;
    }
    private void openSessionPolling() {
        restService.openSessionPolling(restHandler);
    }


    private void startConnection(Session session) {

        if(session.getHostId().equals(restService.getUserId())) {

            progress = ProgressDialog.show(MapActivity.this, "Connecting",
                    "Bluetooth connection is being established ...", true);
            bluetoothService.openServer();
        }
        else {
            progress.setMessage("Bluetooth connection is being established ...");
            bluetoothService.connectToServer(session.getHostMac());
        }
    }

    private void startGame() {
        Intent i = new Intent(this, AndroidLauncher.class);
        startActivity(i);
    }

    private final Handler restHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == RestService.MESSAGE_ERROR) Utils.showConnectionErrorDialog(MapActivity.this);

            String stringMessage = (String) msg.obj;
            JsonObject message = (JsonObject) Json.parse(stringMessage);
            if(!message.getBoolean("success", false)) {
                Log.e(TAG, "Server request was not successful.");
                return;
            }

            Log.d(TAG, "server response: " + message.toString());
            String type = message.getString("type", null);

            switch(type) {
                case "postsession":
                    openSessionPolling();
                    break;
                case "getsessions":
                    JsonArray jsonSessions = (JsonArray)message.get("sessions");
                    updateSessions(convertSessionArray(jsonSessions));
                    break;
                case "opensessionpolling":
                    if(message.getBoolean("open", true)) {
                        openSessionPolling();
                    }
                    else {
                        Session session = Session.fromJson((JsonObject) message.get("session"));
                        startConnection(session);
                    }
                    break;
                case "joinsession":
                    Session session2 = Session.fromJson((JsonObject)message.get("session"));
                    startConnection(session2);
                    break;
                default: break;
            }
        }
    };


    private final Handler btHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case BluetoothService.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Log.d(TAG, readMessage);
                    break;
                case BluetoothService.MESSAGE_STATE_CHANGE:
                    int state = (int)msg.obj;
                    if(state == BluetoothService.STATE_CONNECTED) startGame();
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
}

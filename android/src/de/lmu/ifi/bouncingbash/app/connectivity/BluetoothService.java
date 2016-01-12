package de.lmu.ifi.bouncingbash.app.connectivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import de.lmu.ifi.bouncingbash.app.IBluetoothService;
import de.lmu.ifi.bouncingbash.app.game.Transmittable;
import de.lmu.ifi.bouncingbash.app.game.views.BallView;

public class BluetoothService implements IBluetoothService {

    public static BluetoothService bluetoothService;

    private final String TAG = "BluetoothService";

    public static final String MAC_LEON = "C0:EE:FB:32:10:1C";
    public static final String MAC_INSA = "94:01:C2:C1:FF:70";
    public static final String MAC_MICHAEL = "0C:14:20:4A:18:DB";
    public static final String MAC_HTC = "7C:61:93:4D:03:0B";
    public static final String BT_MAC_ADDRESS = BluetoothAdapter.getDefaultAdapter().getAddress();

    public static final UUID APP_UUID = UUID.fromString("193d88e8-5507-4c7f-b5d5-3ef7b765de49");
    public static final int MESSAGE_READ = 0;
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_DEVICE_NAME = 2;
    public static final int MESSAGE_CONN_FAILED = 3;
    public static final int MESSAGE_CONN_LOST = 4;

    private Handler handler;
    private BluetoothAdapter bluetoothAdapter;
    private int state;
    private ArrayList<String> queue = new ArrayList<>();
    private boolean queueing = false;

    private AcceptThread acceptThread;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device

    private long meanLatency = 0;


    // static methods -----------------------------------------------------------------------------

    public static BluetoothService initBluetoothService(Handler h) {
        bluetoothService = new BluetoothService(h);
        return bluetoothService;
    }

    public static BluetoothService getBluetoothService() {
        return bluetoothService;
    }

    public static void setHandler(Handler h) {
        bluetoothService.handler = h;
    }

    // instance methods ---------------------------------------------------------------------------

    private BluetoothService(Handler h) {
        state = STATE_NONE;
        handler = h;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private synchronized void setState(int s) {
        state = s;
        handler.obtainMessage(MESSAGE_STATE_CHANGE, state).sendToTarget();
    }
    public synchronized int getState() { return state; }

    public synchronized boolean isQueueing() { return queueing; }
    public synchronized void setQueueing(boolean q) { queueing = q; }

    public long getMeanLatency() { return meanLatency; }
    public void setMeanLatency(long meanLatency) { this.meanLatency = meanLatency; }


    // connection methods ---------------------------------------------------------------------------

    public void openServer() {
        Log.i(TAG, "openServer");

        // Cancel any thread attempting to make a connection
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        // Cancel any thread currently running a connection
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        setState(STATE_LISTEN);

        if(acceptThread == null) {
            acceptThread = new AcceptThread();
            acceptThread.start();
        }
    }

    public void connectToServer() { connectToServer(MAC_LEON); }
    public void connectToServer(String mac) {
        Log.i(TAG, "connectToServer, MAC: " + mac);

        if(state == STATE_CONNECTING) {
            if(connectThread != null) {
                connectThread.cancel();
                connectThread = null;
            }
        }

        // Cancel any thread listening for a connection
        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
        }

        // Cancel any thread currently running a connection
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        connectThread = new ConnectThread(mac);
        connectThread.start();
        setState(STATE_CONNECTING);
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        Log.d(TAG, "connected to " + device.getName());

        // Cancel the thread that completed the connection
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        // Cancel any thread currently running a connection
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        // Cancel the accept thread because we only want to connect to one device
        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        connectedThread = new ConnectedThread(socket);
        connectedThread.start();

        // Send the name of the connected device back to the UI Activity
        handler.obtainMessage(MESSAGE_DEVICE_NAME, device.getName()).sendToTarget();

        setState(STATE_CONNECTED);
    }

    public synchronized void stop() {
        Log.d(TAG, "stop");

        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
        }
        setState(STATE_NONE);
    }

    public synchronized String[] read() {
        if(!queueing) return null;

        String[] result = new String[queue.size()];
        queue.toArray(result);
        queue.clear();

        return result;
    }

    public void write(String out) {
        try {
            byte[] b = out.getBytes("US-ASCII");
            write(b);
        } catch(UnsupportedEncodingException e) { e.printStackTrace(); }
    }

    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (state != STATE_CONNECTED) return;
            r = connectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }

    private void connectionFailed(String mac) {
        // Send a failure message back to the Activity
        handler.obtainMessage(MESSAGE_CONN_FAILED, mac).sendToTarget();
    }

    private void connectionLost() {
        // Send a failure message back to the Activity
        handler.obtainMessage(MESSAGE_CONN_LOST).sendToTarget();
    }


    // inner classes ------------------------------------------------------------------------------

    private class ConnectedThread extends Thread {

        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {

            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.i(TAG, "BEGIN connectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);

                    if(queueing) {
                        // write bytes into queue
                        String s = new String(buffer, 0, bytes, "US-ASCII");
                        //Log.d(TAG, "receiving: "+s);
                        queue.add(s);
                    }
                    else {
                        // Send the obtained bytes to the UI Activity
                        handler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    break;
                }
            }
        }

        public void write(byte[] buffer) {

            try {
                mmOutStream.write(buffer);
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
                handler.obtainMessage(MESSAGE_CONN_LOST);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close of connect socket failed", e);
            }
        }
    }

    private class AcceptThread extends Thread {

        private final BluetoothServerSocket serverSocket;

        public AcceptThread() {
            super();

            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code
                tmp = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("BouncingBash", APP_UUID);
            } catch (IOException e) { e.printStackTrace(); }
           serverSocket = tmp;
        }

        public void run() {

            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned
            while (state != STATE_CONNECTED) {
                try {
                    Log.i(TAG, "accept");
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "exeption during accept");
                    e.printStackTrace();
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (BluetoothService.this) {
                        switch (state) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                // Situation normal. Start the connected thread.
                                connected(socket, socket.getRemoteDevice());
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                // Either not ready or already connected. Terminate new socket.
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    Log.e(TAG, "Could not close unwanted socket", e);
                                }
                                break;
                        }
                    }
                }
            }
        }

        /** Will cancel the listening socket, and cause the thread to finish */
        public void cancel() {
            try {
                serverSocket.close();
            } catch (IOException e) { e.printStackTrace(); }
        }
    }

    private class ConnectThread extends Thread {

        private final BluetoothSocket socket;
        private final BluetoothDevice device;
        private final String address;

        public ConnectThread(String address) {
            super();
            this.address = address;
            device = bluetoothAdapter.getRemoteDevice(address);
            BluetoothSocket tmp = null;

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                tmp = device.createInsecureRfcommSocketToServiceRecord(APP_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Socket Type: create() failed", e);
            }
            socket = tmp;
        }

        public void run() {

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                Log.i(TAG, "connect");
                socket.connect();
            } catch (Exception connectException) {
                Log.e(TAG, "exeption during connect");
                connectException.printStackTrace();
                // Unable to connect; close the socket and get out
                try {
                    Log.i(TAG, "close");
                    socket.close();
                } catch (IOException closeException) {
                    closeException.printStackTrace();
                    Log.e(TAG, "exeption during close()");
                }
                connectionFailed(address);
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothService.this) {
                connectThread = null;
            }

            // Start the connected thread
            connected(socket, socket.getRemoteDevice());
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) { }
        }
    }



    @Override
    public void transmit(JsonObject message) {

        write(message.toString());
    }

    @Override
    public JsonObject receiveLatest() {
        String[] messages = read();
        if(messages == null || messages.length == 0) return null;
        JsonObject jsonMessage = null;
        try {
            jsonMessage = (JsonObject) Json.parse(messages[messages.length - 1]);
        } catch(Exception e) { e.printStackTrace(); }
        return jsonMessage;
    }

    @Override
    public JsonArray receiveAll() {
        String[] messages = read();
        if(messages == null || messages.length == 0) return null;
        JsonArray jsonArray = new JsonArray();
        for(int i = 0; i < messages.length; i++) {
            try {
                JsonObject msg = (JsonObject) Json.parse(messages[i]);
                jsonArray.add(msg);
            } catch(Exception e) { e.printStackTrace(); }
        }
        return jsonArray;
    }


    /*
    public void transmit(ArrayList<Transmittable> ts) {

        JsonObject message = new JsonObject();
        JsonArray elements = new JsonArray();

        for(Transmittable t : ts) {
            elements.add(toJsonObj(t));
        }

        message.add("elements", elements);
        write(message.toString());
    }

    public ArrayList<Transmittable> receive() {
        ArrayList<Transmittable> ts = new ArrayList<>();
        String[] messages = read();
        if(messages == null || messages.length == 0) return null;

        String lastestMsg = messages[messages.length-1];
        JsonObject message = (JsonObject) Json.parse(lastestMsg);
        JsonArray elements = (JsonArray) message.get("elements");
        Iterator it = elements.iterator();
        while(it.hasNext()) {
            JsonObject obj = (JsonObject) it.next();
            ts.add(toTransmittable(obj));
        }

        return ts;
    }

    private JsonObject toJsonObj(Transmittable t) {
        JsonObject obj = new JsonObject();

        HashMap<String, String> map = t.getDataMap();
        Collection values = map.values();
        Iterator<String> it = values.iterator();
        while(it.hasNext()) {
            String s = it.next();
            obj.add(s, map.get(s));
        }

        return obj;
    }*/
}
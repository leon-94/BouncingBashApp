package de.lmu.ifi.bouncingbash.app;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

/**
 * Created by Leon on 31.12.2015.
 */
public interface IBluetoothService {

    public void write(String s);
    public void transmit(JsonObject message);
    public JsonArray receiveAll();
    public JsonObject receiveLatest();
    public String[] read();
    public boolean isQueueing();
    public void setQueueing(boolean q);
}

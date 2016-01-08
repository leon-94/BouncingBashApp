package de.lmu.ifi.bouncingbash.app;

/**
 * Created by Leon on 31.12.2015.
 */
public interface IBluetoothService {

    public void write(String s);
    public String[] read();
    public boolean isQueueing();
    public void setQueueing(boolean q);
}

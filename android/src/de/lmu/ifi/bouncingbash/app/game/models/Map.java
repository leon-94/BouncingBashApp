package de.lmu.ifi.bouncingbash.app.game.models;

import java.util.ArrayList;

/**
 * Created by Michael on 11.12.2015.
 */
public class Map {
    private ArrayList<Platform> platformArrayList = new ArrayList<Platform>();
    private ArrayList<Item> itemArrayList = new ArrayList<Item>();

    public ArrayList<Item> getItemArrayList() {
        return itemArrayList;
    }

    public void setItemArrayList(ArrayList<Item> itemArrayList) {
        this.itemArrayList = itemArrayList;
    }

    public void setPlatformArrayList(ArrayList<Platform> platformArrayList) {
        this.platformArrayList = platformArrayList;
    }

    public ArrayList<Platform> getPlatformArrayList() {

        return platformArrayList;
    }
}

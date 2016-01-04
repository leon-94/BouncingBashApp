package de.lmu.ifi.bouncingbash.app.game.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;

/**
 * Created by Michael on 11.12.2015.
 */
public class Map {
    private ArrayList<Platform> platformArrayList = new ArrayList<Platform>();
    private ArrayList<Item> itemArrayList = new ArrayList<Item>();
    //große Platform in der mitte
    private Platform mainPlatform = new Platform();
    private String backGround = "background.png";


    public Platform getMainPlatform() {
        return mainPlatform;
    }

    public void setMainPlatform(Platform mainPlatform) {
        this.mainPlatform = mainPlatform;
    }

    public String getBackGround() {
        return backGround;
    }

    public void setBackGround(String backGround) {
        this.backGround = backGround;
    }

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

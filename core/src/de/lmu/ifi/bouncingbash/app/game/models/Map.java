package de.lmu.ifi.bouncingbash.app.game.models;

import java.util.ArrayList;

/**
 * Created by Michael on 11.12.2015.
 */
public class Map {
    private ArrayList<de.lmu.ifi.bouncingbash.app.game.models.Platform> platformArrayList = new ArrayList<de.lmu.ifi.bouncingbash.app.game.models.Platform>();
    private ArrayList<Item> itemArrayList = new ArrayList<Item>();
    //große Platform in der mitte
    private de.lmu.ifi.bouncingbash.app.game.models.Platform mainPlatform = new de.lmu.ifi.bouncingbash.app.game.models.Platform();
    private String backGround = "background.png";


    public de.lmu.ifi.bouncingbash.app.game.models.Platform getMainPlatform() {
        return mainPlatform;
    }

    public void setMainPlatform(de.lmu.ifi.bouncingbash.app.game.models.Platform mainPlatform) {
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

    public void setPlatformArrayList(ArrayList<de.lmu.ifi.bouncingbash.app.game.models.Platform> platformArrayList) {
        this.platformArrayList = platformArrayList;
    }

    public ArrayList<de.lmu.ifi.bouncingbash.app.game.models.Platform> getPlatformArrayList() {

        return platformArrayList;
    }
}

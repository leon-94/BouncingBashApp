package de.lmu.ifi.bouncingbash.app.game.models;

/**
 * Created by Michael on 11.12.2015.
 */
public enum UpgradeType {
    SPEEDUP("speedUp.png",10),FIREUP("fireUp.png",10);
    private String name;
    private int length;

    UpgradeType(String name, int length)
    {
        this.name=name;
        this.length = length;
    }
    public String getName() {
        return name;
    }

    public int getLength() {
        return length;
    }
}

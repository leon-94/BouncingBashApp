package de.lmu.ifi.bouncingbash.app.game.models;

import android.graphics.Bitmap;

/**
 * Created by Michael on 11.12.2015.
 */
public class Item {
    private EffectType type;
    private int xCoordinates;
    private int yCoordinates;
    private Bitmap texture;

    public int getxCoordinates() {
        return xCoordinates;
    }

    public int getyCoordinates() {
        return yCoordinates;
    }

    public Bitmap getTexture() {
        return texture;
    }

    public void setxCoordinates(int xCoordinates) {
        this.xCoordinates = xCoordinates;
    }

    public void setyCoordinates(int yCoordinates) {
        this.yCoordinates = yCoordinates;
    }

    public void setTexture(Bitmap texture) {
        this.texture = texture;
    }
}

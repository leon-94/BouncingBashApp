package de.lmu.ifi.bouncingbash.app.game.models;

import android.graphics.Bitmap;

/**
 * Created by Michael on 11.12.2015.
 */
public class Ball {

    private int xCoordinates;
    private int yCoordinates;
    private Bitmap texture;
    private Item item=null;

    public void setItem(Item item) {
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

    public boolean hasItem()
    {
        if (item.equals(null))
        {
            return false;
        }
        else
        {
            return true;
        }
    }

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

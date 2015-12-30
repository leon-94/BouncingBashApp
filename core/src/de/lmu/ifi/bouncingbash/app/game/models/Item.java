package de.lmu.ifi.bouncingbash.app.game.models;


import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Michael on 11.12.2015.
 */
public class Item {
    private EffectType type;
    private int xCoordinates;
    private int yCoordinates;
    private Texture texture;

    public int getxCoordinates() {
        return xCoordinates;
    }

    public int getyCoordinates() {
        return yCoordinates;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setxCoordinates(int xCoordinates) {
        this.xCoordinates = xCoordinates;
    }

    public void setyCoordinates(int yCoordinates) {
        this.yCoordinates = yCoordinates;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }
}

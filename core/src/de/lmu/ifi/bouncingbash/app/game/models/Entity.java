package de.lmu.ifi.bouncingbash.app.game.models;

import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by Michi on 16.01.2016.
 */
public abstract class Entity {
    private Sprite sprite;
    private int x=0,y=0;
    public Sprite getSprite() {
        return sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}

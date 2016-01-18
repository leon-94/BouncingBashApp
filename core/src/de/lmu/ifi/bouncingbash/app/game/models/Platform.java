package de.lmu.ifi.bouncingbash.app.game.models;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by Michi on 11.12.2015.
 */
/**Model für die Hauptplatform und kleinere Platformen in der Karte**/
public class Platform extends Entity {

    private String texture = "platform.png";
    private Sprite sprite;
    private int height= 30,width =  Gdx.graphics.getWidth()-60 ;

    public Platform()
    {

    }
    public Platform(int width ,int x, int y)
    {
        this.width=width;
        setX(x);
        setY(y);
    }
    public Platform(int width ,int height,int x, int y)
    {
        this.width=width;
        this.height=height;
        setX(x);
        setY(y);
    }

    public String getTexture() {
        return texture;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }



}

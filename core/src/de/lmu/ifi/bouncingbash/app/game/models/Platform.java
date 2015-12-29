package de.lmu.ifi.bouncingbash.app.game.models;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Michi on 11.12.2015.
 */
/**Model für die Hauptplatform und kleinere Platformen in der Karte**/
public class Platform {

    private Texture texture = new Texture(Gdx.files.internal("platform.png"));
    private int height= 30 ;
    private int width =  Gdx.graphics.getWidth()-60;

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
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

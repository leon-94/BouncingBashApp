package de.lmu.ifi.bouncingbash.app.game.models;



import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by Michael on 11.12.2015.
 */
public class Ball {

    //default Texture
    private String texture = "kugel.png";

    private Item item=null;
    private float jumpSpeed=20;

    public float getJumpSpeed() {
        return jumpSpeed;
    }

    public void setJumpSpeed(float jumpSpeed) {
        this.jumpSpeed = jumpSpeed;
    }

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
    public String getTexture() {
        return texture;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }




}

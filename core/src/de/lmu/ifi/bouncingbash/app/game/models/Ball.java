package de.lmu.ifi.bouncingbash.app.game.models;


/**
 * Created by Michael on 11.12.2015.
 */
public class Ball {

    //default Texture
    private String texture = "kugel.png";

    private Item item=null;
    private float jumpSpeed=20;

    private int x=0,y=0;

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

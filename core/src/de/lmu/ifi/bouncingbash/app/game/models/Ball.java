package de.lmu.ifi.bouncingbash.app.game.models;



import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by Michael on 11.12.2015.
 */
public class Ball {

    private float xCoordinates=0;
    private float yCoordinates=0;
    //default Texture
    private Texture texture = new Texture(Gdx.files.internal("kugel.png"));
    private Sprite sprite = new Sprite(texture);

    private Item item=null;
    private JumpStates jumpState= JumpStates.STANDING;
    private float jumpSpeed=20;
    private float originalJumpSpeed=20;
    /**ycoordinaten vor dem Sprung**/
    private float yCoordinatesOriginal=0;


    public Sprite getSprite() {
        return sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public float getOriginalJumpSpeed() {
        return originalJumpSpeed;
    }

    public void setOriginalJumpSpeed(float originalJumpSpeed) {
        this.originalJumpSpeed = originalJumpSpeed;
    }

    public float getyCoordinatesOriginal() {
        return yCoordinatesOriginal;
    }

    public void setyCoordinatesOriginal(float yCoordinatesOriginal) {
        this.yCoordinatesOriginal = yCoordinatesOriginal;
    }

    public float getJumpSpeed() {
        return jumpSpeed;
    }

    public void setJumpSpeed(float jumpSpeed) {
        this.jumpSpeed = jumpSpeed;
    }

    public JumpStates getJumpState() {
        return jumpState;
    }

    public void setJumpState(JumpStates jumpState) {
        this.jumpState = jumpState;
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

    public float getxCoordinates() {
        return xCoordinates;
    }

    public float getyCoordinates() {
        return yCoordinates;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setxCoordinates(float xCoordinates) {
        this.xCoordinates = xCoordinates;
    }

    public void setyCoordinates(float yCoordinates) {
        this.yCoordinates = yCoordinates;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

}

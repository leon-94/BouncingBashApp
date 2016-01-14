package de.lmu.ifi.bouncingbash.app.game.models;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by Michael on 11.12.2015.
 */
public class Item {
    private EffectType type = EffectType.SPEEDUP;
    private Sprite sprite = new Sprite(new Texture(Gdx.files.internal(type.getName())));

    public Sprite getSprite() {
        return sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }
    public EffectType getType() {
        return type;
    }

    public void setType(EffectType type) {
        this.type = type;
    }

}

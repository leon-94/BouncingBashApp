package de.lmu.ifi.bouncingbash.app.game.models;



import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by Michael on 11.12.2015.
 */
public class Item extends Entity {
    private EffectType type = EffectType.SPEEDUP;

    public EffectType getType() {
        return type;
    }

    public void setType(EffectType type) {
        this.type = type;
    }


}

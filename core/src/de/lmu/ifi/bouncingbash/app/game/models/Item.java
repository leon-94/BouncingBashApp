package de.lmu.ifi.bouncingbash.app.game.models;


/**
 * Created by Michael on 11.12.2015.
 */
public class Item {
    private EffectType type;
    private String texture;

    public String getTexture() {
        return texture;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }
    public EffectType getType() {
        return type;
    }

    public void setType(EffectType type) {
        this.type = type;
    }

}

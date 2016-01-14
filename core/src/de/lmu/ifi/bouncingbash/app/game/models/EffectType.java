package de.lmu.ifi.bouncingbash.app.game.models;

/**
 * Created by Michael on 11.12.2015.
 */
public enum EffectType {
    SPEEDUP("speedUp.png");
    private String name;

    EffectType(String name)
    {
        this.name=name;
    }
    public String getName() {
        return name;
    }

}

package de.lmu.ifi.bouncingbash.app.game.animation;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Leon on 17.01.2016.
 */
public abstract class BasicAnimation {

    protected boolean done = false;

    public abstract void update(float elapsedTime);
    public abstract void render(SpriteBatch batch);
}

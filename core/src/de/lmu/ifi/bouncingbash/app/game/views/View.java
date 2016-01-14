package de.lmu.ifi.bouncingbash.app.game.views;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;

import de.lmu.ifi.bouncingbash.app.game.models.GameModel;

/**
 * Created by Michi on 14.01.2016.
 */
public interface View {
    void setup();
    void draw();
}

package de.lmu.ifi.bouncingbash.app.game.views;

/**
 * Created by Michi on 15.01.2016.
 */

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;

import java.util.HashMap;

import de.lmu.ifi.bouncingbash.app.game.models.Entity;


/**all Views were the bodys are relevant**/
public interface BodyView {
    HashMap<Entity, Body> getBodys();
    void setup();
    void draw();
}

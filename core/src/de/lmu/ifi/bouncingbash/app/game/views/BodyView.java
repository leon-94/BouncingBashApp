package de.lmu.ifi.bouncingbash.app.game.views;

/**
 * Created by Michi on 15.01.2016.
 */

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;

import java.util.HashMap;


/**all Views were the bodys are relevant**/
public interface BodyView {
    HashMap<Sprite, Body> getBodys();
    void setup();
    void draw();
}

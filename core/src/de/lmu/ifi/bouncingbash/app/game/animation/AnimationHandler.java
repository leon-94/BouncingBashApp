package de.lmu.ifi.bouncingbash.app.game.animation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

import de.lmu.ifi.bouncingbash.app.game.Textures;

/**
 * Created by Leon on 16.01.2016.
 */
public class AnimationHandler {

    private final String TAG = "AnimationHandler";

    private ArrayList<BasicAnimation> animations;

    public AnimationHandler() {
        animations = new ArrayList<>();
    }

    public synchronized void update(float elapsedTime) {
        for(BasicAnimation a : animations) {
            if(a.done) {
                animations.remove(a);
                continue;
            }
            a.update(elapsedTime);
        }
    }

    public void render(SpriteBatch batch) {
        for(BasicAnimation a : animations) a.render(batch);
    }

    public void contactAnim(Vector2 position, int rotation) {
        SpriteAnimation a = new SpriteAnimation(position, 1/40f, 20, Textures.getTextures().getTexture("TEX_CIRCLE"), rotation, 1f, 1/20f, new Vector2(50, 50), 1/5f, Color.CYAN);
        animations.add(a);
    }

    public void jumpAnim(Vector2 position) {
        SpriteAnimation a = new SpriteAnimation(position, 1/40f, 20, Textures.getTextures().getTexture("TEX_CIRCLE"), 0, 1f, 1/20f, new Vector2(50, 50), 1/5f, Color.CYAN);
        animations.add(a);
    }

    public void gravityAnim(Vector2 position, int rotation) {
        SpriteAnimation a = new SpriteAnimation(position, 1/40f, 40, Textures.getTextures().getTexture("TEX_ARROW"), rotation, 1f, 1/40f, new Vector2(150, 150), 1/20f, Color.CYAN);
        animations.add(a);
    }

    public void switchtAnim(Vector2 position) {
        SpriteAnimation a = new SpriteAnimation(position, 1/40f, 20, Textures.getTextures().getTexture("TEX_DIAMONT"), 0, 1f, 1/20f, new Vector2(50, 50), 1/5f, Color.CYAN);
        animations.add(a);
    }
}

package de.lmu.ifi.bouncingbash.app.game.animation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

import de.lmu.ifi.bouncingbash.app.game.Assets;

/**
 * Created by Leon on 16.01.2016.
 */
public class AnimationHandler {

    private final String TAG = "AnimationHandler";

    private ArrayList<BasicAnimation> animations;
    private ArrayList<BasicAnimation> toBeRemoved;

    public AnimationHandler() {
        animations = new ArrayList<>();
        toBeRemoved = new ArrayList<>();
    }

    public synchronized void update(float elapsedTime) {
        for(BasicAnimation a : animations) {
            a.update(elapsedTime);
            if(a.done) toBeRemoved.add(a);
        }
        for (BasicAnimation a : toBeRemoved) {
            animations.remove(a);
        }
        toBeRemoved.clear();
    }

    public void render(SpriteBatch batch) {
        for(BasicAnimation a : animations) a.render(batch);
    }

    public synchronized void contactAnim(Vector2 position, int rotation) {
        SpriteAnimation a = new SpriteAnimation(position, 1/60f, 30, Assets.getAssets().getTexture("TEX_CIRCLE"), rotation, 1f, 1/30f, new Vector2(50, 50), 1/7f, Color.CYAN);
        animations.add(a);
    }

    public synchronized void jumpAnim(Vector2 position) {
        SpriteAnimation a = new SpriteAnimation(position, 1/60f, 30, Assets.getAssets().getTexture("TEX_CIRCLE"), 0, 1f, 1/30f, new Vector2(50, 50), 1/7f, Color.CYAN);
        animations.add(a);
    }

    public synchronized void gravityAnim(Vector2 position, int rotation) {
        SpriteAnimation a = new SpriteAnimation(position, 1/60f, 60, Assets.getAssets().getTexture("TEX_ARROW"), rotation, 1f, 1/60f, new Vector2(150, 150), 1/30f, Color.CYAN);
        animations.add(a);
    }

    public synchronized void switchtAnim(Vector2 position) {
        SpriteAnimation a = new SpriteAnimation(position, 1/60f, 30, Assets.getAssets().getTexture("TEX_DIAMONT"), 0, 1f, 1/30f, new Vector2(50, 50), 1/7f, Color.CYAN);
        animations.add(a);
    }

    public synchronized void spawnAnim(Vector2 position) {
        SpriteAnimation a = new SpriteAnimation(position, 1/60f, 30, Assets.getAssets().getTexture("TEX_CIRCLE2"), 0, 1f, 1/30f, new Vector2(50, 50), 1/7f, Color.CYAN);
        animations.add(a);
    }
}

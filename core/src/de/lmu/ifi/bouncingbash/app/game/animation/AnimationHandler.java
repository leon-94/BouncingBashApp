package de.lmu.ifi.bouncingbash.app.game.animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;

import java.util.ArrayList;

import de.lmu.ifi.bouncingbash.app.game.Assets;
import de.lmu.ifi.bouncingbash.app.game.Constants;
import de.lmu.ifi.bouncingbash.app.game.Game;

/**
 * Created by Leon on 16.01.2016.
 */
public class AnimationHandler {

    private final String TAG = "AnimationHandler";

    private Game game;

    private ArrayList<BasicAnimation> animations;
    private ArrayList<BasicAnimation> toBeRemoved;

    private float lastContactAnim = 0;

    public AnimationHandler(Game g) {
        game = g;
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

//    public synchronized void contactAnim(Vector2 position, int rotation) {
//        if(Game.getGameTime() - lastContactAnim < .5f) return;
//        lastContactAnim = Game.getGameTime();
//
//        SpriteAnimation a = new SpriteAnimation(position, 1/60f, 30, Assets.getAssets().getTexture("TEX_CIRCLE"), rotation, 1f, 1/30f, new Vector2(50, 50), 1/7f, Color.RED);
//        animations.add(a);
//    }
    public synchronized void contactAnim(Vector2 position, int rotation) {
        if(Game.getGameTime() - lastContactAnim < .2f) return;
        lastContactAnim = Game.getGameTime();

        SpriteAnimation a = new SpriteAnimation(position, 1/60f, 30, Assets.getAssets().getTexture("TEX_CIRCLE"), rotation, 1f, 1/30f, new Vector2(30, 30), 1/7f, Color.GRAY);
        animations.add(a);
    }

    public synchronized void jumpAnim(Vector2 position, Color c) {
        SpriteAnimation a = new SpriteAnimation(position, 1/60f, 30, Assets.getAssets().getTexture("TEX_CIRCLE"), 0, 1f, 1/30f, new Vector2(50, 50), 1/7f, c);
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

    public synchronized void spawnAnim(Vector2 position, Color c) {
        SpriteAnimation a = new SpriteAnimation(position, 1/60f, 30, Assets.getAssets().getTexture("TEX_CIRCLE2"), 0, 1f, 1/30f, new Vector2(50, 50), 1/7f, c);
        animations.add(a);
    }

    public synchronized void wonAnim(Color c) {

        SpriteAnimation a1 = new SpriteAnimation(new Vector2(Constants.WIDTH/2, Constants.HEIGHT/2), 1/60f, 240, Assets.getAssets().getTexture("TEX_DOT"), 0, 0f, -1/120f, new Vector2(Constants.WIDTH, Constants.HEIGHT), 0f, c);
        animations.add(a1);

        SpriteAnimation a = new SpriteAnimation(new Vector2(Constants.WIDTH/2, Constants.HEIGHT/2), 1/60f, 240, Assets.getAssets().getTexture("TEX_YOUWON"), 0, 0f, -1/20f, new Vector2(960, 540), 1/180f, 1.5f, Color.BLACK,
                new Timer.Task() {
                    @Override
                    public void run() {
                        Gdx.app.exit();
                    }
                });
        animations.add(a);
    }

    public synchronized void lostAnim(Color c) {

        SpriteAnimation a1 = new SpriteAnimation(new Vector2(Constants.WIDTH/2, Constants.HEIGHT/2), 1/60f, 240, Assets.getAssets().getTexture("TEX_DOT"), 0, 0f, -1/120f, new Vector2(Constants.WIDTH, Constants.HEIGHT), 0f, c);
        animations.add(a1);

        SpriteAnimation a = new SpriteAnimation(new Vector2(Constants.WIDTH/2, Constants.HEIGHT/2), 1/60f, 180, Assets.getAssets().getTexture("TEX_YOULOST"), 0, 0f, -1/20f, new Vector2(960, 540), 1/120f, 1.5f, Color.BLACK,
                new Timer.Task() {
                    @Override
                    public void run() {
                        Gdx.app.exit();
                    }
                });
        animations.add(a);
    }
}

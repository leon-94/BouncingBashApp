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

    public void upadte(float elapsedTime) {
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
//        AtlasAnimation a = new AtlasAnimation(position, 1/8f, Textures.getTextures().getTextureAtlas("ATLAS_DUST"), false, 1f, 0.2f, new Vector2(128, 128), 0f, Color.WHITE);
//        SpriteAnimation a = new SpriteAnimation(position, 1/40f, 20, Textures.getTextures().getTexture("TEX_DUST"), 0, 1f, 1/20f, new Vector2(50, 50), 1/5f, Color.WHITE);
        SpriteAnimation a = new SpriteAnimation(position, 1/40f, 20, Textures.getTextures().getTexture("TEX_CIRCLE"), rotation, 1f, 1/20f, new Vector2(50, 50), 1/5f, Color.CYAN);
        animations.add(a);
    }

    public void jumpAnim(Vector2 position) {
        //AtlasAnimation a = new AtlasAnimation(position, 1/20f, Textures.ATLAS_CIRCLE, false, 1f, .1f, new Vector2(50, 50), .4f, Color.CYAN);
        SpriteAnimation a = new SpriteAnimation(position, 1/40f, 20, Textures.getTextures().getTexture("TEX_CIRCLE"), 0, 1f, 1/20f, new Vector2(50, 50), 1/5f, Color.CYAN);
        animations.add(a);
    }

    public void gravityAnim(Vector2 position, int rotation) {
        //AtlasAnimation a = new AtlasAnimation(position, 1/20f, Textures.ATLAS_CIRCLE, false, 1f, .1f, new Vector2(50, 50), .4f, Color.CYAN);
        SpriteAnimation a = new SpriteAnimation(position, 1/40f, 40, Textures.getTextures().getTexture("TEX_ARROW"), rotation, 1f, 1/40f, new Vector2(150, 150), 1/20f, Color.CYAN);
        animations.add(a);
    }
}

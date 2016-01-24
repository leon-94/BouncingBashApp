package de.lmu.ifi.bouncingbash.app.game.animation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Leon on 16.01.2016.
 */
public class AtlasAnimation extends BasicAnimation {

    protected Vector2 position;
    protected float[] sizes;
    protected Animation animation;
    protected float stateTime = 0;
    protected boolean repeating ;
    protected float alphaValues[];
    protected Color color;

    public AtlasAnimation(Vector2 position, float frametime, TextureAtlas atlas, boolean repeating,
                          float initAlpha, float fadingFactor, Vector2 initSize, float scalingFactor,
                          Color color) {
        this.repeating = repeating;
        this.position = position;
        this.color = color;

        animation = new Animation(frametime, atlas.getRegions());

        alphaValues = new float[animation.getKeyFrames().length];
        sizes = new float[animation.getKeyFrames().length];

        for(int i = 0; i < alphaValues.length; i++){
            alphaValues[i] = initAlpha - i*fadingFactor;
            sizes[i] = initSize.x * (1 + i*scalingFactor);
        }
    }

    @Override
    public void update(float elapsedTime) {
        stateTime += elapsedTime;
        if(!repeating && animation.isAnimationFinished(stateTime)) {
            done = true;
            onDone();
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        int frameIndex = animation.getKeyFrameIndex(stateTime);
        TextureRegion keyFrame = animation.getKeyFrame(stateTime, true);
        batch.setColor(color.r, color.g, color.b, alphaValues[frameIndex]);
        batch.draw(keyFrame, position.x - sizes[frameIndex] / 2, position.y - sizes[frameIndex] / 2, sizes[frameIndex], sizes[frameIndex]);
        batch.setColor(1, 1, 1, 1);
    }
}

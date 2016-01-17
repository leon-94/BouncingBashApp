package de.lmu.ifi.bouncingbash.app.game.animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import de.lmu.ifi.bouncingbash.app.game.Game;

/**
 * Created by Leon on 16.01.2016.
 */
public class SpriteAnimation extends BasicAnimation {

    protected Vector2 position;
    protected float[] sizes;
    protected float startTime;
    protected float stateTime = 0;
    protected float frametime;
    protected int frames;
    protected int currentFrame = 0;
    protected float alphaValues[];
    protected Color color;
    protected Texture texture;
    protected Sprite sprite;
    protected int rotation;

    public SpriteAnimation(Vector2 position, float frametime, int frames, Texture tex, int rot,
                           float initAlpha, float fadingFactor, Vector2 initSize, float scalingFactor,
                           Color color) {

        this.frames = frames;
        this.frametime = frametime;
        this.position = position;
        this.color = color;
        this.texture = tex;
        this.rotation = rot;

        startTime = Game.getGameTime();
        sprite = new Sprite(tex);

        alphaValues = new float[frames];
        sizes = new float[frames];
        for(int i = 0; i < alphaValues.length; i++){
            alphaValues[i] = initAlpha - i*fadingFactor;
            sizes[i] = initSize.x * (1 + i*scalingFactor);
        }
    }

    @Override
    public void update(float elapsedTime) {
        currentFrame = (int)( (Game.getGameTime() - startTime) / frametime);
        if(currentFrame >= frames) {
            currentFrame = frames-1;
            done = true;
        }
    }

    @Override
    public void render(SpriteBatch batch) {

        batch.setColor(color.r, color.g, color.b, alphaValues[currentFrame]);
        batch.draw(sprite, position.x - sizes[currentFrame] / 2, position.y - sizes[currentFrame] / 2, sizes[currentFrame] / 2, sizes[currentFrame] / 2, sizes[currentFrame], sizes[currentFrame], 1, 1, rotation);
//        sprite.setPosition(position.x - sizes[currentFrame] / 2, position.y - sizes[currentFrame]);
//        sprite.setSize(sizes[currentFrame], sizes[currentFrame]);
//        sprite.setRotation(rotation);
//        sprite.draw(batch);
        batch.setColor(1, 1, 1, 1);

        Gdx.app.log("SpriteAnimation", "render");
        Gdx.app.log("SpriteAnimation", "    position: "+ (position.x - sizes[currentFrame] / 2) +", "+ (position.y - sizes[currentFrame]));
        Gdx.app.log("SpriteAnimation", "    size:     "+ sizes[currentFrame] +", "+ sizes[currentFrame]);
        Gdx.app.log("SpriteAnimation", "    rotation: "+ rotation);
    }
}

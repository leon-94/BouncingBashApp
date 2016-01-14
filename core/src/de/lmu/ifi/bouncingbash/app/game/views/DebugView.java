package de.lmu.ifi.bouncingbash.app.game.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;

import de.lmu.ifi.bouncingbash.app.game.models.GameModel;

/**
 * Created by Michi on 30.12.2015.
 */
public class DebugView implements View {
    public GameModel gameModel;
    private Texture textureBackground;
    private Sprite spriteBackground;
    private SpriteBatch batch;
    final float PIXELS_TO_METERS = 100f;
    public DebugView(GameModel gameModel,SpriteBatch batch)
    {
        this.gameModel= gameModel;
        this.batch=batch;
        setup();
    }
    public void setup()
    {
        textureBackground = new Texture(Gdx.files.internal(gameModel.getMap().getBackGround()));
        spriteBackground = new Sprite(textureBackground);

    }
    public void draw()
    {
        Matrix4 debugMatrix = batch.getProjectionMatrix().cpy().scale(PIXELS_TO_METERS,
                PIXELS_TO_METERS, 0);    }
}

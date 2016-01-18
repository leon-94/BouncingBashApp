package de.lmu.ifi.bouncingbash.app.game.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.lmu.ifi.bouncingbash.app.game.models.GameModel;

/**
 * Created by Michi on 30.12.2015.
 */
public class BackgroundView {
    public GameModel gameModel;
    private Texture textureBackground;
    private Sprite spriteBackground;
    private SpriteBatch batch;
    public BackgroundView(GameModel gameModel,SpriteBatch batch)
    {
        this.gameModel= gameModel;
        this.batch=batch;
        setup();
    }
    public void setup()
    {
        textureBackground = new Texture(Gdx.files.internal("background.png"));
        spriteBackground = new Sprite(textureBackground);

    }
    public void draw()
    {
        batch.draw(spriteBackground, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }
}

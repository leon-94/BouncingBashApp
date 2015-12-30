package de.lmu.ifi.bouncingbash.app.game.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

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
        textureBackground = gameModel.getMap().getBackGround();
        spriteBackground = new Sprite(textureBackground);

    }
    public void drawBackground()
    {
        batch.draw(spriteBackground, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }
}

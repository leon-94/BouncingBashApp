package de.lmu.ifi.bouncingbash.app.game.components.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.lmu.ifi.bouncingbash.app.game.Constants;
import de.lmu.ifi.bouncingbash.app.game.Player;

/**
 * Created by Leon on 19.01.2016.
 */
public class LifeCounter extends UIComponent {

    private final int width = 400;
    private final int height = 50;
    private final int padding = 5;

    private float x, y;
    private Texture backgroundTexture;
    private Sprite backgroundSprite;
    private float w, h;

//    private Player player;
    private Player player;

    public LifeCounter(float x, float y, Player pl) {

        this.player = pl;
        this.x = x;
        this.y = y;

        w = (width - (Constants.MAX_LIVES+1) * padding) / Constants.MAX_LIVES;
        h = height - 2* padding;

        Pixmap p = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        p.setColor(Color.WHITE);
        p.fill();
        texture = new Texture(p);
        sprite = new Sprite(texture);
        sprite.setSize(w, h);

        p = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        p.setColor(Color.BLACK);
        p.fill();
        backgroundTexture = new Texture(p);
        p.dispose();
        backgroundSprite = new Sprite(backgroundTexture);
        backgroundSprite.setSize(width, height);
        backgroundSprite.setPosition(x, y);

    }

    @Override
    public void render(SpriteBatch batch) {

        backgroundSprite.draw(batch);
        for(int i = 0; i < Constants.LIVES; i++) {
            sprite.setPosition(x + (i+1)*padding + i*w, y + padding);
            sprite.setColor(i < player.getLives() ? Color.CYAN : Color.GRAY);
            sprite.draw(batch);
        }
    }
}
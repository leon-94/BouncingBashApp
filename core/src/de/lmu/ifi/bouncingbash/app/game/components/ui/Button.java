package de.lmu.ifi.bouncingbash.app.game.components.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.lmu.ifi.bouncingbash.app.game.Assets;

/**
 * Created by Leon on 12.01.2016.
 */
public class Button extends UIComponent {

    private final String TAG = "Button";

    private final int borderThickness = 0;
    private BitmapFont font;
    private String text;

    public Button(String text, float x, float y, int w, int h) {

        this.text = text;
        this.alphaValue = .5f;

        font = Assets.getAssets().getFont("FONT_CALIBRI_64");

        Pixmap p = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        p.setColor(Color.DARK_GRAY);
        p.fill();
        p.setColor(Color.BLACK);
        p.fillRectangle(0, 0, w, borderThickness);
        p.fillRectangle(0, h - borderThickness, w, borderThickness);
        p.fillRectangle(0, 0, borderThickness, h);
        p.fillRectangle(w-borderThickness, 0, borderThickness, h);
        texture = new Texture(p);
        p.dispose();
        sprite = new Sprite(texture);
        sprite.setPosition(x, y);
    }

    @Override
    public void onClick(float x, float y) {
    }

    @Override
    public void update(float elapsedTime) {

    }

    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);
        font.draw(batch, text, sprite.getX() + 30, sprite.getY() + sprite.getHeight()/2 + font.getCapHeight()/2);
    }
}

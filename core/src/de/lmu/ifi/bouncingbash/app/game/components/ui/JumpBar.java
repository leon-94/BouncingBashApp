package de.lmu.ifi.bouncingbash.app.game.components.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.lmu.ifi.bouncingbash.app.game.Game;
import de.lmu.ifi.bouncingbash.app.game.Constants;

/**
 * Created by Leon on 12.01.2016.
 */
public class JumpBar extends UIComponent {

    private final int width = 400;
    private final int height = 50;
    private final int borderThickness = 5;

    private Texture fillingTex;
    private Sprite filling;

    private float lastJump = 0;

    public JumpBar(float x, float y) {

        Pixmap p = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        p.setColor(Color.WHITE);
        p.fill();
        p.setColor(Color.BLACK);
        p.fillRectangle(0, 0, width, borderThickness);
        p.fillRectangle(0, height - borderThickness, width, borderThickness);
        p.fillRectangle(0, 0, borderThickness, height);
        p.fillRectangle(width-borderThickness, 0, borderThickness, height);
        texture = new Texture(p);
        p.dispose();

        Pixmap p2 = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        p2.setColor(Color.CYAN);
        p2.fill();
        fillingTex = new Texture(p2);
        filling = new Sprite(fillingTex);
        p2.dispose();

        sprite = new Sprite(texture);
        sprite.setX(x);
        sprite.setY(y);

        filling.setX(x + borderThickness);
        filling.setY(y + borderThickness);
        filling.setSize(width-2*borderThickness, height-2*borderThickness);
    }

    @Override
    public void update(float elapsedTime) {

        int max = width-2*borderThickness;
        float percentage = (Game.getGameTime() - lastJump) / Constants.JUMP_FREQ;
        int current = (int)(percentage * max);
        if(current > max) current = max;
        filling.setSize(current, height-2*borderThickness);
/*
        int max = width-2*borderThickness;
        int current = (int)( (Game.getGameTime() - lastJump) * max / Constants.JUMP_FREQ);
        if(current > max) current = max;
        filling.setSize(current, height-2*borderThickness);*/
    }

    public void onJump() {
        lastJump = Game.getGameTime();
    }

    public void render(SpriteBatch batch) {
        super.render(batch);
        filling.draw(batch);
    }
}

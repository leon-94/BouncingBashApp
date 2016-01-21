package de.lmu.ifi.bouncingbash.app.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Leon on 12.01.2016.
 */
public abstract class GameComponent {

    private final String TAG = "GameComponent";

    protected Texture texture;
    protected Sprite sprite;

    protected boolean down = false;

    public void update(float elapsedTime) {

    }

    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public boolean inBounds(int screenX, int screenY) {
        int x = (int)sprite.getX();
        int y = (int)sprite.getY();
        int width = (int)sprite.getWidth();
        int height = (int)sprite.getHeight();

        if(screenX >= x && screenX <= x + width && screenY >= y && screenY <= y + height) return true;
        return false;
    }

    public void onClick(float x, float y) {

    }

    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if(inBounds(screenX, screenY)) {
            down = true;
        }
        return false;
    }

    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if(inBounds(screenX, screenY)) {
            if(down) this.onClick(screenX, screenY);
        }
        down = false;
        return false;
    }
}

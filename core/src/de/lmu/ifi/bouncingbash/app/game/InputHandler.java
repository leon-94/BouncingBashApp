package de.lmu.ifi.bouncingbash.app.game;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

import de.lmu.ifi.bouncingbash.app.game.components.Ball;
import de.lmu.ifi.bouncingbash.app.game.components.GameComponent;

/**
 * Created by Leon on 12.01.2016.
 */
public class InputHandler implements InputProcessor {

    OrthographicCamera camera;
    private ArrayList<de.lmu.ifi.bouncingbash.app.game.components.GameComponent> gameComponents;
    private de.lmu.ifi.bouncingbash.app.game.components.Ball ball;

    public InputHandler(OrthographicCamera c, ArrayList<de.lmu.ifi.bouncingbash.app.game.components.GameComponent> u, Ball ball) {
        camera = c;
        this.ball = ball;
        gameComponents = u;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 translatedPos = camera.unproject(new Vector3(screenX, screenY, 0));
        for(GameComponent g : gameComponents) g.touchDown((int)translatedPos.x, (int)translatedPos.y, pointer, button);
        ball.onJump();
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Vector3 translatedPos = camera.unproject(new Vector3(screenX, screenY, 0));
        for(de.lmu.ifi.bouncingbash.app.game.components.GameComponent g : gameComponents) g.touchUp((int)translatedPos.x, (int)translatedPos.y, pointer, button);
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}

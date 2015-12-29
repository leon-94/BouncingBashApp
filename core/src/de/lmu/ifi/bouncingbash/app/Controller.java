package de.lmu.ifi.bouncingbash.app;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.physics.box2d.Body;

import de.lmu.ifi.bouncingbash.app.game.models.Ball;
import de.lmu.ifi.bouncingbash.app.game.models.GameModel;
import de.lmu.ifi.bouncingbash.app.game.models.JumpStates;

/**
 * Created by Michi on 28.12.2015.
 */
public class Controller implements InputProcessor {
    private GameModel gameModel;
    private Body body;
    public Controller(GameModel gameModel,Body body)
    {
        this.gameModel=gameModel;
        this.body=body;
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
    /**jump**/
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Gdx.app.log("Controller", "pressed Jump");
        /**Ball b = gameModel.getPlayer().getBall();
        Gdx.app.log("Controller", "getJumpSpeed : "+b.getJumpSpeed()+" getOriginalJumpSpeed : "+b.getOriginalJumpSpeed()
                +" getJumpState : "+b.getJumpState()+" getyCoordinates : "+b.getyCoordinates()
                +" getyCoordinatesOriginal : "+b.getyCoordinatesOriginal());
        if(b.getJumpState() != JumpStates.ASCENDING  || b.getJumpState() != JumpStates.DESCENDING   )
        {
            b.setJumpState(JumpStates.ASCENDING);
            b.setyCoordinatesOriginal(b.getyCoordinates());
        }**/
        body.applyForceToCenter(0f,10f,true);
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
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

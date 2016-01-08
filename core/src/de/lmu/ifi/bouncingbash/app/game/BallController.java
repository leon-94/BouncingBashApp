package de.lmu.ifi.bouncingbash.app.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.physics.box2d.Body;

import de.lmu.ifi.bouncingbash.app.game.models.GameModel;

/**
 * Created by Michi on 31.12.2015.
 */

    public class BallController implements InputProcessor {
        private GameModel gameModel;
        private Body body;
        public BallController(GameModel gameModel,Body body)
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
            body.setLinearVelocity(body.getLinearVelocity().x, 100f);


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



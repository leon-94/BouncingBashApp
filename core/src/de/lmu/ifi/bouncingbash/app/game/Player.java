package de.lmu.ifi.bouncingbash.app.game;

import com.badlogic.gdx.graphics.Color;

import de.lmu.ifi.bouncingbash.app.game.components.Ball;

/**
 * Created by Leon on 21.01.2016.
 */
public class Player {

    private int lives = Constants.LIVES;
    private Ball ball;

    public int getLives() {
        return lives;
    }
    public void setLives(int lives) {
        this.lives = lives;
    }
    public void loseLife() {
        lives--;
    }

    public Ball getBall() {
        return ball;
    }

    public void setBall(Ball b) {
        ball = b;
    }


    public Player() {

    }
}

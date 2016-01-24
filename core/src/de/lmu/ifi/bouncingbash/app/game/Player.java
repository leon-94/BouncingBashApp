package de.lmu.ifi.bouncingbash.app.game;

import com.badlogic.gdx.graphics.Color;

import de.lmu.ifi.bouncingbash.app.game.components.Ball;
import de.lmu.ifi.bouncingbash.app.game.components.Item;

/**
 * Created by Leon on 21.01.2016.
 */
public class Player {

    private int lives = Constants.LIVES;
    private Color color;
    private Ball ball;
    private Item item;

    public Player(Color c) {
        color = c;
    }

    public Color getColor() {
        return color;
    }
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
    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}

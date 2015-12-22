package de.lmu.ifi.bouncingbash.app.game.models;


/**
 * Created by Michael on 11.12.2015.
 */
public class Player {
    private Ball ball;
    private Map map;

    public Player(Map map)
    {
        this.map = map;
        ball = new Ball();
    }
}

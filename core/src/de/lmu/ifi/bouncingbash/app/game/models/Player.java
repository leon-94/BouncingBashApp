package de.lmu.ifi.bouncingbash.app.game.models;


/**
 * Created by Michael on 11.12.2015.
 */
public class Player {
    private Ball ball;
    /**als Identifikation für denjeningen der das Spiel angeboten/gestartet hat**/
    private boolean host;
    public Player()
    {
        ball = new Ball();
    }

    public boolean isHost() {
        return host;
    }

    public void setHost(boolean host) {
        this.host = host;
    }
    public Ball getBall() {
        return ball;
    }

    public void setBall(Ball ball) {
        this.ball = ball;
    }
}

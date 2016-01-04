package de.lmu.ifi.bouncingbash.app.game.models;

import java.util.ArrayList;

/**
 * Created by Michael on 11.12.2015.
 */
public class GameModel {

    private Map map;
    /**Player who sends the game Request**/
    private Player player1;
    private Player player2;

    public GameModel()
    {
        map = new Map();
        player1.setHost(true);
    }

    public Player getPlayer1() {
        return player1;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

}

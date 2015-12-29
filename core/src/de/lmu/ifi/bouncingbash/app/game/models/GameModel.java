package de.lmu.ifi.bouncingbash.app.game.models;

import java.util.ArrayList;

/**
 * Created by Michael on 11.12.2015.
 */
public class GameModel {
    private Map map = new Map();
    private Player player;
    private Player enemy;

    public GameModel()
    {
        map = new Map();
        player =  new Player(map);
        enemy =  new Player(map);

    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getEnemy() {
        return enemy;
    }

    public void setEnemy(Player enemy) {
        this.enemy = enemy;
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

}

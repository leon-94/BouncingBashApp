package de.lmu.ifi.bouncingbash.app.game.models;

import java.util.ArrayList;

/**
 * Created by Michael on 11.12.2015.
 */
public class GameModel {
    private Map map = new Map();
    private ArrayList<Player> playerArrayList = new ArrayList<Player>();

    public GameModel()
    {
        playerArrayList.add(new Player(map));
    }
}

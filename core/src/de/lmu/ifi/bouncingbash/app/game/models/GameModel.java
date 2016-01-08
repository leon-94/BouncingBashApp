package de.lmu.ifi.bouncingbash.app.game.models;

/**
 * Created by Michael on 11.12.2015.
 */
public class GameModel {

    private de.lmu.ifi.bouncingbash.app.game.models.Map map;
    /**Player who sends the game Request**/
    private Player player1;
    private Player player2;

    public GameModel()
    {
        map = new de.lmu.ifi.bouncingbash.app.game.models.Map();
        player1 = new Player();
        player2 = new Player();
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

    public de.lmu.ifi.bouncingbash.app.game.models.Map getMap() {
        return map;
    }

    public void setMap(de.lmu.ifi.bouncingbash.app.game.models.Map map) {
        this.map = map;
    }

}

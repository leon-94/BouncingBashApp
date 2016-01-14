package de.lmu.ifi.bouncingbash.app.game;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import de.lmu.ifi.bouncingbash.app.game.models.GameModel;
import de.lmu.ifi.bouncingbash.app.game.models.Item;

/**
 * Created by Michi on 14.01.2016.
 */
public class ItemSpawnController {
    private GameModel gameModel;
    private boolean spawn=true;

    public ItemSpawnController(GameModel gameModel)
    {
        this.gameModel = gameModel;
    }
    /**Methode fürs itemSpawnen:
     * Startet einen Timertask mit einer zufälligen Zeit zwischen 5 und 10s
     * Nach dem die Zeit vergangen ist wird der ItemArraylist ein Item hinzugefügt TODO items werden ausgewürfelt
     *
     * **/
    public void spawnItems()
    {
        if(spawn) {
            spawn = false;
            int random = randInt(5,10);
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    //if (gameModel.getPlayer1().isHost()) {
                        gameModel.getMap().getItemArrayList().add(new Item());
                        spawn = true;
                   // }
                }
            };
            Timer timer = new Timer();
            timer.schedule(timerTask, randInt(5, 10)*1000);

        }
    }
    public boolean isSpawn() {
        return spawn;
    }

    public void setSpawn(boolean spawn) {
        this.spawn = spawn;
    }
    private static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }
}

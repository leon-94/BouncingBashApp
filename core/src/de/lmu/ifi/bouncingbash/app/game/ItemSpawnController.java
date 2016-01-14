package de.lmu.ifi.bouncingbash.app.game;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import de.lmu.ifi.bouncingbash.app.game.models.GameModel;
import de.lmu.ifi.bouncingbash.app.game.models.Item;
import de.lmu.ifi.bouncingbash.app.game.models.Platform;

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
     *Items werden auf Platformen gesetzt
     * **/
    public void spawnItems()
    {
        if(spawn) {
            spawn = false;
            int random = randInt(5,10);
            System.out.println("Random "+random);
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    //if (gameModel.getPlayer1().isHost()) {
                    //zufällige Platform zum spawnen aussuchen
                   /** int platforms=0;
                    for(Platform p: gameModel.getMap().getPlatformArrayList())
                    {
                        platforms++;
                    }
                    int randomPlatformNumber = randInt(0,platforms);**/
                    Platform randomPlatform = gameModel.getMap().getPlatformArrayList().get(0);
                    Item item = new Item();
                    item.setX(randomPlatform.getX());
                    item.setY(randomPlatform.getY());
                    System.out.println("spawn Item");
                        gameModel.getMap().getItemArrayList().add(item);
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

package de.lmu.ifi.bouncingbash.app.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;

import de.lmu.ifi.bouncingbash.app.game.models.Item;
import de.lmu.ifi.bouncingbash.app.game.models.Platform;
import de.lmu.ifi.bouncingbash.app.game.models.GameModel;


/**
 * Created by Michi on 31.12.2015.
 */
//TODO Platformen in model generieren, enmyplayer setzen. items generieren sollte nur von player 1 gemacht werden
public class GameController {

    private GameModel gameModel;

    public GameController()
    {
        gameModel = new GameModel();
        levelOne();
    }
    /**Anzahl der Platformen außer MainPlatform zufällig generieren nur ne Idee kann auch fest gemacht werden**/
    private void randomPlatformGenerator()
    {
        int p = MathUtils.random(0,6);
        for(int i = p; p>=0;i++)
        {
            gameModel.getMap().getPlatformArrayList().add(new Platform());
        }
    }
    /**TODO Items müssen noch gesendet werden**/
    public void spawnItems()
    {
        if(gameModel.getPlayer1().isHost()) {
            gameModel.getMap().getItemArrayList().add(new Item());
        }
    }
    public GameModel getGameModel() {
        return gameModel;
    }

    private void levelOne()
    {
        int screenWidth =Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();
        gameModel.getMap().getPlatformArrayList().add(new Platform(screenWidth/4,
                screenWidth/2,
                screenHeight/2));
        gameModel.getMap().getPlatformArrayList().add(new Platform(screenWidth/4,
                screenWidth/2,
                0));
        gameModel.getMap().getPlatformArrayList().add(new Platform(screenWidth/4,
                0,
                0));
        gameModel.getMap().getPlatformArrayList().add(new Platform(screenWidth/4,
                0,
                screenHeight/2));
        System.out.println("Height: "+screenHeight+" Width: "+screenWidth);
    }

}

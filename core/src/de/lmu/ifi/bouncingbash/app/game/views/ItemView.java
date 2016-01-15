package de.lmu.ifi.bouncingbash.app.game.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import java.util.HashMap;

import de.lmu.ifi.bouncingbash.app.game.ItemSpawnController;
import de.lmu.ifi.bouncingbash.app.game.models.GameModel;
import de.lmu.ifi.bouncingbash.app.game.models.Item;
import de.lmu.ifi.bouncingbash.app.game.models.Platform;

/**
 * Created by Michi on 14.01.2016.
 */
public class ItemView implements BodyView{
    public GameModel gameModel;
    private Sprite spriteItem;
    final float PIXELS_TO_METERS = 100f;
    private SpriteBatch batch;
    private World world;
    private ItemSpawnController itemSpawnController;
    private  HashMap<Sprite, Body> itemBodys = new HashMap<Sprite, Body>();

    public ItemView(GameModel gameModel,World world,SpriteBatch batch)
    {
        this.gameModel= gameModel;
        this.batch=batch;
        this.world=world;
        //einmal am anfang ausführen fals am anfang schon items gesetzt werden sollen
        setup();
        itemSpawnController = new ItemSpawnController(gameModel);

    }

    public void setup()
    {
        for(Item i : gameModel.getMap().getItemArrayList())
        {
            //wenn noch kein sprite gesetzt wurde setze es
            if(i.getSprite()==null) {
                Sprite s = new Sprite(new Texture(Gdx.files.internal(i.getType().getName())));
                i.setSprite(s);
                spriteItem = i.getSprite();
                spriteItem.setPosition(i.getX(), i.getY());


                BodyDef bodyDef = new BodyDef();
                bodyDef.type = BodyDef.BodyType.StaticBody;
                bodyDef.position.set(
                        (spriteItem.getX() + spriteItem.getWidth() / 2) / PIXELS_TO_METERS,
                        (spriteItem.getY() + spriteItem.getHeight() / 2) / PIXELS_TO_METERS);
                Body b = world.createBody(bodyDef);
                getBodys().put(spriteItem, b);

                PolygonShape shape = new PolygonShape();
                shape.setAsBox(
                        ((spriteItem.getWidth() / PIXELS_TO_METERS) / 2),
                        (spriteItem.getHeight() / PIXELS_TO_METERS) / 2);


                FixtureDef fixtureDef = new FixtureDef();
                fixtureDef.shape = shape;
                fixtureDef.density = 1f;

                Fixture fixture2 = b.createFixture(fixtureDef);


                shape.dispose();
            }

        }
    }
    public void draw()
    {
        itemSpawnController.spawnItems();
        setup();
        for(Item i : gameModel.getMap().getItemArrayList()) {

            batch.draw(i.getSprite(),
                    i.getSprite().getX()
                    , i.getSprite().getY()
                   );
            Body body = getBodys().get(i.getSprite());
            i.getSprite().setPosition((body.getPosition().x * PIXELS_TO_METERS) - i.getSprite().
                            getWidth() / 2,
                    (body.getPosition().y * PIXELS_TO_METERS) - i.getSprite().getHeight() / 2);
        }

    }


    @Override
    public HashMap<Sprite, Body> getBodys() {
        return itemBodys;
    }
}

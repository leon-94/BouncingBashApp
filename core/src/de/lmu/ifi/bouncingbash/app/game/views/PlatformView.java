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

import java.util.ArrayList;

import de.lmu.ifi.bouncingbash.app.game.models.GameModel;
import de.lmu.ifi.bouncingbash.app.game.models.Platform;

/**
 * Created by Michi on 30.12.2015.
 */
public class PlatformView {
    public GameModel gameModel;
    private Texture texturePlatform;
    private Sprite spritePlatform;
    final float PIXELS_TO_METERS = 100f;
    private Body platformBody;
    private SpriteBatch batch;
    private World world;
    private ArrayList<Body> platformBodys = new ArrayList<Body>();

    public PlatformView(GameModel gameModel,World world,SpriteBatch batch)
    {
        this.gameModel= gameModel;
        this.batch=batch;
        this.world=world;
        texturePlatform = new Texture(Gdx.files.internal("platform.png"));
        setupMainPlatform();
    }
    /**texture, sprite und body der MainPlatform definiert**/
    public void setupMainPlatform()
    {
        spritePlatform = new Sprite(texturePlatform);
        spritePlatform.setPosition(gameModel.getMap().getMainPlatform().getHeight(), 0);
        spritePlatform.setSize(gameModel.getMap().getMainPlatform().getWidth(),
                gameModel.getMap().getMainPlatform().getHeight());

        BodyDef bodyDef2 = new BodyDef();
        bodyDef2.type = BodyDef.BodyType.StaticBody;
        bodyDef2.position.set((
                        spritePlatform.getX() + spritePlatform.getWidth() + gameModel.getMap().getMainPlatform().getHeight()) / 2 / PIXELS_TO_METERS,
                spritePlatform.getY() / PIXELS_TO_METERS);
        platformBody = world.createBody(bodyDef2);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(
                ((spritePlatform.getWidth()/ PIXELS_TO_METERS) / 2 ),
                (spritePlatform.getHeight() / PIXELS_TO_METERS)  );

        FixtureDef fixtureDef2 = new FixtureDef();
        fixtureDef2.shape = shape;
        fixtureDef2.density = 1f;

        Fixture fixture2 = platformBody.createFixture(fixtureDef2);


        shape.dispose();
    }

    public void drawMainPlatform()
    {
        batch.draw(spritePlatform,
                spritePlatform.getX()
                , spritePlatform.getY(),
                gameModel.getMap().getMainPlatform().getWidth(),
                gameModel.getMap().getMainPlatform().getHeight());

    }
    public void setupPlatforms()
    {
        for(Platform p : gameModel.getMap().getPlatformArrayList())
        {

            spritePlatform = p.getSprite();
            spritePlatform.setPosition(p.getHeight()+p.getY(), p.getX());
            spritePlatform.setSize(p.getWidth(), p.getHeight());

            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set((
                            spritePlatform.getX() + spritePlatform.getWidth() + gameModel.getMap().getMainPlatform().getHeight()) / 2 / PIXELS_TO_METERS,
                    spritePlatform.getY() / PIXELS_TO_METERS);
            Body b = world.createBody(bodyDef);
            platformBodys.add(b);

            PolygonShape shape = new PolygonShape();
            shape.setAsBox(
                    ((spritePlatform.getWidth()/ PIXELS_TO_METERS) / 2 ),
                    (spritePlatform.getHeight() / PIXELS_TO_METERS)  );

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = 1f;

            Fixture fixture2 = b.createFixture(fixtureDef);


            shape.dispose();
        }
    }
    public void drawPlatforms()
    {
        for(Platform p : gameModel.getMap().getPlatformArrayList()) {
            batch.draw(p.getSprite(),
                    p.getSprite().getX()
                    , p.getSprite().getY(),
                    p.getWidth(),
                    p.getHeight());
        }

    }
    public Body getPlatformBody() {
        return platformBody;
    }
    public ArrayList<Body> getPlatformBodys() {
        return platformBodys;
    }

}

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
import java.util.HashMap;

import de.lmu.ifi.bouncingbash.app.game.models.Entity;
import de.lmu.ifi.bouncingbash.app.game.models.GameModel;
import de.lmu.ifi.bouncingbash.app.game.models.Platform;

/**
 * Created by Michi on 30.12.2015.
 */
public class PlatformView implements BodyView{
    public GameModel gameModel;
    private Sprite spritePlatform;
    final float PIXELS_TO_METERS = 100f;
    private Body platformBody;
    private SpriteBatch batch;
    private World world;
    private  HashMap<Entity, Body> platformBodys = new HashMap<Entity, Body>();

    public PlatformView(GameModel gameModel,World world,SpriteBatch batch)
    {
        this.gameModel= gameModel;
        this.batch=batch;
        this.world=world;
        setup();
    }

    public void setup()
    {
        for(Platform p : gameModel.getMap().getPlatformArrayList())
        {

         Sprite s = new Sprite(new Texture(Gdx.files.internal(p.getTexture())));
         p.setSprite(s);
        spritePlatform = p.getSprite();
        spritePlatform.setPosition(p.getX(), p.getY());
        spritePlatform.setSize(p.getWidth(), p.getHeight());

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(
        (spritePlatform.getX() + spritePlatform.getWidth() /2 )/ PIXELS_TO_METERS,
        (spritePlatform.getY() + spritePlatform.getHeight() /2 )/ PIXELS_TO_METERS);
        Body b = world.createBody(bodyDef);
        getBodys().put(p, b);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(
        ((spritePlatform.getWidth()/ PIXELS_TO_METERS) /2 ),
        (spritePlatform.getHeight() / PIXELS_TO_METERS)/2  );


        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;

        Fixture fixture2 = b.createFixture(fixtureDef);


        shape.dispose();
        }

    }
    public void draw()
    {

            for(Platform p : gameModel.getMap().getPlatformArrayList()) {
                batch.draw(p.getSprite(),
                        p.getSprite().getX()
                        , p.getSprite().getY(),
                        p.getWidth(),
                        p.getHeight());
                Body body = getBodys().get(p);
                p.getSprite().setPosition((body.getPosition().x * PIXELS_TO_METERS) - p.getSprite().
                                getWidth() / 2,
                        (body.getPosition().y * PIXELS_TO_METERS) - p.getSprite().getHeight() / 2);
            }


    }


    @Override
    public HashMap<Entity, Body> getBodys() {
        return platformBodys;
    }
}

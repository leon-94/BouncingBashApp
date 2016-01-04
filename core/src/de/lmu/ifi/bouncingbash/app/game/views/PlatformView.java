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

import de.lmu.ifi.bouncingbash.app.game.models.GameModel;

/**
 * Created by Michi on 30.12.2015.
 */
public class PlatformView {
    public GameModel gameModel;
    private Texture texturePlatform;
    private Sprite spritePlatform;
    final float PIXELS_TO_METERS = 100f;
    private Body body2;
    private SpriteBatch batch;
    private World world;
    public PlatformView(GameModel gameModel,World world,SpriteBatch batch)
    {
        this.gameModel= gameModel;
        this.batch=batch;
        this.world=world;
        setupMainPlatform();
    }
    /**texture, sprite und body der MainPlatform definiert**/
    public void setupMainPlatform()
    {
        texturePlatform = new Texture(Gdx.files.internal("platform.png"));

        spritePlatform = new Sprite(texturePlatform);
        spritePlatform.setPosition(gameModel.getMap().getMainPlatform().getHeight(), 0);
        spritePlatform.setSize(gameModel.getMap().getMainPlatform().getWidth(),
                gameModel.getMap().getMainPlatform().getHeight());

        BodyDef bodyDef2 = new BodyDef();
        bodyDef2.type = BodyDef.BodyType.StaticBody;
        bodyDef2.position.set((
                        spritePlatform.getX() + spritePlatform.getWidth() + gameModel.getMap().getMainPlatform().getHeight()) / 2 / PIXELS_TO_METERS,
                spritePlatform.getY() / PIXELS_TO_METERS);
        body2 = world.createBody(bodyDef2);

        PolygonShape shape2 = new PolygonShape();
        shape2.setAsBox(
                ((spritePlatform.getWidth()/ PIXELS_TO_METERS) / 2 ),
                (spritePlatform.getHeight() / PIXELS_TO_METERS)  );

        FixtureDef fixtureDef2 = new FixtureDef();
        fixtureDef2.shape = shape2;
        fixtureDef2.density = 1f;

        Fixture fixture2 = body2.createFixture(fixtureDef2);


        shape2.dispose();
    }

    public void drawMainPlatform()
    {
        batch.draw(spritePlatform,
                spritePlatform.getX()
                , spritePlatform.getY(),
                gameModel.getMap().getMainPlatform().getWidth(),
                gameModel.getMap().getMainPlatform().getHeight());

    }

}

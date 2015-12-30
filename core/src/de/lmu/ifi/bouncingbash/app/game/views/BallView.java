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

import de.lmu.ifi.bouncingbash.app.Controller;
import de.lmu.ifi.bouncingbash.app.game.models.GameModel;

/**
 * Created by Michi on 30.12.2015.
 */
public class BallView {
    public GameModel gameModel;
    private Texture textureBall;
    private Sprite spriteBall;
    final float PIXELS_TO_METERS = 100f;
    private Body body1;
    private SpriteBatch batch;
    private World world;
    public BallView(GameModel gameModel,World world,SpriteBatch batch)
    {
        this.gameModel= gameModel;
        this.batch=batch;
        this.world=world;
        setupBall();

    }
    public void setupBall()
    {
        textureBall = gameModel.getPlayer().getBall().getTexture();

        spriteBall = gameModel.getPlayer().getBall().getSprite();
        spriteBall.setPosition(Gdx.graphics.getWidth() / 2 - spriteBall.getWidth() / 2,
                Gdx.graphics.getHeight() / 2);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(
                (spriteBall.getX() +spriteBall.getWidth() )/PIXELS_TO_METERS,
                spriteBall.getY()/PIXELS_TO_METERS );
        body1= world.createBody(bodyDef);
        //TODO circle shape
        PolygonShape shape1 = new PolygonShape();
        shape1.setAsBox(
                (spriteBall.getWidth()) / PIXELS_TO_METERS / 2,
                (spriteBall.getHeight()) / PIXELS_TO_METERS / 2);


        FixtureDef fixtureDef1 = new FixtureDef();
        fixtureDef1.shape = shape1;
        fixtureDef1.density = 1f;


        Fixture fixture = body1.createFixture(fixtureDef1);
        shape1.dispose();
        /**setze die steuerung des Balls**/
        Controller ballController = new Controller(gameModel,body1);
        Gdx.input.setInputProcessor(ballController);

    }

    public void drawBall()
    {
        spriteBall.setPosition(
                (body1.getPosition().x * PIXELS_TO_METERS) - spriteBall.getWidth() / 2,
                (body1.getPosition().y * PIXELS_TO_METERS) - spriteBall.getHeight() / 2);
        batch.draw(spriteBall, spriteBall.getX(), spriteBall.getY());
    }

    public void roll()
    {
        float adjustedY = Gdx.input.getAccelerometerY();
        body1.setLinearVelocity(adjustedY, 0);


    }
}

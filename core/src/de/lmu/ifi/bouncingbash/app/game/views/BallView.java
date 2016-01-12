package de.lmu.ifi.bouncingbash.app.game.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;


import de.lmu.ifi.bouncingbash.app.game.BallController;
import de.lmu.ifi.bouncingbash.app.game.models.GameModel;

/**
 * Created by Michi on 30.12.2015.
 */
public class BallView {
    public GameModel gameModel;
    private Texture textureBall;
    private Sprite spriteBall;
    final float PIXELS_TO_METERS = 100f;
    private Body ballBody;
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
        textureBall = new Texture(Gdx.files.internal(gameModel.getPlayer1().getBall().getTexture()));

        spriteBall = new Sprite(textureBall);
        spriteBall.setPosition(Gdx.graphics.getWidth() / 2 - spriteBall.getWidth() / 2,
                Gdx.graphics.getHeight() / 2);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(
                (spriteBall.getX() +spriteBall.getWidth() )/PIXELS_TO_METERS,
                spriteBall.getY()/PIXELS_TO_METERS );
        ballBody = world.createBody(bodyDef);
        //TODO circle shape
        CircleShape circleShape= new CircleShape();
        circleShape.setRadius(  (spriteBall.getHeight()) / PIXELS_TO_METERS / 2);


        FixtureDef fixtureDef1 = new FixtureDef();
        fixtureDef1.shape = circleShape;
        fixtureDef1.density = 1;
        fixtureDef1.friction = 0.5f;
        fixtureDef1.restitution = 0.3f;


        Fixture fixture = ballBody.createFixture(fixtureDef1);
        circleShape.dispose();
        /**setze die steuerung des Balls**/
        BallController ballController = new BallController(gameModel, ballBody);
        Gdx.input.setInputProcessor(ballController);

    }

    public void drawBall()
    {
        spriteBall.setPosition(
                (ballBody.getPosition().x * PIXELS_TO_METERS) - spriteBall.getWidth() / 2,
                (ballBody.getPosition().y * PIXELS_TO_METERS) - spriteBall.getHeight() / 2);
        spriteBall.setRotation(ballBody.getAngle()* MathUtils.radiansToDegrees);
        batch.draw(spriteBall, spriteBall.getX(), spriteBall.getY());
    }

    public void roll()
    {
        float adjustedY = Gdx.input.getAccelerometerY();
        ballBody.setLinearVelocity(adjustedY, 0);


    }
    public Body getBallBody() {
        return ballBody;
    }
}

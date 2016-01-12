package de.lmu.ifi.bouncingbash.app.game.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.eclipsesource.json.JsonObject;


import java.util.HashMap;

import de.lmu.ifi.bouncingbash.app.game.BallController;
import de.lmu.ifi.bouncingbash.app.game.Transmittable;
import de.lmu.ifi.bouncingbash.app.game.models.GameModel;

/**
 * Created by Michi on 30.12.2015.
 */
public class BallView implements Transmittable {

    private String id;

    public GameModel gameModel;
    private Texture textureBall;
    private Sprite spriteBall;
    final float PIXELS_TO_METERS = 100f;
    private Body body1;
    private SpriteBatch batch;
    private World world;
    private boolean controllable;
    public BallView(boolean isHost, boolean controllable, GameModel gameModel,World world,SpriteBatch batch)
    {
        this.gameModel= gameModel;
        this.batch=batch;
        this.world=world;
        this.id = isHost ? "ball1" : "ball2";
        this.controllable = controllable;
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
        body1= world.createBody(bodyDef);
        //TODO circle shape
        CircleShape circleShape= new CircleShape();
        circleShape.setRadius(  (spriteBall.getHeight()) / PIXELS_TO_METERS / 2);


        FixtureDef fixtureDef1 = new FixtureDef();
        fixtureDef1.shape = circleShape;
        fixtureDef1.density = 1;
        fixtureDef1.friction = 0.5f;
        fixtureDef1.restitution = 0.3f;


        Fixture fixture = body1.createFixture(fixtureDef1);
        circleShape.dispose();
        /**setze die steuerung des Balls**/
        if(controllable) {
            BallController ballController = new BallController(gameModel,body1);
            Gdx.input.setInputProcessor(ballController);
        }

    }

    public void drawBall()
    {
        spriteBall.setPosition(
                (body1.getPosition().x * PIXELS_TO_METERS) - spriteBall.getWidth() / 2,
                (body1.getPosition().y * PIXELS_TO_METERS) - spriteBall.getHeight() / 2);
        spriteBall.setRotation(body1.getAngle()* MathUtils.radiansToDegrees);
        batch.draw(spriteBall, spriteBall.getX(), spriteBall.getY());
    }

    public void roll()
    {
        if(controllable) {
            float adjustedY = Gdx.input.getAccelerometerY();
            body1.setLinearVelocity(adjustedY, 0);
        }

    }

    @Override
    public void setId(String _id) { id = _id; }

    @Override
    public String getId() { return id; }

    @Override
    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.add("id", this.id);
        obj.add("posx", body1.getPosition().x);
        obj.add("posy", body1.getPosition().y);
        obj.add("speedx", body1.getLinearVelocity().x);
        obj.add("speedy", body1.getLinearVelocity().y);

        return obj;
    }

    @Override
    public void fromJson(JsonObject obj) {
        System.out.println("BallView#fromJson");

        Vector2 position = body1.getPosition();
        System.out.println("position");
        System.out.println("before: "+ position.x +", "+ position.y);
        float x = obj.getFloat("posx", position.x);
        float y = obj.getFloat("posy", position.y);
        body1.setTransform(x, y, body1.getAngle());
        position = body1.getPosition();
        System.out.println("after: " + position.x + ", " + position.y);

        Vector2 velocity = body1.getLinearVelocity();
        float speedx = obj.getFloat("speedx", velocity.x);
        float speedy = obj.getFloat("speedy", velocity.y);
        body1.setLinearVelocity(speedx, speedy);
    }
}

package de.lmu.ifi.bouncingbash.app.game.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import de.lmu.ifi.bouncingbash.app.game.Game;
import de.lmu.ifi.bouncingbash.app.game.*;

/**
 * Created by Leon on 17.01.2016.
 */
public class Switch extends PhysicsObject {

    private float lastToggle = 0;

    public Switch(Game g, World w, float x, float y) {
        super(g, w);


//        Pixmap p = new Pixmap(50, 50, Pixmap.Format.RGBA8888);
//        p.setColor(Color.GRAY);
//        p.fill();
//        p.setColor(Color.BLACK);
//        p.fillRectangle(10, 10, 30, 30);
//        texture = new Texture(p);
//        p.dispose();

        texture = Assets.getAssets().getTexture("TEX_SWITCH");

        sprite = new Sprite(texture);
        sprite.setSize(130, 130);
        sprite.setPosition(x, y);

        // Now create a BodyDefinition.  This defines the physics objects type and position in the simulation
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        // We are going to use 1 to 1 dimensions.  Meaning 1 in physics engine is 1 pixel
        // Set our body to the same position as our sprite
        bodyDef.position.set( (sprite.getX() + sprite.getWidth()/2 ) / Constants.PIXELS_TO_METERS, (sprite.getY() + sprite.getHeight()/2 ) / Constants.PIXELS_TO_METERS);

        // Create a body in the world using our definition
        body = world.createBody(bodyDef);



        Vector2[] vertices = new Vector2[4];
//        vertices[0] = new Vector2(0, (sprite.getHeight()/2 ) / Constants.PIXELS_TO_METERS);
//        vertices[1] = new Vector2((sprite.getWidth()/2 ) / Constants.PIXELS_TO_METERS, (sprite.getHeight() ) / Constants.PIXELS_TO_METERS);
//        vertices[2] = new Vector2((sprite.getWidth() ) / Constants.PIXELS_TO_METERS, (sprite.getHeight()/2 ) / Constants.PIXELS_TO_METERS);
//        vertices[3] = new Vector2((sprite.getWidth()/2 ) / Constants.PIXELS_TO_METERS, 0);

        vertices[0] = new Vector2(-1*sprite.getWidth()/ 2 / Constants.PIXELS_TO_METERS, 0);
        vertices[1] = new Vector2(0, sprite.getHeight()/ 2 / Constants.PIXELS_TO_METERS);
        vertices[2] = new Vector2(sprite.getWidth()/ 2 / Constants.PIXELS_TO_METERS, 0);
        vertices[3] = new Vector2(0, -1*sprite.getHeight()/ 2 / Constants.PIXELS_TO_METERS);
        PolygonShape shape = new PolygonShape();
        shape.set(vertices);
//        shape.setAsBox(sprite.getWidth() / 2 / Constants.PIXELS_TO_METERS, sprite.getHeight() / 2 / Constants.PIXELS_TO_METERS);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0.4f;

        body.createFixture(fixtureDef);
        shape.dispose();
    }

    @Override
    public void onCollision(Contact contact, Vector2 contactpoint, Body a, Body b) {
        if(a != body && b != body) return;
        Gdx.app.log("SWITCH", "onCollision");

        if( (a == game.myBall.getBody() || a == game.otherBall.getBody() ||
                b == game.myBall.getBody() || b == game.otherBall.getBody())
                && Game.getGameTime()-lastToggle > Constants.GRAVITY_TOGGLE_COOLDOWN) {
            game.onToggleGravity();
            game.animationHandler.switchtAnim(new Vector2(sprite.getX() + sprite.getWidth()/2, sprite.getY() + sprite.getHeight()/2));
            lastToggle = Game.getGameTime();
        }
    }

}

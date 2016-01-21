package de.lmu.ifi.bouncingbash.app.game.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.World;

import de.lmu.ifi.bouncingbash.app.game.Game;
import de.lmu.ifi.bouncingbash.app.game.Constants;

/**
 * Created by Leon on 11.01.2016.
 */
public abstract class PhysicsObject extends GameComponent{

    protected Game game;
    protected World world;
    protected Body body;

    public PhysicsObject(Game g,World w) {
        world = w;
        game = g;
    }

    public void update(float elapsedTime) {

    }

    public void onCollision(Contact contact, Vector2 contactpoint, Body a, Body b) {
        if(a != body && b != body) return;
    }

    public void render(SpriteBatch batch) {
        Vector2 position = body.getPosition();
        sprite.setPosition(position.x * Constants.PIXELS_TO_METERS - sprite.getWidth() / 2, position.y * Constants.PIXELS_TO_METERS - sprite.getHeight() / 2);
        sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
        sprite.setColor(color);
        sprite.draw(batch, alphaValue);
    }

    public Vector2 getPosition() {
        return new Vector2(body.getPosition().x * Constants.PIXELS_TO_METERS, body.getPosition().y * Constants.PIXELS_TO_METERS);
    }

    public void setPosition(float x, float y){
        body.setTransform(x/Constants.PIXELS_TO_METERS, y/Constants.PIXELS_TO_METERS, body.getAngle());
    }
}

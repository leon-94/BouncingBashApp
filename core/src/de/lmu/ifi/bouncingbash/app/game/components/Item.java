package de.lmu.ifi.bouncingbash.app.game.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import de.lmu.ifi.bouncingbash.app.game.Constants;
import de.lmu.ifi.bouncingbash.app.game.Game;

/**
 * Created by Michi on 20.01.2016.
 */
public class Item extends PhysicsObject {

        private int width=100,height=100;
        private boolean spawned=false;

        public Item(Game g, World w, int x, int y) {
            super(g, w);
            Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);

            pixmap.setColor(Color.BLACK);
            pixmap.fillCircle(0,0,width/2);

            texture = new Texture(pixmap);
            sprite = new Sprite(texture);
            sprite.setPosition(x, y);

            // Now create a BodyDefinition.  This defines the physics objects type and position in the simulation
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            // We are going to use 1 to 1 dimensions.  Meaning 1 in physics engine is 1 pixel
            // Set our body to the same position as our sprite
            bodyDef.position.set( (sprite.getX() + sprite.getWidth()/2 ) / Constants.PIXELS_TO_METERS, (sprite.getY() + sprite.getHeight()/2 ) / Constants.PIXELS_TO_METERS);

            // Create a body in the world using our definition
            body = world.createBody(bodyDef);

            PolygonShape shape = new PolygonShape();
            shape.setAsBox(sprite.getWidth() / 2 / Constants.PIXELS_TO_METERS, sprite.getHeight() / 2 / Constants.PIXELS_TO_METERS);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = 1f;
            fixtureDef.friction = 0.4f;

            body.createFixture(fixtureDef);

            // Shape is the only disposable of the lot, so get rid of it
            shape.dispose();
        }
    @Override
    public void onCollision(Contact contact, Vector2 contactpoint, Body a, Body b) {
        if(a != body && b != body) return;
        Gdx.app.log("ITEM", "onCollision");

        if( (a == game.myBall.getBody() || a == game.otherBall.getBody() ||
                b == game.myBall.getBody() || b == game.otherBall.getBody())) {

            game.animationHandler.switchtAnim(new Vector2(sprite.getX() + sprite.getWidth()/2, sprite.getY() + sprite.getHeight()/2));

        }
    }


}

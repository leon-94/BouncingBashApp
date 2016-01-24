package de.lmu.ifi.bouncingbash.app.game.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.eclipsesource.json.JsonObject;

import de.lmu.ifi.bouncingbash.app.game.Game;
import de.lmu.ifi.bouncingbash.app.game.*;

/**
 * Created by Leon on 11.01.2016.
 */
public class Wall extends PhysicsObject {

    int x, y, width, height;

    public Wall(Game g, World w, int x, int y, int width, int height) {
        super(g, w);

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        initWall();
    }

    public Wall(Game g, World w, JsonObject jsonWall) {
        super(g, w);

        this.x = jsonWall.getInt("x", 0);
        this.y = jsonWall.getInt("y", 0);
        this.width = jsonWall.getInt("width", 0);
        this.height = jsonWall.getInt("height", 0);

        initWall();
    }

    public JsonObject toJson() {
        JsonObject jsonWall = new JsonObject();

        jsonWall.add("x", x);
        jsonWall.add("y", y);
        jsonWall.add("width", width);
        jsonWall.add("height", height);

        return jsonWall;
    }


    public void initWall() {

        int borderWidth = 5;

        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);

        pixmap.setColor(Color.CYAN);
        pixmap.fill();
        pixmap.setColor(Color.BLACK);
        pixmap.fillRectangle(0, 0, width, borderWidth);
        pixmap.fillRectangle(0, height - borderWidth, width, height);
        pixmap.fillRectangle(0, 0, borderWidth, height);
        pixmap.fillRectangle(width - borderWidth, 0, width, height);

        texture = new Texture(pixmap);
        pixmap.dispose();
        sprite = new Sprite(texture);
        sprite.setSize(width, height);
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
}

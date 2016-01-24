package de.lmu.ifi.bouncingbash.app.game.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.eclipsesource.json.JsonObject;

import de.lmu.ifi.bouncingbash.app.game.Game;
import de.lmu.ifi.bouncingbash.app.game.*;

/**
 * Created by Leon on 11.01.2016.
 */
public class Ball extends PhysicsObject {

    private final String TAG = "Ball";

    public final int STATE_INIT = 0;
    public final int STATE_SPAWNING = 1;
    public final int STATE_ALIVE = 2;
    public final int STATE_DEAD = 3;
    private int state = STATE_INIT;

//    public final int SUBSTATE_NONE = 0;
//    public final int SUBSTATE_COLORED = 1;
//    private int substate = SUBSTATE_NONE;

    boolean controllable;

    private float spawnTime;

    private Player player;
    public Player getPlayer() { return player; }

    // transmission
    private float m_timestamp = 0;
    private int m_state = 0;
    private float m_posx = 0;
    private float m_posy = 0;
    private float m_angle = 0;
    private float m_speedx = 0;
    private float m_speedy = 0;
    private float m_angularspeed = 0;
    private float m_accelerometer = 0;

    private float lastJump = 0;
    private int transmitJump = 0;
    private float m_lastJump = 0;
    private float m_jump = 0;

    private float lastDeath = 0;
    private int transmitDeath = 0;
    private float m_lastDeath = 0;
    private float m_death = 0;


    public Ball(Game g, World w, Player p, boolean controllable) {
        super(g, w);

        this.player = p;
        this.controllable = controllable;

//        Color color;
//        if(controllable){
//            color = Color.CYAN;
//            alphaValue = 1;
//        }
//        else {
//            color = Color.GREEN;
//            alphaValue = 1f;
//        }
//
//        Pixmap p = new Pixmap((int)Constants.BALL_RADIUS*2, (int)Constants.BALL_RADIUS*2, Pixmap.Format.RGBA8888);
//        p.setColor(color);
//        p.fillCircle(p.getWidth() / 2, p.getHeight() / 2, Constants.BALL_RADIUS);
//        p.setColor(Color.CYAN.BLACK);
//        p.drawCircle(p.getWidth() / 2, p.getHeight() / 2, Constants.BALL_RADIUS);
//        p.drawCircle(p.getWidth() / 2, p.getHeight() / 2, Constants.BALL_RADIUS-1);
//        p.drawLine(Constants.BALL_RADIUS, Constants.BALL_RADIUS * 2, Constants.BALL_RADIUS, 0);
//        texture = new Texture(p);
//        p.dispose();

//        color = controllable ? Color.GREEN : Color.YELLOW;
        color = player.getColor();
        texture = Assets.getAssets().getTexture("TEX_BALL");

        sprite = new Sprite(texture);
        sprite.setSize(Constants.BALL_RADIUS * 2, Constants.BALL_RADIUS * 2);
        sprite.setPosition(0, 0);
        sprite.setOriginCenter();
        sprite.setColor(color);

        // Now create a BodyDefinition.  This defines the physics objects type and position in the simulation
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        // We are going to use 1 to 1 dimensions.  Meaning 1 in physics engine is 1 pixel
        // Set our body to the same position as our sprite
        bodyDef.position.set( (sprite.getX() + sprite.getWidth()/2 ) / Constants.PIXELS_TO_METERS, (sprite.getY() + sprite.getHeight()/2 ) / Constants.PIXELS_TO_METERS);

        // Create a body in the world using our definition
        body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(Constants.BALL_RADIUS / Constants.PIXELS_TO_METERS);

        // FixtureDef is a confusing expression for physical properties
        // Basically this is where you, in addition to defining the shape of the body
        // you also define it's properties like density, restitution and others  we will see shortly
        // If you are wondering, density and area are used to calculate over all mass
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.6f;
        /*
        // turn off collision between balls
        fixtureDef.filter.categoryBits = 0x2;
        fixtureDef.filter.maskBits = 0xD;
        */
        body.createFixture(fixtureDef);
        shape.dispose();
    }

    @Override
    public void update(float elapsedTime) {

        switch(state) {

            case STATE_ALIVE:

                // check for out of bounds
                boolean dead =  (game.getGravityDirection() == -1 && body.getPosition().y < -200 / Constants.PIXELS_TO_METERS ||
                        game.getGravityDirection() == 1 && body.getPosition().y > (200 + Constants.HEIGHT) / Constants.PIXELS_TO_METERS ||
                        body.getPosition().x < -200 / Constants.PIXELS_TO_METERS ||
                        body.getPosition().x > (200 + Constants.WIDTH) / Constants.PIXELS_TO_METERS);

                if(dead) {
                    state = STATE_DEAD;
                    Gdx.app.log(TAG, "DEAD");
                }

                if (controllable) {
                    float adjustedY = Gdx.input.getAccelerometerY();
                    body.applyForceToCenter(inputToForce(adjustedY) * elapsedTime, 0, true);

                    if(dead) onDeath();

                } else {
                    if (Game.reconMethod == Game.RECON_METHOD_PER_UPDATE)
                        reconcilePerUpdate(elapsedTime);
                    else if (Game.reconMethod == Game.RECON_METHOD_INPUT)
                        reconcileInput(elapsedTime);
                }
                break;

            case STATE_SPAWNING:
                spawnTime += elapsedTime;
                if (spawnTime < Constants.BALL_SPAWN_DURATION) {
                    alphaValue = spawnTime / Constants.BALL_SPAWN_DURATION;
                    body.setLinearVelocity(0, 0);
                } else {
                    body.setGravityScale(1);
                    state = STATE_ALIVE;

//                    color = Color.RED;
//                    substate = SUBSTATE_COLORED;
//                    game.onFinishSpawning(body.getPosition().x * Constants.PIXELS_TO_METERS, body.getPosition().y * Constants.PIXELS_TO_METERS);
                }
                break;

            default:
                break;
        }

//        switch (substate) {
//            case SUBSTATE_COLORED:
//                color.r += 1*elapsedTime;
//                if(color.r > 1) color.r = 1;
//                color.g += 1*elapsedTime;
//                if(color.g > 1) color.g = 1;
//                color.b += 1*elapsedTime;
//                if(color.b > 1) color.b = 1;
//                if(color.r == 1 && color.g == 1 && color.b == 1) substate = SUBSTATE_NONE;
//                break;
//
//            default:
//                break;
//        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if(state == STATE_DEAD || state == STATE_INIT) return;
        super.render(batch);
    }

    private float inputToForce(float y) {
        return Constants.ACCEL_STRENGTH *y/Constants.PIXELS_TO_METERS;
        //return 1*y/body.getLinearVelocity().x/Constants.PIXELS_TO_METERS;
    }

    private void onDeath() {
        Gdx.app.log(TAG, "onDeath");
//        if(state != STATE_ALIVE) return;

        transmitDeath = Constants.CONN_REPETITIONS;
        doDeath();
    }
    private void doDeath() {
        Gdx.app.log(TAG, "doDeath");
        lastDeath = Game.getGameTime();
        game.doDeath(this);
    }

    public void onJump() {
        if(state != STATE_ALIVE) return;

        if(Game.getGameTime() - lastJump > Constants.JUMP_FREQ && game.getState() == Game.State.RUNNING) {
            transmitJump = Constants.CONN_REPETITIONS;
            doJump();
        }
    }

    private void doJump() {
        lastJump = Game.getGameTime();

        body.setLinearVelocity(body.getLinearVelocity().x, 0);
        body.applyForceToCenter(0, -1 * Math.signum(world.getGravity().y) * Constants.JUMP_STRENGTH / Constants.PIXELS_TO_METERS, true);

        Vector2 pos = body.getPosition();
        game.onJump(this);
    }

    public void respawn(Vector2 p) {
        if(state == STATE_SPAWNING) return;
        state = STATE_SPAWNING;

        spawnTime = 0;
        alphaValue = 0;

        body.setGravityScale(0);
        body.setTransform(p.x / Constants.PIXELS_TO_METERS, p.y / Constants.PIXELS_TO_METERS, 0);
        body.setLinearVelocity(0, 0);
        body.setAngularVelocity(0);
    }


    // ----------------------------------------------------------------
    // -------------------- reconciliation methods --------------------
    // ----------------------------------------------------------------

    public JsonObject toJson() {
        JsonObject jsonBall = new JsonObject();

        Vector2 position = body.getPosition();
        Vector2 speed = body.getLinearVelocity();

        jsonBall.add("posx", position.x);
        jsonBall.add("posy", position.y);
        jsonBall.add("speedx", speed.x);
        jsonBall.add("speedy", speed.y);
        jsonBall.add("angle", body.getAngle());
        jsonBall.add("anglularspeed", body.getAngularVelocity());
        jsonBall.add("accelerometer", Gdx.input.getAccelerometerY());
        jsonBall.add("state", state);

        // handle events
        if(transmitJump > 0) {
            jsonBall.add("jump", lastJump);
            transmitJump--;
        }
        if(transmitDeath > 0) {
            Gdx.app.log(TAG, "transmitted death");
            jsonBall.add("death", lastDeath);
            transmitDeath--;
        }

        return jsonBall;
    }


    public synchronized void processGameData(JsonObject message) {

        JsonObject jsonBall = (JsonObject) message.get("ball");

        m_timestamp = message.getFloat("timestamp", 0);
        m_posx = jsonBall.getFloat("posx", 0);
        m_posy = jsonBall.getFloat("posy", 0);
        m_angle = jsonBall.getFloat("angle", 0);
        m_speedx = jsonBall.getFloat("speedx", 0);
        m_speedy = jsonBall.getFloat("speedy", 0);
        m_angularspeed = jsonBall.getFloat("anglularspeed", 0);
        m_accelerometer = jsonBall.getFloat("accelerometer", 0);
        m_jump = jsonBall.getFloat("jump", m_lastJump);
        m_death = jsonBall.getFloat("death", m_lastDeath);
        m_state = jsonBall.getInt("state", state);

        if(Game.reconMethod == Game.RECON_METHOD_PER_MESSAGE || Game.reconMethod == Game.RECON_METHOD_INPUT) reconcilePerMessage();
    }

    private synchronized void reconcilePerMessage() {

        // reconcile position
        float m_elapsedTime = Game.getGameTime() - m_timestamp;

        float dx = m_elapsedTime * m_speedx;
        float dy = m_elapsedTime * m_speedy;
        float da = m_elapsedTime * m_angularspeed;

        float c_posx = m_posx + dx;
        float c_posy = m_posy + dy;
        float c_angle = m_angle + da;

        float new_posx = (c_posx + body.getPosition().x) / 2;
        float new_posy = (c_posy + body.getPosition().y) / 2;
        float new_angle = (c_angle + body.getAngle()) / 2;

        // if the deviation is too big
        if(c_posx - body.getPosition().x > Constants.ALLOWED_DEVIATION || c_posy - body.getPosition().y > Constants.ALLOWED_DEVIATION) {
            body.setTransform(c_posx, c_posy, c_angle);
            body.setLinearVelocity(m_speedx, m_speedy);
            body.setAngularVelocity(m_angularspeed);
        }
        else {
            body.setTransform(new_posx, new_posy, new_angle);
            body.setLinearVelocity(m_speedx, m_speedy);
            body.setAngularVelocity(m_angularspeed);
        }

        // events
        if(m_jump != m_lastJump) {
            Gdx.app.log(TAG, "received jump");
            doJump();
            m_lastJump = m_jump;
        }
        if(m_death != m_lastDeath) {
            Gdx.app.log(TAG, "received death");
            doDeath();
            m_lastDeath = m_death;
        }

        // other stuff
//        state = m_state;
    }

    private synchronized void reconcilePerUpdate(float elapsedTimeSinceUpdate) {

        // elapsed time in seconds
        float m_elapsedTime = Game.getGameTime() - m_timestamp;
        Gdx.app.log(TAG, "elapsedTimeSinceUpdate: "+ elapsedTimeSinceUpdate);

        float m_dx = m_elapsedTime * m_speedx;
        float m_dy = m_elapsedTime * m_speedy;
        float m_da = m_elapsedTime * m_angularspeed;

        float c_posx = m_posx + m_dx;
        float c_posy = m_posy + m_dy;
        float c_angle = m_angle + m_da;

        // if the deviation is too big
        if(c_posx - body.getPosition().x > Constants.ALLOWED_DEVIATION || c_posy - body.getPosition().y > Constants.ALLOWED_DEVIATION) {
            body.setTransform(c_posx, c_posy, c_angle);
            body.setLinearVelocity(m_speedx, m_speedy);
            body.setAngularVelocity(m_angularspeed);
        }

        float dx = c_posx - body.getPosition().x;
        float dy = c_posy - body.getPosition().y;
        float da = c_angle - body.getAngle();


        body.setTransform(body.getPosition().x + dx * elapsedTimeSinceUpdate * Constants.RECON_FACTOR,
                body.getPosition().y + dy * elapsedTimeSinceUpdate * Constants.RECON_FACTOR,
                body.getAngle() + da * elapsedTimeSinceUpdate * Constants.RECON_FACTOR);

    }

    private synchronized void reconcileInput(float elapsedTime) {

        float adjustedY = m_accelerometer;
        body.applyForceToCenter(inputToForce(adjustedY) * elapsedTime, 0, true);
    }


    public Body getBody() {
        return body;
    }
}

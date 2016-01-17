package de.lmu.ifi.bouncingbash.app.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Timer;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import java.util.ArrayList;

import de.lmu.ifi.bouncingbash.app.IActivity;
import de.lmu.ifi.bouncingbash.app.IBluetoothService;
import de.lmu.ifi.bouncingbash.app.game.animation.AnimationHandler;
import de.lmu.ifi.bouncingbash.app.game.components.Ball;
import de.lmu.ifi.bouncingbash.app.game.components.Button;
import de.lmu.ifi.bouncingbash.app.game.components.GameComponent;
import de.lmu.ifi.bouncingbash.app.game.components.JumpBar;
import de.lmu.ifi.bouncingbash.app.game.components.PhysicsObject;
import de.lmu.ifi.bouncingbash.app.game.components.Switch;
import de.lmu.ifi.bouncingbash.app.game.components.UIComponent;
import de.lmu.ifi.bouncingbash.app.game.components.Wall;


/***Spiel das gezeichnet wird**/
public class Game extends ApplicationAdapter {

    public Texture TEX_CIRCLE;
    public Texture TEX_ARROW;
    public Texture TEX_SWITCH;
    public Texture TEX_DUST;

//    public TextureAtlas ATLAS_DUST;

    private void load() {
        TEX_CIRCLE = new Texture("circle.png");
        TEX_ARROW = new Texture("arrow.png");
        TEX_SWITCH = new Texture("switch.png");
        TEX_DUST = new Texture("dust.png");

//        ATLAS_DUST = new TextureAtlas("dust/dust.pack");

        Textures.load(this);
    }


    public final static int RECON_METHOD_PER_MESSAGE = 0;
    public final static int RECON_METHOD_PER_UPDATE = 1;
    public final static int RECON_METHOD_INPUT = 2;
    public final static int reconMethod = RECON_METHOD_INPUT;

    private long latencies = 0;
    private int latencyCounter = 0;
    private int flash = 0;

	private static final String TAG = "Game";

	private IBluetoothService btService;
	private IActivity activity;
	private long lastReadingTime = 1000;
	private long lastWritingTime = 1000;
    ClockSynchronizer clockSync;

	private SpriteBatch batch;
    private OrthographicCamera camera;
	private BitmapFont font1;
    private BitmapFont font2;
	private World world;

    private AnimationHandler animationHandler;

    // all components
    public ArrayList<GameComponent> gameComponents;

    // UI components
    public ArrayList<UIComponent> uiComponents;

    private JumpBar jumpBar;

    // physical components
    public ArrayList<PhysicsObject> physicsObjects;

    private Ball ball1;
    private Ball ball2;
    public Ball myBall;
    public Ball otherBall;

	// game state
    public enum State {RUNNING, PAUSING, PAUSED, RESUMING, INIT};
	private State state = State.INIT;
	public State getState() { return state; }
	public void setState(State state) {
		this.state = state;
		Gdx.app.log(TAG, "game state changed to: "+ state);
	}
    public float getGravityDirection() {
        return Math.signum(world.getGravity().y);
    }

	public Game(IActivity act, IBluetoothService bts) {
		super();

		// handle singleplayer debug mode
		if(!GameData.debug_sp) {
			btService = bts;
			btService.setQueueing(true);
			activity = act;
		}
		else {
			GameData.isHost = true;
		}

        gameComponents = new ArrayList<>();
        uiComponents = new ArrayList<>();
        physicsObjects = new ArrayList<>();
	}

	public static float getGameTime() {
        if(GameData.gameStartTime < 1) return 0;
        return (float)(System.currentTimeMillis() - GameData.gameStartTime) / 1000;
	}

	@Override
	public void create () {
        load();

        batch = new SpriteBatch();
        camera = new OrthographicCamera(Constants.WIDTH, Constants.HEIGHT);
        camera.setToOrtho(false, Constants.WIDTH, Constants.HEIGHT);

        world = new World(new Vector2(0, -1 * Constants.GRAVITY), true);

        initUI();
        initGame();

        Gdx.input.setInputProcessor(new InputHandler(camera, gameComponents, myBall));

        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {

                Vector2 contactpoint = contact.getWorldManifold().getPoints()[0];
                Body a = contact.getFixtureA().getBody();
                Body b = contact.getFixtureB().getBody();

                if ((a == myBall.getBody() && b == otherBall.getBody()) ||
                        (a == otherBall.getBody() && b == myBall.getBody())) {

                    Gdx.app.log(TAG, "collision");

                    Vector2 normal = a.getPosition().sub(b.getPosition()).rotate90(1);
                    int rotation = (int)normal.angle();

                    animationHandler.contactAnim(contactpoint.scl(Constants.PIXELS_TO_METERS), rotation);
                }

                for (PhysicsObject p : physicsObjects) p.onCollision(contact, contactpoint, a, b);
            }

            @Override
            public void endContact(Contact contact) {
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
            }
        });

        animationHandler = new AnimationHandler();

		batch = new SpriteBatch();

		font1 = new BitmapFont();
		font1.setColor(Color.RED);
        font1.getData().setScale(2, 2);
        font2 = new BitmapFont();
        font2.setColor(Color.RED);
		font2.getData().setScale(5, 5);

		setState(State.INIT);

        clockSync = new ClockSynchronizer();
        if(GameData.debug_sp) {
            Gdx.app.log(TAG, "DEBUG SINGLEPLAYER MODE");
            startGame();
        }
        else if(GameData.isHost) clockSync.start();

        Gdx.app.log(TAG, "GameData.isHost: "+ GameData.isHost);

        // data exchange
        if(!GameData.debug_sp) {
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    receive();
                }
            }, 0, Constants.CONN_FREQUENCY);
        }

    }

    private void initUI() {

        jumpBar = new JumpBar(5, 5);
        add(jumpBar);

        Button button1 = new Button("Quit", Constants.WIDTH - 200, Constants.HEIGHT - 80, 200, 80){
            @Override
            public void onClick(float x, float y){
                Gdx.app.exit();
            }
        };
        add(button1);
    }

    private void initGame() {

        // create balls
        ball1 = new Ball(this, world, 50, 50, GameData.isHost);
        ball2 = new Ball(this, world, 100, 300, !GameData.isHost);

        // indentify own ball
        if(GameData.isHost) {
            myBall = ball1;
            otherBall = ball2;
            add(ball2);
            add(ball1);
        }
        else {
            myBall = ball2;
            otherBall = ball1;
            add(ball1);
            add(ball2);
        }

        loadMap();
    }

    private void loadMap() {

        Wall wall1 = new Wall(this, world, 0, 0, (int)Constants.WIDTH, 20);
        add(wall1);
        Wall wall2 = new Wall(this, world, 0, 300, (int)Constants.WIDTH/2, 20);
        add(wall2);
        Wall wall3 = new Wall(this, world, (int)Constants.HEIGHT/2, 600, (int)Constants.WIDTH/2, 20);
        add(wall3);
        Wall wall4 = new Wall(this, world, (int)Constants.WIDTH - 20, 0, 20, (int)Constants.HEIGHT/2);
        add(wall4);
        Wall wall5 = new Wall(this, world, 0, 600, 20, (int)Constants.HEIGHT - 600);
        add(wall5);
        Wall wall6 = new Wall(this, world, 0, (int)Constants.HEIGHT-20, (int)Constants.WIDTH - 500, 20);
        add(wall6);
        add(new Switch(this, world, (int)Constants.WIDTH - 150, 20));
    }

    public void startGame() {
        if(getState() != State.INIT) return;
        Gdx.app.log(TAG, "Game#startGame");

        GameData.gameStartTime = System.currentTimeMillis();
        Gdx.app.log(TAG, "gameStartTime: "+ GameData.gameStartTime);
        setState(State.RUNNING);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                Game.this.update(Constants.SIM_FREQUENCY);
                for (UIComponent u : uiComponents) u.update(Constants.SIM_FREQUENCY);
                if(getState() == State.RUNNING) {
                    for (PhysicsObject p : physicsObjects) p.update(Constants.SIM_FREQUENCY);
                    animationHandler.upadte(Constants.SIM_FREQUENCY);
                    world.step(Constants.SIM_FREQUENCY, 6, 2);
                }
            }
        }, 0, Constants.SIM_FREQUENCY);



        // data exchange
        if(!GameData.debug_sp) {

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    transmit();
                }
            }, 0, Constants.CONN_FREQUENCY);

        }
    }

    public void update(float elapsedTime) {

    }

	@Override
	public void render() {

        if((int)(getGameTime()) == flash + 1) {
            flash++;
            Gdx.gl.glClearColor(0, 0, 1, 1);
        }
        else Gdx.gl.glClearColor(1, 1, 1, 1);

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        switch(getState()) {
            case RUNNING:
                for(PhysicsObject p : physicsObjects) p.render(batch);
                for(GameComponent u : uiComponents) u.render(batch);
                animationHandler.render(batch);
                break;
            case INIT:
                break;
            case PAUSED:
                break;
        }
        font1.draw(batch, "Game State: "+ getState().toString(), 5, Constants.HEIGHT - 20);
        font1.draw(batch, "Game Time: "+ Float.toString(getGameTime()) +" ms", 5, Constants.HEIGHT - 50);

        batch.end();
	}

    /*@Override
    public void dispose() {
        world.dispose();
        Textures.dispose();
    }

    @Override
    public void resume() {
        Textures.load();
    }*/

    public void toggleGravity() {
        Gdx.app.log(TAG, "toggleGravity");
        float sign = getGravityDirection() * -1;
        world.setGravity(new Vector2(0, sign * Constants.GRAVITY));
        int rotation = getGravityDirection() == 1 ? 0 : 180;
        animationHandler.gravityAnim(new Vector2(Constants.WIDTH / 2, Constants.HEIGHT / 2), rotation);
    }

    public void onJump(float x, float y) {
        jumpBar.onJump();
        animationHandler.jumpAnim(new Vector2(x, y + Math.signum(world.getGravity().y) * Constants.BALL_RADIUS));
    }

    public void onDeath(Ball ball) {
        // TODO
        ball.respawn();
    }

    private void add(GameComponent c) {

        gameComponents.add(c);

        if(c instanceof PhysicsObject) {
            PhysicsObject p = (PhysicsObject) c;
            physicsObjects.add(p);
        }
        else if(c instanceof UIComponent) {
            UIComponent u = (UIComponent) c;
            uiComponents.add(u);
        }
    }

    // --------------- communication methods ---------------

	private void transmit() {

        Gdx.app.log(TAG, "Game#transmit");
		lastWritingTime = System.currentTimeMillis();

		JsonObject message = new JsonObject();
		// add data to message
		// ...
        message.add("type", "gamedata");
		message.add("timestamp", getGameTime());

        message.add("ball", myBall.toJson());
        btService.transmit(message);

		Gdx.app.log(TAG, "transmitted: " + message.toString());
	}

	private void receive() {
        Gdx.app.log(TAG, "Game#receive");

		lastReadingTime = System.currentTimeMillis();

        if(getState() != State.INIT) {
            receiveGameData();
            return;
        }
		JsonArray messages = btService.receiveAll();
        if(messages == null) return;
        for(int i = 0; i < messages.size(); i++) {
            JsonObject message = (JsonObject) messages.get(i);
            if(message == null) continue;
            Gdx.app.log(TAG, "message["+i+"]: "+ message.toString());
            String type = message.getString("type", null);
            if(type == null) { Gdx.app.log(TAG, "received message without type attribute"); return; }

            switch(type) {
                case "clocksync":
                    clockSync.onReceiveClockSync(message);
                    break;
                case "clocksynccallback":
                    clockSync.clockSyncCallback(message);
                    break;
                case "startgame":
                    GameData.meanLatency = message.getLong("meanlatency", 0);
                    startGame();
                case "ping":
                    Long timestamp = message.getLong("timestamp", 0);
                    float diff = getGameTime() - timestamp;
                    Gdx.app.log(TAG, "ingame latency: " + diff +", timestamp: "+timestamp);
                default:
                    break;
            }
        }

		// get data from message
		// ...

	}

    private void receiveGameData() {
        JsonObject message = btService.receiveLatest();
        if(message == null) return;
        otherBall.processGameData(message);
    }





	private class ClockSynchronizer {

        private void start() {
            Gdx.app.log(TAG, "ClockSynchronizer#start");
            new Thread() {
                @Override
                public void run() {
                    try {
                        Gdx.app.log(TAG, "start sleeping");
                        Thread.sleep(5000);
                        Gdx.app.log(TAG, "stop sleeping");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    startClockSync();
                }
            }.start();
        }

        private void startClockSync() {
            Gdx.app.log(TAG, "ClockSynchronizer#startClockSync");
            new Thread() {
                @Override
                public void run() {
                    int i = 0;
                    while (i < 30) {
                        i++;
                        JsonObject message = new JsonObject();
                        message.add("type", "clocksync");
                        message.add("timestamp", System.currentTimeMillis());
                        btService.write(message.toString());
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }

        private void onReceiveClockSync(JsonObject message) {
            Gdx.app.log(TAG, "ClockSynchronizer#onReceiveClockSync");

            try {
                long timestamp = message.getLong("timestamp", 0);
                JsonObject msg2 = new JsonObject();
                msg2.add("type", "clocksynccallback");
                msg2.add("timestamp", timestamp);
                btService.transmit(msg2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void clockSyncCallback(JsonObject message) {
            Gdx.app.log(TAG, "ClockSynchronizer#clockSyncCallback");
            if (latencyCounter > 20) return;

            try {
                long timestamp = message.getLong("timestamp", 0);
                long rtd = System.currentTimeMillis() - timestamp;
                Gdx.app.log(TAG, "rtd: " + rtd);
                Gdx.app.log(TAG, "latency: " + rtd / 2);
                latencies += rtd;
                latencyCounter++;
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (latencyCounter == 20) {
                Gdx.app.log(TAG, "latencyCounter reached 20");

                final long meanLatency = latencies / 40;
                Gdx.app.log(TAG, "mean latency: " + meanLatency);
                GameData.meanLatency = meanLatency;

                JsonObject msg2 = new JsonObject();
                msg2.add("type", "startgame");
                msg2.add("meanlatency", meanLatency);
                btService.write(msg2.toString());

                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(meanLatency);
                            startGame();
                        } catch (InterruptedException ie) {
                            Gdx.app.log(TAG, "couldn't start game", ie);
                        } catch (IllegalArgumentException ae) {
                            Gdx.app.log(TAG, "meanLatency: " + meanLatency);
                            throw ae;
                        }
                    }
                }.start();
            }
        }
    }
}
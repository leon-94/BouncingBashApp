package de.lmu.ifi.bouncingbash.app.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
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
import de.lmu.ifi.bouncingbash.app.game.components.ui.Button;
import de.lmu.ifi.bouncingbash.app.game.components.GameComponent;
import de.lmu.ifi.bouncingbash.app.game.components.ui.JumpBar;
import de.lmu.ifi.bouncingbash.app.game.components.PhysicsObject;
import de.lmu.ifi.bouncingbash.app.game.components.Switch;
import de.lmu.ifi.bouncingbash.app.game.components.ui.LifeCounter;
import de.lmu.ifi.bouncingbash.app.game.components.ui.UIComponent;
import de.lmu.ifi.bouncingbash.app.game.components.Wall;


/***Spiel das gezeichnet wird**/
public class Game extends ApplicationAdapter {

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
    private BitmapFont font_calibri_32;
    private BitmapFont font_calibri_64;
    private OrthographicCamera camera;
	private World world;

    public AnimationHandler animationHandler;

    // graphical elements
    Sprite loadingScreen;
    Sprite loadingScreen_ball;
    Sprite loadingScreen_ball2;

    // all components
    public ArrayList<GameComponent> gameComponents;

    // UI components
    public ArrayList<UIComponent> uiComponents;

    private JumpBar jumpBar;
    private LifeCounter lifeCounter1;
    private LifeCounter lifeCounter2;

    // physical components
    public ArrayList<PhysicsObject> physicsObjects;

    private Player player1;
    private Player player2;
    private Player myPlayer;
    private Player otherPlayer;

    private Ball ball1;
    private Ball ball2;
    public Ball myBall;
    public Ball otherBall;

	// game state
    public enum State {INIT, RUNNING, PAUSED, DONE};
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

        batch = new SpriteBatch();
        camera = new OrthographicCamera(Constants.WIDTH, Constants.HEIGHT);
        camera.setToOrtho(false, Constants.WIDTH, Constants.HEIGHT);

        world = new World(new Vector2(0, -1 * Constants.GRAVITY), true);

        Assets.load();
        font_calibri_32 = Assets.getAssets().getFont("FONT_CALIBRI_32");
        font_calibri_64 = Assets.getAssets().getFont("FONT_CALIBRI_64");

        // set up loading screen
        initLoadingScreen();

        initUI();
        initGame();

        Gdx.input.setInputProcessor(new InputHandler(camera, gameComponents, myBall));

        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
            }

            @Override
            public void endContact(Contact contact) {
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

                Vector2 contactpoint = contact.getWorldManifold().getPoints()[0];
                Body a = contact.getFixtureA().getBody();
                Body b = contact.getFixtureB().getBody();

                // check for ball contact
                if ((a == myBall.getBody() && b == otherBall.getBody()) ||
                        (a == otherBall.getBody() && b == myBall.getBody())) {

                    Vector2 normal = a.getPosition().sub(b.getPosition()).rotate90(1);
                    int rotation = (int) normal.angle();

                    // start contact animation
                    animationHandler.contactAnim(contactpoint.scl(Constants.PIXELS_TO_METERS), rotation);
                }

                for (PhysicsObject p : physicsObjects) p.onCollision(contact, contactpoint, a, b);
            }
        });

        animationHandler = new AnimationHandler();

		batch = new SpriteBatch();

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
            }, 0, Constants.CONN_FREQUENCY_READ);
        }

    }

    private void initLoadingScreen() {

        loadingScreen = new Sprite(Assets.getAssets().getTexture("TEX_TITLE"));
        loadingScreen.setSize(Constants.WIDTH, Constants.HEIGHT);
        loadingScreen_ball = new Sprite(Assets.getAssets().getTexture("TEX_BALL"));
        loadingScreen_ball.setSize(128, 128);
        loadingScreen_ball.setOriginCenter();
        loadingScreen_ball.setPosition(500, 300);
        loadingScreen_ball2 = new Sprite(Assets.getAssets().getTexture("TEX_BALL"));
        loadingScreen_ball2.setSize(128, 128);
        loadingScreen_ball2.setOriginCenter();
        loadingScreen_ball2.setPosition(loadingScreen_ball.getX() + 90, loadingScreen_ball.getY() + 90);
    }

    private void disposeLoadingScreen() {
        loadingScreen = null;
        loadingScreen_ball = null;
        loadingScreen_ball2 = null;
        Assets.getAssets().disposeLoadingScreen();
    }

    private void initUI() {

        // create players
        player1 = new Player();
        player2 = new Player();

        // UI
        jumpBar = new JumpBar(5, 5);
        add(jumpBar);
        lifeCounter1 = new LifeCounter(5, Constants.HEIGHT - 55, player1);
        add(lifeCounter1);
        lifeCounter2 = new LifeCounter(Constants.WIDTH - 405, Constants.HEIGHT - 55, player2);
        add(lifeCounter2);

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
        ball1 = new Ball(this, world, player1, GameData.isHost);
        player1.setBall(ball1);
        ball2 = new Ball(this, world, player2, !GameData.isHost);
        player2.setBall(ball2);

        // indentify own ball
        if(GameData.isHost) {
            myPlayer = player1;
            otherPlayer = player2;

            myBall = ball1;
            otherBall = ball2;
            add(ball2);
            add(ball1);
        }
        else {
            myPlayer = player2;
            otherPlayer = player1;

            myBall = ball2;
            otherBall = ball1;
            add(ball1);
            add(ball2);
        }

        loadMap();
    }

    private void loadMap() {

        // walls
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

        // switches
        add(new Switch(this, world, (int)Constants.WIDTH - 150, 20));
//        add(new Switch(this, world, (int)Constants.WIDTH/2, 120));
        add(new Switch(this, world, 20, (int)Constants.HEIGHT - 150));
    }

    public void startGame() {
        if(getState() != State.INIT) return;
        Gdx.app.log(TAG, "Game#startGame");

        GameData.gameStartTime = System.currentTimeMillis();
        Gdx.app.log(TAG, "gameStartTime: " + GameData.gameStartTime);
        setState(State.RUNNING);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                Game.this.update(Constants.SIM_FREQUENCY);
                for (UIComponent u : uiComponents) u.update(Constants.SIM_FREQUENCY);
                if(getState() == State.RUNNING) {
                    for (PhysicsObject p : physicsObjects) p.update(Constants.SIM_FREQUENCY);
                    animationHandler.update(Constants.SIM_FREQUENCY);
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
            }, 0, Constants.CONN_FREQUENCY_WRITE);

        }

        // spawn balls
        myBall.respawn(Constants.WIDTH/2 - Constants.BALL_RADIUS, 50);
        otherBall.respawn(Constants.WIDTH/2 - Constants.BALL_RADIUS, 350);
    }

    public void update(float elapsedTime) {

    }

	@Override
	public void render() {

//        if((int)(getGameTime()) == flash + 1) {
//            flash++;
//            Gdx.gl.glClearColor(0, 0, 1, 1);
//        }
//        else Gdx.gl.glClearColor(1, 1, 1, 1);

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        switch(getState()) {
            case RUNNING:
                for(PhysicsObject p : physicsObjects) p.render(batch);
                for(UIComponent u : uiComponents) u.render(batch);
                animationHandler.render(batch);
                break;
            case INIT:
//                font_calibri_64.draw(batch, "Loading ...", (Constants.WIDTH - 400)/2, (Constants.HEIGHT - font_calibri_64.getLineHeight())/2);
                loadingScreen.draw(batch);
                loadingScreen_ball.setRotation(loadingScreen_ball.getRotation() + 2);
                loadingScreen_ball.draw(batch);
                loadingScreen_ball2.setRotation(loadingScreen_ball2.getRotation() - 2);
                loadingScreen_ball2.draw(batch);
                break;
            case DONE:
                for(PhysicsObject p : physicsObjects) p.render(batch);
                for(UIComponent u : uiComponents) u.render(batch);
                animationHandler.render(batch);
                break;
            case PAUSED:
                break;
        }
        font_calibri_32.draw(batch, "Game State: " + getState().toString(), 5, Constants.HEIGHT - 20);
        font_calibri_32.draw(batch, "Game Time: " + Float.toString(getGameTime()) + " ms", 5, Constants.HEIGHT - 50);

        batch.end();
	}

    @Override
    public void dispose() {
        world.dispose();
        Assets.getAssets().dispose();
    }

//    private int lastEventId = GameData.isHost ? -2 : -1;
//    private synchronized int getNewEventId() {
//        lastEventId += 2;
//        return lastEventId;
//    }
//    private synchronized void notifyIdCounter(int id) {
//        if(lastEventId < id) lastEventId = id;
//        else if(lastEventId == id) Gdx.app.log(TAG, "big problem");
//    }

    private int event_toggle_gravity = -1;
    private int event_toggle_gravity_tc = 0;
    public void onToggleGravity() {
        Gdx.app.log(TAG, "onToggleGravity");

        // assign id to event
        event_toggle_gravity++;
        // set transmission counter so event (and its id) will be transmitted
        event_toggle_gravity_tc = Constants.CONN_REPETITIONS;

        doToggleGravity();
    }
    private void doToggleGravity() {
        Gdx.app.log(TAG, "doToggleGravity");

        float sign = getGravityDirection() * -1;
        world.setGravity(new Vector2(0, sign * Constants.GRAVITY));
        int rotation = getGravityDirection() == 1 ? 0 : 180;
        animationHandler.gravityAnim(new Vector2(Constants.WIDTH / 2, Constants.HEIGHT / 2), rotation);
    }

    private int event_death = -1;
    private int event_death_tc = 0;
    public void onDeath(Ball ball) {

        // assign id to event
        event_death++;
        // set transmission counter so event (and its id) will be transmitted
        event_death_tc = Constants.CONN_REPETITIONS;

        doDeath(ball, 1);
    }
    private void doDeath(Ball ball, float delay) {
//        final Ball b = ball;
//        Timer.schedule(new Timer.Task() {
//            @Override
//            public void run() {
//                b.respawn();
//            }
//        }, delay);

        Player p = ball.getPlayer();
        p.loseLife();
        if(p.getLives() == 0) {
            endGame(p);
        }
        else {
            ball.respawn(Constants.WIDTH / 2 - Constants.BALL_RADIUS, 150);
            animationHandler.spawnAnim(new Vector2(Constants.WIDTH/2 - Constants.BALL_RADIUS, 150));
        }
    }
//    public void onFinishSpawning(float x, float y) {
//        animationHandler.spawnAnim(new Vector2(x, y));
//    }

    private void endGame(Player loser) {
        state = State.DONE;
    }

    public void onJump(float x, float y) {
        jumpBar.onJump();
        animationHandler.jumpAnim(new Vector2(x, y + Math.signum(world.getGravity().y) * Constants.BALL_RADIUS));
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

        JsonObject events = new JsonObject();
//        if(event_toggle_gravity_tc > 0) {
            events.add("event_toggle_gravity", event_toggle_gravity);
            event_toggle_gravity_tc--;
//        }
//        if(event_death_tc > 0) {
            events.add("event_death", event_death);
            event_death_tc--;
//        }

        message.add("events", events);
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
                    Gdx.app.log(TAG, "mean latency: "+ GameData.meanLatency);
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
        float timestamp = message.getFloat("timestamp", getGameTime());

        otherBall.processGameData(message);
        JsonObject events = (JsonObject) message.get("events");

        // handle events

        // toggle gravity
        int m_event_toggle_gravity = events.getInt("event_toggle_gravity", event_toggle_gravity);
        if (m_event_toggle_gravity > event_toggle_gravity) {
            event_toggle_gravity++;
            doToggleGravity();
        }

        // death
        int m_event_death = events.getInt("event_death", event_death);
        if (m_event_toggle_gravity > event_toggle_gravity) {
            event_death++;
            doDeath(otherBall, 1 - (getGameTime() - timestamp));
        }
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
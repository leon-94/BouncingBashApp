package de.lmu.ifi.bouncingbash.app.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
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
import de.lmu.ifi.bouncingbash.app.game.components.Map;
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
    private ClockSynchronizer clockSync;

	private SpriteBatch batch;
    private BitmapFont font_calibri_32;
    private BitmapFont font_calibri_64;
    private OrthographicCamera camera;
	private World world;
    private Map map;

    public AnimationHandler animationHandler;

    // graphical elements
    Sprite background0;
    Sprite background1;
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

//    public ArrayList<Item> myItems;

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
        setState(State.INIT);

        batch = new SpriteBatch();
        camera = new OrthographicCamera(Constants.WIDTH, Constants.HEIGHT);
        camera.setToOrtho(false, Constants.WIDTH, Constants.HEIGHT);

        // set up loading screen
        Assets.init();
        initLoadingScreen();

        // load assets
        Assets.getAssets().loadAssets();

        background0 = new Sprite(Assets.getAssets().getTexture("TEX_BACKGROUND0"));
        background0.setPosition(0, 0);
        background0.setSize(Constants.WIDTH, Constants.HEIGHT);

        font_calibri_32 = Assets.getAssets().getFont("FONT_CALIBRI_32");
        font_calibri_64 = Assets.getAssets().getFont("FONT_CALIBRI_64");

        // init graphics objects
        batch = new SpriteBatch();
        animationHandler = new AnimationHandler(this);

        // init world
        world = new World(new Vector2(0, -1 * Constants.GRAVITY), true);
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


//                    Vector2 n = contact.getWorldManifold().getNormal();
//                    Vector2 va = a.getLinearVelocity();
//                    Vector2 vb = b.getLinearVelocity();
//                    float f = Math.abs(va.dot(n)) + Math.abs(vb.dot(n));
//
//                    Gdx.app.log(TAG, "collision strength: "+ f);
//                    if(f < 2) return;
                    Vector2 normal = a.getPosition().sub(b.getPosition()).rotate90(1);
                    int rotation = (int) normal.angle();

                    // start contact animation
                    animationHandler.contactAnim(new Vector2(contactpoint.x * Constants.PIXELS_TO_METERS, contactpoint.y * Constants.PIXELS_TO_METERS), rotation);
                }

                for (PhysicsObject p : physicsObjects) p.onCollision(contact, contactpoint, a, b);
            }
        });

        // init game
        initUI();
        initGame();

        // debugging?
        if(GameData.debug_sp) {
            Gdx.app.log(TAG, "DEBUG SINGLEPLAYER MODE");
//            startGame();
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    startGame();
                }
            }, 1.5f);
        } else {
            // init synchronizer
            clockSync = new ClockSynchronizer();

            // start listening to bluetooth connection
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    receive();
                }
            }, 0, Constants.CONN_FREQUENCY_READ);

            if(GameData.isHost) {
                // start clock sync
                clockSync.start();
            }
        }

    }

    private void initLoadingScreen() {

        Assets.getAssets().loadLoadingScreenAssets();
        background1 = new Sprite(Assets.getAssets().getTexture("TEX_BACKGROUND1"));
        background1.setSize(Constants.WIDTH, Constants.HEIGHT);
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
        background1 = null;
        loadingScreen = null;
        loadingScreen_ball = null;
        loadingScreen_ball2 = null;
        Assets.getAssets().disposeLoadingScreen();
    }

    private void initEndScreen() {
        Assets.getAssets().loadEndScreenAssets();
        background1 = new Sprite(Assets.getAssets().getTexture("TEX_BACKGROUND1"));
        background1.setSize(Constants.WIDTH, Constants.HEIGHT);
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

    private void initUI() {

        // create players
        player1 = new Player(Color.GREEN);
        player2 = new Player(Color.YELLOW);
        if(GameData.isHost) {
            myPlayer = player1;
            otherPlayer = player2;
        }
        else {
            myPlayer = player2;
            otherPlayer = player1;
        }

        // UI
        jumpBar = new JumpBar(5, 5, myPlayer.getColor());
        add(jumpBar);
        lifeCounter1 = new LifeCounter(5, Constants.HEIGHT - 55, myPlayer);
        add(lifeCounter1);
        lifeCounter2 = new LifeCounter(Constants.WIDTH - 405, Constants.HEIGHT - 55, otherPlayer);
        add(lifeCounter2);

//        Button button1 = new Button("Quit", Constants.WIDTH - 200, Constants.HEIGHT - 80, 200, 80){
//            @Override
//            public void onClick(float x, float y){
//                Gdx.app.exit();
//            }
//        };
//        add(button1);
    }

    private void initGame() {

        // create balls
        ball1 = new Ball(this, world, player1, GameData.isHost);
        player1.setBall(ball1);
        ball2 = new Ball(this, world, player2, !GameData.isHost);
        player2.setBall(ball2);

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

        // init input processor
        Gdx.input.setInputProcessor(new InputHandler(camera, gameComponents, myBall));

        // load map
        loadMap();
    }

    private void loadMap() {
        if(GameData.debug_sp) GameData.map = GameData.defaultMap;

//        // -----------------------------------------------------------------------------------------
//        // -----------------------------------------------------------------------------------------
//        // create map
//        int w1 = 500;
//        int dis = 300;
//        int th = 30;
//
//        Wall[] walls = new Wall[6];
//        walls[0] = new Wall(this, world, (int)Constants.WIDTH/2-w1/2, 0, w1, th);
//        walls[1] = new Wall(this, world, (int)Constants.WIDTH/2-w1/2, (int)Constants.HEIGHT-th, w1, th);
//        walls[2] = new Wall(this, world, 0, dis, w1, th);
//        walls[3] = new Wall(this, world, (int)Constants.WIDTH-w1, dis, w1, th);
//        walls[4] = new Wall(this, world, 0, (int)Constants.HEIGHT-dis-th, w1, th);
//        walls[5] = new Wall(this, world, (int)Constants.WIDTH-w1, (int)Constants.HEIGHT-dis-th, w1, th);
//
//        // switches
//        Switch[] switches = new Switch[1];
//        switches[0] = new Switch(this, world, (int)Constants.WIDTH/2-65, (int)Constants.HEIGHT/2-65);
//
//        // spawn points
//        Vector2[] spawnPoints = new Vector2[3];
//        spawnPoints[0] = new Vector2((int)Constants.WIDTH/2, Constants.HEIGHT/3-Constants.BALL_RADIUS);
//        spawnPoints[1] = new Vector2(100, Constants.HEIGHT/2-Constants.BALL_RADIUS);
//        spawnPoints[2] = new Vector2((int)Constants.WIDTH-100-Constants.BALL_RADIUS, Constants.HEIGHT/2-Constants.BALL_RADIUS);
//
//        Map m = new Map(this, world, walls, switches, spawnPoints);
//        String mapString = m.toJson().toString();
//        Gdx.app.log(TAG, "------ map ------");
//        Gdx.app.log(TAG, mapString);
//        Gdx.app.log(TAG, "------ map ------");
//        // -----------------------------------------------------------------------------------------
//        // -----------------------------------------------------------------------------------------

        map = new Map(this, world, GameData.map);
        for(int i = 0; i < map.walls.length; i++) {
            add(map.walls[i]);
        }
        for(int i = 0; i < map.switches.length; i++) {
            add(map.switches[i]);
        }

//        //items/itemspawnerpunkte
//        Item i1=new Item(this,world,100,100);
//        add(i1);
//        myItems.add(i1);
//        Item i2=new Item(this,world,100,400);
//        add(i2);
//        myItems.add(i2);
//        Item i3=new Item(this,world,400,100);
//        myItems.add(i3);
//        add(i3);
    }

    public void startGame() {
        if(getState() != State.INIT) return;
        Gdx.app.log(TAG, "Game#startGame");

        GameData.gameStartTime = System.currentTimeMillis();
        Gdx.app.log(TAG, "gameStartTime: " + GameData.gameStartTime);
        disposeLoadingScreen();
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
        ball1.respawn(map.getSpawnPoints()[0]);
        ball2.respawn(map.getSpawnPoints()[1]);
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
                background0.draw(batch);
                for(PhysicsObject p : physicsObjects) p.render(batch);
                for(UIComponent u : uiComponents) u.render(batch);
                animationHandler.render(batch);
                break;
            case INIT:
                background1.draw(batch);
                loadingScreen.draw(batch);
                loadingScreen_ball.setRotation(loadingScreen_ball.getRotation() + 2);
                loadingScreen_ball.draw(batch);
                loadingScreen_ball2.setRotation(loadingScreen_ball2.getRotation() - 2);
                loadingScreen_ball2.draw(batch);
                break;
            case DONE:
                background1.draw(batch);
                loadingScreen.draw(batch);
                loadingScreen_ball.setRotation(loadingScreen_ball.getRotation() + 2);
                loadingScreen_ball.draw(batch);
                loadingScreen_ball2.setRotation(loadingScreen_ball2.getRotation() - 2);
                loadingScreen_ball2.draw(batch);
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

    public void onToggleGravity() {
        Gdx.app.log(TAG, "onToggleGravity");

        doToggleGravity();
    }
    private void doToggleGravity() {
        Gdx.app.log(TAG, "doToggleGravity");

        float sign = getGravityDirection() * -1;
        world.setGravity(new Vector2(0, sign * Constants.GRAVITY));
        int rotation = getGravityDirection() == 1 ? 0 : 180;
        animationHandler.gravityAnim(new Vector2(Constants.WIDTH / 2, Constants.HEIGHT / 2), rotation);
    }

    public void doDeath(Ball ball) {
        Gdx.app.log(TAG, "doDeath");

        Player p = ball.getPlayer();
        p.loseLife();
        if(p.getLives() == 0) {
            endGame(p);
        }
        else {
            Vector2 sp = getRandomFromArray(map.getSpawnPoints());
            ball.respawn(sp);
            animationHandler.spawnAnim(sp, ball.getPlayer().getColor());
        }
    }

    private Vector2 getRandomFromArray(Vector2[] ps) {
        double r = Math.random();
        return ps[(int)(r*ps.length)];
    }

    private void endGame(final Player loser) {

        initEndScreen();

        boolean won = loser.equals(otherPlayer);
        GameData.postgame = true;
        GameData.won = won;
        if(won) animationHandler.wonAnim(Color.CYAN);
        else animationHandler.lostAnim(Color.RED);
//        if(won) animationHandler.wonAnim(myPlayer.getColor());
//        else animationHandler.lostAnim(otherPlayer.getColor());
    }

//    public void showEndScreen() {
//        setState(Game.State.DONE);
//
//        Timer.schedule(new Timer.Task() {
//            @Override
//            public void run() {
//                Gdx.app.exit();
//            }
//        }, 2);
//    }

    public void onJump(Ball b) {
        jumpBar.onJump();
        animationHandler.jumpAnim(new Vector2(b.getPosition().x,
                b.getPosition().y + Math.signum(world.getGravity().y) * Constants.BALL_RADIUS),
                b.getPlayer().getColor());
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
        message.add("type", "gamedata");
        message.add("timestamp", getGameTime());
        message.add("ball", myBall.toJson());
        message.add("gravity", getGravityDirection());

//        // events
//        JsonObject events = new JsonObject();
//        events.add("event_toggle_gravity", event_toggle_gravity);
//        event_toggle_gravity_tc--;
//        events.add("event_death", event_death);
//        event_death_tc--;
//
//        message.add("events", events);
//
////        //items
////            for(Item i : myItems)
////            {
////                if(i.taken) {
////                    message.add("item", i.toJson());
////                }
////            }

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
        Gdx.app.log(TAG, "received: " + message.toString());

        float timestamp = message.getFloat("timestamp", getGameTime());

        // let ball handle its position
        otherBall.processGameData(message);

        // correct gravity direction if necassary
        final float direction = message.getFloat("gravity", getGravityDirection());
        if(direction != getGravityDirection()) {
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    if(direction != getGravityDirection()) doToggleGravity();
                }
            }, GameData.meanLatency*2);
        }

        //        JsonObject events = (JsonObject) message.get("events");

//        // handle events
//
//        // toggle gravity
//        int m_event_toggle_gravity = events.getInt("event_toggle_gravity", event_toggle_gravity);
//        if (m_event_toggle_gravity > event_toggle_gravity) {
//            event_toggle_gravity++;
//            doToggleGravity();
//        }
//        // death
//        int m_event_death = events.getInt("event_death", event_death);
//        if (m_event_death > event_death) {
//            event_death++;
//            doDeath(otherBall, 1 - (getGameTime() - timestamp));
//        }
    }





	private class ClockSynchronizer {

        private void start() {
            new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    startClockSync();
                }
            }.start();
        }

        private void startClockSync() {
            new Thread() {
                @Override
                public void run() {
                    int i = 0;
                    while (i < 50) {
                        i++;
                        JsonObject message = new JsonObject();
                        message.add("type", "clocksync");
                        message.add("timestamp", System.currentTimeMillis());
                        btService.write(message.toString());
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }

        private void onReceiveClockSync(JsonObject message) {

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
            if (latencyCounter > 20) return;

            try {
                long timestamp = message.getLong("timestamp", 0);
                long rtd = System.currentTimeMillis() - timestamp;
                latencies += rtd;
                latencyCounter++;
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (latencyCounter == 20) {

                final long meanLatency = latencies / 40;
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
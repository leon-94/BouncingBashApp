package de.lmu.ifi.bouncingbash.app;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.ArrayList;

import de.lmu.ifi.bouncingbash.app.game.GameController;
import de.lmu.ifi.bouncingbash.app.game.GameData;
import de.lmu.ifi.bouncingbash.app.game.Transmittable;
import de.lmu.ifi.bouncingbash.app.game.models.GameModel;
import de.lmu.ifi.bouncingbash.app.game.views.BackgroundView;
import de.lmu.ifi.bouncingbash.app.game.views.BallView;
import de.lmu.ifi.bouncingbash.app.game.views.PlatformView;


/***Spiel das gezeichnet wird**/
public class Game extends ApplicationAdapter {


    private long latencies = 0;
    private int latencyCounter = 0;
    private int flash = 0;



	private final String TAG = "Game";

	private IBluetoothService btService;
	private IActivity activity;
	private long lastReadingTime = 1000;
	private long lastWritingTime = 1000;
    ClockSynchronizer clockSync;

	private SpriteBatch batch;
	private BitmapFont font;
	private World world;
	final float PIXELS_TO_METERS = 100f;

	// game state
	private enum State {RUNNING, PAUSING, PAUSED, RESUMING, INIT};
	private State state = State.PAUSED;
	public State getState() { return state; }
	public void setState(State state) {
		this.state = state;
		Gdx.app.log(TAG, "game state changed to: "+ state);
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
	}

	public long getGameTime() {
		if(getState().equals(State.INIT)) return 0;
        return System.currentTimeMillis() - GameData.gameStartTime;
	}

	@Override
	public void create () {

        clockSync = new ClockSynchronizer();
        if(GameData.isHost) clockSync.start();

		batch = new SpriteBatch();
		font = new BitmapFont();
		font.setColor(Color.RED);
		font.getData().setScale(3, 3);

		world = new World(new Vector2(0, -100f),true);

		setState(State.INIT);

        Gdx.app.log(TAG, "debug_sp: "+GameData.debug_sp);
    }

	@Override
	public void render() {

        if((int)(getGameTime()/1000) == flash + 1) {
            flash++;
            Gdx.gl.glClearColor(0, 1, 1, 1);
        }
        else Gdx.gl.glClearColor(1, 1, 1, 1);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();

        /*switch(getState()) {
            case RUNNING:
                world.step(1f / 30f, 6, 2);

                // draw background
                // ...

                // draw all components
                // ...

                // draw text stuff
                font.draw(batch, "RUNNING", 200, 400);
                font.draw(batch, Long.toString(getGameTime()), 200, 200);

                // update all components
                // ...
                break;
            case INIT:
                font.draw(batch, "INIT", 200, 400);
                font.draw(batch, Long.toString(getGameTime()), 200, 200);
                break;
            case PAUSED:
                font.draw(batch, "PAUSED", 200, 400);
                font.draw(batch, Long.toString(getGameTime()), 200, 200);
                break;
        }*/
        font.draw(batch, getState().toString(), 200, 400);
        font.draw(batch, Long.toString(getGameTime()), 200, 200);
        font.draw(batch, "latencyCounter: " + latencyCounter, 400, 200);

        batch.end();


		// data exchange
		if(!GameData.debug_sp) {
            if (System.currentTimeMillis() - lastWritingTime >= 100 && getState().equals(State.RUNNING)) transmit();
			if (System.currentTimeMillis() - lastReadingTime >= 50) receive();
		}
	}

	private void transmit() {
        Gdx.app.log(TAG, "Game#transmit");
		lastWritingTime = System.currentTimeMillis();

		JsonObject message = new JsonObject();
		// add data to message
		// ...
        message.add("type", "ping");
		message.add("timestamp", getGameTime());
		btService.transmit(message);
		Gdx.app.log(TAG, "transmitted: " + message.toString());
	}

	private void receive() {
        Gdx.app.log(TAG, "Game#receive");

		lastReadingTime = System.currentTimeMillis();

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
                    startGame();
                    GameData.meanLatency = message.getLong("meanlatency", 0);
                case "ping":
                    Long timestamp = message.getLong("timestamp", 0);
                    long diff = getGameTime() - timestamp;
                    Gdx.app.log(TAG, "ingame latency: " + diff +", timestamp: "+timestamp);
                default:
                    break;
            }
        }

		// get data from message
		// ...

	}

    public void startGame() {
        Gdx.app.log(TAG, "Game#startGame");

        setState(State.RUNNING);
        GameData.gameStartTime = System.currentTimeMillis();
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

            try{
                long timestamp = message.getLong("timestamp", 0);
                JsonObject msg2 = new JsonObject();
                msg2.add("type", "clocksynccallback");
                msg2.add("timestamp", timestamp);
                btService.transmit(msg2);
            } catch(Exception e) { e.printStackTrace(); }
        }

        private void clockSyncCallback(JsonObject message) {
            Gdx.app.log(TAG, "ClockSynchronizer#clockSyncCallback");
            if(latencyCounter > 20) return;

            try{
                long timestamp = message.getLong("timestamp", 0);
                long rtd = System.currentTimeMillis() - timestamp;
                Gdx.app.log(TAG, "rtd: " + rtd);
                Gdx.app.log(TAG, "latency: " + rtd/2);
                latencies += rtd;
                latencyCounter++;
            } catch(Exception e) { e.printStackTrace(); }

            if(latencyCounter == 20) {
                Gdx.app.log(TAG, "latencyCounter reached 20");

                final long meanLatency = latencies / 40;
                Gdx.app.log(TAG, "mean latency: " + meanLatency);
                GameData.meanLatency = meanLatency;

                JsonObject msg2 = new JsonObject();
                msg2.add("type", "startgame");
                msg2.add("meanlatency", meanLatency);
                btService.write(msg2.toString());
                startGame();

                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(meanLatency);
                            startGame();
                        } catch (InterruptedException ie) {
                            Gdx.app.log(TAG, "couldn't start game", ie);
                        } catch (IllegalArgumentException ae) {
                            Gdx.app.log(TAG, "meanLatency: "+meanLatency);
                            throw ae;
                        }
                    }
                }.start();
            }
        }
/*
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
                    JsonObject msg2 = new JsonObject();
                    msg2.add("type", "startgame");
                    msg2.add("meanlatency", 0);
                    btService.write(msg2.toString());
                    startGame();
                    //startClockSync();
                }
            }.start();
        }

        private void startClockSync() {
            Gdx.app.log(TAG, "ClockSynchronizer#startClockSync");
			latencies = 0;
			latencyCounter = 0;

			JsonObject message = new JsonObject();
			message.add("type", "startclocksync");
			btService.transmit(message);
		}

		private void writeClockSync() {
            Gdx.app.log(TAG, "ClockSynchronizer#writeClockSync");
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
							Thread.sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}.start();
		}

		private void clockSyncCallback(JsonObject message) {
            Gdx.app.log(TAG, "ClockSynchronizer#clockSyncCallback");
            if(latencyCounter > 20) return;

			try{
				long timestamp = message.getLong("timestamp", 0);
				long latency = System.currentTimeMillis() - timestamp;
				Gdx.app.log(TAG, "latency: " + latency);
				latencies += latency;
				latencyCounter++;
			} catch(Exception e) { e.printStackTrace(); }

			if(latencyCounter == 20) {
                Gdx.app.log(TAG, "latencyCounter reached 20");

                final long meanLatency = latencies / 20;
                Gdx.app.log(TAG, "mean latency: " + meanLatency);
                GameData.meanLatency = meanLatency;

                JsonObject msg2 = new JsonObject();
                msg2.add("type", "startgame");
                msg2.add("meanlatency", meanLatency);
                btService.write(msg2.toString());
                startGame();

                new Thread() {
                    @Override
                    public void run() {
                        try {
                            //Thread.sleep(meanLatency);
                            Thread.sleep(10);
                            startGame();
                        } catch (InterruptedException ie) {
                            Gdx.app.log(TAG, "couldn't start game", ie);
                        } catch (IllegalArgumentException ae) {
                            Gdx.app.log(TAG, "meanLatency: "+meanLatency);
                            throw ae;
                        }
                    }
                }.start();
            }
		}
*/
	}

}





















	/*

    private final String TAG = "Game";

	private IBluetoothService btService;
	private IActivity activity;
    private long lastReadingTime = 0;
	private long lastWritingTime = 0;

    private SpriteBatch batch;
    private BitmapFont font;
	private GameModel gameModel;
	private Body body1,body2,bodyEdgeScreen;

	private World world;

	final float PIXELS_TO_METERS = 100f;
	Box2DDebugRenderer debugRenderer;
	private PlatformView platformView;
	private BallView myBall;
    //private BallView otherBall;
	private BackgroundView backgroundView;

	private boolean paused = true;



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
	}

	private void exitGame(boolean won) {
		Gdx.app.log(TAG, "exitGame");
		activity.exitGame(won);
	}

	private void exitInFive() {
		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
				}
				exitGame(true);
			}
		}.start();
	}

	public long getGameTime() {
		return System.currentTimeMillis() - GameData.gameStartTime;
	}


	@Override
	public void create () {
		GameController gameController = new GameController();
		gameModel = gameController.getGameModel();

		world = new World(new Vector2(0, -100f),true);

		batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.RED);
        font.getData().setScale(2, 2);

        // set up balls
		myBall = new BallView(GameData.isHost, GameData.isHost, gameModel, world, batch);
        //otherBall = new BallView(isHost, false, gameModel, world, batch);


		backgroundView = new BackgroundView(gameModel, batch);
		platformView = new PlatformView(gameModel,world, batch);

		BodyDef bodyDef3 = new BodyDef();
		bodyDef3.type = BodyDef.BodyType.StaticBody;
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		bodyDef3.position.set(0,0);

		FixtureDef fixtureDef3 = new FixtureDef();
		EdgeShape edgeShape = new EdgeShape();
		edgeShape.set(-w / 2, 0, w / 2, 0);
        Gdx.app.log(TAG, "w: " + w + " h: " + h);
		fixtureDef3.shape = edgeShape;

		bodyEdgeScreen = world.createBody(bodyDef3);
		bodyEdgeScreen.createFixture(fixtureDef3);
		edgeShape.dispose();
		// Shape is the only disposable of the lot, so get rid of it



		collision();
		debugRenderer = new Box2DDebugRenderer();
	}

	private void collision() {
		world.setContactListener(new ContactListener() {
			@Override
			public void beginContact(Contact contact) {
				Body fA = contact.getFixtureA().getBody();
				Body fB = contact.getFixtureB().getBody();
				if ((fA == body2 && fB == body1) ||
						(fA == body1 && fB == body2)) {

					Gdx.app.log(TAG, "CONTACT " + "bodyC: " + body1.getPosition().x + " " + body1.getPosition().y);
					Gdx.app.log(TAG, "" + fA + " " + fB);
				}
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
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		world.step(1f / 30f, 6, 2);


		batch.disableBlending();
		batch.begin();

		Matrix4 debugMatrix = batch.getProjectionMatrix().cpy().scale(PIXELS_TO_METERS,
				PIXELS_TO_METERS, 0);
		backgroundView.drawBackground();
		batch.enableBlending();

        // draw all components
		myBall.drawBall();
        //otherBall.drawBall();

		platformView.drawMainPlatform();

        // draw text stuff
        font.draw(batch, Long.toString(getGameTime()), 200, 200);
		batch.end();

        // update all components
        myBall.roll();
        //otherBall.roll();

        debugRenderer.render(world, debugMatrix);

        // data exchange
        if(!GameData.debug_sp) {
            if (getGameTime() - lastWritingTime >= 100 && GameData.isHost) transmit();
            if (getGameTime() - lastReadingTime >= 50) receive();
        }
	}

	private void transmit() {
        lastWritingTime = getGameTime();

		JsonObject message = new JsonObject();
		JsonObject jsonBall = myBall.toJson();
		message.add(jsonBall.getString("id", "NULL"), jsonBall);
        message.add("timestamp", getGameTime());
		btService.transmit(message);
        //Gdx.app.log(TAG, "transmitted: "+ message.toString());
	}

	private void receive() {
        lastReadingTime = getGameTime();

		JsonObject message = btService.receive();
        if(message == null) return;
        //Gdx.app.log(TAG, "received: "+ message.toString());
        long diff = getGameTime() - message.getLong("timestamp", 0);
        Gdx.app.log(TAG, "ingame latency: " + diff);
		JsonValue test = message.get("ball1");
		if(test == null) return;
		JsonObject jsonBall = (JsonObject) test;
        myBall.fromJson(jsonBall);

	}

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }

	private void startGame() {
		paused = false;
	}



	private class ClockSynchronizer {

		private long latencies;
		private int latencyCounter;

		private void startClockSync() {
			if(GameData.isHost) {
				latencies = 0;
				latencyCounter = 0;
			}
			else writeClockSync();
		}

		private void writeClockSync() {
			new Thread() {
				@Override
				public void run() {
					int i = 0;
					while (i < 20) {
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

		private void clockSyncCallback(JsonObject message) {

			try{
				long timestamp = message.getLong("timestamp", 0);
				long latency = System.currentTimeMillis() - timestamp;
				Gdx.app.log(TAG, "latency: " + latency);
				latencies += latency;
				latencyCounter++;
			} catch(Exception e) { e.printStackTrace(); }

			if(latencyCounter == 20) {

				final long meanLatency = latencies / 20;
				btService.setMeanLatency(meanLatency);
				Gdx.app.log(TAG, "mean latency: " + meanLatency);
				GameData.meanLatency = meanLatency;

				if(GameData.isHost) {

					JsonObject msg2 = new JsonObject();
					msg2.add("type", "startgame");
					msg2.add("meanlatency", meanLatency);
					btService.write(msg2.toString());

					new Thread(){
						@Override
						public void run() {
							try {
								Thread.sleep(meanLatency);
								startGame();
							} catch (InterruptedException e) {
								Gdx.app.log(TAG, "couldn't start game", e);
							}
						}
					}.start();
				}
				else {
					Gdx.app.log(TAG, "shouldn't happen");
				}
				startGame();
			}
		}
	}

}

*/

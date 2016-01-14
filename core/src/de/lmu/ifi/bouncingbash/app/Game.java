package de.lmu.ifi.bouncingbash.app;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
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

import de.lmu.ifi.bouncingbash.app.game.GameController;
import de.lmu.ifi.bouncingbash.app.game.models.GameModel;
import de.lmu.ifi.bouncingbash.app.game.views.BackgroundView;
import de.lmu.ifi.bouncingbash.app.game.views.BallView;
import de.lmu.ifi.bouncingbash.app.game.views.ItemView;
import de.lmu.ifi.bouncingbash.app.game.views.PlatformView;
import de.lmu.ifi.bouncingbash.app.game.views.View;


/***Spiel das gezeichnet wird**/
public class Game extends ApplicationAdapter  {

	private IBluetoothService btService;
	private IActivity activity;
	private boolean isHost;
	private long lastWritingTime;

    private SpriteBatch batch;
	private GameModel gameModel;
	private Body body1,body2,bodyEdgeScreen;

	private World world;


	final float PIXELS_TO_METERS = 100f;
	Box2DDebugRenderer debugRenderer;
	private View platformView,ballView,backgroundView,itemView;



	public Game(IActivity act, IBluetoothService bts, boolean host) {
		super();
		btService = bts;
		//btService.setQueueing(true);
		isHost = host;

		activity = act;
	}

	private void exitGame(boolean won) {
		System.out.println("exitGame");
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

	/*private void writeToConn() {
		if(isHost && System.currentTimeMillis() - lastWritingTime > 24) {
			btService.write(Float.toString(x) + "," + Float.toString(y));
			lastWritingTime = System.currentTimeMillis();
		}
		else {
			String[] messages = btService.read();
			if(messages == null || messages.length == 0) return;
			String latest = messages[messages.length-1];
			System.out.println("latest message: "+latest);
			String[] split = latest.split(",");
			float _x = Float.parseFloat(split[0]);
			float _y = Float.parseFloat(split[1]);
			x = _x;
			y = _y;
		}
	}*/


	@Override
	public void create () {
		GameController gameController = new GameController();
		gameModel = gameController.getGameModel();

		world = new World(new Vector2(0, -980f/PIXELS_TO_METERS),true);

		batch = new SpriteBatch();
		ballView = new BallView(gameModel,world, batch);
		backgroundView = new BackgroundView(gameModel, batch);
		platformView = new PlatformView(gameModel,world, batch);
		itemView = new ItemView(gameModel,world,batch);

		BodyDef bodyDef3 = new BodyDef();
		bodyDef3.type = BodyDef.BodyType.StaticBody;
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		bodyDef3.position.set(0,0);

		FixtureDef fixtureDef3 = new FixtureDef();
		EdgeShape edgeShape = new EdgeShape();
		edgeShape.set(-w / 2, 0, w / 2, 0);
		System.out.println("w: "+w+" h: "+h);
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
				if((fA == body2 && fB == body1) ||
						(fA == body1 && fB == body2)) {

					System.out.println("CONTACT " + "bodyC: " + body1.getPosition().x + " " + body1.getPosition().y);
					System.out.println(""+fA+" "+fB);
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
		backgroundView.draw();
		batch.enableBlending();
		ballView.draw();
		//platformView.drawMainPlatform();
		platformView.draw();
		itemView.draw();

		batch.end();

		debugRenderer.render(world, debugMatrix);


	}

}

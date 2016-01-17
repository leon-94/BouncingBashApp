package de.lmu.ifi.bouncingbash.app;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;

import de.lmu.ifi.bouncingbash.app.game.CollisionHandler;
import de.lmu.ifi.bouncingbash.app.game.GameController;
import de.lmu.ifi.bouncingbash.app.game.models.GameModel;
import de.lmu.ifi.bouncingbash.app.game.views.BackgroundView;
import de.lmu.ifi.bouncingbash.app.game.views.BallView;
import de.lmu.ifi.bouncingbash.app.game.views.BodyView;
import de.lmu.ifi.bouncingbash.app.game.views.ItemView;
import de.lmu.ifi.bouncingbash.app.game.views.PlatformView;


/***Spiel das gezeichnet wird**/
public class Game extends ApplicationAdapter  {

	private IBluetoothService btService;
	private IActivity activity;
	private boolean isHost;
	private long lastWritingTime;

    private SpriteBatch batch;
	private GameModel gameModel;

	private World world;


	final float PIXELS_TO_METERS = 100f;
	Box2DDebugRenderer debugRenderer;
	private BodyView platformView,ballView,itemView;
	private BackgroundView backgroundView;
	/**ArrayList für alle views mit bodys**/
	private ArrayList<BodyView> bodyViews = new ArrayList<BodyView>();



	public Game(IActivity act, IBluetoothService bts, boolean host) {
		super();
		btService = bts;
		btService.setQueueing(true);
		isHost = host;

		activity = act;
	}
	/**nur für mich zum testen :D **/
	public Game()
	{
		super();
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
		//views initiallisieren und der ArrayList hinzufügen
		ballView = new BallView(gameModel,world, batch);
		backgroundView = new BackgroundView(gameModel, batch);
		platformView = new PlatformView(gameModel,world, batch);
		itemView = new ItemView(gameModel,world,batch);
		bodyViews.add(ballView);
		bodyViews.add(platformView);
		bodyViews.add(itemView);
		/**initialisiere den CollisionHandler**/
		CollisionHandler collisionHandler = new CollisionHandler(bodyViews,world,gameModel);
		collisionHandler.collision();
		debugRenderer = new Box2DDebugRenderer();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		world.step(1f / 30f, 6, 2);


		//batch.disableBlending();
		batch.begin();

		//Matrix4 debugMatrix = batch.getProjectionMatrix().cpy().scale(PIXELS_TO_METERS,
			//	PIXELS_TO_METERS, 0);
		//backgroundView.draw();
		//batch.enableBlending();
		for(BodyView b : bodyViews)
		{
			b.draw();
		}

		batch.end();

		//debugRenderer.render(world, debugMatrix);


	}

}

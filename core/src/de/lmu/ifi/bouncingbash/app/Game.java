package de.lmu.ifi.bouncingbash.app;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Button;

import de.lmu.ifi.bouncingbash.app.game.models.Ball;
import de.lmu.ifi.bouncingbash.app.game.models.GameModel;
import de.lmu.ifi.bouncingbash.app.game.models.JumpStates;

/***Spiel das gezeichnet wird**/
public class Game extends ApplicationAdapter implements InputProcessor {
	private SpriteBatch batch;
	private Texture textureBall;
	private Sprite spriteBall;
	private SpriteBatch batchBackground;
	private Texture textureBackground;
	private Sprite spriteBackground;
	private SpriteBatch batchPlatform;
	private Texture texturePlatform;
	private Sprite spritePlatform;
	private GameModel gameModel;
	private Ball b;
	private Body body1,body2,bodyEdgeScreen;

	private World world;

	private RollStates rollStates;


	private static final float MAX_MOVEMENT_SPEED = 250;
	@Override
	public void create () {
		gameModel = new GameModel();
		b = gameModel.getPlayer().getBall();
		b.setyCoordinates(gameModel.getMap().getMainPlatform().getHeight());
		//kontrolliert touch input TODO Accelerometer Abfrage einfügen
		//
		world = new World(new Vector2(0, -100f),true);

		batch = new SpriteBatch();
		textureBall = gameModel.getPlayer().getBall().getTexture();
        spriteBall = gameModel.getPlayer().getBall().getSprite();
		spriteBall.setPosition(Gdx.graphics.getWidth() / 2 - spriteBall.getWidth() / 2,
				Gdx.graphics.getHeight() / 2);
        textureBackground = gameModel.getMap().getBackGround();
        spriteBackground = new Sprite(textureBackground);
		texturePlatform = gameModel.getMap().getMainPlatform().getTexture();
		spritePlatform = new Sprite(texturePlatform);
		System.out.println("Game: " + Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer));
		//TODO
		//Controller c = new Controller(gameModel,body1);
		Gdx.input.setInputProcessor(this);
		//
		PolygonShape shape1 = new PolygonShape();
		shape1.setAsBox(spriteBall.getWidth() / 2, spriteBall.getHeight() / 2);
		PolygonShape shape2 = new PolygonShape();
		shape2.setAsBox(spritePlatform.getWidth()/2 , spritePlatform.getHeight() /2 );

		// Sprite1's Physics body
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(spriteBall.getX() , spriteBall.getY() );
		body1= world.createBody(bodyDef);

		// Sprite2's physics body
		BodyDef bodyDef2 = new BodyDef();
		bodyDef2.type = BodyDef.BodyType.StaticBody;
		bodyDef2.position.set(spritePlatform.getX(), spritePlatform.getY());
		body2 = world.createBody(bodyDef2);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape1;
		fixtureDef.density = 1f;

		Fixture fixture = body1.createFixture(fixtureDef);
		Fixture fixture2 = body2.createFixture(fixtureDef);


		BodyDef bodyDef3 = new BodyDef();
		bodyDef3.type = BodyDef.BodyType.StaticBody;
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		bodyDef3.position.set(0,0);

		FixtureDef fixtureDef2 = new FixtureDef();
		EdgeShape edgeShape = new EdgeShape();
		edgeShape.set(-w/2,0,w/2,0);
		System.out.println("w: "+w+" h: "+h);
		fixtureDef2.shape = edgeShape;

		bodyEdgeScreen = world.createBody(bodyDef3);
		bodyEdgeScreen.createFixture(fixtureDef2);
		edgeShape.dispose();
		// Shape is the only disposable of the lot, so get rid of it
		shape1.dispose();
		shape2.dispose();

		collision();
	}

	private void collision() {
		world.setContactListener(new ContactListener() {
			@Override
			public void beginContact(Contact contact) {
				Body fA = contact.getFixtureA().getBody();
				Body fB = contact.getFixtureB().getBody();
				if((fA == body2 && fB == body1) ||
						(fA == body1 && fB == body2)) {
					body1.applyForceToCenter(0, 600,true);


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
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		world.step(1f / 60f, 6, 2);
		// Now update the spritee position accordingly to it's now updated Physics body
		spriteBall.setPosition(body1.getPosition().x, body1.getPosition().y);

		batch.disableBlending();
		batch.begin();
		batch.draw(spriteBackground, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.enableBlending();
		batch.draw(spriteBall, spriteBall.getX(), spriteBall.getY());

		batch.draw(texturePlatform,
				gameModel.getMap().getMainPlatform().getHeight()
				, 0,
				gameModel.getMap().getMainPlatform().getWidth(),
				gameModel.getMap().getMainPlatform().getHeight());
		roll();
		//System.out.println("X: "+ spriteBall.getX()+" Y: "+ spriteBall.getY());
		//System.out.println("X1: "+ body1.getPosition().x+" Y1: "+ body1.getPosition().y);
		batch.end();

	}

	public void roll()
	{
		float adjustedY = Gdx.input.getAccelerometerY();
		body1.setLinearVelocity(adjustedY * 40, 0);
		if( (adjustedY<1||adjustedY>-1) && RollStates.STAND!=rollStates)
		{
			rollStates=RollStates.STAND;
		}
		if(adjustedY>1 && RollStates.RIGHT!=rollStates)
		{
			body1.setLinearVelocity(adjustedY * 40, 0);
			rollStates=RollStates.RIGHT;
		}
		if(adjustedY<-1 && RollStates.LEFT!=rollStates)
		{
			body1.setLinearVelocity(adjustedY * 40, 0);
			rollStates=RollStates.LEFT;
		}
	}



	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}
	/**jump**/
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		Gdx.app.log("Controller", "pressed Jump");

		body1.setLinearVelocity(0, 300f);


		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}

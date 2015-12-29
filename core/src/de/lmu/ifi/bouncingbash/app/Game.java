package de.lmu.ifi.bouncingbash.app;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
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
	private Body body1,body2;

	private World world;


	private static final float MAX_MOVEMENT_SPEED = 250;
	@Override
	public void create () {
		gameModel = new GameModel();
		b = gameModel.getPlayer().getBall();
		b.setyCoordinates(gameModel.getMap().getMainPlatform().getHeight());
		//kontrolliert touch input TODO Accelerometer Abfrage einfügen
		//
		world = new World(new Vector2(0, -30f),true);

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

		// Sprite1's Physics body
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(spriteBall.getX() , spriteBall.getY() );
		body1= world.createBody(bodyDef);

		// Sprite2's physics body
		BodyDef bodyDef2 = new BodyDef();
		bodyDef2.type = BodyDef.BodyType.DynamicBody;
		bodyDef2.position.set(spritePlatform.getX() , spritePlatform.getY() );
		body2 = world.createBody(bodyDef2);
		//TODO
		//Controller c = new Controller(gameModel,body1);
		Gdx.input.setInputProcessor(this);
		//
		PolygonShape shape1 = new PolygonShape();
		shape1.setAsBox(spriteBall.getWidth() / 2, spriteBall.getHeight() / 2);
		PolygonShape shape2 = new PolygonShape();
		shape2.setAsBox(spritePlatform.getWidth()/2 , spritePlatform.getHeight() /2 );


		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape1;
		fixtureDef.density = 1f;

		Fixture fixture = body1.createFixture(fixtureDef);
		Fixture fixture2 = body2.createFixture(fixtureDef);

		// Shape is the only disposable of the lot, so get rid of it
		shape1.dispose();
		shape2.dispose();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		world.step(Gdx.graphics.getDeltaTime(), 6, 2);
		// Now update the spritee position accordingly to it's now updated Physics body
		spriteBall.setPosition(body1.getPosition().x, body1.getPosition().y);

		batch.disableBlending();
		batch.begin();
		batch.draw(spriteBackground, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.enableBlending();
		batch.draw(spriteBall, spriteBall.getX(), spriteBall.getY());
		//
		batch.draw(texturePlatform,
				gameModel.getMap().getMainPlatform().getHeight()
				, 0,
				gameModel.getMap().getMainPlatform().getWidth(),
				gameModel.getMap().getMainPlatform().getHeight());
		roll();
		//jump();
		System.out.println("X: "+ spriteBall.getX()+" Y: "+ spriteBall.getY());
		batch.end();

	}

	public void roll()
	{

		// y: -2 (left), 0 (still), 2 (right)
		float adjustedY = Gdx.input.getAccelerometerY();
		//if( adjustedY < - 2f ) adjustedY = - 2f; else if( adjustedY > 2f ) adjustedY = 2f;
		// since 2 is 100% of movement speed, let's calculate the final speed percentage
		//adjustedY /= 2;

		// notice the inverted axis because the game is displayed in landscape mode()20 temporärer speed
		//b.getSprite().setX(b.getSprite().getX() + adjustedY*20f );
		body1.setLinearVelocity(adjustedY*40, 0);


	}
	//prüft JumpStatus der Kugel fals er nicht auf standing ist wird ein Sprung oder ein Fall ausgeführt
	/**public void jump()
	{
		JumpStates j = b.getJumpState();
		float speed = b.getJumpSpeed();
		if (j == JumpStates.ASCENDING)
		{
			//We're ascending. Decrease the Y coordinate of the sprite by the speed.
			//DecreaseYCoordinateBy(Speed);
			b.setyCoordinates(b.getyCoordinates()+b.getJumpSpeed());
			b.setJumpSpeed(b.getJumpSpeed()-1);
			// DecreaseByValue(Speed, Value); //The character needs to jump smoothly, so the speed should decrease as he ascends.
			if (b.getJumpSpeed() <= 0)
			{
				b.setJumpState(JumpStates.DESCENDING);//If speed is <= 0, then the character should fall down.
				b.setJumpSpeed(0);
			}
		}
		else if (j == JumpStates.DESCENDING)
		{
			//We're descending. Increase the Y coordinate by the speed (at first, it's 0).
			b.setyCoordinates(b.getyCoordinates()-b.getJumpSpeed());
			b.setJumpSpeed(b.getJumpSpeed()+1); //Increase the speed, so the character falls gradually faster.
			if (b.getyCoordinates() >= b.getyCoordinatesOriginal())
			{
				//If we reached the original Y coordinate, we hit the ground. Mark the character as standing.
				b.setJumpState(JumpStates.STANDING);
				b.setyCoordinates(b.getyCoordinatesOriginal());
				b.setJumpSpeed(b.getOriginalJumpSpeed());
			}
		}
	}**/


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
		/**Ball b = gameModel.getPlayer().getBall();
		 Gdx.app.log("Controller", "getJumpSpeed : "+b.getJumpSpeed()+" getOriginalJumpSpeed : "+b.getOriginalJumpSpeed()
		 +" getJumpState : "+b.getJumpState()+" getyCoordinates : "+b.getyCoordinates()
		 +" getyCoordinatesOriginal : "+b.getyCoordinatesOriginal());
		 if(b.getJumpState() != JumpStates.ASCENDING  || b.getJumpState() != JumpStates.DESCENDING   )
		 {
		 b.setJumpState(JumpStates.ASCENDING);
		 b.setyCoordinatesOriginal(b.getyCoordinates());
		 }**/
		body1.setLinearVelocity(0, 50f);

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

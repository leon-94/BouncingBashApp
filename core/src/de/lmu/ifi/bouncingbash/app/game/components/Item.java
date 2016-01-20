package de.lmu.ifi.bouncingbash.app.game.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.eclipsesource.json.JsonObject;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import de.lmu.ifi.bouncingbash.app.game.Constants;
import de.lmu.ifi.bouncingbash.app.game.Game;
import de.lmu.ifi.bouncingbash.app.game.GameData;
import de.lmu.ifi.bouncingbash.app.game.Textures;
import de.lmu.ifi.bouncingbash.app.game.UpgradeType;

/**
 * Created by Michi on 20.01.2016.
 */
public class Item extends PhysicsObject {

        private int width=100,height=100,x=0,y=0;
        private boolean spawn=true;
        public boolean taken=true;
        private UpgradeType upgradeType=null;
        private Game g;

        public Item(Game g, World w, int x, int y) {
            super(g, w);
            this.x=x;
            this.y=y;
            this.g=g;
            Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);

            pixmap.setColor(Color.BLACK);
            pixmap.fillCircle(0,0,width/2);

            texture = new Texture(pixmap);
            sprite = new Sprite(texture);
            sprite.setPosition(x, y);
        }
    @Override
    public void onCollision(Contact contact, Vector2 contactpoint, Body a, Body b) {
        if(a != body && b != body) return;
        Gdx.app.log("ITEM", "onCollision");

        if( (a == game.myBall.getBody() || a == game.otherBall.getBody() ||
                b == game.myBall.getBody() || b == game.otherBall.getBody())) {
            taken=true;
            switch(upgradeType)
            {
                case SPEEDUP:
                    break;
                case FIREUP:
                    break;
                default:
                    break;
            }
           // game.animationHandler.switchtAnim(new Vector2(sprite.getX() + sprite.getWidth()/2, sprite.getY() + sprite.getHeight()/2));

        }
    }

    /**Methode fürs itemSpawnen:
     * Startet einen Timertask mit einer zufalligen Zeit zwischen 5 und 20s
     * Nach dem die Zeit vergangen ist wird der ItemArraylist ein Item hinzugefügt
     *
     * **/
    public void spawnItem()
    {
        if(spawn ) {
            spawn = false;
            //5-20s spawnzeit
            int random = randInt(5,20);
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                            //setze den typen des upgrades speed, fire etc.
                            upgradeType = UpgradeType.randomUpgrade();

                    createItem();


                    taken=false;
                    }

            };

            Timer timer = new Timer();
            timer.schedule(timerTask, random * 1000);

        }
    }
    /**creates item sprite and body**/
    public void createItem()
    {
        sprite=new Sprite(Textures.getTextures().getTexture(upgradeType.getName()));
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
    @Override
    public void render(SpriteBatch batch) {
        Vector2 position = body.getPosition();
        //setze einen timer für den nächsten spawn (aber nur wenn du host bist)
        if(GameData.isHost){
            spawnItem();
        }

        sprite.setPosition(position.x * Constants.PIXELS_TO_METERS - sprite.getWidth() / 2, position.y * Constants.PIXELS_TO_METERS - sprite.getHeight() / 2);
        sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
        sprite.draw(batch, alphaValue);
    }
    private static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }
    public JsonObject toJson() {
        JsonObject jsonItem = new JsonObject();

        Vector2 position = body.getPosition();

        jsonItem.add("posxItem", position.x);
        jsonItem.add("posyItem", position.y);
        jsonItem.add("taken", taken);
        jsonItem.add("upgradeType", upgradeType.getName());
        return jsonItem;
    }
    public synchronized void processGameData(JsonObject message) {

        JsonObject jsonItem = (JsonObject) message.get("item");

        float posxItem = jsonItem.getFloat("posxItem", 0);
        float posyItem = jsonItem.getFloat("posyItem", 0);
        boolean taken2= jsonItem.getBoolean("taken", false);
        String upgradeType2 = jsonItem.getString("upgradeType", "");

        for(UpgradeType up:UpgradeType.values())
        {
            if(up.getName()==upgradeType2)
            {
                upgradeType=up;
            }
        }

                if(posxItem ==x&&posyItem ==y &&upgradeType==null)
                {
                    sprite=new Sprite(Textures.getTextures().getTexture(upgradeType.getName()));
                    taken=taken2;
                }



    }
}

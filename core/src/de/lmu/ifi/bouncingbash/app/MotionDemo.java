package de.lmu.ifi.bouncingbash.app;

/**
 * Created by Michi on 27.12.2015.
 */
        import com.badlogic.gdx.Gdx;
        import com.badlogic.gdx.ApplicationListener;
        import com.badlogic.gdx.Input.Orientation;
        import com.badlogic.gdx.Input.Peripheral;
        import com.badlogic.gdx.graphics.Color;
        import com.badlogic.gdx.graphics.GL20;
        import com.badlogic.gdx.graphics.g2d.BitmapFont;
        import com.badlogic.gdx.graphics.g2d.SpriteBatch;
/**Kann statt game in Android Launcher gestartet werden um informationen über den Status des Handys an zu zeigen.
 *  z.B. Winkel des handys,y-achse vom Accelometer**/
public class MotionDemo implements ApplicationListener {

    private SpriteBatch batch;

    private BitmapFont font;

    private String message = "Do something already!";

    private float highestY = 0.0f;

    @Override

    public void create() {

        batch = new SpriteBatch();

        font = new BitmapFont();

        font.setColor(Color.RED);

    }

    @Override

    public void dispose() {

        batch.dispose();

        font.dispose();

    }

    @Override

    public void render() {

        int w = Gdx.graphics.getWidth();

        int h = Gdx.graphics.getHeight();

        Gdx.gl.glClearColor(1, 1, 1, 1);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        int deviceAngle = Gdx.input.getRotation();

        Orientation orientation = Gdx.input.getNativeOrientation();

        float accelY = Gdx.input.getAccelerometerY();

        if(accelY > highestY)

            highestY = accelY;

        message = "Device rotated to:" + Integer.toString(deviceAngle) + " degrees\n";

        message += "Device orientation is ";

        switch(orientation){

            case Landscape:

                message += " landscape.\n";

                break;

            case Portrait:

                message += " portrait. \n";

                break;

            default:

                message += " complete crap!\n";

                break;

        }



        message += "Device Resolution: " + Integer.toString(w) + "," + Integer.toString(h) + "\n";

        message += "Y axis accel: " + Float.toString(accelY) + " \n";

        message += "Highest Y value: " + Float.toString(highestY) + " \n";

        if(Gdx.input.isPeripheralAvailable(Peripheral.Vibrator)){

            if(accelY > 7){

                Gdx.input.vibrate(100);

            }

        }

        if(Gdx.input.isPeripheralAvailable(Peripheral.Compass)){

            message += "Azmuth:" + Float.toString(Gdx.input.getAzimuth()) + "\n";

            message += "Pitch:" + Float.toString(Gdx.input.getPitch()) + "\n";

            message += "Roll:" + Float.toString(Gdx.input.getRoll()) + "\n";

        }

        else{

            message += "No compass available\n";

        }

        font.draw(batch, message, 0, h);

        batch.end();

    }

    @Override

    public void resize(int width, int height) {

        batch.dispose();

        batch = new SpriteBatch();

        String resolution = Integer.toString(width) + "," + Integer.toString(height);

        Gdx.app.log("MJF", "Resolution changed " + resolution);

    }

    @Override

    public void pause() {

    }

    @Override

    public void resume() {

    }



}

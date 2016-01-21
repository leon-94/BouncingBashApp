package de.lmu.ifi.bouncingbash.app.game;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Leon on 11.01.2016.
 */
public class Constants {
    public static final float PIXELS_TO_METERS = 100;
    public static final float SIM_FREQUENCY = 1/60f;
    public static final float CONN_FREQUENCY_READ = 1/30f;
    public static final float CONN_FREQUENCY_WRITE = 1/20f;
    public static final int CONN_REPETITIONS = 3;
    public static final float CONN_MAX_DELTA_TIME_TOLERANCE = 0.1f;
    public static final float RECON_FACTOR = 2f;
    public static final float ALLOWED_DEVIATION = 2f;   // in meters
    public static final float HEIGHT = 1080f;
    public static final float WIDTH = 1920f;
    public static final int LIVES = 5;

    public static final float GRAVITY = 20f;
    public static final float ACCEL_STRENGTH = 24000f;
    public static final int BALL_RADIUS = 64;
    public static final float BALL_SPAWN_DURATION = 1;
    public static final float JUMP_STRENGTH = 100000f;
    public static final float JUMP_FREQ = 1f;
    public static final Vector2 RESPAWN = new Vector2(WIDTH/2 - BALL_RADIUS, 100);
    public static final float GRAVITY_TOGGLE_COOLDOWN = 1f;
    public static final int MAX_LIVES = 5;
}

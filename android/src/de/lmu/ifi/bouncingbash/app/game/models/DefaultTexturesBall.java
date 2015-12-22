package de.lmu.ifi.bouncingbash.app.game.models;

import android.graphics.Bitmap;

/**
 * Created by Michael on 11.12.2015.
 */
public enum DefaultTexturesBall {
    TEXTURES_BALL1("C:\\Users\\Gabriele\\Desktop\\gameBilder");

    private String path;
    DefaultTexturesBall(String path)
    {
        this.path = path;
    }
    public String getPath()
    {
        return path;
    }
}

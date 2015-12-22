package de.lmu.ifi.bouncingbash.app.game.view;

/**
 * Created by Michi on 14.12.2015.
 */
import android.graphics.Canvas;

public interface ISurface {
    void onInitalize();
    void onDraw(Canvas canvas);
    void onUpdate(long gameTime);
}

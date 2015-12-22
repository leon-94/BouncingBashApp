package de.lmu.ifi.bouncingbash.app.game.view;

/**
 * Created by Michi on 14.12.2015.
 */
import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class AnimationThread extends Thread {
    private SurfaceHolder surfaceHolder;
    private ISurface panel;
    private boolean run = false;

    public AnimationThread(SurfaceHolder surfaceHolder, ISurface panel) {
        this.surfaceHolder = surfaceHolder;
        this.panel = panel;

        panel.onInitalize();
    }

    public void setRunning(boolean value) {
        run = value;
    }

    private long timer;

    @Override
    public void run() {

        Canvas c;
        while (run) {
            c = null;
            timer = System.currentTimeMillis();
            panel.onUpdate(timer);

            try {
                c = surfaceHolder.lockCanvas(null);
                synchronized (surfaceHolder) {
                    panel.onDraw(c);
                }
            } finally {
                if (c != null) {
                    surfaceHolder.unlockCanvasAndPost(c);
                }
            }
        }
    }
}

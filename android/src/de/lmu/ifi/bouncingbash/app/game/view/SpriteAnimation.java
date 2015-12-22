package de.lmu.ifi.bouncingbash.app.game.view;

/**
 * Created by Michi on 14.12.2015.
 */
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.animation.TranslateAnimation;

public class SpriteAnimation {
    private Bitmap animation;
    private int x;
    private int y;
    private Rect sRectangle;
    private int fps;
    private int numFrames;
    private int currentFrame;
    private long frameTimer;
    private int spriteHeight;
    private int spriteWidth;

    public SpriteAnimation() {
        sRectangle = new Rect(0, 0, 0, 0);
        frameTimer = 0;
        currentFrame = 0;
        x = 80;
        y = 200;
    }

    public void Initialize(Bitmap bitmap, int height, int width, int fps, int frameCount) {
        this.animation = bitmap;
        this.spriteHeight = height;
        this.spriteWidth = width;
        this.sRectangle.top = 0;
        this.sRectangle.bottom = spriteHeight;
        this.sRectangle.left = 0;
        this.sRectangle.right = spriteWidth;
        this.fps = 1000 / fps;
        this.numFrames = frameCount;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int value) {
        x = value;
    }

    public void setY(int value) {
        y = value;
    }

    public void Update(long gameTime) {
        if( gameTime > frameTimer + fps) {
            frameTimer = gameTime;
            currentFrame += 1;

            if( currentFrame >= numFrames ) {
                currentFrame = 0;
            }

            sRectangle.left = currentFrame * spriteWidth;
            sRectangle.right = sRectangle.left + spriteWidth;
        }
    }

    public void draw(Canvas canvas) {
        Rect dest = new Rect(getX(), getY(), getX() + spriteWidth,
                getY() + spriteHeight);
        canvas.drawBitmap(animation, sRectangle, dest, null);

    }
}

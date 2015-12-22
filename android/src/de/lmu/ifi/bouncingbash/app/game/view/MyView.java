package de.lmu.ifi.bouncingbash.app.game.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import de.lmu.ifi.bouncingbash.app.android.R;

/**
 * Created by Leon on 28.11.2015.
 */
public class MyView extends View{

    public static final int MESSAGE_UPDATE_POSITION = 0;

    private Handler handler;

    private Paint paint;
    private int x;
    private int y;

    public MyView(Context context, AttributeSet params) {
        super(context, params);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLUE);

        x = 100;
        y = 100;
    }

    public void init(Handler h) {
        handler = h;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(x, y, 30, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch(event.getAction()){
            case MotionEvent.ACTION_UP:
                setCirclePosition((int)event.getX(), (int)event.getY());
                handler.obtainMessage(MESSAGE_UPDATE_POSITION, x, y).sendToTarget();
                break;
        }
        return true;
    }

    public void setCirclePosition(int x, int y) {
        this.x = x;
        this.y = y;
        invalidate();
    }
}

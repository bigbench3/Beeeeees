package cage.elon.com.beeeees;

/**
 * Created by hcesko on 10/7/2015.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.Display;
import android.view.WindowManager;

public class NickCage {

    protected int x, y;
    private float width, height;
    private Bitmap bitmap;

    private int screenWidth, screenHeight;

    private final float SCALE = 0.3f;

    public NickCage(Context context) {

        // get the image
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.nick);

        // scale the size
        width = bitmap.getWidth() * SCALE;
        height = bitmap.getHeight() * SCALE;

        // figure out the screen width
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        // start in center
        x = screenWidth/2;
        y = screenHeight/2;
    }

    public void doDraw(Canvas canvas) {
        // draw the bird
        canvas.drawBitmap(bitmap,
                null,
                new Rect((int) (x - width/2), (int) (y- height/2),
                        (int) (x + width/2), (int) (y + height/2)),
                null);
    }

    public void doUpdate(float sensorX ,float sensorY) {
        x = x + (int) sensorX;
        y = y + (int) sensorY;

        if(x + (int)sensorX < 64) {
            x = 64;
        }
        if(x + (int)sensorX > screenWidth - 64) {
            x = screenWidth - 64;
            //System.out.println(screenHeight);
        }
        if(y + (int)sensorY < 64) {
            y = 64;
        }
        if(y + (int)sensorY > screenHeight - 100) {
            y = screenHeight - 100;
        }
    }

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }

    public float getWidth(){
        return width;
    }

    public float getHeight(){
        return height;
    }

    public void notTheBees(Context context){
        bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.beecage);
    }

    public void noMoreBees(Context context){
        bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.nick);
    }
}

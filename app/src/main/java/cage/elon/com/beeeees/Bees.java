package cage.elon.com.beeeees;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Vibrator;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by hcesko on 10/7/2015.
 */
public class Bees {


    protected float x, y;
    private float width, height;
    private Bitmap bitmap;

    private int screenWidth, screenHeight;

    private final float SCALEX = 0.08f;
    private final float SCALEY = 0.1f;

    private double timeOnBees;

    public Bees(Context context) {

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize((size));
        screenWidth = size.x;
        screenHeight = size.y;

        // get the image
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.bees);

        // scale the size
        width = bitmap.getWidth() * SCALEX;
        height = bitmap.getHeight() * SCALEY;

        x = (float) Math.random()* (screenWidth - width);
        y = (float) Math.random()* (screenHeight + height);

        if(x - width/2 < screenWidth) {
            if(x < width) {
                x = x + width/2;
            }
        } if(x + width/2  > screenWidth) {
            if (x > screenWidth - width) {
                x = screenWidth - width;
            }
        } if( y - height/2 < screenHeight) {
            if (y < height) {
                y = y + height/2;
            }
        } if(y + height/2 > screenHeight) {
            if (y > screenHeight - height) {
                y = screenHeight - height;
            }
        }
    }

    public void doDraw(Canvas canvas) {
        // draw Nick
        canvas.drawBitmap(bitmap,
                null,
                new Rect((int) (x - width/2), (int) (y- height/2),
                        (int) (x + width/2), (int) (y + height/2)),
                null);
    }

    public void doUpdate(double elapsed, NickCage nick) {
        if(nick.getX()-(nick.getWidth()/2) > getX() - (getWidth()/2)&&
                nick.getX()+(nick.getWidth()/2) < getX() + (getWidth()/2)&&
                nick.getY()-(nick.getHeight()/2) > getY() - (getHeight()/2)&&
                nick.getY()+(nick.getHeight()/2) < getY() + (getHeight()/2)) {

            timeOnBees += elapsed;

            if(timeOnBees >= 3){
                x = (float) Math.random()* (screenWidth - width);
                y = (float) Math.random()* (screenHeight + height);

                if(x - width/2 < screenWidth) {
                    if(x < width) {
                        x = x + width/2;
                    }
                } if(x + width/2  > screenWidth) {
                    if (x > screenWidth - width) {
                        x = screenWidth - width;
                    }
                } if( y - height/2 < screenHeight) {
                    if (y < height) {
                        y = y + height/2;
                    }
                } if(y + height/2 > screenHeight) {
                    if (y > screenHeight - height) {
                        y = screenHeight - height;
                    }
                }

                timeOnBees = 0;
            }
        } else {
            timeOnBees = 0;
        }

    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth(){
        return width;
    }

    public float getHeight(){
        return height;
    }

}

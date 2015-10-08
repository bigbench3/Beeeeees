package cage.elon.com.beeeees;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.util.ArrayList;

/**
 * Created by hcesko on 10/7/2015.
 */
public class GameLoopView extends SurfaceView implements SurfaceHolder.Callback, SensorEventListener {

    private GameLoopThread thread;
    private SurfaceHolder surfaceHolder;
    private Context context;
    private SensorManager sensorManager;
    private Sensor mAccel;
    private float x,y;
    private double time =0;
    private int screenWidth, screenHeight;

    public GameLoopView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // remember the context for finding resources
        this.context = context;

        // want to know when the surface changes
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        // game loop thread
        thread = new GameLoopThread();

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, mAccel, SensorManager.SENSOR_DELAY_NORMAL);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize((size));
        screenWidth = size.x;
        screenHeight = size.y;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        y = event.values[0];
        x = event.values[1];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    // SurfaceHolder.Callback methods:
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        // thread exists, but is in terminated state
        if (thread.getState() == Thread.State.TERMINATED) {
            thread = new GameLoopThread();
        }

        // start the game loop
        thread.setIsRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        boolean retry = true;
        thread.setIsRunning(false);

        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }


    // Game Loop Thread
    private class GameLoopThread extends Thread {

        private boolean isRunning = false;
        private long lastTime;
        private Bitmap bitmap;

        // the nick sprite
        private NickCage nick;

        // The bees sprite
        private Bees bees;


        // frames per second calculation
        private int frames;
        private long nextUpdate;

        public GameLoopThread() {
            nick = new NickCage(context);
            bees = new Bees(context);

            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.board);
        }

        public void setIsRunning(boolean isRunning) {
            this.isRunning = isRunning;
        }

        // the main loop
        @Override
        public void run() {

            lastTime = System.currentTimeMillis();

            while (isRunning) {

                // grab hold of the canvas
                Canvas canvas = surfaceHolder.lockCanvas();
                if (canvas == null) {
                    // trouble -- exit nicely
                    isRunning = false;
                    continue;
                }

                synchronized (surfaceHolder) {

                    // compute how much time since last time around
                    long now = System.currentTimeMillis();
                    double elapsed = (now - lastTime) / 1000.0;
                    lastTime = now;

                    // update/draw
                    doUpdate(elapsed);
                    doDraw(canvas);

                    //updateFPS(now);
                }

                // release the canvas
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }

        // an approximate frames per second calculation
        private void updateFPS(long now) {
            float fps = 0.0f;
            ++frames;
            float overtime = now - nextUpdate;
            if (overtime > 0) {
                fps = frames / (1 + overtime/1000.0f);
                frames = 0;
                nextUpdate = System.currentTimeMillis() + 1000;
                System.out.println("FPS: " + (int) fps);
            }
        }

        /* THE GAME */

        // move all objects in the game
        private void doUpdate(double elapsed) {

            nick.doUpdate(x, y);
            bees.doUpdate(elapsed, nick);

            if(nick.getX()-(nick.getWidth()/2) > bees.getX() - (bees.getWidth()/2)&&
                    nick.getX()+(nick.getWidth()/2) < bees.getX() + (bees.getWidth()/2)&&
                    nick.getY()-(nick.getHeight()/2) > bees.getY() - (bees.getHeight()/2)&&
                    nick.getY()+(nick.getHeight()/2) < bees.getY() + (bees.getHeight()/2)){
                nick.notTheBees(context);
            } else {
                nick.noMoreBees(context);
            }
        }

        // draw all objects in the game
        private void doDraw(Canvas canvas) {
            // draw the background
            canvas.drawBitmap(bitmap,null,new Rect(0,0,screenWidth,screenHeight),null);

            bees.doDraw(canvas);
            nick.doUpdate(x, y);
            nick.doDraw(canvas);
        }
    }
}

package com.snavi.sensorgame.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.snavi.sensorgame.R;
import com.snavi.sensorgame.activities.GameActivity;
import com.snavi.sensorgame.activities.GameOverActivity;
import com.snavi.sensorgame.activities.MainMenuActivity;
import com.snavi.sensorgame.controls.Shake;
import com.snavi.sensorgame.controls.Smash;
import com.snavi.sensorgame.game_objects.Bullet;
import com.snavi.sensorgame.game_objects.Floor;
import com.snavi.sensorgame.game_objects.Hammer;

import java.util.ArrayList;

public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {


    // const //////////////////////////////////////////////////////////////////////////////////////
    public static final int DEFAULT_OPACITY = 255;

    public static final int[] DEFAULT_BG_COLOR          = {255, 255, 255};
    public static final int[] DEFAULT_GAME_OBJECT_COLOR = {42, 14, 14};

    public static final int[][] DEFAULT_GOOD_COLORS =
            {
                    {69, 197, 26},
                    {23, 52, 241}
            };
    public static final int[][] DEFAULT_BAD_COLORS =
            {
                    {0,0,0},
                    {228, 1, 4}
            };
    public static final int DEFAULT_POINTS_COLOR = Color.BLACK;
    public static final int DEFAULT_SHAKE_COLOR  = Color.GREEN;

    public static final int DEFAULT_FLOOR_HEIGHT = 200;
    public static final int DEFAULT_HAMMER_WIDTH = 300;
    public static final int DEFAULT_SPACE_BETWEEN_FLOOR_AND_HAMMER = 270;
    public static final int DEFAULT_BULLET_SIZE  = 100;
    public static final int DEFAULT_HAMMER_X_FROM_RIGHT = 100;
    public static final int DEFAULT_HAMMER_SPEED = 90;
    public static final int INITIAL_BULLET_SPEED = 15;
    public static final int INITIAL_TICKS_BETWEEN_BULLETS = 70;
    public static final int TIME_BETWEEN_TICKS = 3;
    public static final int TICKS_PER_SPEEDUP  = 500;
    public static final int POINTS_X = 300;
    public static final int POINTS_Y = 300;
    public static final int POINTS_TEXT_SIZE = 300;
    public static final int SHAKE_TEXT_SIZE  = 400;
    public static final int TIME_BETWEEN_OPTIONS_IN_LOTERY = 100;

    public static final String SHAKE_PROMPT = "Shake!";

    public static final int HART_SIZE = 400;


    // fields /////////////////////////////////////////////////////////////////////////////////////
    private Thread          m_drawThread;
    private SurfaceHolder   m_holder;
    private Canvas          m_canvas;

    private int m_screenWidth;
    private int m_screenHeight;

    private RectF m_fullScreenRectF;

    // game objects ///////////////////////////////////////////////////////////////////////////////
    private Floor    m_floor;
    private Hammer   m_hammer;
    private ArrayList<Bullet> m_bullets;

    // game objects dimensions ////////////////////////////////////////////////////////////////////
    private int m_floorHeight;
    private int m_hammerWidth;
    private int m_spaceBetweenFloorAndHammer;
    private int m_bulletSize;                   // length of side of bullets

    // game objects positions /////////////////////////////////////////////////////////////////////
    private int m_hammerX;

    // game mechanics /////////////////////////////////////////////////////////////////////////////
    private int m_hammerSpeed;
    private int m_bulletSpeed;
    private int m_ticksBetweenBullets;

    // game state /////////////////////////////////////////////////////////////////////////////////
    private boolean m_end;
    private int m_ticksSinceBulletGeneration;
    private int m_ticksSinceSpeedUp;

    // appearance /////////////////////////////////////////////////////////////////////////////////
    private Paint m_backgroundPaint;
    private Paint m_floorPaint;
    private Paint m_hammerPaint;
    private Paint m_pointsPaint;    // color of displayed points
    private Paint m_shakePaint;     // color of text "Shake!" displayed when player loses
    private Paint[] m_goodColors;   // colors of bullets that cannot be smashed
    private Paint[] m_badColors;    // colors of bullets that must be smashed

    // images /////////////////////////////////////////////////////////////////////////////////////
    Bitmap m_hartBlack;
    Bitmap m_hartRed   = BitmapFactory.decodeResource(getResources(), R.drawable.hart_red);

    // controls ///////////////////////////////////////////////////////////////////////////////////
    private Smash m_smash;
    private Shake m_shake;

    // stats //////////////////////////////////////////////////////////////////////////////////////
    private int m_points;




    // initialization /////////////////////////////////////////////////////////////////////////////



    public GameView(Context context, int screenWidth, int screenHeight)
    {
        super(context);

        defaultInit(context, screenWidth, screenHeight);
    }



    public GameView(Context context, int screenWidth, int screenHeight, int bulletSpeed, int points)
    {
        super(context);

        defaultInit(context, screenWidth, screenHeight);
        m_bulletSpeed = bulletSpeed;
        m_points      = points;
    }



    private void defaultInit(Context context, int screenWidth, int screenHeight)
    {
        getHolder().addCallback(this);

        m_holder       = getHolder();
        m_screenWidth  = screenWidth;
        m_screenHeight = screenHeight;
        m_end          = false;
        m_ticksSinceBulletGeneration = 0;
        m_bullets = new ArrayList<>();

        setScreenRectF();
        initializeColors(DEFAULT_OPACITY, DEFAULT_BG_COLOR, DEFAULT_GAME_OBJECT_COLOR, DEFAULT_GAME_OBJECT_COLOR, DEFAULT_GOOD_COLORS, DEFAULT_BAD_COLORS);
        initializeDimensions(DEFAULT_FLOOR_HEIGHT, DEFAULT_HAMMER_WIDTH, DEFAULT_SPACE_BETWEEN_FLOOR_AND_HAMMER, DEFAULT_BULLET_SIZE);
        initializeGameObjectsPositions(m_screenWidth - DEFAULT_HAMMER_X_FROM_RIGHT - m_hammerWidth);
        initializeGameMechanics(DEFAULT_HAMMER_SPEED, INITIAL_BULLET_SPEED, INITIAL_TICKS_BETWEEN_BULLETS);
        initializeBitmaps();
        createGameObjects();

        m_smash = new Smash(context, m_hammer);
        m_shake = new Shake(context);
    }



    private void setScreenRectF()
    {
        m_fullScreenRectF           = new RectF();
        m_fullScreenRectF.left      = 0;
        m_fullScreenRectF.top       = 0;
        m_fullScreenRectF.right     = m_screenWidth;
        m_fullScreenRectF.bottom    = m_screenHeight;
    }



    private void initializeDimensions(int floorHeight, int hammerWidth, int spaceBetweenFloorAndHammer, int bulletSize)
    {
        m_floorHeight = floorHeight;
        m_hammerWidth = hammerWidth;
        m_bulletSize  = bulletSize;
        m_spaceBetweenFloorAndHammer = spaceBetweenFloorAndHammer;
    }



    private void initializeGameObjectsPositions(int hammerX)
    {
        m_hammerX = hammerX;
    }



    private void initializeGameMechanics(int hammerSpeed, int bulletSpeed, int ticksBetweenBullets)
    {
        m_hammerSpeed = hammerSpeed;
        m_bulletSpeed = bulletSpeed;
        m_ticksBetweenBullets = ticksBetweenBullets;
    }



    private void initializeColors(int opacity, int[] bgColor, int[] floorColor, int[] hammerColor,
                                  int[][] goodColors, int[][] badColors)
    {
        m_backgroundPaint = new Paint();
        m_backgroundPaint.setARGB(opacity, bgColor[0], bgColor[1], bgColor[2]);

        m_floorPaint = new Paint();
        m_floorPaint.setARGB(opacity, floorColor[0], floorColor[1], floorColor[2]);

        m_hammerPaint = new Paint();
        m_hammerPaint.setARGB(opacity, hammerColor[0], hammerColor[1], hammerColor[2]);

        setBulletsPaints(opacity, goodColors, badColors);
        setTextsPaints();
    }



    private void setBulletsPaints(int opacity, int[][] goodColors, int [][] badColors)
    {
        m_goodColors = new Paint[goodColors.length];
        for (int i = 0; i < m_goodColors.length; i++) m_goodColors[i] = new Paint();
        for (int i = 0; i < goodColors.length; i++) m_goodColors[i].setARGB(opacity, goodColors[i][0], goodColors[i][1], goodColors[i][2]);

        m_badColors = new Paint[badColors.length];
        for (int i = 0; i < m_badColors.length; i++) m_badColors[i] = new Paint();
        for (int i = 0; i < badColors.length; i++) m_badColors[i].setARGB(opacity, badColors[i][0], badColors[i][1], badColors[i][2]);
    }



    private void setTextsPaints()
    {
        m_pointsPaint = new Paint();
        m_pointsPaint.setTextSize(POINTS_TEXT_SIZE);
        m_pointsPaint.setColor(DEFAULT_POINTS_COLOR);

        m_shakePaint = new Paint();
        m_shakePaint.setTextSize(SHAKE_TEXT_SIZE);
        m_shakePaint.setColor(DEFAULT_SHAKE_COLOR);
    }




    private void createGameObjects()
    {
        m_floor = new Floor(m_screenWidth, m_screenHeight, m_floorHeight, m_floorPaint);
        int hammerHeight = m_screenHeight - m_spaceBetweenFloorAndHammer - m_floorHeight;
        m_hammer = new Hammer(m_hammerX, m_hammerWidth, hammerHeight, m_hammerPaint, m_hammerSpeed, m_screenHeight - m_floorHeight);
    }



    private void startDrawThread()
    {
        m_drawThread = new Thread(this);
        m_drawThread.start();
    }



    private void initializeBitmaps()
    {
        m_hartBlack = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(), R.drawable.hart_black),
                HART_SIZE,
                HART_SIZE,
                false);

        m_hartRed = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(), R.drawable.hart_red),
                HART_SIZE,
                HART_SIZE,
                false);
    }



    // game ////////////////////////////////////////////////////////////////////////////////////////



    @Override
    public void run()
    {
        while(!m_end)
        {
            m_canvas = m_holder.lockCanvas();

            drawAll();
            tick();

            m_holder.unlockCanvasAndPost(m_canvas);
            try
            {
                Thread.sleep(TIME_BETWEEN_TICKS);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        gameOver();
    }



    private void tick()
    {
        generateBullet();
        moveBullets();
        m_hammer.tick();
        detectSmashedBullets();
        dealWithBulletsBehindScreen();
        increaseBulletsSpeed();
    }


    /**
     * calculates points
     * detects game over
     * removes old bullets
     */
    private void dealWithBulletsBehindScreen()
    {
        Bullet curr = null;
        for (int i = 0; i < m_bullets.size(); ++i)
        {
            curr = m_bullets.get(i);
            if (curr.getLeft() > m_screenWidth)
            {
                if (curr.isGood()) ++m_points;
                else               m_end = true;
                m_bullets.remove(i);
                --i;
            }
        }
    }



    private void increaseBulletsSpeed()
    {
        ++m_ticksSinceSpeedUp;
        if (m_ticksSinceSpeedUp > TICKS_PER_SPEEDUP)
        {
            m_ticksSinceSpeedUp = 0;
            m_bulletSpeed += 5;
        }
    }



    private void detectSmashedBullets()
    {
        Bullet curr = null;
        for (int i = 0; i < m_bullets.size(); i++)
        {
            curr = m_bullets.get(i);
            if (
                    curr.getLeft() >= m_hammer.getLeft() &&
                    curr.getRight() <= m_hammer.getRight() &&
                    curr.getTop() <= m_hammer.getBottom()
            )
            {
                m_bullets.remove(i);
                --i;
            }
        }
    }



    private void generateBullet()
    {
        if (m_ticksSinceBulletGeneration < m_ticksBetweenBullets)
        {
            m_ticksSinceBulletGeneration++;
            return;
        }

        m_ticksSinceBulletGeneration = 0;

        boolean isGood = ((int) (Math.random() * 2)) == 1;

        Paint paint;
        if(isGood) paint = m_goodColors[(int) (Math.random() * m_goodColors.length)];
        else       paint = m_badColors[(int) (Math.random() * m_badColors.length)];

        Bullet bullet = new Bullet(m_screenHeight - m_floorHeight - m_bulletSize, m_bulletSize, paint, m_bulletSpeed, isGood);
        m_bullets.add(bullet);
    }



    private void moveBullets()
    {
        for (Bullet bullet : m_bullets) bullet.move();
    }



    private void gameOver() {
        boolean nextLife = getNextLife();

        if (nextLife)
        {
            Intent intent = new Intent(getContext(), GameActivity.class);
            intent.putExtra(GameActivity.SAVED_BULLET_SPEED_KEY, m_bulletSpeed);
            intent.putExtra(GameActivity.SAVED_POINTS_KEY, m_points);
            getContext().startActivity(intent);
        }
        else
        {
            Intent intent = new Intent(getContext(), GameOverActivity.class);
            intent.putExtra(GameOverActivity.POINTS_KEY, m_points);
            getContext().startActivity(intent);
        }
    }


    /**
     *
     * @return true if player won next chance
     */
    private boolean getNextLife()
    {
        m_shake.start();
        drawShakePrompt();
        while (!m_shake.isShaking()) {}

        boolean nextLife = runLottery();

        m_shake.stop();
        return nextLife;
    }



    private boolean runLottery()
    {
        boolean nextLife   = false;
        double  randNum    = 0.0;
        long    lastChange = System.currentTimeMillis();

        while (m_shake.isShaking())
        {
            if (System.currentTimeMillis() - lastChange >= TIME_BETWEEN_OPTIONS_IN_LOTERY)
            {
                randNum = Math.random();
                nextLife = randNum > 0.7;
                lastChange = System.currentTimeMillis();

                drawAllLottery(nextLife);
            }
        }

        return nextLife;
    }



    // drawing ////////////////////////////////////////////////////////////////////////////////////



    private void drawAll()
    {
        clearCanvas();
        drawFloor();
        drawHammer();
        drawBullets();
        drawPoints();
    }



    private void clearCanvas()
    {
        m_canvas.drawRect(m_fullScreenRectF, m_backgroundPaint);
    }



    private void drawFloor()
    {
        m_floor.draw(m_canvas);
    }



    private void drawHammer()
    {
        m_hammer.draw(m_canvas);
    }



    private void drawBullets()
    {
        for(Bullet bullet : m_bullets) bullet.draw(m_canvas);
    }



    private void drawPoints()
    {
        m_canvas.drawText(m_points + "", POINTS_X, POINTS_Y, m_pointsPaint);
    }



    private void drawShakePrompt()
    {
        m_canvas = m_holder.lockCanvas();
        m_canvas.drawText(
                SHAKE_PROMPT,
                m_screenWidth / 2 - m_shakePaint.measureText(SHAKE_PROMPT) / 2,
                m_screenHeight / 2 - (m_shakePaint.descent() + m_shakePaint.ascent()) / 2, m_shakePaint);
        m_holder.unlockCanvasAndPost(m_canvas);
    }



    private void drawAllLottery(boolean win)
    {
        m_canvas = m_holder.lockCanvas();
        drawAll();
        if (win)
            m_canvas.drawBitmap(m_hartRed, m_screenWidth / 2 - HART_SIZE / 2, m_screenHeight / 2 - HART_SIZE / 2, new Paint());
        else
            m_canvas.drawBitmap(m_hartBlack, m_screenWidth / 2 - HART_SIZE / 2, m_screenHeight / 2 - HART_SIZE / 2, new Paint());
        m_holder.unlockCanvasAndPost(m_canvas);
    }



    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {

    }



    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
       startDrawThread();
    }



    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {

    }


}


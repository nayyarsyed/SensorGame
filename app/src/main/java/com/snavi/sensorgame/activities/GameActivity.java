package com.snavi.sensorgame.activities;

import android.content.SharedPreferences;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;

import com.snavi.sensorgame.views.GameView;

public class GameActivity extends AppCompatActivity {


    // CONST ///////////////////////////////////////////////////////////////////////////////////////
    public static final String SAVED_BULLET_SPEED_KEY = "saved_bullet_speed";
    public static final String SAVED_POINTS_KEY       = "saved_points";
    public static final int BULLET_SPEED_IDX          = 1;  // idx in array with previous state (if user won another life)
    public static final int POINTS_IDX                = 0;  // idx in array with previous state (if user won another life)



    private int m_screenWidth;
    private int m_screenHeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        updateScreenDimensions();
        GameView gameView;

        int savedSpeed = getIntent().getIntExtra(SAVED_BULLET_SPEED_KEY, -1);
        if(savedSpeed < 0)
        {
            gameView = new GameView(this, m_screenWidth, m_screenHeight);
        }
        else
        {
            int savedPoints = getIntent().getIntExtra(SAVED_POINTS_KEY, -1);
            gameView = new GameView(this, m_screenWidth, m_screenHeight, savedSpeed, savedPoints);
        }

        setContentView(gameView);
    }



    private void updateScreenDimensions()
    {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        m_screenWidth  = size.x;
        m_screenHeight = size.y;
    }


    @Override
    public void onBackPressed() {}

}

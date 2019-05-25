package com.snavi.sensorgame.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.snavi.sensorgame.R;

public class GameOverActivity extends AppCompatActivity {


    // CONST ///////////////////////////////////////////////////////////////////////////////////////
    public static final String POINTS_KEY = "points";


    // fields //////////////////////////////////////////////////////////////////////////////////////
    private int m_points;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        m_points = getIntent().getIntExtra(POINTS_KEY, -1);
        TextView tv_points = findViewById(R.id.game_over_tv_points);
        tv_points.setText(m_points + "");
        updateBest();
        setRestartButtonOnClick();
        setHomeButtonOnClick();
    }



    private void setRestartButtonOnClick()
    {
        ImageButton restart = findViewById(R.id.game_over_but_restart);
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restartGame();
            }
        });
    }



    private void restartGame()
    {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }



    private void setHomeButtonOnClick()
    {
        ImageButton home = findViewById(R.id.game_over_but_home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                home();
            }
        });
    }



    private void home()
    {
        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
    }



    private void updateBest()
    {
        SharedPreferences pref = getSharedPreferences(MainMenuActivity.SHARED_PREFERECNES_NAME, MODE_PRIVATE);
        int currBest = pref.getInt(MainMenuActivity.BEST_KEY, 0);

        if (currBest < m_points)
        {
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt(MainMenuActivity.BEST_KEY, m_points);
            editor.commit();
        }
    }



    @Override
    public void onBackPressed() {}
}

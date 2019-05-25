package com.snavi.sensorgame.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.snavi.sensorgame.R;

public class MainMenuActivity extends AppCompatActivity {


    // CONST //////////////////////////////////////////////////////////////////////////////////////
    public static final String SHARED_PREFERECNES_NAME = "preferences";
    public static final String BEST_KEY                = "best";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setBest();
        setStartButtonOnClick();
        setQuitButtonOnClick();
    }



    private void setStartButtonOnClick()
    {
        Button start = findViewById(R.id.main_menu_but_start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame();
            }
        });
    }



    private void startGame()
    {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }



    private void setQuitButtonOnClick()
    {
        Button but = findViewById(R.id.main_menu_but_quit);
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quitGame();
            }
        });
    }



    private void quitGame()
    {
        finishAffinity();
        System.exit(0);
    }



    private int getBest()
    {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFERECNES_NAME, MODE_PRIVATE);
        return prefs.getInt(BEST_KEY, 0);
    }



    private void setBest()
    {
        TextView tv_best = findViewById(R.id.main_menu_tv_best);
        int best = getBest();
        tv_best.setText(best + "");
    }


    @Override
    public void onBackPressed() {}
}

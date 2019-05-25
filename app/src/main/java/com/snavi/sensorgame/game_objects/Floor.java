package com.snavi.sensorgame.game_objects;

import android.graphics.Paint;
import android.util.Log;

public class Floor extends RectangularGameObject {


    public Floor(int screenWidth, int screenHeight, int height, Paint paint)
    {
        super(
                0,                       // x
                screenHeight - height,   // y
                screenWidth,                // width
                height,                     // height
                paint                       // paint
        );
    }
}

package com.snavi.sensorgame.game_objects;

import android.graphics.Paint;

/**
 * Class that represents squares that are moving from left to right with certain speed. Player goal
 * is to smash bullets in certain color.
 */
public class Bullet extends RectangularGameObject {


    private int m_velocityX;          // if > 0 -> moves towards right
    private int m_x;                  // x position of left edge
    private int m_y;                  // y position of bottom edge
    private boolean m_isGood;         // false -> player must destroy it


    public Bullet(int y, int sideLength, Paint paint, int velocityX, boolean isGood)
    {
        super(-sideLength, y, sideLength, sideLength, paint);   // -sideLength, so that bullet
                                                                // appears gradually
        m_velocityX = velocityX;
        m_isGood    = isGood;
    }



    public void move()
    {
        move(m_velocityX, 0);
    }



    public boolean isGood() {return m_isGood;}


    public void incVelocityX(int amount) {m_velocityX += amount;}

    // getters && setters //////////////////////////////////////////////////////////////////////////



    public void setVelocityX(int speedX)
    {
        m_velocityX = speedX;
    }


}

package com.snavi.sensorgame.game_objects;

import android.graphics.Paint;
import android.util.Log;

public class Hammer extends RectangularGameObject {


    // const
    private final static int WAIT_MODE   = 0;   // hammer is not moving
    private final static int SMASH_MODE  = 1;   // hammer is moving down
    private final static int RETURN_MODE = 2;   // hammer is moving up


    private int m_speed;
    private int m_mode;
    private int m_groundLevel;  // maximum value of y that hammer will move to
    private int m_normalY;      // value of y to which hammer returns after smash


    /**
     *
     * @param x distance of left edge from left
     * @param width
     * @param paint
     * @param speed
     * @param groundLevel maximum value of y that hammer will move to
     */
    public Hammer(int x, int width, int height, Paint paint, int speed, int groundLevel)
    {
        super(x, 0, width, height, paint);

        m_speed         = speed;
        m_groundLevel   = groundLevel;
        m_normalY       = height;
        m_mode          = WAIT_MODE;
    }



    public void tick()
    {
        // change modes if necessary
        if (m_rectF.bottom > m_groundLevel)
        {
            m_rectF.bottom = m_groundLevel - 1;
            m_mode = RETURN_MODE;
        }
        if (m_rectF.bottom < m_normalY)
        {
            m_mode = WAIT_MODE;
            m_rectF.bottom = m_normalY;
        }

        // move hammer
        if (m_mode == SMASH_MODE)  m_rectF.bottom += m_speed;
        if (m_mode == RETURN_MODE) m_rectF.bottom -= m_speed;
    }



    public void smash() { m_mode = SMASH_MODE; }

}

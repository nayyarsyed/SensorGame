package com.snavi.sensorgame.game_objects;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;


/**
 * Class which represents any rectangular object displayed in game.
 */
public class RectangularGameObject implements GameObject {


    protected Paint m_paint;
    protected RectF m_rectF;


    public RectangularGameObject(int x, int y, int width, int height, Paint paint)
    {
        m_paint  = paint;

        m_rectF         = new RectF();
        m_rectF.left    = x;
        m_rectF.top     = y;
        m_rectF.right   = x + width;
        m_rectF.bottom  = y + height;
    }



    @Override
    public void draw(Canvas canvas)
    {
        canvas.drawRect(m_rectF, m_paint);
    }



    public void move(int dx, int dy)
    {
        m_rectF.left    += dx;
        m_rectF.right   += dx;
        m_rectF.top     += dy;
        m_rectF.bottom  += dy;
    }



    public int getLeft()    {return (int) m_rectF.left;  }
    public int getTop()     {return (int) m_rectF.top;   }
    public int getRight()   {return (int) m_rectF.right; }
    public int getBottom()  {return (int) m_rectF.bottom;}

    public Paint getPaint() {return m_paint;}

}

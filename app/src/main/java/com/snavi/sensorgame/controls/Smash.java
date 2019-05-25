package com.snavi.sensorgame.controls;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import com.snavi.sensorgame.game_objects.Hammer;

public class Smash implements SensorEventListener {


    private SensorManager m_sensorManager;
    private Sensor m_sensor;
    private Hammer m_hammer;



    public Smash(Context context, Hammer hammer)
    {
        m_sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        m_sensor        = m_sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        m_sensorManager.registerListener(this, m_sensor, SensorManager.SENSOR_DELAY_FASTEST);

        m_hammer   = hammer;
    }



    @Override
    public void onSensorChanged(SensorEvent sensorEvent)
    {
        if (sensorEvent.values[0] < 1) m_hammer.smash();
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int i)
    {

    }
}

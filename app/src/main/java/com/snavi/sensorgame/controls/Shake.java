package com.snavi.sensorgame.controls;

import android.app.Service;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Shake implements SensorEventListener {


    // CONST ///////////////////////////////////////////////////////////////////////////////////////
    public static final int    MIN_SHAKES_PER_CHECK    = 5;
    public static final int    TIME_BETWEEN_CHECKS     = 1000;
    public static final double MIN_DIFFERENCE_TO_SHAKE = 3;


    // fields //////////////////////////////////////////////////////////////////////////////////////
    private SensorManager m_sensorManager;
    private Sensor  m_sensor;

    private boolean m_isShaking;    // if after start player stopped shaking for long enough -> false
    private double  m_lastRead;     // last read of acceleration
    private int m_shakes;
    private long m_lastCheckTime;   // time of last check in millis
    private int m_lastCheckAmount;  // how much shakes were detected on previous check


    public Shake(Context context)
    {
        m_sensorManager = (SensorManager) context.getSystemService(Service.SENSOR_SERVICE);
        m_sensor = m_sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        m_isShaking = false;
        m_shakes = 0;
        m_lastCheckTime = System.currentTimeMillis();
        m_lastCheckAmount = 0;
    }



    public void start()
    {
        m_sensorManager.registerListener(this, m_sensor, SensorManager.SENSOR_DELAY_FASTEST);
    }



    public void stop()
    {
        m_sensorManager.unregisterListener(this);
    }



    @Override
    public void onSensorChanged(SensorEvent sensorEvent)
    {
        double read = sensorEvent.values[1];

        if (read - m_lastRead >= MIN_DIFFERENCE_TO_SHAKE) m_shakes += 1;
        checkIfShaking();
        m_lastRead = read;
    }



    private void checkIfShaking()
    {
        if (System.currentTimeMillis() - m_lastCheckTime >= TIME_BETWEEN_CHECKS)
        {
            m_isShaking = m_shakes - m_lastCheckAmount > MIN_SHAKES_PER_CHECK;

            m_lastCheckAmount = m_shakes;
            m_lastCheckTime   = System.currentTimeMillis();
        }
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }



    // getters /////////////////////////////////////////////////////////////////////////////////////
    public boolean isShaking() {return m_isShaking;}
}

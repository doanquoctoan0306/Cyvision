package com.example.tuan_dong.map.listener;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;

/**
 * Created by QuangTran on 2/11/2018.
 * Ref: https://developer.android.com/guide/topics/sensors/sensors_position.html
 */

public class CompassSensorListener implements SensorEventListener {
    private float[] m_acceleratorReading;
    private float[] m_magnetorReading;

    public CompassSensorListener() {
        m_acceleratorReading = new float[3];
        m_magnetorReading = new float[3];
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            System.arraycopy(sensorEvent.values, 0, m_acceleratorReading, 0, m_acceleratorReading.length);
        if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            System.arraycopy(sensorEvent.values, 0, m_magnetorReading, 0, m_magnetorReading.length);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public String getOrientationInString() {
        double azimutDegree = this.getOrientation();

        // North : 0 -> -22.5 ~ 22.5
        if (azimutDegree >= -22.5 && azimutDegree <= 22.5)
            return "Bắc";
            // North east -> 22.5 ~ 67.5
        else if (azimutDegree >= 22.5 && azimutDegree <= 67.5)
            return "Đông Bắc";
            // East : 90 -> 67.5 ~ 112.5
        else if (azimutDegree >= 67.5 && azimutDegree <= 112.5)
            return "Đông";
            // South East -> 112.5 ~ 167.5
        else if (azimutDegree >= 112.5 && azimutDegree <= 167.5)
            return "Đông Nam";
            // South : 180 -> 167.5 ~ -167.5
        else if ((azimutDegree >= 167.5 && azimutDegree <= 180) || (azimutDegree >= -180 && azimutDegree <= -167.5))
            return "Nam";
            // West South -> -167.5 ~ -112.5
        else if (azimutDegree <= -112.5 && azimutDegree >= -167.5)
            return "Tây Nam";
            // West : -90 -> -112.5 ~ 67.5
        else if (azimutDegree <= -67.5 && azimutDegree >= -112.5)
            return "Tây";
            // North West -> -67.5 ~ -22.5
        else
            return "Tây Bắc";
    }

    /**
     * get degree from North to current
     *
     * @return
     */
    public double getOrientation() {
        float[] rotationMatrix = new float[9];
        float[] angles = new float[3];
        SensorManager.getRotationMatrix(rotationMatrix, null, m_acceleratorReading, m_magnetorReading);
        SensorManager.getOrientation(rotationMatrix, angles);

        return angles[0] * 180 / Math.PI;
    }
}

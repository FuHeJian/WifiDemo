package com.example.wifidemo1.customview;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.example.wifidemo1.log.MyLog;

/**
 * @author: fuhejian
 * @date: 2023/3/24
 */
public class CompassView extends androidx.appcompat.widget.AppCompatTextView implements SensorEventListener {

    private SensorManager sensorManager;
    float[] gravity = new float[3];
    float[] geomagnetic = new float[3];

    private int azimuth;
    private int oldAzimuth;
    private float azimuthFix;

    private long startTime;


    public CompassView(Context context) {
        this(context, null);
    }

    public CompassView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CompassView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        sensorManager = (SensorManager) getContext()
                .getSystemService(Context.SENSOR_SERVICE);

        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST, SensorManager.SENSOR_DELAY_FASTEST);

        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST, SensorManager.SENSOR_DELAY_FASTEST);

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        final float alpha = 1f;
        float[] R = new float[9];
        float[] I = new float[9];
        float[] values = new float[3];

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagnetic[0] = alpha * geomagnetic[0] +
                    event.values[0];
            geomagnetic[1] = alpha * geomagnetic[1] +
                    event.values[1];
            geomagnetic[2] = alpha * geomagnetic[2] + (1 - alpha)
                    * event.values[2];
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            gravity[0] = alpha * gravity[0] +
                    event.values[0];
            gravity[1] = alpha * gravity[1] +
                    event.values[1];
            gravity[2] = alpha * gravity[2] +
                    event.values[2];

        }

        boolean b = SensorManager.getRotationMatrix(R, null, gravity, geomagnetic);

        if (b) {
            SensorManager.getOrientation(R, values);
            StringBuilder builder = new StringBuilder();
            azimuth = (int) Math.toDegrees(values[0]); // orientation
            azimuth = (int) (azimuth + azimuthFix + 360) % 360;
            if (oldAzimuth != azimuth) {
                setText(String.valueOf(azimuth));
                oldAzimuth = azimuth;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        MyLog.printLog("当前类:CompassView,当前方法：onAccuracyChanged,当前线程:" + Thread.currentThread().getName() + ",信息:");
    }

}

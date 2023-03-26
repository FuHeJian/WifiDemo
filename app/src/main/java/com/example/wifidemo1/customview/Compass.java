package com.example.wifidemo1.customview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.provider.CalendarContract;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.motion.widget.MotionController;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.core.widget.ImageViewCompat;

import com.google.android.material.internal.ViewUtils;

/**
 * com.example.wifidemo1.customview
 */
public class Compass extends AppCompatImageView implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor gsensor;
    private Sensor msensor;

    private Sensor magnetic;

    private int azimuth;
    private int oldAzimuth;
    private long time;

    private Animator mAnimator;

    private int mAniStart;
    private int mAniEnd;

    private Paint mPaint = new Paint();

    private Listener mListener;


    public Compass(@NonNull Context context) {
        this(context, null);
    }

    public Compass(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Compass(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        sensorManager = (SensorManager) getContext()
                .getSystemService(Context.SENSOR_SERVICE);
//        gsensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        magnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        msensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

//        sensorManager.registerListener(this, gsensor,
//                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, msensor,
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, magnetic,
                SensorManager.SENSOR_DELAY_GAME);

        mAnimator = ObjectAnimator.ofFloat(this, "rotation", oldAzimuth, azimuth);

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        sensorManager.unregisterListener(this);
    }

    public void setListener(Listener listener){
        mListener = listener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ROTATION_VECTOR: {
                azimuth = updateCompass(event);
                long cTime = System.currentTimeMillis();
                if (oldAzimuth != azimuth && cTime - time > 100L) {
                    time = System.currentTimeMillis();
                    setRotation(-azimuth);
                    if(mListener!=null){
                        mListener.onPositionChanged(azimuth);
                    }
                    oldAzimuth = azimuth;
                }
                break;
            }

            case Sensor.TYPE_MAGNETIC_FIELD: {

                break;
            }
        }

    }

    private int updateCompass(SensorEvent event) {
        int rotation = getDisplayRotation();
        float[] values1 = new float[3];
        values1[0] = event.values[0];
        values1[1] = event.values[1];
        values1[2] = event.values[2];
        float[] values = new float[9];
        SensorManager.getRotationMatrixFromVector(values, values1);

        float[] outR = new float[values.length];
        //若屏幕旋转矩阵需要重新转换
        switch (rotation) {
            case Surface.ROTATION_0: {
                SensorManager.remapCoordinateSystem(values, SensorManager.AXIS_X, SensorManager.AXIS_Y, outR);
                break;
            }
            case Surface.ROTATION_90: {
                SensorManager.remapCoordinateSystem(values, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, outR);
                break;
            }
            case Surface.ROTATION_180: {
                SensorManager.remapCoordinateSystem(values, SensorManager.AXIS_MINUS_X, SensorManager.AXIS_MINUS_Y, outR);
                break;
            }
            case Surface.ROTATION_270: {
                SensorManager.remapCoordinateSystem(values, SensorManager.AXIS_MINUS_Y, SensorManager.AXIS_X, outR);
                break;
            }
        }
        float[] azimuth = new float[3];
        SensorManager.getOrientation(outR, azimuth);
        return (int) (Math.toDegrees(azimuth[0]) + 360)%360;
    }

    private int getDisplayRotation() {
        return getDisplayCompat().getRotation();
    }

    private Display getDisplayCompat() {
        Display display;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            display = getContext().getDisplay();
        } else {
            display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        }
        return display;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        switch (sensor.getType()) {
            case Sensor.TYPE_MAGNETIC_FIELD: {
                if(mListener!=null){
                    mListener.onAccuracyChanged(sensor,accuracy);
                }
                break;
            }
        }
    }


    public static interface Listener{

        void onPositionChanged(int value);

        void onAccuracyChanged(Sensor sensor, int accuracy);

    }

}

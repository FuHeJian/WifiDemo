package com.example.wifidemo1.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * com.example.wifidemo1.customview
 */
public class TwoSliderSeekBar extends View {
    public TwoSliderSeekBar(Context context) {
        this(context,null);
    }

    public TwoSliderSeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TwoSliderSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }

    public TwoSliderSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }


    private GestureDetector mScrollDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener(){

        @Override
        public boolean onScroll(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
            return super.onScroll(e1, e2, distanceX, distanceY);
        }



    });


    @Override
    public boolean onTouchEvent(MotionEvent event) {


        return true;
    }


}

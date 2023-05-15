package com.example.wifidemo1.customview;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MaxMinSeekbar extends View {
    private static final String TAG = MaxMinSeekbar.class.getSimpleName();


    public MaxMinSeekbar(Context context) {
        super(context);
        init();
    }

    public MaxMinSeekbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MaxMinSeekbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public MaxMinSeekbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    private Paint paint;


    private float minProgress = 1.6f;

    private float maxProgress = 6.3f;

    private float currentMinProgress = 1;
    private float currentMaxProgress = 2;

    public void setDatalist(ArrayList<Float> list) {
        if (list == null || list.size() == 0) return;
        this.datalist.clear();
        this.datalist.addAll(list);

        list.sort(new Comparator<Float>() {
            @Override
            public int compare(Float f1, Float f2) {
                return Float.compare(f1, f2);
            }
        });

        minProgress = datalist.get(0);
        maxProgress = datalist.get(datalist.size() - 1);
        invalidate();
    }


    private List<Float> datalist;


    public float getCurrentMinProgress() {
        return currentMinProgress;
    }

    public void setCurrentMinProgress(float currentMinProgress) {
        this.currentMinProgress = currentMinProgress;
        invalidate();
    }

    public float getCurrentMaxProgress() {
        return currentMaxProgress;
    }

    public void setCurrentMaxProgress(float currentMaxProgress) {
        this.currentMaxProgress = currentMaxProgress;
        invalidate();
    }


    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        datalist = new CopyOnWriteArrayList<Float>();

        for (int i = 0; i < 10; i++) {
            datalist.add((float) i);
        }
    }

    public static Float findClosest(List<Float> list, Float target) {
        if (list == null || list.size() == 0) return target;
        Float closest = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            Float current = list.get(i);
            if (Math.abs(current - target) < Math.abs(closest - target)) {
                closest = current;
            }
        }
        return closest;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float SliderSize = getHeight() / 3;
        paint.setColor(Color.parseColor("#AD1C252F"));
        canvas.drawRoundRect(0, getHeight() / 3, getWidth(), getHeight() * 2 / 3, 6, 6, paint);
        float seekbarLenght = getWidth() - 2 * SliderSize;


        float minRight = SliderSize + (currentMinProgress - minProgress) * seekbarLenght / (maxProgress - minProgress);
        float maxLeft = SliderSize + (currentMaxProgress - minProgress) * seekbarLenght / (maxProgress - minProgress);

        paint.setColor(Color.parseColor("#0ABD46"));
        canvas.drawRoundRect(minRight - SliderSize, getHeight() / 3, maxLeft + SliderSize, getHeight() * 2 / 3, 6, 6, paint);

        paint.setColor(Color.WHITE);
        canvas.drawRoundRect(minRight - SliderSize, getHeight() / 3, minRight, getHeight() * 2 / 3, 3, 3, paint);
        canvas.drawRoundRect(maxLeft, getHeight() / 3, maxLeft + SliderSize, getHeight() * 2 / 3, 3, 3, paint);

    }

    boolean touchMin;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!canTouch) return false;
        ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true);
        }
        float SliderSize = getHeight() / 3;


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                float minRight = SliderSize + (currentMinProgress - minProgress) * (getWidth() - SliderSize * 2) / (maxProgress - minProgress);
                float maxRight = SliderSize + (currentMaxProgress - minProgress) * (getWidth() - SliderSize * 2) / (maxProgress - minProgress);

                if (event.getX() > (minRight + maxRight) / 2) {
                    touchMin = false;
                } else {
                    touchMin = true;
                }


                break;
            case MotionEvent.ACTION_MOVE:
                if (touchMin) {
                    currentMinProgress = minProgress + ((event.getX() - SliderSize) / (getWidth() - 2 * SliderSize) * (maxProgress - minProgress));
                } else {
                    currentMaxProgress = minProgress + ((event.getX() - SliderSize) / (getWidth() - 2 * SliderSize) * (maxProgress - minProgress));
                }


                if (currentMinProgress < minProgress) currentMinProgress = minProgress;
                if (currentMinProgress > maxProgress) currentMinProgress = maxProgress;

                if (currentMaxProgress < minProgress) currentMaxProgress = minProgress;
                if (currentMaxProgress > maxProgress) currentMaxProgress = maxProgress;


                if (touchMin) {
                    if (currentMinProgress > currentMaxProgress) {
                        currentMinProgress = currentMaxProgress;
                    }
                    currentMinProgress = findClosest(datalist, currentMinProgress);
                } else {
                    if (currentMaxProgress < currentMinProgress) {
                        currentMaxProgress = currentMinProgress;
                    }
                    currentMaxProgress = findClosest(datalist, currentMaxProgress);
                }


                Log.e(TAG, "onTouchEvent: currentMinProgress =" + currentMinProgress + ",currentMaxProgress =" + currentMaxProgress);

                if (maxMinSeekbarListener != null) maxMinSeekbarListener.onMove(currentMinProgress, currentMaxProgress);
                break;
            case MotionEvent.ACTION_UP:
                if (touchMin) {
                    if (currentMinProgress > currentMaxProgress) {
                        currentMinProgress = currentMaxProgress;
                    }
                    currentMinProgress = findClosest(datalist, currentMinProgress);
                } else {
                    if (currentMaxProgress < currentMinProgress) {
                        currentMaxProgress = currentMinProgress;
                    }
                    currentMaxProgress = findClosest(datalist, currentMaxProgress);
                }

                if (maxMinSeekbarListener != null) maxMinSeekbarListener.onUp(currentMinProgress, currentMaxProgress);

                break;
        }
        postInvalidate();
        return true;
    }

    boolean canTouch = true;

    public void setCanTouch(boolean canTouch) {
        this.canTouch = canTouch;
    }


    public void setMaxMinSeekbarListener(MaxMinSeekbarListener maxMinSeekbarListener) {
        this.maxMinSeekbarListener = maxMinSeekbarListener;
    }

    MaxMinSeekbarListener maxMinSeekbarListener;

    public interface MaxMinSeekbarListener {

        void onMove(float minProgress, float maxProgress);

        void onUp(float minProgress, float maxProgress);
    }


}


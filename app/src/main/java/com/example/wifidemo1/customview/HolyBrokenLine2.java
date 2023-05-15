package com.example.wifidemo1.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.Nullable;

import com.google.android.material.internal.ViewUtils;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

/**
 * @author: fuhejian
 * @date: 2023/5/15
 */
@SuppressLint("RestrictedApi")
public class HolyBrokenLine2 extends View {
    public HolyBrokenLine2(Context context) {
        this(context, null);
    }

    public HolyBrokenLine2(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HolyBrokenLine2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public HolyBrokenLine2(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init();
    }

    private float mScale = 1;
    private ScaleGestureDetector mScaleGesDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener() {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScale *= detector.getScaleFactor();
            mLinesMargin *= mScale;
            invalidate();
            return super.onScale(detector);
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            //在down事件中返回true,后续事件也可以继续让父类消耗，这里通过requestDisallowInterceptTouchEvent(true)防止父类消耗
            getParent().requestDisallowInterceptTouchEvent(true);
            return true;
        }
    });

    private MyPoint mCurrentTapDown;
    private boolean mCanScrollHorizon;

    private GestureDetector mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            //点击事件


            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {

            mCurrentTapDown = findPoint(mMoveLength + e.getX(), e.getY());

            if (mCurrentTapDown != null) {//触摸到点
                getParent().requestDisallowInterceptTouchEvent(true);
                mCanScrollHorizon = true;
            }

            return super.onDown(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            if (Math.abs(distanceY) > Math.abs(distanceX) || mCanScrollHorizon) {
                mCanScrollHorizon = true;

                //表示后续事件到UP事件不要拦截
                getParent().requestDisallowInterceptTouchEvent(true);

                if (mCurrentTapDown != null) {//由于触摸到点，则根据点开始滑动，点移出画布后，画布也跟着移动


                } else {//移动画布
                    mMoveLength += distanceY;
                    mMoveAbsLength = Math.abs(mMoveLength);

                    MyLine myLine = mLines.get(mLines.size() - 1);

                    if (mMoveAbsLength > myLine.rect.right) {
                        mMoveAbsLength = myLine.rect.right - mCanvasWidth;
                        mMoveLength = -mMoveAbsLength;
                    } else if (mMoveLength < 0) {
                        mMoveLength = 0;
                        mMoveAbsLength = 0;
                    }
                }

            } else {

                mCanScrollHorizon = false;

            }
            return true;
        }

    });

    private float mMoveLength;
    private float mMoveAbsLength;//mMoveLength的绝对值

    /**
     * 寻找已经存在的点
     *
     * @param x
     * @param y
     * @return
     */
    private MyPoint findPoint(float x, float y) {

        return null;
    }

    private MyPoint findAndUpdateSingleTapPosition(float x, float y) {

        //确定 x 属于哪条线


        //确定 y 属于哪个值


        return null;
    }

    private Duration mStartTime = Duration.of(0, ChronoUnit.MINUTES);

    public void setData(Duration startTime, ArrayList<MyPoint> points) {

        mStartTime = startTime;

        if (mStartTime.toMinutes() % 30 == 0) {
            mLinesNum = 48;
        } else {
            mLinesNum = 49;
        }

        mLines.clear();

        for (int i = 0; i < mLinesNum; i++) {

            MyLine myLine = new MyLine();

            mLines.add(myLine);

        }

        mPoints.clear();

        mPoints = points;

        invalidate();

    }

    private int mLinesNum = 48;

    private ArrayList<MyPoint> mPoints = new ArrayList<>();

    public class MyPoint {
        MyLine line;
    }

    private ArrayList<MyLine> mLines = new ArrayList<>();

    public class MyLine {

        /**
         * 触摸响应范围
         */
        Rect rect;//触摸响应范围

        /**
         * 当前线上的点
         */
        ArrayList<MyPoint> points = new ArrayList<>();

    }

    private String[] mTextCoords = {
            "+5", "+4", "+3", "+2", "+1", "0", "-1", "-2", "-3", "-4", "-5"
    };

    private Paint mPaint = new Paint();
    private Paint mTextPaint = new TextPaint();

    private int mTextSize = (int) ViewUtils.dpToPx(getContext(), 16);

    /**
     * 竖坐标距离 折线图的距离
     */
    private float mTextCoordsMarginToCanvas = ViewUtils.dpToPx(getContext(),3);

    /**
     *
     * MaxTextWidth文本最大宽度
     *
     */
    private int mMaxTextWidth;

    /**
     *
     * MaxTextHeight文本最大高度
     *
     */
    private int mMaxTextHeight;

    private float mLinesMargin;
    public void init() {

        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "cai978.ttf");
        mTextPaint.setTypeface(typeface);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(mTextSize);

        //获取文字大小
        Rect rect = new Rect();
        for (int i = 0; i < mTextCoords.length; i++) {
            mTextPaint.getTextBounds(mTextCoords[i], 0, mTextCoords[i].length(), rect);
            if (mMaxTextWidth < rect.width()) {
                mMaxTextWidth = rect.width();
            }
            if (mMaxTextHeight < rect.height()) {
                mMaxTextHeight = rect.height();
            }
        }

        mCanvasStartX = mMaxTextWidth + mTextCoordsMarginToCanvas;

        mPaint.setAntiAlias(true);


        setData(Duration.of(0,ChronoUnit.MINUTES),new ArrayList<>());

    }

    private float mCanvasStartX;
    private float mCanvasWidth;

    private float mLineWidth = ViewUtils.dpToPx(getContext(),2);

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        mCanvasWidth = getWidth() - mCanvasStartX;

        mLinesMargin = mCanvasWidth / 7;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //从startTime开始画
        canvas.save();

        canvas.translate(mMoveAbsLength, 0);
        mCanvasStartX += mMoveAbsLength;
        canvas.clipRect(mCanvasStartX - mLineWidth, 0, mCanvasStartX + mCanvasWidth + mLineWidth, getBottom());

        //绘制线
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);
        mPaint.setAlpha(190);
        for (int i = 0; i < mLines.size(); i++) {

            float startX = i * mLinesMargin;

            float startY = mMaxTextHeight/2f;

            canvas.drawLine(startX,startY,startX,getHeight() - startY,mPaint);

        }

        canvas.restore();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        mScaleGesDetector.onTouchEvent(event);

        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN: {


                return true;//表示接收后续事件，但是父类仍然可以拦截
            }

            case MotionEvent.ACTION_UP: {
                mCurrentTapDown = null;
                break;
            }

        }

        return super.onTouchEvent(event);
    }


}

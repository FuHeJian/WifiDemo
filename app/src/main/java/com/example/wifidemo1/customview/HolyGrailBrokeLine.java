package com.example.wifidemo1.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.internal.ViewUtils;

import java.util.ArrayList;

/**
 * @author: fuhejian
 * @date: 2023/4/28
 */
@SuppressLint("RestrictedApi")
public class HolyGrailBrokeLine extends View {

    public HolyGrailBrokeLine(Context context) {
        this(context, null);
    }

    public HolyGrailBrokeLine(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HolyGrailBrokeLine(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public HolyGrailBrokeLine(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);


    }

    float lineMargin = 0;//每条线的间隔
    int lineNum = 23; //线的个数
    private Paint mPaint = new Paint();

    private float lineWidth = ViewUtils.dpToPx(getContext(), 1);//线宽
    private float textSize = ViewUtils.dpToPx(getContext(), 15);

    private float mRadius = ViewUtils.dpToPx(getContext(), 8);

    private String[] texts = {
            "+5", "+4", "+3", "+2", "+1", "0", "-1", "-2", "-3", "-4", "-5"
    };

    //偶数为一小时处

    //保存每条线是否有点
    private ArrayList<LinePoint> mPoints = new ArrayList<>(lineNum * 2);
    //保存每条线的位置
    private ArrayList<Float> mLinesPosition = new ArrayList<>(lineNum * 2);

    private TimeState mTimeState = TimeState.HOUR;

    private float mSmoothLength = 0;

    private float mEndCanvasX;
    private float mCanvasWidth;

    Path mPath = new Path();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mLinesPosition.size() == 0) {
            for (int i = 0; i < lineNum * 2; i++) {
                mLinesPosition.add(0f);
            }
        }

        if (mPoints.size() == 0) {
            for (int i = 0; i < lineNum * 2; i++) {
                mPoints.add(new LinePoint(0, 0, false));
            }
        }

        mPaint.setColor(Color.parseColor("#444B52"));
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(lineWidth);
        mPaint.setTextSize(textSize);
        Rect textRect = new Rect();

        mPaint.getTextBounds(texts[0], 0, texts[0].length(), textRect);

        int textHeight = textRect.height();

        int textWidth = textRect.width();
        float width = getWidth() - getPaddingLeft() - getPaddingRight() - textWidth - lineMargin;
        lineMargin = width / lineNum;
        mCanvasWidth = width;
        //滑动,更新原点坐标为(mSmoothLength,0)
        canvas.translate(mSmoothLength, 0);

        float startX = 0;

        if (mScale == 2) {
            //半小时
            mTimeState = TimeState.MINUTE;

            //画线
            for (int i = 0; i < lineNum * 2; i++) {
                startX = i * lineMargin * mScale + i * lineWidth + getPaddingLeft() + textWidth + lineMargin;

                //判断是否移除屏幕
                if (startX - Math.abs(mSmoothLength) >= (getPaddingLeft() + textWidth + lineMargin)) {
                    canvas.drawLine(startX, textHeight / 2f, startX, getHeight(), mPaint);

                    if ((mClickX + Math.abs(mSmoothLength)) > (startX - (lineMargin / 2f)) && (mClickX + Math.abs(mSmoothLength)) < (startX + lineMargin / 2f) && hasClicked) {

                        hasClicked = false;
                        if (mPoints.get(i).enable) {
                            //同一横坐标的不同位置
                            if (mPoints.get(i).y != mClickY) {
                                mClickX = startX;
                                mPoints.get(i).y = mClickY;
                            } else {
                                mPoints.get(i).enable = false;
                            }
                        } else {
                            mClickX = startX;
                            mPoints.get(i).x = mClickX;
                            mPoints.get(i).y = mClickY;
                            mPoints.get(i).enable = true;
                        }

                    }

                } else {

                }

                mLinesPosition.set(i, startX);

            }
        } else {
            //一小时
            mTimeState = TimeState.HOUR;
            //画线
            for (int i = 0; i < lineNum; i++) {
                startX = i * lineMargin * mScale + i * lineWidth + getPaddingLeft() + textWidth + lineMargin;
                if (startX - Math.abs(mSmoothLength) >= (getPaddingLeft() + textWidth + lineMargin)) {
                    canvas.drawLine(startX, textHeight / 2f, startX, getHeight(), mPaint);
                    if ((mClickX + Math.abs(mSmoothLength)) > startX - lineMargin / 2f && (mClickX + mSmoothLength) < (startX + lineMargin / 2f) && hasClicked) {

                        if (mPoints.get(i).enable) {
                            //同一横坐标的不同位置
                            if (mPoints.get(i).y != mClickY) {
                                mClickX = startX;
                                mPoints.get(i).y = mClickY;
                            } else {
                                mPoints.get(i).enable = false;
                            }
                        } else {
                            mClickX = startX;
                            mPoints.get(i).x = mClickX;
                            mPoints.get(i).y = mClickY;
                            mPoints.get(i).enable = true;
                        }
                        hasClicked = false;
                    }
                }

                mLinesPosition.set(i*2, startX);

            }
        }

        //绘制线
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.parseColor("#0ABD46"));
        mPaint.setStrokeWidth(mRadius);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPath.reset();
        for (int i = 0; i < mPoints.size(); i=i*2) {
            if (mLinesPosition.get(i) - Math.abs(mSmoothLength) >= (getPaddingLeft() + textWidth + lineMargin)) {

                if (mPoints.get(i).enable) {
                    if (mPath.isEmpty()) {
                        mPath.moveTo(mPoints.get(i).x, mPoints.get(i).y);
                    } else {
                        mPath.lineTo(mPoints.get(i).x, mPoints.get(i).y);
                    }
                } else {
                    if (mPath.isEmpty()) {
                        mPath.moveTo(mLinesPosition.get(i), getHeight() / 2f);
                    } else {
                        mPath.lineTo(mLinesPosition.get(i), getHeight() / 2f);
                    }
                }

            } else {
                //移除不可见的path
                mPath.rewind();
            }
        }
        canvas.drawPath(mPath, mPaint);

        //绘制点
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);
        for (int i = 0; i < mPoints.size(); i *=2) {
            if (mLinesPosition.get(i) - Math.abs(mSmoothLength) >= (getPaddingLeft() + textWidth + lineMargin) && mPoints.get(i).enable) {
                drawPoint(canvas, mPoints.get(i).x, mPoints.get(i).y, mRadius, mPaint);
            }
        }

        mEndCanvasX = startX;
        canvas.translate(-mSmoothLength, 0);

        mPaint.reset();

        float yy = ((float) getHeight() - texts.length * textHeight) / (texts.length - 1);//坐标间的间隙

        //绘制坐标
        //绘制竖坐标
        mPaint.setColor(Color.parseColor("#444B52"));
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(textSize);
        for (int i = 0; i < texts.length; i++) {
            String text = texts[i];
            float x = getPaddingLeft();
            float y = yy * i + (i + 1) * textHeight;
            canvas.drawText(text, x, y, mPaint);
        }

        //初始化竖坐标的响应范围
        int lastRangeY = 0;
        int rectLeft = (int) (getPaddingLeft() + textWidth + lineMargin) - 1;
        if (mCoordRects.size() == 0) {

            for (int i = 0; i < texts.length; i++) {

                int y = (int) (yy * i + (i + 1) * textHeight);

                if (i == texts.length - 1) {

                    Rect rect = new Rect(0, lastRangeY, getWidth(), getHeight());

                    mCoordRects.add(new RectCoord(rect, getHeight() - mRadius));

                } else {

                    Rect rect = new Rect(0, lastRangeY, getWidth(), y + ((int) yy) / 2);

                    lastRangeY = y + ((int) yy) / 2;

                    mCoordRects.add(new RectCoord(rect, y - textHeight / 2));

                }
            }
        }

        hasClicked = false;

    }

    ArrayList<RectCoord> mCoordRects = new ArrayList<>();

    /**
     * 画点
     *
     * @param canvas
     * @param x,y    圆心的位置
     */
    private void drawPoint(Canvas canvas, float x, float y, float radius, Paint paint) {
        canvas.drawCircle(x, y, radius, paint);
    }


    private float getCoordYposition(float y, float x) {

        for (int i = 0; i < mCoordRects.size(); i++) {

            if (mCoordRects.get(i).mRect.contains((int) x, (int) y)) {
                return mCoordRects.get(i).mY;
            }

        }

        return mCoordRects.get(0).mY;
    }

    private class RectCoord {

        public Rect mRect;
        public float mY;

        public RectCoord(Rect rect, float y) {
            mRect = rect;
            mY = y;
        }

    }

    private class LinePoint {

        public boolean enable = false;

        public float x;

        public float y;

        public LinePoint(float _x, float _y, boolean _enable) {
            x = _x;
            y = _y;
            enable = _enable;

        }

    }

    float lastMoveX = 0;
    private float mClickX;
    private float mClickY;
    private boolean hasClicked = false;
    private GestureDetector mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {

            float x = e.getX();
            float y = e.getY();

            mClickX = x;

            mClickY = getCoordYposition(y, x);

            hasClicked = true;

            invalidate();

            return true;
        }

    });

    private int mScale = 1;
    private int mMinScale = 1;
    private int mMaxScale = 2;

    ScaleGestureDetector mScaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener() {


        private boolean isHasMagnifyInvalidate = false;
        private boolean isHasShrinkInvalidate = false;

        @Override
        public boolean onScale(@NonNull ScaleGestureDetector detector) {

            mScale *= detector.getScaleFactor();

            if (mScale > mMaxScale) {
                mScale = mMaxScale;

                if (!isHasMagnifyInvalidate) {
                    invalidate();
                    isHasMagnifyInvalidate = true;
                    isHasShrinkInvalidate = false;
                }

            } else {
                mScale = mMinScale;
                if (!isHasShrinkInvalidate) {
                    invalidate();
                    isHasMagnifyInvalidate = false;
                    isHasShrinkInvalidate = true;
                }
            }

            return true;
        }

        @Override
        public boolean onScaleBegin(@NonNull ScaleGestureDetector detector) {
            return true;
        }

    });

    boolean lastInvalidate = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        mGestureDetector.onTouchEvent(event);
        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN: {

                lastMoveX = event.getX();

                break;
            }

            case MotionEvent.ACTION_MOVE: {

                float dx = event.getX() - lastMoveX;

                //减少误差
                if (Math.abs(dx) < 5) {
                    break;
                }

                mSmoothLength += dx;

                if (mSmoothLength > 0) {

                    mSmoothLength = 0;

                    if (!lastInvalidate) {
                        invalidate();
                        lastInvalidate = true;
                    }

                } else {
                    //mSmoothLength<0
                    if (Math.abs(mSmoothLength - mCanvasWidth) > mEndCanvasX) {
                        mSmoothLength = -(mEndCanvasX - mCanvasWidth);
                        if (!lastInvalidate) {
                            invalidate();
                            lastInvalidate = true;
                        }
                    } else if (Math.abs(mSmoothLength - mCanvasWidth) < mEndCanvasX) {
                        invalidate();
                        lastInvalidate = false;
                    }
                }

                lastMoveX = event.getX();

                break;
            }

            case MotionEvent.ACTION_UP: {

                break;
            }

        }

        return true;
    }

    private enum TimeState {

        MINUTE(2),

        HOUR(1);

        private int value;

        TimeState(int v) {
            value = v;
        }

    }

}

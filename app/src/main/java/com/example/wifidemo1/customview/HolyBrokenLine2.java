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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.internal.ViewUtils;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
    /**
     * 缩放时保证线在中间，反向偏移一部分
     */
    private float mScaleNeedMoveAbsLength;

    private ScaleGestureDetector mScaleGesDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener() {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            if (Math.abs(detector.getScaleFactor()) < 0.07) return true;//太小的缩放不执行，防止界面颤动

            mScale = mScale * detector.getScaleFactor();
            mScaleNeedMoveAbsLength = (mConstLinesMargin * mScale - mLinesMargin) * (mMoveAbsLength + mCanvasWidth / 2) / mLinesMargin;
            mLinesMargin = mConstLinesMargin * mScale;

            if (mLinesMargin < mMinLinesMargin) {
                mScale = mMinLinesMargin / mConstLinesMargin;
                mLinesMargin = mMinLinesMargin;
            }

            mMoveAbsLength += mScaleNeedMoveAbsLength;

            invalidate();
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            //在down事件中返回true,后续事件也可以继续让父类消耗，这里通过requestDisallowInterceptTouchEvent(true)防止父类消耗
            getParent().requestDisallowInterceptTouchEvent(true);
            mIsOnScale = true;
            return true;
        }

        @Override
        public void onScaleEnd(@NonNull ScaleGestureDetector detector) {

        }
    });

    /**
     * 触摸到的point
     */
    private MyPoint mCurrentTapDown;

    private boolean mCanScrollHorizon;
    private boolean mIsOnScale;

    private boolean isNeedRemovePoint = false;

    private GestureDetector mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {

            //点击事件

            if (mCurrentTapDown != null) {
                mCurrentTapDown.isChoose = true;
            } else {
                MyPoint point = findAndUpdateSingleTapPosition((int) (mMoveAbsLength + e.getX()), (int) e.getY(), true);
                if (point != null) {
                    point.isChoose = true;
                }
            }

            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {

            mCurrentTapDown = findPoint((int) (mMoveAbsLength + e.getX()), (int) e.getY());

            if (mCurrentTapDown != null) {//触摸到点
                getParent().requestDisallowInterceptTouchEvent(true);
                mCanScrollHorizon = true;
            }

            return super.onDown(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            if (Math.abs(distanceX) > Math.abs(distanceY) || mCanScrollHorizon) {
                mCanScrollHorizon = true;

                //表示后续事件到UP事件不要拦截
                getParent().requestDisallowInterceptTouchEvent(true);

                if (mCurrentTapDown != null) {//由于触摸到点，则根据点开始滑动，点移出画布后，画布也跟着移动

                    int lines = (int) (distanceX / mLinesMargin);
                    mCurrentTapDown.isChoose = true;
                    int newIndex = mCurrentTapDown.line.index - lines;
                    if (newIndex > 0 && newIndex < mLines.size()) {
                        mCurrentTapDown.line = mLines.get(newIndex);
                        if(mCurrentTapDown.line.points!=null){
                            isNeedRemovePoint = true;
                        }else {
                            isNeedRemovePoint = false;
                        }
                        sortPoints();
                    }

                    if (mCurrentTapDown.line.rect.right > (mCanvasStartX + mCanvasWidth + mMoveAbsLength)) {
                        mMoveAbsLength = mCurrentTapDown.line.rect.right - mCanvasWidth - mCanvasStartX;
                    } else if (mCurrentTapDown.line.rect.left < (mCanvasStartX + mMoveAbsLength)) {
                        mMoveAbsLength = mCurrentTapDown.line.rect.left - mCanvasStartX;
                    }

                    invalidate();

                } else {//移动画布

                    mMoveLength = mMoveAbsLength + distanceX;
                    mMoveAbsLength = Math.abs(mMoveLength);

                    invalidate();

                }

            } else {

                mCanScrollHorizon = false;

            }
            return true;
        }



    });

    /**
     * 水平移动距离 range
     */
    private float mMoveAbsLength;//mMoveLength的绝对值

    private float mMoveLength;//mMoveLength

    /**
     * 寻找已经存在的点
     *
     * @param x
     * @param y
     * @return
     */
    private MyPoint findPoint(int x, int y) {
        return findAndUpdateSingleTapPosition(x, y, false);
    }

    /**
     * 寻找点，若线上可以新增点
     *
     * @param x
     * @param y
     * @param add 没有找到是否新增点
     * @return 返回找到的点或者新增的点，如果点击范围不匹配则返回null
     */
    private MyPoint findAndUpdateSingleTapPosition(int x, int y, boolean add) {

        MyLine foundLine = null;
        //确定 x 属于哪条线
        for (int i = 0; i < mLines.size(); i++) {

            MyLine line = mLines.get(i);

            if (line.rect.contains((int) x, (int) y)) {
                foundLine = line;
            }

        }

        if (foundLine == null) {
            return null;
        }


        //确定 y 属于哪个值
        MyYCoords foundYCoord = null;
        for (int i = 0; i < mYCoords.size(); i++) {
            MyYCoords yCoords = mYCoords.get(i);
            if (yCoords.rect.contains((int) x, (int) y)) {
                foundYCoord = yCoords;
            }
        }

        if (foundYCoord == null) {
            return null;
        }

        MyPoint point = foundLine.points;
        if (point.yCoord == foundYCoord) {
            return point;
        }

        if (add) {
            MyPoint addPoint = new MyPoint();
            addPoint.yCoord = foundYCoord;
            addPoint.line = foundLine;
            foundLine.points = addPoint;
            mPoints.add(addPoint);
            Collections.sort(mPoints, new Comparator<MyPoint>() {
                @Override
                public int compare(MyPoint o1, MyPoint o2) {
                    return o1.line.index - o2.line.index;
                }
            });
            return addPoint;
        }

        return null;
    }

    private void deleteCurrentTapDownPoint() {

        if (mCurrentTapDown != null) {
            mPoints.remove(mCurrentTapDown);
        }

    }

    private void sortPoints() {
        Collections.sort(mPoints, new Comparator<MyPoint>() {
            @Override
            public int compare(MyPoint o1, MyPoint o2) {
                return o1.line.index - o2.line.index;
            }
        });
    }

    private Duration mStartTime = Duration.of(0, ChronoUnit.MINUTES);

    public void setData(Duration startTime, ArrayList<MyPoint> points) {

        mStartTime = startTime;

        if (mStartTime.toMinutes() % 30 == 0) {
            mLinesNum = 48;
        } else {
            mLinesNum = 49;
        }

        mMinLinesMargin = mCanvasWidth / (mLinesNum);

        mLines.clear();

        for (int i = 0; i < mLinesNum; i++) {

            MyLine myLine = new MyLine();
            myLine.index = i;

            mLines.add(myLine);

        }

        mPoints.clear();

        if (mPoints != null) {
            mPoints = points;
        }

        invalidate();

    }

    private int mLinesNum = 48;

    private ArrayList<MyPoint> mPoints = new ArrayList<>();

    public class MyPoint {
        MyLine line;

        MyYCoords yCoord;

        boolean isChoose = false;

    }


    private ArrayList<MyYCoords> mYCoords = new ArrayList<>();

    /**
     * 横坐标对象
     */
    private class MyYCoords {

        /**
         * 触摸响应范围
         */
        Rect rect = new Rect();//触摸响应范围

        String rawValue;

        float value;

    }

    private ArrayList<MyLine> mLines = new ArrayList<>();

    public class MyLine {

        /**
         * 触摸响应范围
         */
        Rect rect = new Rect();//触摸响应范围

        /**
         * 当前线上的点
         */
        MyPoint points;

        /**
         * 在mLines中的位置
         */
        int index;

    }

    private Paint mPaint = new Paint();
    private Paint mTextPaint = new TextPaint();

    private int mTextSize = (int) ViewUtils.dpToPx(getContext(), 20);

    /**
     * 竖坐标距离 折线图的距离
     */
    private float mTextCoordsMarginToCanvas = ViewUtils.dpToPx(getContext(), 10);

    /**
     * MaxTextWidth文本最大宽度
     */
    private int mMaxTextWidth;

    /**
     * MaxTextHeight文本最大高度
     */
    private int mMaxTextHeight;
    private int mSumAllTextHeight;

    private float mLinesMargin;

    private float mMinLinesMargin;

    private float mPaddingTop;
    private float mPaddingBottom;

    private float mPaddingLeft;
    private float mPaddingRight;

    /**
     * 文本颜色
     */
    private final int mTextColor = Color.WHITE;

    public void init() {

        Typeface typeface = null;
        try {
            typeface = Typeface.createFromAsset(getContext().getAssets(), "cai978.ttf");
        } catch (Exception e) {

        }
        mTextPaint.setTypeface(typeface);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(mTextSize);

        //获取文字大小
        Rect rect = new Rect();
        for (int i = 0; i < mYCoordsText.length; i++) {
            mTextPaint.getTextBounds(mYCoordsText[i], 0, mYCoordsText[i].length(), rect);
            if (mMaxTextWidth < rect.width()) {
                mMaxTextWidth = rect.width();
            }
            if (mMaxTextHeight < rect.height()) {
                mMaxTextHeight = rect.height();
            }
            mSumAllTextHeight += rect.height();
        }

        mPaddingTop = getPaddingTop();
        mPaddingBottom = getPaddingBottom();

        mPaddingLeft = getPaddingLeft();
        mPaddingRight = getPaddingRight();

        mCanvasStartX = mMaxTextWidth + mTextCoordsMarginToCanvas + mPaddingLeft;

        mPaint.setAntiAlias(true);

        setData(Duration.of(0, ChronoUnit.MINUTES), new ArrayList<>());

    }

    /**
     * 纵坐标宽度 + 纵坐标leftMargin
     */
    private float mCanvasStartX;
    private float mCanvasWidth;

    /**
     * 线宽
     */
    private float mLineWidth = ViewUtils.dpToPx(getContext(), 2);

    private float mConstLinesMargin;

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        mCanvasWidth = getWidth() - mCanvasStartX - mPaddingRight;

        mLinesMargin = mCanvasWidth / 7;

        mMinLinesMargin = mCanvasWidth / (mLinesNum);

        mConstLinesMargin = mLinesMargin;


        for (int i = 0; i < mYCoordsText.length; i++) {
            MyYCoords myYCoords = new MyYCoords();
            myYCoords.rawValue = mYCoordsText[i];
            try {
                myYCoords.value = Float.parseFloat(mYCoordsText[i]);
            } catch (Exception e) {

            }
            mYCoords.add(myYCoords);
        }

    }

    private float mEndVisibleCanvasX;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //从startTime开始画

        drawCoords(canvas);

        canvas.save();
        restrictMoveLength();

        canvas.clipRect(mCanvasStartX, mPaddingTop, mCanvasStartX + mCanvasWidth, getHeight() - mPaddingBottom);

        //只影响translate之后的动作，因为translate改变的是坐标轴，所以translate之前绘制的内容也不会随之改变，只是坐标轴变化了
        canvas.translate(-mMoveAbsLength, 0);//坐标轴向左平移mMoveAbsLength大小

        mEndVisibleCanvasX = mCanvasStartX + mMoveAbsLength + mCanvasWidth;

        drawLines(canvas);

        //平移会改变坐标的位置

        canvas.restore();

    }

    /**
     * 绘制线
     *
     * @param canvas
     */
    private void drawLines(Canvas canvas) {

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(mLineWidth);
        mPaint.setAlpha(190);

        for (int i = 0; i < mLines.size(); i++) {

            MyLine line = mLines.get(i);

            float startX = i * mLinesMargin + mLinesMargin / 2f + mCanvasStartX;//线的横坐标

            float startY = mMaxTextHeight / 2f + mPaddingTop;//留出半个文字高度

            float endY = getHeight() - mMaxTextHeight / 2f - mPaddingBottom;

            line.rect.top = (int) startY;

            line.rect.bottom = (int) endY;

            line.rect.left = (int) (startX - mLinesMargin / 2f);

            line.rect.right = (int) (startX + mLinesMargin / 2f);

            canvas.drawLine(startX, startY, startX, endY, mPaint);

        }

    }

    private String[] mYCoordsText = {
            "+5", "+4", "+3", "+2", "+1", "0", "-1", "-2", "-3", "-4", "-5"
    };

    /**
     * 绘制纵坐标
     *
     * @param canvas
     */
    private void drawCoords(Canvas canvas) {

        float startX = mPaddingLeft;

        float textMargin = (getHeight() - mPaddingTop - mPaddingBottom - mSumAllTextHeight) / (mYCoords.size() - 1);

        Rect rect = new Rect();

        float lastY = mPaddingTop;
        for (int i = 0; i < mYCoords.size(); i++) {

            String value = mYCoords.get(i).rawValue;

            mTextPaint.getTextBounds(value, 0, value.length(), rect);

            int textW = rect.width();

            int textH = rect.height();

            float y = lastY + textH + textMargin;

            if (i == 0) y = mPaddingTop + textH;

            lastY = y;

            float x = startX + mMaxTextWidth - textW;

            Rect y_rect = mYCoords.get(i).rect;
            y_rect.top = (int) (y - textH - textMargin / 2);
            if (y_rect.top < mPaddingTop) {
                y_rect.top = (int) mPaddingTop;
            }
            y_rect.left = (int) mCanvasStartX;

            y_rect.right = (int) (mCanvasStartX + mCanvasWidth);

            y_rect.bottom = (int) (y + textMargin / 2);

            if (y_rect.bottom > getHeight() - mPaddingBottom) {
                y_rect.bottom = (int) (getHeight() - mPaddingBottom);
            }

            canvas.drawText(value, x, y, mTextPaint);

        }

    }

    /**
     * 限制canvas移动范围
     */
    private void restrictMoveLength() {

        float endX = (mLines.size() - 1) * mLinesMargin + mLinesMargin + mCanvasStartX;

        if (mMoveAbsLength + mCanvasWidth + mCanvasStartX > endX) {
            mMoveAbsLength = endX - mCanvasWidth - mCanvasStartX;
        } else if (mMoveLength < 0 || mMoveAbsLength < 0) {
            mMoveAbsLength = 0;
        }

    }

    /**
     * =
     * 绘制点
     *
     * @param canvas
     */
    private void drawPoint(Canvas canvas) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getPointerCount() > 1) {
            mScaleGesDetector.onTouchEvent(event);
        } else if (!mIsOnScale) {
            mGestureDetector.onTouchEvent(event);
        }

        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN: {


                return true;//表示接收后续事件，但是父类仍然可以拦截
            }

            case MotionEvent.ACTION_UP: {
                mCanScrollHorizon = false;
                mIsOnScale = false;
                if(isNeedRemovePoint&&mCurrentTapDown!=null){//移动后的点占据了原先点的位置，在松手时更新为移动过来的点
                    isNeedRemovePoint = false;
                    mPoints.remove(mCurrentTapDown.line.points);
                    mCurrentTapDown.line.points = mCurrentTapDown;
                }
                mCurrentTapDown = null;
                break;
            }

        }

        return super.onTouchEvent(event);
    }


}

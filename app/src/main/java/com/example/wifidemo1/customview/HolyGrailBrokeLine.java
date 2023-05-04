package com.example.wifidemo1.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Range;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ScaleGestureDetectorCompat;

import com.example.wifidemo1.activity.impl.LoadClassLoader;
import com.google.android.material.internal.ViewUtils;

import java.nio.channels.FileLock;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * com.example.wifidemo1.customview
 * <p>
 * fhj
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

        float[] interval = {mPointRadius / 2, mPointRadius / 4};

        DashPathEffect dashPathEffect = new DashPathEffect(interval, 0);
        mDashPathPaint.setColor(Color.WHITE);
        mDashPathPaint.setStyle(Paint.Style.STROKE);
        mDashPathPaint.setStrokeWidth(mPointRadius / 6);
        mDashPathPaint.setPathEffect(dashPathEffect);

    }


    private float mLinesStartX;
    private float mLinesEndX;
    private float mLinesAllWidth;
    private float mScrollLength;

    private float mLineWidth = ViewUtils.dpToPx(getContext(), 2);

    private float mTextSize = ViewUtils.dpToPx(getContext(), 16);

    private float mPointRadius = ViewUtils.dpToPx(getContext(), 8);

    private float mLineMargin;

    private float mYCoordWidth;

    private float mTextHeight;

    private float mTextWidth;

    private int mLinesNum = 24;

    private ArrayList<YCoord> mYCoord = new ArrayList<>(mLinesNum * 2);

    private ArrayList<Line> mLines = new ArrayList<>(mLinesNum * 2);

    private ArrayList<LinePoint> mLinePoints = new ArrayList<>(mLinesNum * 2);

    /**
     * 获取时间轴上的值
     *
     * @return {@link LinePoint}
     */
    public ArrayList<LinePoint> getLinePoints() {
        return mLinePoints;
    }

    private String[] mCoordTexts = {
            "+5", "+4", "+3", "+2", "+1", "0", "-1", "-2", "-3", "-4", "-5"
    };

    /**
     * 时间轴上点的实例
     */
    public class LinePoint {

        /**
         * 时间
         */
        Duration duration;//时间

        /**
         * 纵坐标值
         */
        float value = 0;

        float x;//x坐标

        float y;//y坐标

        /**
         * 是否虚线标记
         */
        boolean isDashPath = false;

        /**
         * 图形
         */
        Drawable drawable;

        /**
         * 是否被标记
         */
        boolean enable = false;

        public LinePoint(Duration _duration, float _x, float _y) {
            duration = _duration;
            x = _x;
            y = _y;
        }

    }

    private class Line {

        Duration duration;//时间

        float x;//x坐标

        Rect rect = new Rect();//此线事件的响应范围

        public Line(Duration _duration, float _x) {
            duration = _duration;
            x = _x;
        }

    }

    private class YCoord {
        Rect rect = new Rect();
        int value;
        float y;//y坐标
    }

    private Paint mPaint = new Paint();

    private Paint mDashPathPaint = new Paint();

    private boolean mIsHasInitTCoord;

    private Path mPath = new Path();

    Range<Float> mXVisibleRange = new Range<Float>(0f, 0f);

    private float mVisibleWidth;

    /**
     * 上一次点击Point
     */
    LinePoint mLastClickPoint;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int skip = mScale == 1 ? 2 : 1;
        //绘制画布
        //绘制坐标，初始化

        if (mYCoord.size() == 0) {
            for (int i = 0; i < mCoordTexts.length; i++) {
                mYCoord.add(new YCoord());
            }
        }

        if (mLines.size() == 0) {
            for (int i1 = 0; i1 < mLinesNum * 2; i1++) {
                mLines.add(new Line(Duration.of(0, ChronoUnit.HOURS), 0));
            }
        }

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(mTextSize);
        if (mTextHeight == 0) {
            Rect rect = new Rect();
            mPaint.getTextBounds(mCoordTexts[0], 0, mCoordTexts[0].length(), rect);
            mTextHeight = rect.height();
            mTextWidth = rect.width();
        }

        for (int i = 0; i < mCoordTexts.length; i++) {

            float margin = getHeight() / (mCoordTexts.length - 1);

            float x = 0;

            float y = i * margin + mTextHeight / 2f;

            canvas.drawText(mCoordTexts[i], x, y, mPaint);

            YCoord yCoord = mYCoord.get(i);
            yCoord.value = Integer.parseInt(mCoordTexts[i]);

            y = i * margin;
            yCoord.y = y;
            Rect rect = yCoord.rect;
            rect.left = 0;
            rect.right = getWidth();
            rect.top = (int) (y - margin / 2f);
            rect.bottom = (int) (y + margin / 2f);

            //初始化点的y坐标
            if (mCoordTexts[i].equals("0")) {
                x = mTextWidth / 4f;

                if (mLinePoints.size() == 0) {
                    for (int i1 = 0; i1 < mLinesNum * 2; i1++) {
                        mLinePoints.add(new LinePoint(Duration.of(0, ChronoUnit.HOURS), 0, y));
                    }
                }

            }
        }

        mLinesStartX = mTextWidth * 2;

        mVisibleWidth = getWidth() - mLinesStartX;

        mXVisibleRange = Range.create(mLinesStartX + Math.abs(mScrollLength), mLinesStartX + Math.abs(mScrollLength) + mVisibleWidth + mLineWidth);

        //绘制画布的线
        int saveCount = canvas.save();
        canvas.translate(mScrollLength, 0);
        if (mScale == 1) {
            mLineMargin = (getWidth() - mLinesStartX) / mLinesNum;
        } else {
            mLineMargin = ((getWidth() - mLinesStartX) / mLinesNum) * 2;
        }

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mLineWidth);

        float startX = 0;
        for (int i = 0; i < mLinesNum * mScale; i++) {

            startX = mLinesStartX + i * mLineWidth + i * mLineMargin;

            int relativeI = i * skip;

            if (mXVisibleRange.contains(startX)) {
                canvas.drawLine(startX, 0, startX, getHeight(), mPaint);
            }
            Line line = mLines.get(relativeI);

            line.x = startX;

            Rect lineRect = line.rect;

            lineRect.left = (int) (startX - mLineMargin / 2f);

            lineRect.right = (int) (startX + mLineMargin / 2f);
            lineRect.top = 0;
            lineRect.bottom = getHeight();

            ChronoUnit chronoUnit = mScale == 1 ? ChronoUnit.HOURS : ChronoUnit.MINUTES;

            int timeScale = mScale == 1 ? 1 : 30;

            line.duration = line.duration.plus((long) i * timeScale, chronoUnit);

            mLines.set(relativeI, line);

/*            String time = String.format("%d", line.duration.toHours()) + " : " + String.format("%d", line.duration.toMinutes() - line.duration.toHours() * 60);

              float timeWidth = mPaint.measureText(time);

              canvas.drawText(time, startX - timeWidth / 2, - mTextWidth / 2f, mPaint);*/

            LinePoint linePoint = mLinePoints.get(relativeI);

            linePoint.x = startX;

            linePoint.duration = linePoint.duration.plus(i * timeScale, chronoUnit);

            mLinePoints.set(relativeI, linePoint);

        }

        //多留出mLineWidth*3的可滑动距离防止手机边缘无法触摸
        mCanScrollRange = Range.create(-(startX + mLineWidth * 3 - mVisibleWidth), 0f);

        //更新点
        if (mSingleClickedXIndex != -1 && mSingleClickedYIndex != -1 && hasNewClicked) {

            LinePoint linePoint = mLinePoints.get(mSingleClickedXIndex);

            Line line = mLines.get(mSingleClickedXIndex);
            float y = mYCoord.get(mSingleClickedYIndex).y;

            if (linePoint.enable && linePoint.x == line.x && linePoint.y == y) {

                if (!linePoint.isDashPath) {
                    if (mLastClickPoint != null) {
                        mLastClickPoint.isDashPath = false;
                    }
                    linePoint.isDashPath = true;

                    mLastClickPoint = linePoint;
                } else {
                    linePoint.y = mYCoord.get(mYCoord.size() / 2).y;

                    linePoint.value = mYCoord.get(mYCoord.size() / 2).value;

                    linePoint.enable = false;

                    linePoint.isDashPath = false;
                }

            } else {

                if (mLastClickPoint != null) {
                    mLastClickPoint.isDashPath = false;
                }

                linePoint.y = y;

                linePoint.x = line.x;

                linePoint.value = mYCoord.get(mYCoord.size() / 2).value;

                linePoint.duration = line.duration;

                linePoint.enable = true;

                linePoint.isDashPath = true;

                mLastClickPoint = linePoint;

            }

            hasNewClicked = false;

        }
        //缩放动作后更新点的x位置
        for (int i = 0; i < mLinePoints.size(); i++) {

            if (mLinePoints.get(i).enable) {

                mLinePoints.get(i).x = mLines.get(i).x;

            }

        }

        //绘制kLine
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.GREEN);
        mPaint.setStrokeWidth(mPointRadius);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        for (int i = 0; i < mLines.size(); i += skip) {
            if (mXVisibleRange.contains(mLines.get(i).x)) {
                if (mPath.isEmpty()) {
                    mPath.moveTo(mLinePoints.get(i).x, mLinePoints.get(i).y);
                } else {
                    mPath.lineTo(mLinePoints.get(i).x, mLinePoints.get(i).y);
                }
            }
        }
        canvas.drawPath(mPath, mPaint);

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);
        //绘制LinePoint

        for (int i = 0; i < mLinePoints.size(); i += skip) {
            if (mLinePoints.get(i).enable && mXVisibleRange.contains(mLinePoints.get(i).x)) {
                canvas.drawCircle(mLinePoints.get(i).x, mLinePoints.get(i).y, mPointRadius, mPaint);

                if (mLinePoints.get(i).isDashPath) {
                    canvas.drawLine(mXVisibleRange.getLower(), mLinePoints.get(i).y, mLinePoints.get(i).x, mLinePoints.get(i).y, mDashPathPaint);
                    canvas.drawLine(mLinePoints.get(i).x, 0, mLinePoints.get(i).x, getHeight(), mDashPathPaint);
                }

            }
        }

        canvas.restoreToCount(saveCount);
        mPath.reset();
        mPaint.reset();

    }

    private int mScale = 1;//代表绘制时的跨度，也能表示：1 -> 小时 ，2->半小时
    private int mMaxScale = 2;

    private int mMinScale = 1;

    private ScaleGestureDetector mScaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.OnScaleGestureListener() {
        @Override
        public boolean onScale(@NonNull ScaleGestureDetector detector) {

            float scale = detector.getScaleFactor();

            if (scale > 1) {
                mScale = mMaxScale;
            } else {
                mScale = mMinScale;
            }

            invalidate();
            return true;
        }

        @Override
        public boolean onScaleBegin(@NonNull ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(@NonNull ScaleGestureDetector detector) {

        }

    });

    private float mSingleClickedX;

    private float mSingleClickedY;

    private int mSingleClickedXIndex;

    private int mSingleClickedYIndex;

    private boolean hasNewClicked = false;

    private GestureDetector mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {

            e.getX();

            mSingleClickedX = e.getX();

            mSingleClickedY = e.getY();

            hasNewClicked = true;

            mSingleClickedXIndex = findClickXIndex();

            mSingleClickedYIndex = findClickYIndex();

            invalidate();

            return true;
        }

        @Override
        public boolean onScroll(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {

            if(e2.getPointerCount()>1)return false;

            mScrollLength += (-distanceX);

            if (mCanScrollRange.contains(mScrollLength)) {
                invalidate();
                mInvalidated = false;
            } else {
                mScrollLength = (Float) mCanScrollRange.clamp(mScrollLength);
                if (!mInvalidated) {
                    mInvalidated = true;
                    invalidate();
                }
            }

            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    });

    private int findClickXIndex() {
        int skip = mScale == 1 ? 2 : 1;
        for (int i = 0; i < mLines.size(); i += skip) {
            Rect lineRect = mLines.get(i).rect;
            if (lineRect.contains((int) (mSingleClickedX + Math.abs(mScrollLength)), 0)) {
                return i;
            }
        }
        return -1;
    }

    private int findClickYIndex() {
        for (int i = 0; i < mYCoord.size(); i++) {
            Rect coordRect = mYCoord.get(i).rect;
            if (coordRect.contains(0, (int) mSingleClickedY)) {
                return i;
            }
        }
        return -1;
    }

    private float mLastMoveX;
    private Range<Float> mCanScrollRange = new Range<>(0f, 0f);

    private boolean mInvalidated = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        mScaleGestureDetector.onTouchEvent(event);

        mGestureDetector.onTouchEvent(event);

        getParent().requestDisallowInterceptTouchEvent(true);

        return true;
    }

}

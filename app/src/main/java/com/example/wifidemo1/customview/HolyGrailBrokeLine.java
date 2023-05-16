package com.example.wifidemo1.customview;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.icu.util.Calendar;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Range;
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
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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

    private int mRuntime = -1;

    private Duration mDeviceStartTime;

    public void setDeviceStartTime(Duration duration) {
        mDeviceStartTime = duration;
    }

    /**
     * 间隔一段时间设置一次
     *
     * @param mRuntime
     */
    public void setRuntime(int mRuntime) {
        if (mDeviceTime == 0) {
            this.mRuntime = 0;
            return;
        }

        mRuntime = (int) (mRuntime - mStartTime.toMinutes());
        if (mRuntime < 0) {
            mRuntime = (int) (24 * 60 + mRuntime - mStartTime.toMinutes());
        }

        if (mListener != null) {
            mListener.updateTime(Duration.of(mRuntime, ChronoUnit.MINUTES));
        }

        postInvalidate();

    }

    public Disposable work;

    public void startWork() {
        work = Schedulers.io().schedulePeriodicallyDirect(new Runnable() {
            @Override
            public void run() {
                //获取一下系统时间
                Calendar instance = Calendar.getInstance();
                int ch = instance.get(Calendar.HOUR_OF_DAY);
                int cm = instance.get(Calendar.MINUTE);
                Duration nowDuration = Duration.of(ch * 60 + cm, ChronoUnit.MINUTES);
                setRuntime((int) nowDuration.toMinutes());

                if (mListener != null) {
                    mListener.updateTime(nowDuration);
                }

            }
        }, 0, 30, TimeUnit.SECONDS);

    }

    public void setTomorrow(boolean tr) {
        this.mRuntime = (int) (mRuntime + 24 * 60);
    }


    private int mDeviceTime;

    public void setDeviceRuntime(int time) {
        if (time == -1) {
            time = 0;
            //获取一下系统时间
            Calendar instance = Calendar.getInstance();
            int ch = instance.get(Calendar.HOUR_OF_DAY);
            int cm = instance.get(Calendar.MINUTE);
            Duration nowDuration = Duration.of(ch * 60 + cm, ChronoUnit.MINUTES);
            mStartTime = nowDuration;
            invalidate();
            return;
        }
        mDeviceTime = time;
        Calendar instance = Calendar.getInstance();
        int ch = instance.get(Calendar.HOUR_OF_DAY);
        int cm = instance.get(Calendar.MINUTE);
        Duration nowDuration = Duration.of(ch * 60 + cm, ChronoUnit.MINUTES);
        mRuntime = (int) (nowDuration.toMinutes() - mStartTime.toMinutes());
        if (mRuntime < 0) {
            mRuntime = (int) (24 * 60 + nowDuration.toMinutes() - mStartTime.toMinutes());
        }

        if (work == null) {
            startWork();
        }

        postInvalidate();
    }

    public HolyGrailBrokeLine(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        float[] interval = {mPointRadius / 2, mPointRadius / 4};

        DashPathEffect dashPathEffect = new DashPathEffect(interval, 0);
        mDashPathPaint.setColor(Color.WHITE);
        mDashPathPaint.setStyle(Paint.Style.STROKE);
        mDashPathPaint.setStrokeWidth(mPointRadius / 6);
        mDashPathPaint.setPathEffect(dashPathEffect);

        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setColor(Color.parseColor("#1AFFFFFF"));
        mTextPaint.setTextSize(mTextSize);

        setTimeFormat();

    }

    public long MAX_DURATION_MINUTES = Duration.of(23 * 60 + 30, ChronoUnit.MINUTES).toMinutes();
    private float mLinesStartX;
    private float mLinesEndX;
    private float mLinesAllWidth;
    private float mScrollLength;

    private float mLineWidth = ViewUtils.dpToPx(getContext(), 2);

    private float mTextSize = ViewUtils.dpToPx(getContext(), 16);

    private float mPointRadius = ViewUtils.dpToPx(getContext(), 8);

    private float mPointBigRadius = ViewUtils.dpToPx(getContext(), 17);

    private float mLineMargin;

    private float mTextHeight;

    private float mTextWidth;

    private int mLinesNum = 24;

    private ArrayList<YCoord> mYCoord = new ArrayList<>();

    private ArrayList<Line> mLines = new ArrayList<>();

    private ArrayList<LinePoint> mLinePoints = new ArrayList<>();
    private ArrayList<LinePoint> mLinePointsGray = new ArrayList<>();

    /**
     * 获取时间轴上的值
     *
     * @return {@link LinePoint}
     */
    public ArrayList<LinePoint> getLinePoints() {
        ArrayList<LinePoint> linePoints = new ArrayList<>();
        for (int i = 0; i < mLinePoints.size(); i++) {
            LinePoint mLinePoint = mLinePoints.get(i);
            if (mLinePoint.enable) {
                linePoints.add(mLinePoint);
            }
        }
        return linePoints;
    }

    private String[] mCoordTexts = {
            "+5", "+4.5", "+4", "+3.5", "+3", "+2.5", "+2", "+1.5", "+1", "+0.5", "0", "-0.5", "-1", "-1.5", "-2", "-2.5", "-3", "-3.5", "-4", "-4.5", "-5"
    };

    /**
     * 时间轴上点的实例
     */
    public static class LinePoint {

        /**
         * 时间
         */
        public Duration duration;//时间

        public int index;

        boolean currentTime = false;

        /**
         * 纵坐标值
         */
        public float value = 0;

        public float x;//x坐标

        public float y;//y坐标

        /**
         * 在第几行或者表示mYCoord的下标
         */
        public int lineNum = 0;

        public int oldLineNum = -1;

        /**
         * 是否虚线标记
         */
        public boolean isDashPath = false;

        /**
         * 图形
         */
        public Drawable drawable;

        /**
         * 是否被标记
         */
        public boolean enable = false;

        public boolean moveEnable = false;

        public LinePoint(int _lineNum) {
            lineNum = _lineNum;
        }

    }

    private class Line {

        public Duration duration;//时间

        public float x;//x坐标

        public Rect rect = new Rect();//此线事件的响应范围

        public Line(Duration _duration, float _x) {
            duration = _duration;
            x = _x;
        }

    }

    private class YCoord {
        public Rect rect = new Rect();
        public float value;
        public float y;//y坐标
    }

    private Paint mPaint = new Paint();
    private Paint mTextPaint = new Paint();

    private Paint mDashPathPaint = new Paint();

    private boolean mIsHasInitTCoord;

    private Path mPath = new Path();

    Range<Float> mXVisibleRange = new Range<Float>(0f, 0f);

    private float mVisibleWidth;

    /**
     * 上一次点击Point
     */
    LinePoint mLastClickPoint;

    private boolean linePointsIsInitial;
    /**
     * 可视的第一条线
     */
    private int mCurrentVisibleStartLineIndex;
    /**
     * 可视的最后一条线
     */
    private int mCurrentVisibleEndLineIndex;

    /**
     * 设置数据显示到界面
     */
    public void setPointData(ArrayList<LinePoint> data, int offset) {
        if (data != null) {

            if (mLinePoints.size() == 0) {
                for (int i1 = 0; i1 < mLinesNum; i1++) {
                    LinePoint linePoint = new LinePoint(0);
                    if (i1 == 0) {
                        linePoint.enable = true;
                    }
                    linePoint.index = i1;
                    mLinePoints.add(linePoint);
                }
            }

            for (int i = 0; i < data.size() && i + offset < mLinePoints.size(); i++) {
                LinePoint linePoint = mLinePoints.get(i + offset);
                LinePoint dataPoint = data.get(i);
                linePoint.lineNum = dataPoint.lineNum;
                linePoint.enable = dataPoint.enable;
            }
            invalidate();
        }
    }

    public ArrayList<Runnable> runnables = new ArrayList<>();

    public void setPointData(int index, LinePoint point) {
        if (point != null) {
            if (index >= 0 && index < mLinePoints.size()) {
                LinePoint linePoint = mLinePoints.get(index);
                linePoint.enable = point.enable;
                linePoint.lineNum = point.lineNum;
            }

            /*if (!mLinePoints.get(0).enable) {
                mLinePoints.get(0).enable = true;
                mLinePoints.get(0).lineNum = mYCoord.size() / 2;
            }*/
        }
    }

    public void resetPoints() {
        if (!mCanEdit) return;
        for (int i = 0; i < mLinePoints.size(); i++) {
            LinePoint linePoint = mLinePoints.get(i);
            if (i == 0) {
                linePoint.enable = true;
                linePoint.lineNum = mYCoord.size() / 2;
            } else {
                linePoint.enable = false;
                linePoint.lineNum = mYCoord.size() / 2;
            }
            linePoint.duration = null;
        }
        System.out.println("测试 执行resetPoints");
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int skip = mScale == 1 ? 2 : 1;
        System.out.println("测试 执行Ondraw");
        if (mStartTime == null) {
            mStartTime = Duration.of(0, ChronoUnit.MINUTES);
            mDeviceStartTime = mStartTime;
        }

        if (mRuntime == -1) {
            mRuntime = 0;
        }

        Duration goneTime = Duration.of(0, ChronoUnit.MINUTES);
        if (mRuntime != -1) {
            long _goneTime = mDeviceStartTime.toMinutes() + mRuntime;

            long dTime = _goneTime - mStartTime.toMinutes();
            if (dTime > 0) {
                goneTime = Duration.of(_goneTime, ChronoUnit.MINUTES);
            } else {
                goneTime = Duration.of(0, ChronoUnit.MINUTES);
            }
        }

        //获取一下系统时间
        Calendar instance = Calendar.getInstance();
        int ch = instance.get(Calendar.HOUR_OF_DAY);
        int cm = instance.get(Calendar.MINUTE);
        Duration nowDuration = Duration.of(ch * 60 + cm, ChronoUnit.MINUTES);

        int a = (int) (mStartTime.toMinutes() / 30);

        Duration temporaryStartTime = Duration.of(mStartTime.toHours() * 60 + (a % 2) * 30, ChronoUnit.MINUTES);
        if (mStartTime.toMinutes() % 30 == 0) {
            mLinesNum = 24 * 2;
        } else {
            mLinesNum = (24 * 2) + 1;
        }

        //绘制画布
        //绘制坐标，初始化
        if (mYCoord.size() == 0) {
            for (int i = 0; i < mCoordTexts.length; i++) {
                mYCoord.add(new YCoord());
            }
        }

        if (mLines.size() == 0) {
            for (int i1 = 0; i1 < mLinesNum; i1++) {
                mLines.add(new Line(null, 0));
            }
            linePointsIsInitial = false;
        }

        if (mLinePoints.size() == 0) {
            for (int i1 = 0; i1 < mLinesNum; i1++) {
                LinePoint linePoint = new LinePoint(mYCoord.size() / 2);
                if (i1 == 0) {
                    linePoint.enable = true;
                }
                linePoint.index = i1;
                mLinePoints.add(linePoint);
            }
        }

        mPaint.setStyle(Paint.Style.FILL);
//        mPaint.setColor(Color.parseColor("#444B52"));
        mPaint.setColor(Color.parseColor("#1AFFFFFF"));
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(mTextSize);
        if (mTextHeight == 0) {
            Rect rect = new Rect();
            mPaint.getTextBounds(mCoordTexts[0], 0, mCoordTexts[0].length(), rect);
            mTextHeight = rect.height();
            mTextWidth = rect.width();
        }

        for (int i = 0; i < mCoordTexts.length; i++) {

            float margin = (getHeight() - getPaddingTop() - getPaddingBottom()) / (mCoordTexts.length - 1);

            float x = 0;

            float y = i * margin + mTextHeight / 2f + getPaddingTop();

            YCoord yCoord = mYCoord.get(i);
            yCoord.value = Float.parseFloat(mCoordTexts[i]);

            if (yCoord.value <= 0) {
                x += mTextHeight / 3f;
            }

            if (mSingleClickedXIndex != -1 && mSingleClickedYIndex != -1) {
                if (hasNewClicked) {

                    if (mSingleClickedYIndex == i) {
                        mPaint.setColor(Color.parseColor("#FFFFFF"));
                    } else {
                        mPaint.setColor(Color.parseColor("#1AFFFFFF"));
                    }

                } else {
                    if (mSingleClickedYIndex == i) {
                        mPaint.setColor(Color.parseColor("#FFFFFF"));
                    } else {
                        mPaint.setColor(Color.parseColor("#1AFFFFFF"));
                    }
                }
            } else {
                mPaint.setColor(Color.parseColor("#1AFFFFFF"));
            }

            if (mCoordTexts[i].length() >= 3) {
                canvas.drawText("-", mTextWidth - 15, y, mPaint);
            } else {
                canvas.drawText(mCoordTexts[i], x, y, mPaint);
            }

            y = i * margin + getPaddingTop();
            yCoord.y = y;
            Rect rect = yCoord.rect;
            rect.left = 0;
            rect.right = getWidth();
            rect.top = (int) (y - margin / 2f);
            rect.bottom = (int) (y + margin / 2f);

            for (int i1 = 0; i1 < mLinePoints.size(); i1++) {
                if (mLinePoints.get(i1).enable && mLinePoints.get(i1).lineNum == i) {
                    LinePoint linePoint = mLinePoints.get(i1);
                    linePoint.y = y;
                    linePoint.value = yCoord.value;
                }
            }

            //初始化点的y坐标
            if (mCoordTexts[i].equals("0") && !linePointsIsInitial) {

                for (int i1 = 0; i1 < mLinesNum; i1++) {
                    LinePoint linePoint = mLinePoints.get(i1);
                    if (!linePoint.enable) {
                        linePoint.y = y;
                        mLinePoints.set(i1, linePoint);
                    }
                }

                linePointsIsInitial = true;

            }
        }

        mLinesStartX = mTextWidth * 3;

        mVisibleWidth = getWidth() - mLinesStartX;


        mXVisibleRange = Range.create(mLinesStartX + Math.abs(mScrollLength) - mPointBigRadius, mLinesStartX + Math.abs(mScrollLength) + mVisibleWidth + mPointBigRadius);

/*        if(mLines.get(mLines.size()-1).x < Math.abs(mCanScrollRange.getLower())+mVisibleWidth + mLinesStartX + 2 * mLineWidth){
            mScrollLength  = mScrollLength + mLinesStartX+mVisibleWidth+mLineWidth;
            mXVisibleRange = Range.create(mLinesStartX + Math.abs(mScrollLength), mLinesStartX + Math.abs(mScrollLength)+mVisibleWidth+mLineWidth);
        }*/

        //绘制画布的线
        int saveCount = canvas.save();

/*        if (mScale == 1) {
            mLineMargin = (getWidth() - mLinesStartX - mLinesNum * mLineWidth) / (mLinesNum - 1);
            mLineMargin *= scale;
            mScrollLength = 0;
        } else {

        }*/

        int _linNum = 8;
        mLineMargin = ((getWidth() - mLinesStartX - _linNum * mLineWidth) / (_linNum)) * scale;

        if (mLines.get(mLines.size() - 1).x != 0) {
            float dT = mXVisibleRange.getUpper() - mLines.get(mLines.size() - 1).x - mPointBigRadius;
            if (dT > 0) {
                mScrollLength = mScrollLength + dT;
                mXVisibleRange = Range.create(mLinesStartX + Math.abs(mScrollLength) - mPointBigRadius, mLinesStartX + Math.abs(mScrollLength) + mVisibleWidth + mPointBigRadius);
                postInvalidate();
            }
        }

        canvas.translate(mScrollLength, 0);

        //限制显示范围
        canvas.clipRect(mXVisibleRange.getLower(), -mPointBigRadius, mXVisibleRange.getUpper(), getHeight() + getPaddingBottom() + 2 * mPointBigRadius);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.parseColor("#444B52"));
        mPaint.setStrokeWidth(mLineWidth);

        float startX = 0;
        mCurrentVisibleStartLineIndex = -1;
        mCurrentVisibleEndLineIndex = -1;
        int _i = 0;

/*        float dScroll;
        if (mXVisibleRange.contains(mLines.get(mLines.size() - 1).x + mLineWidth + mPointBigRadius + mLineWidth)) {
            float newScrollLength = -(mLines.get(mLines.size() - 1).x + mLineWidth + mPointBigRadius - mVisibleWidth);
            dScroll = mScrollLength - newScrollLength;
            mScrollLength = newScrollLength;

            canvas.translate(dScroll, 0);
        }*/

        for (int i = 0; i < mLinesNum; i++) {

            startX = mLinesStartX + i * mLineWidth + i * mLineMargin;

            int relativeI = i * skip;

            Line line = mLines.get(relativeI);

            line.x = startX;

            Rect lineRect = line.rect;

            lineRect.left = (int) (startX - mLineMargin / 2f);

            lineRect.right = (int) (startX + mLineMargin / 2f);
            lineRect.top = 0;
            lineRect.bottom = getHeight();

            ChronoUnit chronoUnit = mScale == 1 ? ChronoUnit.HOURS : ChronoUnit.MINUTES;

            int timeScale = mScale == 1 ? 1 : 30;

            if (i == 0) {
                line.duration = mStartTime;
            } else {
                Duration _t = temporaryStartTime.plus(Duration.of((i) * timeScale, chronoUnit));

/*                if (_t.toMinutes() > MAX_DURATION_MINUTES) {
                    temporaryStartTime = Duration.of(0, ChronoUnit.MINUTES);
                    _i = i;
                    _t = temporaryStartTime;
                }*/

                line.duration = _t;
            }

            if (mXVisibleRange.contains(startX)) {
                if (mCurrentVisibleStartLineIndex == -1) {
                    mCurrentVisibleStartLineIndex = relativeI;
                } else {
                    mCurrentVisibleEndLineIndex = relativeI;
                }
                canvas.drawLine(startX, getPaddingTop(), startX, getHeight() - getPaddingBottom(), mPaint);
                if (i % 2 == 0 && i != 0) {
                    canvas.drawText(String.valueOf(i / 2), startX - mTextWidth / 3, getHeight() - getPaddingBottom() + mTextHeight + 10, mTextPaint);
                }
            }

            mLines.set(relativeI, line);

/*            String time = String.format("%d", line.duration.toHours()) + " : " + String.format("%d", line.duration.toMinutes() - line.duration.toHours() * 60);

              float timeWidth = mPaint.measureText(time);

              canvas.drawText(time, startX - timeWidth / 2, - mTextWidth / 2f, mPaint);*/

            LinePoint linePoint = mLinePoints.get(relativeI);

            linePoint.x = startX;

            linePoint.duration = line.duration;

/*            if (!linePoint.enable) {

                linePoint.lineNum = mYCoord.size() / 2;

            }*/

            mLinePoints.set(relativeI, linePoint);

        }

        mPaint.setColor(Color.parseColor("#818181"));

        canvas.drawLine(mLines.get(0).x, mYCoord.get(mYCoord.size() / 2).y, mLines.get(mLines.size() - 1).x, mYCoord.get(mYCoord.size() / 2).y, mPaint);

        if (mVisibleWidth - startX > 0) {
            mCanScrollRange = Range.create(-1f, 0f);
        } else {
            System.out.println(-(startX - mVisibleWidth));
            mCanScrollRange = Range.create(-(startX - mVisibleWidth), 0f);
        }

        //触摸更新位置
        if (hasNewDown) {
            hasNewDown = false;
            if (temporaryScrollPointXDistance != 0 && mLinePoints.get((int) temporaryScrollPointXDistance).duration != null && mLinePoints.get((int) temporaryScrollPointXDistance).duration.toMinutes() > mDeviceStartTime.toMinutes() + mRuntime) {//水平选择 0 点不能移动
//&& mLinePoints.get(temporaryScrollPointXDistance) != mLastClickPoint
                //取消上次的选择
                if (mLastClickPoint != null) {
                    mLastClickPoint.isDashPath = false;
                    if (mLastClickPoint.moveEnable && mLastClickPoint.enable) {
                        mLastClickPoint.enable = false;
                        mLastClickPoint.moveEnable = false;
                    } else if (mLastClickPoint.moveEnable) {
                        mLastClickPoint.moveEnable = false;
                    }
                    if (mLastClickPoint.oldLineNum != -1) {
                        mLastClickPoint.lineNum = mLastClickPoint.oldLineNum;
                        mLastClickPoint.enable = true;
                    }

                }

                if (mLastClickPoint != null && mLastClickPoint.index != temporaryScrollPointXDistance) {

                    int lineNum = mLinePoints.get(mSingleDownXIndex).lineNum;

                    mLinePoints.get(mSingleDownXIndex).enable = false;

                    LinePoint scrollHorizonLinePoint = mLinePoints.get((int) temporaryScrollPointXDistance);

                    if (scrollHorizonLinePoint.enable) {

                        scrollHorizonLinePoint.oldLineNum = scrollHorizonLinePoint.lineNum;

                    } else {

                        scrollHorizonLinePoint.oldLineNum = -1;

                    }

                    scrollHorizonLinePoint.lineNum = lineNum;

                    scrollHorizonLinePoint.enable = true;

                    scrollHorizonLinePoint.isDashPath = true;

                    mLastClickPoint = scrollHorizonLinePoint;

                    mLastClickPoint.moveEnable = true;

                    if (mListener != null) {
                        mListener.onChoosePoint(scrollHorizonLinePoint);
                    }
                }
            }
        }

        //更新点
        if (mSingleClickedXIndex != -1 && mSingleClickedYIndex != -1 && hasNewClicked && mLinePoints.get(mSingleClickedXIndex).duration.toMinutes() > mDeviceStartTime.toMinutes() + mRuntime) {

            LinePoint linePoint = mLinePoints.get(mSingleClickedXIndex);

            Line line = mLines.get(mSingleClickedXIndex);
            float y = mYCoord.get(mSingleClickedYIndex).y;

            if (mLastClickPoint != null && mLastClickPoint.index != mSingleClickedXIndex) {
                mLastClickPoint.isDashPath = false;
            }

            linePoint.y = y;

            linePoint.x = line.x;

            linePoint.value = mYCoord.get(mSingleClickedYIndex).value;

            linePoint.duration = line.duration;

            linePoint.enable = true;

            linePoint.isDashPath = true;

            linePoint.lineNum = mSingleClickedYIndex;

            mLastClickPoint = linePoint;

            System.out.println(linePoint.value);

            hasNewClicked = false;


            if (mListener != null) {
                mListener.onChoosePoint(linePoint);
            }

        }

        //寻找最后一个和第一个LinePoint
        LinePoint lastPoint = null;
        LinePoint firstPoint = null;
        int lastIndex = 0;
        for (int i = 0; i < mLinePoints.size(); i += skip) {
            if (mLinePoints.get(i).enable) {
                if (firstPoint == null) {
                    firstPoint = mLinePoints.get(i);
                }
                lastPoint = mLinePoints.get(i);
                lastIndex = i;
            }
        }

/*        if (lastPoint != null) {
            for (int i = lastIndex + skip; i < mLinePoints.size(); i += skip) {
                LinePoint linePoint = mLinePoints.get(i);
                linePoint.y = lastPoint.y;
                linePoint.value = lastPoint.y;
                linePoint.lineNum = lastPoint.lineNum;
            }
        }*/


        if (mSingleClickedXIndex == -1 && mSingleClickedYIndex == -1) {
            if (mListener != null) {
                mListener.onNothingPointChecked();
            }
        }

        //缩放动作后更新点的x位置
        for (int i = 0; i < mLinePoints.size(); i++) {
            mLinePoints.get(i).x = mLines.get(i).x;
        }

        //绘制kLine
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.parseColor("#0ABD46"));
        mPaint.setStrokeWidth(mPointRadius);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

/*        for (int i = 0; i < mLines.size(); i += skip) {
            if (mXVisibleRange.contains(mLines.get(i).x)) {
                if (mPath.isEmpty()) {
                    mPath.moveTo(mLinePoints.get(i).x, mLinePoints.get(i).y);
                } else {
                    mPath.lineTo(mLinePoints.get(i).x, mLinePoints.get(i).y);
                }
            }

        }*/

        for (int i = 0; i < mLinePoints.size(); i++) {
            mLinePoints.get(i).y = mYCoord.get(mLinePoints.get(i).lineNum).y;
        }

        boolean timeGone = true;
        long intervalTime = goneTime.toMinutes() - mStartTime.toMinutes();
        LinePoint lastGrayPoint = mLinePoints.get(0);
        if (firstPoint != null) {//有点

            if (firstPoint.index == 0) {
                mPath.moveTo(firstPoint.x, firstPoint.y);
            } else {
                mPath.moveTo(mLinePoints.get(0).x, mLinePoints.get(0).y);

                mPath.lineTo(mLinePoints.get(firstPoint.index - skip).x, mLinePoints.get(firstPoint.index - skip).y);

            }


            ArrayList arrayList = new ArrayList();

            for (int i = 0; i < mLines.size(); i += skip) {

                if (mLinePoints.get(i).enable) {
                    if (mLinePoints.get(i).duration.compareTo(goneTime) < 0 && goneTime.toMinutes() > 0) {
                        mPaint.setColor(Color.parseColor("#C4C4C4"));
//                        - mLinePoints.get(i).duration.toMinutes()
                        intervalTime = intervalTime - (mLinePoints.get(i).duration.toMinutes() - lastGrayPoint.duration.toMinutes());
                        lastGrayPoint = mLinePoints.get(i);
                        timeGone = true;
                    } else {
                        timeGone = false;
                    }
                    mPath.lineTo(mLinePoints.get(i).x, mLinePoints.get(i).y);
                    canvas.drawPath(mPath, mPaint);
                    mPath.reset();
                    mPath.moveTo(mLinePoints.get(i).x, mLinePoints.get(i).y);
                    mPaint.setColor(Color.parseColor("#0ABD46"));
                }

            }

            //最后一个点连接到末尾
            mPath.lineTo(mLinePoints.get(mLinePoints.size() - skip).x, lastPoint.y);

            for (int i = lastIndex + 1; i < mLinePoints.size(); i++) {
                mLinePoints.get(i).y = lastPoint.y;
                mLinePoints.get(i).value = lastPoint.value;
                mLinePoints.get(i).lineNum = lastPoint.lineNum;
            }

        } else {//没有点

            mPath.moveTo(mLinePoints.get(0).x, mLinePoints.get(0).y);

            mPath.lineTo(mLinePoints.get(mLinePoints.size() - skip).x, mLinePoints.get(mLinePoints.size() - skip).y);

            for (int i = 0; i < mLinePoints.size(); i++) {
                mLinePoints.get(i).y = mLinePoints.get(0).y;
                mLinePoints.get(i).value = mLinePoints.get(0).value;
                mLinePoints.get(i).lineNum = mLinePoints.get(0).lineNum;
            }

        }

        canvas.drawPath(mPath, mPaint);

        float currentTimeX = 0;

        float currentTimeY = 0;

        mLinePointsGray.clear();
        for (int i = 0; i < mLinePoints.size(); i++) {
            LinePoint linePoint = mLinePoints.get(i);
            if (goneTime.toMinutes() - linePoint.duration.toMinutes() >= 0 && goneTime.toMinutes() > 0) {
                mLinePointsGray.add(linePoint);
            } else {
                break;
            }
        }

        if (intervalTime % 30 != 0 && mLinePointsGray.size() > 0 && intervalTime > 0) {

            LinePoint firstCanUsePoint = mLinePoints.get(0);

            for (int i = mLinePointsGray.size() - 1; i >= 0; i--) {
                if (mLinePoints.get(i).enable) {
                    firstCanUsePoint = mLinePoints.get(i);
                    break;
                }
            }
            currentTimeX = firstCanUsePoint.x + (mLineMargin * (intervalTime / 30f));
            LinePoint lastCanUsePoint = mLinePoints.get(mLinePoints.size() - 1);

            for (int i = mLinePointsGray.size(); i < mLinePoints.size(); i++) {
                if (mLinePoints.get(i).enable) {
                    lastCanUsePoint = mLinePoints.get(i);
                    break;
                }
            }

            float range = (currentTimeX - firstCanUsePoint.x) / (lastCanUsePoint.x - firstCanUsePoint.x);

            float iy = lastCanUsePoint.y - firstCanUsePoint.y;
            float y = firstCanUsePoint.y + (iy / (lastCanUsePoint.x - firstCanUsePoint.x)) * ((currentTimeX - firstCanUsePoint.x));
            currentTimeY = y;

            Path grayPath = new Path();
            grayPath.moveTo(firstCanUsePoint.x, firstCanUsePoint.y);
            grayPath.lineTo(currentTimeX, currentTimeY);

            mPaint.setColor(Color.parseColor("#C4C4C4"));

            canvas.drawPath(grayPath, mPaint);

        }

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);
        //绘制LinePoint

        for (int i = 0; i < mLinePoints.size(); i += skip) {

            if (mLinePoints.get(i).enable && mXVisibleRange.contains(mLinePoints.get(i).x)) {

                if (mLinePoints.get(i).isDashPath) {
                    canvas.drawCircle(mLinePoints.get(i).x, mLinePoints.get(i).y, mPointBigRadius, mPaint);
                    canvas.drawLine(mXVisibleRange.getLower(), mLinePoints.get(i).y, mLinePoints.get(i).x, mLinePoints.get(i).y, mDashPathPaint);
                    canvas.drawLine(mLinePoints.get(i).x, 0, mLinePoints.get(i).x, getHeight(), mDashPathPaint);

                } else {
                    canvas.drawCircle(mLinePoints.get(i).x, mLinePoints.get(i).y, mPointRadius, mPaint);
                }
            }
        }


        //寻找是否存在enable的点，若不存在则在中间绘制一个图标
        if (firstPoint == null) {
            float x = mLines.get(mScale == 1 ? mLinesNum - 2 : (24 / mScale / 2)).x;
            float y = mYCoord.get(mYCoord.size() / 2).y;
            canvas.drawCircle(x, y, mPointRadius, mPaint);
            mPaint.setStrokeWidth(mLineWidth);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(Color.BLACK);
            canvas.drawLine(x - mPointRadius / 2, y, x + mPointRadius / 2, y, mPaint);
            canvas.drawLine(x, y - mPointRadius / 2, x, y + mPointRadius / 2, mPaint);
        }

        canvas.restoreToCount(saveCount);
        mPath.reset();
        mPaint.reset();

        int runnablesSize = runnables.size();
        for (int i = 0; i < runnablesSize; i++) {
            runnables.get(i).run();
        }

        if (runnablesSize > 0) {
            invalidate();
        }

        runnables.clear();

    }

    private LinePoint currentTimePoint = new LinePoint(0);


    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, @Nullable int[] consumed, @Nullable int[] offsetInWindow) {
        return super.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    private Duration mStartTime;

    /**
     * 设置延时摄影的开始时间
     */
    public void setStartTime(Duration duration) {
        if (duration != null) {
            mStartTime = duration;
            Log.d("时间设置", mStartTime.toString());
            mLines.clear();
            mLinePoints.clear();
        }
        invalidate();
    }

    public void cancelChoosePoint() {
        for (int i = 0; i < mLinePoints.size(); i++) {
            if (mLinePoints.get(i).isDashPath) {
                mLinePoints.get(i).isDashPath = false;
                mSingleClickedXIndex = -1;
                mSingleClickedYIndex = -1;
                invalidate();
                break;
            }
        }
    }

    private int mTimeFormat = 24;

    public void setTimeFormat() {
        ContentResolver cv = getContext().getContentResolver();
        String strTimeFormat = android.provider.Settings.System.getString(cv,
                android.provider.Settings.System.TIME_12_24);

        if ("12".equals(strTimeFormat)) {
            mTimeFormat = 12;
        } else {
            mTimeFormat = 24;
        }

/*        Date time = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
        String format = simpleDateFormat.format(new Date(System.currentTimeMillis()));
        System.out.println();*/

    }

    public int getTimeFormat() {
        return mTimeFormat;
    }

    public void deleteCurrentPoint() {
        if (!mCanEdit) return;
        for (int i = 0; i < mLinePoints.size(); i++) {
            if (mLinePoints.get(i).isDashPath && i != 0) {
                LinePoint linePoint = mLinePoints.get(i);
                linePoint.value = 0;
                linePoint.y = mYCoord.get(mYCoord.size() / 2).y;
                linePoint.enable = false;
                linePoint.isDashPath = false;
                linePoint.lineNum = mYCoord.size() / 2;
                mSingleClickedXIndex = -1;
                mSingleClickedYIndex = -1;
                invalidate();
                break;
            }
        }
    }

    private int mScale = 2;//代表绘制时的跨度，也能表示：1 -> 小时 ，2->半小时
    private int mMaxScale = 2;

    private int mMinScale = 1;

    private float scale = 1;

    private boolean isScaling = false;
    private ScaleGestureDetector mScaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.OnScaleGestureListener() {
        @Override
        public boolean onScale(@NonNull ScaleGestureDetector detector) {

            float scaleFactor = detector.getScaleFactor();
            if (Float.isNaN(scaleFactor) || Float.isInfinite(scaleFactor)) {
                return false;
            }
            scale *= scaleFactor;

            if (scale < 0.147) {
                scale = 0.147f;
                invalidate();
            } else {
                invalidate();
            }

            return true;
        }

        @Override
        public boolean onScaleBegin(@NonNull ScaleGestureDetector detector) {
            getParent().requestDisallowInterceptTouchEvent(true);
            isScaling = true;
            return true;
        }

        @Override
        public void onScaleEnd(@NonNull ScaleGestureDetector detector) {

        }


    });

    private float mSingleClickedX;

    private float mSingleClickedY;

    private int mSingleClickedXIndex = -1;

    private int mSingleClickedYIndex = -1;

    private int mSingleDownXIndex = -1;

    private int mSingleDownYIndex = -1;

    private boolean hasNewClicked = false;

    private boolean hasNewDown = false;

    private boolean canScroll = false;

    private float scrollPointStartY = 0;
    private float scrollPointYDistance = 0;
    private float scrollPointXDistance = 0;
    private int temporaryScrollPointXDistance = 0;
    private int mIsScrollHorizon = 0;
    private GestureDetector mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (mIsScrollHorizon == 1) return true;

            mSingleClickedX = e.getX();

            mSingleClickedY = e.getY();

            hasNewClicked = true;

            mSingleClickedXIndex = findClickXIndex(mSingleClickedX);

            mSingleClickedYIndex = findClickYIndex(mSingleClickedY);

            if (mSingleClickedXIndex == -1 && mSingleClickedYIndex == -1) {
                return false;
            }

            invalidate();

            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onScroll(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {

            if (e2.getPointerCount() > 1) return false;
            if (mLinePoints == null || mLinePoints.size() == 0) return false;
            if (canScroll) {
                //滑动选择
                if ((Math.abs(distanceX) > Math.abs(distanceY) || mIsScrollHorizon == 1) && mSingleDownXIndex != -1 && mSingleDownYIndex != -1 && mSingleDownXIndex != 0) {

                    if (mIsScrollHorizon == -1 && !mLinePoints.get(mSingleDownXIndex).enable)
                        return false;
                    mIsScrollHorizon = 1;

                    //水平移动
                    scrollPointXDistance += (-distanceX) / mLineMargin;

                    int newLineNum = (mSingleDownXIndex + (int) scrollPointXDistance);
                    if (newLineNum >= mLines.size() || newLineNum <= 0 || newLineNum == temporaryScrollPointXDistance || Math.abs(scrollPointXDistance) < 1) {
                        if (newLineNum < 0) {
                            scrollPointXDistance = 0;
                        }
                    } else {
                        hasNewDown = true;
                        System.out.println("执行");

                        temporaryScrollPointXDistance = newLineNum;

                        invalidate();
                    }

                } else {

                    mIsScrollHorizon = -1;

                    scrollPointYDistance = (e2.getY() - scrollPointStartY) / (getHeight() / mCoordTexts.length);

                    int newLineNum = (mLinePoints.get(mSingleClickedXIndex).lineNum + (int) scrollPointYDistance);

                    if (newLineNum >= mCoordTexts.length || newLineNum < 0 || mSingleClickedYIndex == newLineNum) {

                    } else {

                        hasNewClicked = true;

                        mSingleClickedYIndex = newLineNum;

                        scrollPointStartY = e2.getY();

                        invalidate();

                    }

                }

                return true;

            } else {
                if (Math.abs(distanceX) > Math.abs(distanceY) || mIsScrollHorizon == 1) {
                    if (mIsScrollHorizon == -1) return false;
                    mIsScrollHorizon = 1;
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
                    return true;
                } else {
                    mIsScrollHorizon = -1;
                    return false;
                }
            }
        }

        @Override
        public boolean onDown(@NonNull MotionEvent e) {

            if ((mSingleClickedXIndex = findClickXIndex(e.getX())) != -1 && mLinePoints.get(mSingleClickedXIndex).isDashPath) {
                canScroll = true;
                scrollPointStartY = e.getY();
            } else {
                canScroll = false;
            }

            mSingleClickedX = e.getX();

            mSingleClickedY = e.getY();

            mSingleDownXIndex = findClickXIndex(mSingleClickedX);

            mSingleDownYIndex = findClickYIndex(mSingleClickedY);

            if (mSingleClickedXIndex != -1 && mSingleClickedYIndex != -1) {

                return true;
            }

            return false;

        }

    });

    private int findClickXIndex(float clickX) {
        int skip = mScale == 1 ? 2 : 1;
        for (int i = 0; i < mLines.size(); i += skip) {
            Rect lineRect = mLines.get(i).rect;
            if (lineRect.contains((int) (clickX + Math.abs(mScrollLength)), 0)) {
                return i;
            }
        }
        return -1;
    }

    private int findClickYIndex(float clickY) {
        for (int i = 0; i < mYCoord.size(); i++) {
            Rect coordRect = mYCoord.get(i).rect;
            if (coordRect.contains(0, (int) clickY)) {
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

        if (event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }

        if (!mCanTouch) return true;
        if (!mCanEdit) return true;
        boolean result = mGestureDetector.onTouchEvent(event);

        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_UP: {
                canScroll = false;
                mIsScrollHorizon = 0;
                scrollPointXDistance = 0;
                isScaling = false;
                invalidate();
                break;
            }
        }

        if (event.getActionMasked() == MotionEvent.ACTION_DOWN || event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {
            if (canScroll) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            if (event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            return true;
        } else {
            if (mIsScrollHorizon == 1) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            if (isScaling) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            return mIsScrollHorizon == 1 || isScaling;
        }

    }

    private PointClickListener mListener;

    public void setListener(PointClickListener mListener) {
        this.mListener = mListener;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        mListener = null;

    }

    public int getLineNum(float value) {
        for (int i = 0; i < mCoordTexts.length; i++) {
            float v = Float.parseFloat(mCoordTexts[i]);
            if (v == value) return i;
        }
        return -1;
    }

    public int findTimeX(Duration duration) {
        for (int i = 0; i < mLines.size(); i++) {
            Duration d = mLines.get(i).duration;
            if (d != null && duration != null) {
                if (d.compareTo(duration) == 0) {
                    return i;
                }
            }
        }
        return -1;
    }


    private boolean mCanEdit = false;

    public boolean getCanEdit() {
        return mCanEdit;
    }

    public void setCanEdit(boolean edit) {
        mCanEdit = edit;
        if (edit) {
            this.setAlpha(1f);
        } else {
            this.setAlpha(0.8f);
        }
//        invalidate();
    }

    public static interface PointClickListener {

        void onChoosePoint(LinePoint point);

        void onNothingPointChecked();

        void updateTime(Duration duration);

    }

    private boolean mCanTouch = true;

    public void setCanTouch(boolean cantouch) {
        mCanTouch = cantouch;
    }

}

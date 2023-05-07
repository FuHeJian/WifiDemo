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
import androidx.core.content.ContextCompat;

import com.google.android.material.internal.ViewUtils;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

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

    private float mPointBigRadius = ViewUtils.dpToPx(getContext(), 17);

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
        ArrayList<LinePoint> linePoints = new ArrayList<>();
        for (LinePoint mLinePoint : mLinePoints) {
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
                for (int i1 = 0; i1 < mLinesNum * 2; i1++) {
                    LinePoint linePoint = new LinePoint(0);
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

    public void setPointData(int index, LinePoint point) {
        if (point != null) {
            if (index > 0 && index < mLinePoints.size()) {
                LinePoint linePoint = mLinePoints.get(index);
                linePoint.enable = point.enable;
                linePoint.lineNum = point.lineNum;
            }
            invalidate();
        }
    }

    public void resetPoints() {
        for (int i = 0; i < mLinePoints.size(); i++) {
            LinePoint linePoint = mLinePoints.get(i);
            linePoint.enable = false;
        }
    }

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
                mLines.add(new Line(null, 0));
            }
            linePointsIsInitial = false;
        }

        if (mLinePoints.size() == 0) {
            for (int i1 = 0; i1 < mLinesNum * 2; i1++) {
                LinePoint linePoint = new LinePoint(0);
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

            float margin = getHeight() / (mCoordTexts.length - 1);

            float x = 0;

            float y = i * margin + mTextHeight / 2f;

            YCoord yCoord = mYCoord.get(i);
            yCoord.value = Float.parseFloat(mCoordTexts[i]);

            if (yCoord.value == 0) {
                x += mTextHeight / 3f;
            }

            if (mSingleClickedXIndex != -1 && mSingleClickedYIndex != -1) {
                if (hasNewClicked) {

                    if (mSingleClickedYIndex == i) {

                        if (!mLinePoints.get(mSingleClickedXIndex).isDashPath) {
                            mPaint.setColor(Color.parseColor("#FFFFFF"));
                        } else {

                            if (mLinePoints.get(mSingleClickedXIndex).value != yCoord.value) {
                                mPaint.setColor(Color.parseColor("#FFFFFF"));
                            } else {
                                mPaint.setColor(Color.parseColor("#1AFFFFFF"));
                            }

                        }

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

            canvas.drawText(mCoordTexts[i], x, y, mPaint);

            y = i * margin;
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

                for (int i1 = 0; i1 < mLinesNum * 2; i1++) {
                    LinePoint linePoint = mLinePoints.get(i1);
                    if (!linePoint.enable) {
                        linePoint.y = y;
                        mLinePoints.set(i1, linePoint);
                    }
                }

                linePointsIsInitial = true;

            }
        }

        mLinesStartX = mTextWidth * 2;

        mVisibleWidth = getWidth() - mLinesStartX;

        mXVisibleRange = Range.create(mLinesStartX + Math.abs(mScrollLength), mLinesStartX + Math.abs(mScrollLength) + mVisibleWidth + mLineWidth);

        //绘制画布的线
        int saveCount = canvas.save();

        if (mScale == 1) {
            mLineMargin = (getWidth() - mLinesStartX - mLinesNum * mLineWidth) / (mLinesNum - 1);
            mScrollLength = 0;
        } else {
            mLineMargin = ((getWidth() - mLinesStartX - mLinesNum * mLineWidth) / (mLinesNum) * 2 - 1);
        }

        canvas.translate(mScrollLength, 0);
        //限制显示范围
        canvas.clipRect(mLinesStartX - mPointBigRadius + Math.abs(mScrollLength), -mPointBigRadius, Math.abs(mScrollLength) + getWidth() + mPointBigRadius, getHeight() + mPointBigRadius);


        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.parseColor("#444B52"));
        mPaint.setStrokeWidth(mLineWidth);

        float startX = 0;
        mCurrentVisibleStartLineIndex = -1;
        mCurrentVisibleEndLineIndex = -1;
        for (int i = 0; i < mLinesNum * mScale; i++) {

            startX = mLinesStartX + i * mLineWidth + i * mLineMargin;

            int relativeI = i * skip;

            if (mXVisibleRange.contains(startX)) {
                if (mCurrentVisibleStartLineIndex == -1) {
                    mCurrentVisibleStartLineIndex = relativeI;
                } else {
                    mCurrentVisibleEndLineIndex = relativeI;
                }
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
            if (line.duration == null) {
                line.duration = Duration.of(i * timeScale, chronoUnit);
            }

            mLines.set(relativeI, line);

/*            String time = String.format("%d", line.duration.toHours()) + " : " + String.format("%d", line.duration.toMinutes() - line.duration.toHours() * 60);

              float timeWidth = mPaint.measureText(time);

              canvas.drawText(time, startX - timeWidth / 2, - mTextWidth / 2f, mPaint);*/

            LinePoint linePoint = mLinePoints.get(relativeI);

            linePoint.x = startX;

            if (linePoint.duration == null) {

                linePoint.duration = Duration.of(i * timeScale, chronoUnit);

            }

            if (!linePoint.enable) {

                linePoint.lineNum = mYCoord.size() / 2;

            }

            mLinePoints.set(relativeI, linePoint);

        }

        mPaint.setColor(Color.parseColor("#818181"));
        if (mCurrentVisibleEndLineIndex != -1 && mCurrentVisibleStartLineIndex != -1)
            canvas.drawLine(mLines.get(mCurrentVisibleStartLineIndex).x, mYCoord.get(mYCoord.size() / 2).y, mLines.get(mCurrentVisibleEndLineIndex).x, mYCoord.get(mYCoord.size() / 2).y, mPaint);

        mCanScrollRange = Range.create(-(startX - mVisibleWidth), 0f);

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

                    linePoint.lineNum = mSingleClickedYIndex;

                } else if (!canScroll) {
                    linePoint.y = mYCoord.get(mYCoord.size() / 2).y;

                    linePoint.value = mYCoord.get(mYCoord.size() / 2).value;

                    linePoint.enable = false;

                    linePoint.isDashPath = false;

                    linePoint.lineNum = mYCoord.size() / 2;

                    mSingleClickedXIndex = -1;
                    mSingleClickedYIndex = -1;

                }

            } else {

                if (mLastClickPoint != null) {
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

            }


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

        if (lastPoint != null) {
            for (int i = lastIndex + skip; i < mLinePoints.size(); i += skip) {
                LinePoint linePoint = mLinePoints.get(i);
                linePoint.y = lastPoint.y;
                linePoint.value = lastPoint.y;
                linePoint.lineNum = lastPoint.lineNum;
            }
        }


        if (mSingleClickedXIndex == -1 && mSingleClickedYIndex == -1) {
            if (mListener != null) {
                mListener.onNothingPointChecked();
            }
        }

        //缩放动作后更新点的x位置
        for (int i = 0; i < mLinePoints.size(); i++) {

            if (mLinePoints.get(i).enable) {

                mLinePoints.get(i).x = mLines.get(i).x;

            }
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

        if (firstPoint != null) {//有点

            if (firstPoint.index == 0) {
                mPath.moveTo(firstPoint.x, firstPoint.y);
            } else {
                mPath.moveTo(mLinePoints.get(0).x, mLinePoints.get(0).y);

                if (firstPoint.index / skip > 1) {
                    mPath.lineTo(mLinePoints.get(firstPoint.index - skip).x, mLinePoints.get(firstPoint.index - skip).y);
                }

            }

            for (int i = 0; i < mLines.size(); i += skip) {

                if (mLinePoints.get(i).enable) {
                    mPath.lineTo(mLinePoints.get(i).x, mLinePoints.get(i).y);
                }

            }

            //最后一个点连接到末尾
            mPath.lineTo(mLinePoints.get(mLinePoints.size() - skip).x, mLinePoints.get(mLinePoints.size() - skip).y);


        } else {//没有点

            mPath.moveTo(mLinePoints.get(0).x, mLinePoints.get(0).y);

            mPath.lineTo(mLinePoints.get(mLinePoints.size() - skip).x, mLinePoints.get(mLinePoints.size() - skip).y);

        }

        canvas.drawPath(mPath, mPaint);

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);
        //绘制LinePoint

        for (int i = 0; i < mLinePoints.size(); i += skip) {

            if (mLinePoints.get(i).enable && mXVisibleRange.contains(mLinePoints.get(i).x)) {

                if (mLinePoints.get(i).isDashPath) {
                    canvas.drawCircle(mLinePoints.get(i).x, mLinePoints.get(i).y, mPointBigRadius, mPaint);
                    canvas.drawLine(mXVisibleRange.getLower(), mLinePoints.get(i).y, mLinePoints.get(i).x, mLinePoints.get(i).y, mDashPathPaint);
                    canvas.drawLine(mLinePoints.get(i).x, 0, mLinePoints.get(i).x, getHeight(), mDashPathPaint);
                    Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.holyline_scroll_unlock_icon);
                    Drawable drawable = null;
                    if (drawable != null) {
                        drawable.setBounds((int) (mLinePoints.get(i).x - mPointBigRadius * 0.5), (int) (mLinePoints.get(i).y - mPointBigRadius * 2 / 3f), (int) (mLinePoints.get(i).x + mPointBigRadius * 0.5), (int) (mLinePoints.get(i).y + mPointBigRadius * 2 / 3f));
                        drawable.draw(canvas);
                    }else{
                        canvas.drawCircle(mLinePoints.get(i).x, mLinePoints.get(i).y, mPointRadius, mPaint);
                    }
                } else {
                    canvas.drawCircle(mLinePoints.get(i).x, mLinePoints.get(i).y, mPointRadius, mPaint);
                }
            }

        }

        //寻找是否存在enable的点，若不存在则在中间绘制一个图标
        for (int i = 0; i < mLinePoints.size(); i++) {
            if (mLinePoints.get(i).enable) {
                break;
            } else if (i == mLinePoints.size() - 1) {
                //绘制图标
                float x = mLines.get(mScale == 1 ? mLinesNum - 2 : (24 / mScale / 2)).x;
                float y = mYCoord.get(mYCoord.size() / 2).y;
                canvas.drawCircle(x, y, mPointRadius, mPaint);
                mPaint.setStrokeWidth(mLineWidth);
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setColor(Color.BLACK);
                canvas.drawLine(x - mPointRadius / 2, y, x + mPointRadius / 2, y, mPaint);
                canvas.drawLine(x, y - mPointRadius / 2, x, y + mPointRadius / 2, mPaint);
            }
        }

        canvas.restoreToCount(saveCount);
        mPath.reset();
        mPaint.reset();

    }

    public void deleteCurrentPoint() {
        for (int i = 0; i < mLinePoints.size(); i++) {
            if (mLinePoints.get(i).isDashPath) {
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

    private int mSingleClickedXIndex = -1;

    private int mSingleClickedYIndex = -1;

    private boolean hasNewClicked = false;

    private boolean canScroll = false;

    private float scrollPointStartY = 0;
    private float scrollPointYDistance = 0;
    private int mIsScrollHorizon = 0;
    private GestureDetector mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {

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
        public boolean onScroll(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {

            if (e2.getPointerCount() > 1) return false;
            if (canScroll) {
                //滑动选择

                scrollPointYDistance = (e2.getY() - scrollPointStartY) / (getHeight() / mCoordTexts.length);

                int newLineNum = (mLinePoints.get(mSingleClickedXIndex).lineNum + (int) scrollPointYDistance);

                if (newLineNum >= mCoordTexts.length || newLineNum < 0 || mSingleClickedYIndex == newLineNum) {

                } else {

                    hasNewClicked = true;

                    mSingleClickedYIndex = newLineNum;

                    scrollPointStartY = e2.getY();

                    invalidate();

                }
                return true;

            } else {
                if (Math.abs(distanceX) > Math.abs(distanceY) || mIsScrollHorizon == 1) {
                    if(mIsScrollHorizon == -1)return false;
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

        if (!mCanTouch) return true;
        mScaleGestureDetector.onTouchEvent(event);

        boolean result = mGestureDetector.onTouchEvent(event);

        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_UP: {
                canScroll = false;
                mIsScrollHorizon = 0;
                break;
            }

        }

        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            if (canScroll) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            return true;
        } else {
            if (mIsScrollHorizon == 1) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            return mIsScrollHorizon == 1;
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

    public static interface PointClickListener {

        void onChoosePoint(LinePoint point);

        void onNothingPointChecked();

    }

    private boolean mCanTouch = true;

    public void setCanTouch(boolean cantouch) {
        mCanTouch = cantouch;
    }

}

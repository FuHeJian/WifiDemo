package com.example.wifidemo1.customview;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.icu.util.Calendar;
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
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

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

    private MyPoint mLastCurrentTapDown;

    private boolean mCanScrollHorizon;
    private boolean mIsOnScale;

    private boolean isNeedRemovePoint = false;

    private float mMovePointDistanceX;
    private float mMovePointDistanceY;

    /**
     * 上下移动点时，每移动一个竖直坐标坐标值所需的像素距离
     */
    private float mVerticalMovePerDistance;

    /**
     * 水平移动点时，每移动一个横向坐标坐标值所需的像素距离
     * <p>
     * 值为{@link #mHorizontalMoveNormalPerDistance}
     * </p>
     */
    private float mHorizontalMovePerDistance;

    /**
     * 正常移动时，所需的距离
     */
    private float mHorizontalMoveNormalPerDistance;

    private GestureDetector mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {

            //点击事件
            if (!mCanEdit) return false;

            if (mCurrentTapDown != null) {
                mCurrentTapDown.isChoose = true;
                //重新确定点的和横坐标
                findAndUpdateSingleTapPosition((int) (mMoveAbsLength + e.getX()), (int) e.getY(), true, true, false);
            } else {

                MyPoint point = findAndUpdateSingleTapPosition((int) (mMoveAbsLength + e.getX()), (int) e.getY(), true, true, false);

                if (point != null) {
                    point.isChoose = true;
                    if (mLastCurrentTapDown != null) {
                        mLastCurrentTapDown.isChoose = false;
                    }
                    mLastCurrentTapDown = point;

                    if (mListener != null) {
                        mListener.onChoosePoint(point);
                    }

                }

            }

            invalidate();

            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {

            if (!mCanEdit) {
                mCurrentTapDown = null;
                return false;
            }

            mCurrentTapDown = findPoint((int) (mMoveAbsLength + e.getX()), (int) e.getY());

            if (mCurrentTapDown != null && !mCurrentTapDown.canEdit) {

                mCurrentTapDown = null;

            }

            if (mCurrentTapDown != null && mLastCurrentTapDown != mCurrentTapDown) {
                if (mLastCurrentTapDown != null) mLastCurrentTapDown.isChoose = false;
            }

            if (mCurrentTapDown != null) {
                mLastCurrentTapDown = mCurrentTapDown;

                if (mListener != null) {
                    mListener.onChoosePoint(mCurrentTapDown);
                }
            }

            if (mCurrentTapDown != null) {//触摸到点,则可以水平竖直移动点
                mCurrentTapDown.isChoose = true;
                getParent().requestDisallowInterceptTouchEvent(true);
                mCanScrollHorizon = true;
            }

            return super.onDown(e);
        }

        private int lastOrientation;

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

//             || mCanScrollHorizon
            if (Math.abs(distanceX) >= Math.abs(distanceY)) {//水平移动
                mCanScrollHorizon = true;

                //表示后续事件到UP事件不要拦截
                getParent().requestDisallowInterceptTouchEvent(true);

                if (mCurrentTapDown != null) {//由于触摸到点，则根据点开始滑动，点移出画布后，画布也跟着移动，可以水平竖直移动点

                    int orientation = distanceX < 0 ? -1 : 1;//移动方向，-1表示向右滑动点
                    if (lastOrientation != orientation) {//恢复正常速度
                        if (autoScrollWork != null && !autoScrollWork.isDisposed()) {
                            autoScrollWork.dispose();
                        }
                    }

                    mMovePointDistanceX += distanceX;

                    int lines = (int) (mMovePointDistanceX / mHorizontalMovePerDistance);
                    System.out.println(lines);
                    mCurrentTapDown.isChoose = true;
                    int newIndex = mCurrentTapDown.line.index - lines;
                    if (lines != 0 && newIndex > 0 && newIndex < mLines.size() && mCurrentTapDown.line.index != 0 && !isInvalidateTime(mLines.get(newIndex))) {//
                        mMovePointDistanceX = mMovePointDistanceX % mHorizontalMovePerDistance;
                        if (mCurrentTapDown.line.movePoint != null) {//mCurrentTapDown.line表示即将离开的线
                            mCurrentTapDown.line.points = mCurrentTapDown.line.movePoint;//恢复移动过的点
                            mCurrentTapDown.line.movePoint = null;
                        } else {
                            mCurrentTapDown.line.points = null;
                        }
                        mCurrentTapDown.line = mLines.get(newIndex);//mCurrentTapDown.line表示即将抵达的线
                        if (mCurrentTapDown.line.points != null) {
                            mCurrentTapDown.line.movePoint = mCurrentTapDown.line.points;
                            isNeedRemovePoint = true;
                        } else {
                            isNeedRemovePoint = false;
                        }

                        mCurrentTapDown.line.points = mCurrentTapDown;//抵达的线更新为当前移动的点

                        if (mListener != null) {
                            mListener.onChoosePoint(mCurrentTapDown);
                        }

                    }

                    if (mCurrentTapDown.line.rect.right >= (mCanvasStartX + mCanvasWidth + mMoveAbsLength) && orientation == -1) {
                        mMoveAbsLength = mCurrentTapDown.line.rect.right - mCanvasWidth - mCanvasStartX;
                        if ((autoScrollWork == null || autoScrollWork.isDisposed()) && lastOrientation == orientation) {
                            autoScroll();
                        }
                    } else if (mCurrentTapDown.line.rect.left <= (mCanvasStartX + mMoveAbsLength) && orientation == 1) {
                        mMoveAbsLength = mCurrentTapDown.line.rect.left - mCanvasStartX;

                        if ((autoScrollWork == null || autoScrollWork.isDisposed()) && lastOrientation == orientation) {
                            autoScroll();
                        }

                    }

                    lastOrientation = orientation;

                } else {
                    mMoveLength = mMoveAbsLength + distanceX;
                    mMoveAbsLength = Math.abs(mMoveLength);
                }

            } else {

                if (mCurrentTapDown != null) {//竖直移动点
                    getParent().requestDisallowInterceptTouchEvent(true);

                    mMovePointDistanceY += distanceY;

                    int dYcoords = (int) (mMovePointDistanceY / mVerticalMovePerDistance);

                    int newYcoords = mCurrentTapDown.yCoord.index - dYcoords;

                    if (dYcoords != 0 && newYcoords >= 0 && newYcoords < mYCoords.size()) {
                        mMovePointDistanceY = mMovePointDistanceY % mVerticalMovePerDistance;
                        mCurrentTapDown.yCoord = mYCoords.get(newYcoords);
                    }

                } else {//嵌套scrollView时，使scrollView执行竖直滑动
                    getParent().requestDisallowInterceptTouchEvent(false);
                }

            }

            invalidate();
            return true;
        }


        private boolean isInvalidateTime(MyLine line) {
            return line == null || line.duration.toMinutes() < (mStartTime.toMinutes() + mRuntime);
        }

        private Disposable autoScrollWork;

        private void autoScroll() {

            if (autoScrollWork == null || autoScrollWork.isDisposed()) {
                autoScrollWork = AndroidSchedulers.mainThread().schedulePeriodicallyDirect(new Runnable() {
                    @Override
                    public void run() {
                        onScroll(null, null, lastOrientation * (mHorizontalMoveNormalPerDistance / 20), 0);
                        mMoveAbsLength += -lastOrientation * (mHorizontalMoveNormalPerDistance / 15);
                    }
                }, 0, 10, TimeUnit.MILLISECONDS);

                mAutoScrollWork = autoScrollWork;

            }

        }

    });

    private Disposable mAutoScrollWork;

    /**
     * 水平移动距离 range
     */
    private float mMoveAbsLength;

    /**
     * 只用于，当mMoveLength小于0时，滑到了最右端时，重置为0
     * <p>
     * 和mMoveAbsLength没有必然联系
     */
    private float mMoveLength;

    /**
     * 寻找已经存在的点
     *
     * @param x
     * @param y
     * @return
     */
    private MyPoint findPoint(int x, int y) {
        return findAndUpdateSingleTapPosition(x, y, false, false, true);
    }

    /**
     * 寻找点，若线上可以新增点
     *
     * @param x
     * @param y
     * @param reset                 若线上有点，是否将此点的竖坐标置为触摸的位置
     * @param add                   没有找到是否新增点
     * @param restrictResponseRange 是否限制触摸感应范围，false为整条线，true为点的周围
     * @return 返回找到的点或者新增的点，如果点击范围不匹配则返回null
     */
    private MyPoint findAndUpdateSingleTapPosition(int x, int y, boolean reset, boolean add, boolean restrictResponseRange) {

        MyLine foundLine = null;
        //确定 x 属于哪条线
        for (int i = 0; i < mLines.size(); i++) {

            MyLine line = mLines.get(i);

            if (line.rect.contains((int) x, (int) y)) {
                foundLine = line;
                break;
            }

        }

        if (foundLine == null) {
            return null;
        }


        //确定 y 属于哪个值
        MyYCoords foundYCoord = null;
        for (int i = 0; i < mYCoords.size(); i++) {
            MyYCoords yCoords = mYCoords.get(i);
            if (yCoords.rect.contains((int) (x - mMoveAbsLength), (int) y)) {
                foundYCoord = yCoords;
                break;
            }
        }

        if (foundYCoord == null) {
            return null;
        }

        MyPoint point = foundLine.points;

        if (point != null && point.canEdit) {

            if (restrictResponseRange) {

                if (point.yCoord != foundYCoord) {

                    //扩大触摸检测范围，
                    int index = foundYCoord.index;

                    if (index > 0 && mYCoords.get(index - 1) == point.yCoord) {
                        return point;
                    } else if (index >= 0 && index < (mYCoords.size() - 1) && mYCoords.get(index + 1) == point.yCoord) {
                        return point;
                    }

                    return null;
                }
            }

            if (reset) {
                point.yCoord = foundYCoord;
            }
            if (point.canEdit) {
                return point;
            } else {
                return null;
            }

        }

        if (add && foundLine.duration.toMinutes() > (mStartTime.toMinutes() + mRuntime)) {
            MyPoint addPoint = new MyPoint();
            addPoint.yCoord = foundYCoord;
            addPoint.line = foundLine;
            foundLine.points = addPoint;
//            mPoints.add(addPoint);
//            Collections.sort(mPoints, new Comparator<MyPoint>() {
//                @Override
//                public int compare(MyPoint o1, MyPoint o2) {
//                    return o1.line.index - o2.line.index;
//                }
//            });
            return addPoint;
        }

        return null;
    }

    /**
     * 删除选中的点
     */
    public void deleteCurrentTapDownPoint() {

        for (int i = 0; i < mLines.size(); i++) {
            MyPoint point = mLines.get(i).points;
            if (point != null && point.isChoose && point.canEdit && point.canDelete) {
                mLines.get(i).points = null;
                mLines.get(i).movePoint = null;
                point.line = null;
                point.yCoord = null;
            }
        }

        mCurrentTapDown = null;
        invalidate();

    }

    /**
     * 重置点
     */
    public void resetPoints() {
        for (int i = 0; i < mLines.size(); i++) {
            MyPoint point = mLines.get(i).points;
            if (point != null) {

                if (point.canEdit && point.canDelete) {
                    mLines.get(i).points = null;
                    mLines.get(i).movePoint = null;
                    point.line = null;
                    point.yCoord = null;
                } else {
                    point.isChoose = false;
                }

            }
        }
        mCurrentTapDown = null;
        mLastCurrentTapDown = null;
        invalidate();
    }

    private Duration mStartTime = Duration.of(0, ChronoUnit.MINUTES);

    /**
     * 启动时间不是半点时，最接近且小于启动时间的半点时间
     */
    private Duration mStartTime49;

    /**
     * 设置失效的时间
     *
     * @param invalidateMinutes
     */
    public void setInvalidateMinutes(int invalidateMinutes) {
        this.mInvalidateMinutes = invalidateMinutes;
    }

    /**
     * 从起始时间开始计算，无效（已走过）的分钟数
     */
    private int mInvalidateMinutes = 0;


    private boolean mNeedUpdateStartTime = false;

    /**
     * 设置起始时间
     *
     * @param startTime
     */
    public void setStartTime(@NonNull Duration startTime) {

        if (startTime == null) return;


        mNeedUpdateStartTime = true;

        mStartTime = startTime;

        if (startTime.toMinutes() % 30 != 0) {

            mStartTime49 = Duration.of(30 * (startTime.toMinutes() / 30), ChronoUnit.MINUTES);

        }

        if (mStartTime.toMinutes() % 30 == 0) {
            mLinesNum = 48;
        } else {
            mLinesNum = 49;
        }

        mMinLinesMargin = mCanvasWidth / (mLinesNum);

        resetPoints();

        mLines.clear();

        Duration _startTime;

        _startTime = Duration.of(30 * (mStartTime.toMinutes() / 30), ChronoUnit.MINUTES);

        for (int i = 0; i < mLinesNum; i++) {

            MyLine myLine = new MyLine();
            myLine.index = i;
            mLines.add(myLine);

            MyPoint point = new MyPoint();
            if (i == 0) {

                point.yCoord = mYCoords.get(mYCoords.size() / 2);
                point.isChoose = false;
                point.line = myLine;

            }

        }

        //初始化0点

        invalidate();

    }

    private long mRuntime = 0;

    /**
     * 从startTime开始已经运行的分钟数
     *
     * @param runTime
     */
    public void setRunTime(int runTime) {

        if (runTime <= -1) {
            finishTimeWork();
            mRuntime = 0;
        } else {//已经开始运行

            Duration runTimeAbs = mStartTime.plus(Duration.of(runTime, ChronoUnit.MINUTES));

            long lastTimeOfToday = Duration.of(24, ChronoUnit.HOURS).minus(mStartTime).toMinutes();

            long currentTimeMinute = getCurrentTime().toMinutes();

            if (runTime <= lastTimeOfToday && currentTimeMinute >= mStartTime.toMinutes()) {//今天

                mRuntime = currentTimeMinute - mStartTime.toMinutes();

            } else {//第二天

                mRuntime = getCurrentTime().toMinutes() + lastTimeOfToday;

            }
            startTimeWork();//周期性更新运行时间
        }

        if (mListener != null) {
            mListener.updateTime(getCurrentTime());
        }

    }

    private int mTimeFormat = 24;

    public int getTimeFormat() {
        ContentResolver cv = getContext().getContentResolver();
        String strTimeFormat = android.provider.Settings.System.getString(cv,
                android.provider.Settings.System.TIME_12_24);

        if ("12".equals(strTimeFormat)) {
            mTimeFormat = 12;
        } else {
            mTimeFormat = 24;
        }
        return mTimeFormat;
    }

    /**
     * 获取手机时间
     *
     * @return
     */
    public Duration getCurrentTime() {
        Calendar instance = Calendar.getInstance();
        int ch = instance.get(Calendar.HOUR_OF_DAY);
        int cm = instance.get(Calendar.MINUTE);
        Duration nowDuration = Duration.of(ch * 60L + cm, ChronoUnit.MINUTES);
        return nowDuration;
    }

    private Disposable mTimeWork;

    /**
     * 启动循环线程来更新运行时间
     */
    private void startTimeWork() {
        if (mTimeWork == null || mTimeWork.isDisposed()) {
            mTimeWork = Schedulers.io().schedulePeriodicallyDirect(new Runnable() {
                @Override
                public void run() {
                    ++mRuntime;
                    if (mListener != null) {
                        mListener.updateTime(getCurrentTime());
                    }
                    postInvalidate();
                }
            }, 0, 1, TimeUnit.MINUTES);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mTimeWork!=null && !mTimeWork.isDisposed()) {
            mTimeWork.dispose();
        }
    }

    /**
     * 关闭用来新运行时间的循环线程
     */
    private void finishTimeWork() {
        if (mTimeWork != null && !mTimeWork.isDisposed()) {
            mTimeWork.dispose();
        }
    }

    /**
     * 设置点
     *
     * @param points
     */
    public void setPoints(ArrayList<MyPoint> points) {
        resetPoints();

        if (points != null) {

            for (int i = 0; i < points.size(); i++) {
                MyPoint _addPoint = points.get(i);
                if (_addPoint.line != null && _addPoint.yCoord != null) {
                    _addPoint.line.points = _addPoint;//画点时是从line中获取的点进行绘制的，而不是从mPoints
                    if (_addPoint.line.index == 0) {
                        _addPoint.canDelete = false;
                    }
//                    mPoints.add(_addPoint);
                }
            }
//            mPoints = points;
        }

        if (points == null || mLines.size() == 0 || mLines.get(0).points == null) {
            MyPoint point = new MyPoint();
            point.line = mLines.get(0);
            point.yCoord = mYCoords.get(mYCoords.size() / 2);
            point.canDelete = false;
            mLines.get(0).points = point;
        }

        invalidate();

    }

    public ArrayList<MyPoint> getPoints() {
        ArrayList<MyPoint> points = new ArrayList<>();
        for (int i = 0; i < mLines.size(); i++) {
            MyPoint point = mLines.get(i).points;
            if (point != null) {
                points.add(point);
            }
        }
        return points;
    }

    /**
     * @param time 距离第一个点的时间
     * @return
     */
    public MyLine getAndFindLine(int time) {

        if (mLines.size() != 0 && time >= 0) {
            long _startTime = 0;

            _startTime = 30 * (mStartTime.toMinutes() / 30);

            long newTime = mStartTime.toMinutes() + time;
            if (newTime % 30 == 0) {

                long index = newTime / 30 - _startTime / 30;

                if (index > 0 && index < mLines.size()) {
                    return mLines.get((int) index);
                } else {
                    return null;
                }

            } else {
                return null;
            }
        } else {
            return null;
        }

    }

    /**
     * 寻找纵坐标
     *
     * @param value 纵坐标值
     * @return
     */
    public MyYCoords getAndFindYCoords(float value) {
        for (int i = 0; i < mYCoords.size(); i++) {
            if (mYCoords.get(i).value == value) {
                return mYCoords.get(i);
            }
        }
        return null;
    }

    private int mLinesNum = 48;

    public static class MyPoint {
        /**
         * 点所在的横坐标
         */
        public MyLine line;

        /**
         * 点所在的纵坐标
         */
        public MyYCoords yCoord;

        /**
         * 点是否可以删除
         */
        public boolean canDelete = true;

        /**
         * 是否可以移动该点
         */
        public boolean canEdit = true;

        /**
         * 点是否被选择
         */
        public boolean isChoose = false;

    }


    private ArrayList<MyYCoords> mYCoords = new ArrayList<>();

    /**
     * 横坐标对象
     */
    public class MyYCoords {

        /**
         * 触摸响应范围
         */
        Rect rect = new Rect();//触摸响应范围

        public String rawValue;

        public float value;

        int index = 0;

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

        /**
         * 当前线表示的时间
         */
        public Duration duration;

        /**
         * 需要恢复的点
         */
        MyPoint movePoint;

    }

    private Paint mPaint = new Paint();
    private Paint mTextPaint = new TextPaint();

    private int mTextSize = (int) ViewUtils.dpToPx(getContext(), 14);

    /**
     * 虚线画笔
     */
    private Paint mDottedLinePaint = new Paint();
    /**
     * 虚线颜色
     */
    private int mDottedLineColor = Color.WHITE;

    /**
     * 竖坐标距离 折线图的距离
     */
    private float mTextCoordsMarginToCanvas = ViewUtils.dpToPx(getContext(), 6);

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
    private final int mTextColor = Color.parseColor("#1AFFFFFF");

    /**
     * 选中 的文本颜色
     */
    private final int mSelectedTextColor = Color.WHITE;

    public void init() {

//      mPoints = new ArrayList<>();

        Typeface typeface = null;
        try {
            typeface = Typeface.createFromAsset(getContext().getAssets(), "cai978.ttf");
        } catch (Exception e) {

        }
        mTextPaint.setTypeface(typeface);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(mTextSize);

        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);

        //获取文字大小
        Rect rect = new Rect();
        for (int i = 0; i < mYCoordsText.length; i++) {

            String value = mYCoordsText[i];

            if (i % 2 != 0) {
                value = mDialText;
            }

            mTextPaint.getTextBounds(value, 0, value.length(), rect);
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

        mDottedLinePaint.setAntiAlias(true);
        mDottedLinePaint.setColor(mDottedLineColor);
        mDottedLinePaint.setStyle(Paint.Style.STROKE);
        mDottedLinePaint.setStrokeWidth(mLineWidth);
        mDottedLinePaint.setPathEffect(new DashPathEffect(new float[]{10, 20}, 100f));

        for (int i = 0; i < mYCoordsText.length; i++) {
            MyYCoords myYCoords = new MyYCoords();
            myYCoords.rawValue = mYCoordsText[i];
            try {
                myYCoords.value = Float.parseFloat(mYCoordsText[i]);
            } catch (Exception e) {

            }
            mYCoords.add(myYCoords);
        }

        setStartTime(mStartTime);

        setPoints(null);

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

    private float mTextCoordsMargin;

    private final String mDialText = "-";

    private int mDialTextWidth;
    private int mDialTextHeight;

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        mCanvasWidth = getWidth() - mCanvasStartX - mPaddingRight;

        mLinesMargin = mCanvasWidth / 7;

        mMinLinesMargin = mCanvasWidth / (mLinesNum);

        mConstLinesMargin = mLinesMargin;

        float textMargin = (getHeight() - mPaddingTop - mPaddingBottom - mSumAllTextHeight) / (mYCoords.size() - 1);

        mTextCoordsMargin = textMargin;

        mUnSelectedPointRadius = textMargin;

        mSelectedPointRadius = mUnSelectedPointRadius * 2;

        mPointLineWidth = mUnSelectedPointRadius;

        Rect rect = new Rect();

        mTextPaint.getTextBounds(mDialText, 0, mDialText.length(), rect);

        mDialTextWidth = rect.width();

        mDialTextHeight = rect.height();

    }

    /**
     * 线的颜色
     */
    private int mLineColor = Color.parseColor("#444B52");

    /**
     * 未选中点的颜色
     */
    private int mUnSelectedPointColor = Color.WHITE;

    /**
     * 选中点的颜色
     */
    private int mSelectedPointColor = Color.WHITE;

    /**
     * 中间横线的颜色
     */
    private int mMidLineColor = Color.parseColor("#818181");

    /**
     * 中间横线的宽度
     */
    private float mMidLineWidth = ViewUtils.dpToPx(getContext(), 2);

    /**
     * 未选中点的半径
     */
    private float mUnSelectedPointRadius = ViewUtils.dpToPx(getContext(), 8);

    /**
     * 选中的点的半径
     * layout中已动态设置
     */
    private float mSelectedPointRadius = ViewUtils.dpToPx(getContext(), 17);

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //从startTime开始画

        drawCoords(canvas);

        canvas.save();
        restrictMoveLength();

//        canvas.clipRect(mCanvasStartX, mPaddingTop, mCanvasStartX + mCanvasWidth, getHeight() - mPaddingBottom);

        canvas.clipRect(mCanvasStartX, 0, mCanvasStartX + mCanvasWidth, getHeight());

        //只影响translate之后的动作，因为translate改变的是坐标轴，所以translate之前绘制的内容也不会随之改变，只是坐标轴变化了
        canvas.translate(-mMoveAbsLength, 0);//坐标轴向左平移mMoveAbsLength大小

        drawLines(canvas);

        drawPointLine(canvas);

        drawPoint(canvas);

        //平移会改变坐标的位置,恢复坐标位置
        canvas.restore();

    }

    /**
     * 绘制线
     *
     * @param canvas
     */
    private void drawLines(Canvas canvas) {

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mLineColor);
        mPaint.setStrokeWidth(mLineWidth);
        mPaint.setAlpha(190);

        for (int i = 0; i < mLines.size(); i++) {

            MyLine line = mLines.get(i);

            if (mNeedUpdateStartTime) {//mStartTime变化了再更新
                if (i == 0) {
                    line.duration = mStartTime;
                } else {
                    if (mLinesNum == 48) {
                        line.duration = mStartTime.plus(30L * i, ChronoUnit.MINUTES);
                    } else if (mStartTime49 != null) {
                        line.duration = mStartTime49.plus(30L * i, ChronoUnit.MINUTES);
                    }
                }
            }

            float startX = i * mLinesMargin + mLinesMargin / 2f + mCanvasStartX;//线的横坐标

            float startY = mMaxTextHeight / 2f + mPaddingTop;//留出半个文字高度

            float endY = getHeight() - mMaxTextHeight / 2f - mPaddingBottom;

            line.rect.top = (int) startY;

            line.rect.bottom = (int) endY;

            line.rect.left = (int) (startX - mLinesMargin / 2f);

            line.rect.right = (int) (startX + mLinesMargin / 2f);

            canvas.drawLine(startX, startY, startX, endY, mPaint);

            //绘制横坐标
            if (i != 0 && i % 2 == 0) {
                String drawValue = String.valueOf(i / 2);
                Rect _rect = new Rect();
                mTextPaint.getTextBounds(drawValue, 0, drawValue.length(), _rect);
                int tW = _rect.width();
                int tH = _rect.height();
                canvas.drawText(drawValue, startX - tW / 2, endY + tH + mTextCoordsMarginToCanvas, mTextPaint);
            }

        }

        //绘制中间横线
        mPaint.setColor(mMidLineColor);
        mPaint.setStrokeWidth(mMidLineWidth);

        float sx = (mLines.get(0).rect.left + mLines.get(0).rect.right) / 2f;

        float ex = (mLines.get(mLines.size() - 1).rect.left + mLines.get(mLines.size() - 1).rect.right) / 2f;

        float y = (mYCoords.get(mYCoords.size() / 2).rect.top + mYCoords.get(mYCoords.size() / 2).rect.bottom) / 2f;

        canvas.drawLine(sx, y, ex, y, mPaint);

        mHorizontalMoveNormalPerDistance = mLinesMargin / 1.2f;

        mHorizontalMovePerDistance = mHorizontalMoveNormalPerDistance;

        mNeedUpdateStartTime = false;

    }

    private String[] mYCoordsText = {
            "+5", "+4.5", "+4", "+3.5", "+3", "+2.5", "+2", "+1.5", "+1", "+0.5", "0", "-0.5", "-1", "-1.5", "-2", "-2.5", "-3", "-3.5", "-4", "-4.5", "-5"
    };

    /**
     * 绘制纵坐标
     *
     * @param canvas
     */
    private void drawCoords(Canvas canvas) {

        float startX = mPaddingLeft;

        float textMargin = mTextCoordsMargin;

        MyPoint choosePoint = findChoosePoint();

        Rect rect = new Rect();

        float lastY = mPaddingTop;

        for (int i = 0; i < mYCoords.size(); i++) {

            String value = mYCoords.get(i).rawValue;

            mTextPaint.getTextBounds(value, 0, value.length(), rect);

            int textW = rect.width();

            int textH = rect.height();

            if (i % 2 != 0) {
                textW = mDialTextWidth;
                textH = mDialTextHeight;
                value = mDialText;
            }

            float y = lastY + textH + textMargin;

            if (i == 0) y = mPaddingTop + textH;

            float x = startX + mMaxTextWidth - textW;

            lastY = y;

            mYCoords.get(i).index = i;

            Rect y_rect = mYCoords.get(i).rect;
            y_rect.top = (int) (y - textH - textMargin / 2);
/*            if (y_rect.top < mPaddingTop) {
                y_rect.top = (int) mPaddingTop;
            }*/
            y_rect.left = (int) mCanvasStartX;

            y_rect.right = (int) (mCanvasStartX + mCanvasWidth);

            y_rect.bottom = (int) (y + textMargin / 2);

            if (choosePoint != null && choosePoint.yCoord == mYCoords.get(i)) {
                mTextPaint.setColor(mSelectedTextColor);
            } else {
                mTextPaint.setColor(mTextColor);
            }

/*            if (y_rect.bottom > getHeight() - mPaddingBottom) {
                y_rect.bottom = (int) (getHeight() - mPaddingBottom);
            }*/

            if (i % 2 != 0) {
                y = y + textMargin / 3;
            }

            canvas.drawText(value, x, y, mTextPaint);

        }

        mTextPaint.setColor(mTextColor);

        mVerticalMovePerDistance = textMargin + mMaxTextHeight;

    }

    private MyPoint findChoosePoint() {
        MyPoint point = null;
        for (int i = 0; i < mLines.size(); i++) {
            if (mLines.get(i).points != null && mLines.get(i).points.isChoose) {
                point = mLines.get(i).points;
                break;
            }
        }
        return point;
    }

    /**
     * 限制canvas移动范围
     */
    private void restrictMoveLength() {

        float endX = (mLines.size() - 1) * mLinesMargin + mLinesMargin + mCanvasStartX;

        if (mMoveAbsLength + mCanvasWidth + mCanvasStartX > endX) {
            mMoveAbsLength = endX - mCanvasWidth - mCanvasStartX;
        } else if (mMoveLength < 0 || mMoveAbsLength < 0) {
            mMoveLength = 0;
            mMoveAbsLength = 0;
        }

    }

    /**
     * 绘制点
     *
     * @param canvas
     */
    private void drawPoint(Canvas canvas) {

        float pointRadius = 0;
        mPaint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < mLines.size(); i++) {

            MyPoint point = mLines.get(i).points;

            if (point == null) continue;

            float x = (point.line.rect.left + point.line.rect.right) / 2f;

            float y = (point.yCoord.rect.top + point.yCoord.rect.bottom) / 2f;

            if (point.line.duration.toMinutes() < (mStartTime.toMinutes() + mRuntime)) {
                point.canEdit = false;
            } else {
                point.canEdit = true;
            }

            if (point.isChoose) {
                pointRadius = mSelectedPointRadius;
                mPaint.setColor(mSelectedPointColor);

                //绘制选中效果
                canvas.drawLine(mLines.get(0).rect.left, y, x, y, mDottedLinePaint);
                canvas.drawLine(x, point.line.rect.top, x, point.line.rect.bottom, mDottedLinePaint);

            } else {
                pointRadius = mUnSelectedPointRadius;
                mPaint.setColor(mUnSelectedPointColor);
            }

            canvas.drawCircle(x, y, pointRadius, mPaint);

            if (point.isChoose) {
                /*Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.holyline_scroll_unlock_icon);
                if (drawable != null) {
                    drawable.setBounds((int) (x - mSelectedPointRadius * 0.5), (int) (y - mSelectedPointRadius * 2 / 3f), (int) (x + mSelectedPointRadius * 0.5), (int) (y + mSelectedPointRadius * 2 / 3f));
                    drawable.draw(canvas);
                }*/
            }

        }

    }


    /**
     * 时间走过时线上的颜色
     */
    private int mPointLineInvalidateColor = Color.parseColor("#C4C4C4");
    /**
     * 点间线段的颜色
     */
    private int mPointLineColor = Color.parseColor("#0ABD46");

    /**
     * 点间线段的宽度
     * <p>
     * 在onLayout中已动态设置
     */
    private float mPointLineWidth = ViewUtils.dpToPx(getContext(), 4);

    /**
     * 绘制点之间的线
     *
     * @param canvas
     */
    private void drawPointLine(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mPointLineWidth);
        mPaint.setColor(mPointLineColor);
        float lastSX = -1;
        float lastSY = -1;
        MyPoint lastPoint = null;
        long lastTimeMinuteNotGo = mRuntime;
        for (int i = 0; i < mLines.size(); i++) {

            MyPoint point = mLines.get(i).points;
            if (point == null) continue;

            float ex = (point.line.rect.left + point.line.rect.right) / 2f;

            float ey = (point.yCoord.rect.top + point.yCoord.rect.bottom) / 2f;

            if (lastSX == -1 || lastSY == -1) {

            } else {

                long currentRunTimeMinute = (mStartTime.toMinutes() + mRuntime);

                long betweenTimeMinute = point.line.duration.toMinutes() - lastPoint.line.duration.toMinutes();

                if (point.line.duration != null && point.line.duration.toMinutes() < (mStartTime.toMinutes() + mRuntime)) {
                    mPaint.setColor(mPointLineInvalidateColor);
                    lastTimeMinuteNotGo = lastTimeMinuteNotGo - betweenTimeMinute;
                } else {
                    mPaint.setColor(mPointLineColor);
                }

                canvas.drawLine(lastSX, lastSY, ex, ey, mPaint);

                //无效时间没有走完线段的全程
                if (lastTimeMinuteNotGo > 0 && currentRunTimeMinute > lastPoint.line.duration.toMinutes() && currentRunTimeMinute < point.line.duration.toMinutes()) {

                    //走过的百分比
                    float factor = ((float) lastTimeMinuteNotGo) / (point.line.duration.toMinutes() - lastPoint.line.duration.toMinutes());

                    float invalidate_x = lastSX + (ex - lastSX) * factor;

                    float invalidate_y = lastSY + (ey - lastSY) * factor;

                    mPaint.setColor(mPointLineInvalidateColor);

                    canvas.drawLine(lastSX, lastSY, invalidate_x, invalidate_y, mPaint);

                    lastTimeMinuteNotGo = 0;

                }

            }

            lastSX = ex;
            lastSY = ey;

            lastPoint = point;
        }

        //判断最后一个点是否在最后一条线上
        mPaint.setColor(mPointLineColor);

        if (lastPoint == null) return;

        if (lastPoint.line.index == mLines.size() - 1) {//在最后一条线

        } else {//不在最后一条线，则由最后一点延申到最后一条线处

            float sx = (lastPoint.line.rect.left + lastPoint.line.rect.right) / 2f;

            float sy = (lastPoint.yCoord.rect.top + lastPoint.yCoord.rect.bottom) / 2f;

            MyLine lastLine = mLines.get(mLines.size() - 1);

            float ex = (lastLine.rect.left + lastLine.rect.right) / 2f;

            canvas.drawLine(sx, sy, ex, sy, mPaint);

            if (lastTimeMinuteNotGo > 0) {

                //走过的百分比
                float factor = ((float) lastTimeMinuteNotGo) / (lastLine.duration.toMinutes() - lastPoint.line.duration.toMinutes());

                float invalidate_x = lastSX + (ex - sx) * factor;

                float invalidate_y = sy;

                mPaint.setColor(mPointLineInvalidateColor);

                canvas.drawLine(lastSX, lastSY, invalidate_x, invalidate_y, mPaint);


            }

        }

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
            cancelChoosePoint();
            this.setAlpha(0.8f);
        }
//        invalidate();
    }

    public void cancelChoosePoint() {
        MyPoint choosePoint = findChoosePoint();
        if (choosePoint != null) {
            choosePoint.isChoose = false;
            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getPointerCount() > 1) {
            mScaleGesDetector.onTouchEvent(event);
            getParent().requestDisallowInterceptTouchEvent(true);
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
                mMovePointDistanceX = 0;
                mMovePointDistanceY = 0;
                if (isNeedRemovePoint && mCurrentTapDown != null) {//移动后的点占据了原先点的位置，在松手时更新为移动过来的点
                    isNeedRemovePoint = false;
                    if (mCurrentTapDown != mCurrentTapDown.line.points) {

                        if (mCurrentTapDown.line.movePoint != null) {
                            mCurrentTapDown.line.movePoint.line = null;
                            mCurrentTapDown.line.movePoint.yCoord = null;
                        }
                        mCurrentTapDown.line.movePoint = null;

//                        mPoints.remove(mCurrentTapDown.line.points);
                    }
                }
                if (mCurrentTapDown != null) {
                    mCurrentTapDown.line.movePoint = null;
                    mCurrentTapDown.line.points = mCurrentTapDown;
                }
                mCurrentTapDown = null;

                if (mAutoScrollWork != null && !mAutoScrollWork.isDisposed()) {
                    mAutoScrollWork.dispose();
                }

                invalidate();
                break;
            }

        }

        return true;
    }

    private PointClickListener mListener;

    public void setListener(PointClickListener mListener) {
        this.mListener = mListener;
    }

    public interface PointClickListener {

        /**
         * 点的位置改变 或者 点被选择时调用
         *
         * @param point
         */
        void onChoosePoint(MyPoint point);

        void onNothingPointChecked();

        /**
         * 更新手机时间
         *
         * @param duration
         */
        void updateTime(Duration duration);

    }

    public class MyPointClickListener implements PointClickListener {

        @Override
        public void onChoosePoint(MyPoint point) {

        }

        @Override
        public void onNothingPointChecked() {

        }

        @Override
        public void updateTime(Duration duration) {

        }
    }


}

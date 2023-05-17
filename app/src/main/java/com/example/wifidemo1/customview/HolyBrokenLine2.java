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
     * 值为{@link #mHorizontalMoveNormalPerDistance} ，{@link #mHorizontalMoveFastPerDistance} 其中之一
     * </p>
     */
    private float mHorizontalMovePerDistance;

    /**
     * 正常移动时，所需的距离
     */
    private float mHorizontalMoveNormalPerDistance;

    /**
     * 快速移动时，所需的距离
     */
    private float mHorizontalMoveFastPerDistance;

    private GestureDetector mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {

            //点击事件

            if (mCurrentTapDown != null) {
                mCurrentTapDown.isChoose = true;
                //重新确定点的和横坐标
                findAndUpdateSingleTapPosition((int) (mMoveAbsLength + e.getX()), (int) e.getY(), true, false);
            } else {
                MyPoint point = findAndUpdateSingleTapPosition((int) (mMoveAbsLength + e.getX()), (int) e.getY(), true, true);
                if (point != null) {
                    point.isChoose = true;
                    if (mLastCurrentTapDown != null) {
                        mLastCurrentTapDown.isChoose = false;
                    }
                    mLastCurrentTapDown = point;
                }
            }

            invalidate();

            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {

            if (mLastCurrentTapDown != null) mLastCurrentTapDown.isChoose = false;

            mCurrentTapDown = findPoint((int) (mMoveAbsLength + e.getX()), (int) e.getY());
            mLastCurrentTapDown = mCurrentTapDown;
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

                    int orientation = distanceX < 0 ? 1 : -1;//移动方向，1表示向右滑动
                    if (lastOrientation != orientation) {//恢复正常速度
                        mHorizontalMovePerDistance = mHorizontalMoveNormalPerDistance;
                    }
                    lastOrientation = orientation;

                    mMovePointDistanceX += distanceX;

                    int lines = (int) (mMovePointDistanceX / mHorizontalMovePerDistance);
                    System.out.println(lines);
                    mCurrentTapDown.isChoose = true;
                    int newIndex = mCurrentTapDown.line.index - lines;
                    if (lines != 0 && newIndex >= 0 && newIndex < mLines.size()) {
                        mMovePointDistanceX = mMovePointDistanceX % mHorizontalMovePerDistance;
                        if (mCurrentTapDown.line.movePoint != null) {
                            mCurrentTapDown.line.points = mCurrentTapDown.line.movePoint;//恢复移动过的点
                        } else {
                            mCurrentTapDown.line.points = null;
                        }
                        mCurrentTapDown.line = mLines.get(newIndex);//下一条线
                        if (mCurrentTapDown.line.points != null) {
                            mCurrentTapDown.line.movePoint = mCurrentTapDown.line.points;
                            isNeedRemovePoint = true;
                        } else {
                            isNeedRemovePoint = false;
                        }

                        mCurrentTapDown.line.points = mCurrentTapDown;//下一跳线的点置为当前点

//                        sortPoints();
                    }

                    if (mCurrentTapDown.line.rect.right >= (mCanvasStartX + mCanvasWidth + mMoveAbsLength)) {
                        mMoveAbsLength = mCurrentTapDown.line.rect.right - mCanvasWidth - mCanvasStartX;
                        mHorizontalMovePerDistance = mHorizontalMoveFastPerDistance; //点移动到边界时加快移动速度
                    } else if (mCurrentTapDown.line.rect.left <= (mCanvasStartX + mMoveAbsLength)) {
                        mMoveAbsLength = mCurrentTapDown.line.rect.left - mCanvasStartX;
                        mHorizontalMovePerDistance = mHorizontalMoveFastPerDistance;//点移动到边界时加快移动速度
                    }

                    System.out.println(mHorizontalMovePerDistance == mHorizontalMoveNormalPerDistance);

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


    });

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
        return findAndUpdateSingleTapPosition(x, y, false, false);
    }

    /**
     * 寻找点，若线上可以新增点
     *
     * @param x
     * @param y
     * @param reset 若线上有点，是否将此点的竖坐标置为触摸的位置
     * @param add   没有找到是否新增点
     * @return 返回找到的点或者新增的点，如果点击范围不匹配则返回null
     */
    private MyPoint findAndUpdateSingleTapPosition(int x, int y, boolean reset, boolean add) {

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
        if (point != null) {
            if (reset) {
                point.yCoord = foundYCoord;
            }
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


    /**
     * 设置起始时间
     *
     * @param startTime
     */
    public void setStartTime(Duration startTime) {

        mStartTime = startTime;

        if (mStartTime.toMinutes() % 30 == 0) {
            mLinesNum = 48;
        } else {
            mLinesNum = 49;
        }

        mMinLinesMargin = mCanvasWidth / (mLinesNum);

        mLines.clear();

        Duration _startTime;

        _startTime = Duration.of(30 * (mStartTime.toMinutes() / 30), ChronoUnit.MINUTES);

        for (int i = 0; i < mLinesNum; i++) {

            MyLine myLine = new MyLine();
            myLine.index = i;

            if (i == 0) {
                myLine.duration = mStartTime;
            } else {
                myLine.duration = _startTime.plus(30L * i, ChronoUnit.MINUTES);
            }

            mLines.add(myLine);

        }

        invalidate();

    }


    /**
     * 设置点
     *
     * @param points
     */
    public void setPoints(ArrayList<MyPoint> points) {

        if (points != null) {
            mPoints.clear();
            for (int i = 0; i < points.size(); i++) {
                MyPoint _addPoint = points.get(i);
                if (_addPoint.line != null && _addPoint.yCoord != null) {
                    _addPoint.line.points = _addPoint;//画点时是从line中获取的点进行绘制的，而不是从mPoints
                    mPoints.add(_addPoint);
                }
            }
            mPoints = points;
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

        if (mLines.size() != 0) {
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

    private ArrayList<MyPoint> mPoints = new ArrayList<>();

    public class MyPoint {
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
         * 点是否被选择
         */
        public boolean isChoose = false;

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
        Duration duration;

        /**
         * 需要恢复的点
         */
        MyPoint movePoint;

    }

    private Paint mPaint = new Paint();
    private Paint mTextPaint = new TextPaint();

    private int mTextSize = (int) ViewUtils.dpToPx(getContext(), 20);

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

        setStartTime(Duration.of(0, ChronoUnit.MINUTES));

        setPoints(new ArrayList<>());

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

    /**
     * 线的颜色
     */
    private int mLineColor = Color.parseColor("#0FFFFFFF");

    /**
     * 坐标文本的颜色
     */
    private int mCoordsColor = Color.parseColor("#FFFFFFFF");

    /**
     * 未选中点的颜色
     */
    private int mUnSelectedPointColor = Color.parseColor("#FFFFFF00");

    /**
     * 选中点的颜色
     */
    private int mSelectedPointColor = Color.parseColor("#FF00FF00");

    /**
     * 未选中点的半径
     */
    private float mUnSelectedPointRadius = ViewUtils.dpToPx(getContext(), 4);

    /**
     * 选中的点的半径
     */
    private float mSelectedPointRadius = ViewUtils.dpToPx(getContext(), 8);

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

        drawPoint(canvas);

        drawPointLine(canvas);

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

        mHorizontalMovePerDistance = mLinesMargin / 2f;

        mHorizontalMoveNormalPerDistance = mLinesMargin / 2f;

        mHorizontalMoveFastPerDistance = mHorizontalMoveNormalPerDistance / 4;

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

            mYCoords.get(i).index = i;

            Rect y_rect = mYCoords.get(i).rect;
            y_rect.top = (int) (y - textH - textMargin / 2);
/*            if (y_rect.top < mPaddingTop) {
                y_rect.top = (int) mPaddingTop;
            }*/
            y_rect.left = (int) mCanvasStartX;

            y_rect.right = (int) (mCanvasStartX + mCanvasWidth);

            y_rect.bottom = (int) (y + textMargin / 2);


/*            if (y_rect.bottom > getHeight() - mPaddingBottom) {
                y_rect.bottom = (int) (getHeight() - mPaddingBottom);
            }*/

            canvas.drawText(value, x, y, mTextPaint);

        }

        mSelectedPointRadius = textMargin;

        mUnSelectedPointRadius = textMargin;

        mVerticalMovePerDistance = textMargin + mMaxTextHeight;

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

            if (point.isChoose) {
                pointRadius = mSelectedPointRadius;
                mPaint.setColor(mSelectedPointColor);
            } else {
                pointRadius = mUnSelectedPointRadius;
                mPaint.setColor(mUnSelectedPointColor);
            }

            canvas.drawCircle(x, y, pointRadius, mPaint);

        }

    }


    /**
     * 点间线段的颜色
     */
    private int mPointLineColor = Color.parseColor("#9F00FF00");

    /**
     * 点间线段的宽度
     */
    private float mPointLineWidth = ViewUtils.dpToPx(getContext(), 2);

    /**
     * 绘制点之间的线
     *
     * @param canvas
     */
    private void drawPointLine(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mPointLineWidth);
        for (int i = 0; i < mPoints.size(); i++) {

            MyPoint point = mPoints.get(i);

            float sx = (point.line.rect.left + point.line.rect.right) / 2f;

            float sy = (point.yCoord.rect.left + point.yCoord.rect.right) / 2f;

            i++;

            if (i >= mPoints.size()) break;

            point = mPoints.get(i);

            float ex = (point.line.rect.left + point.line.rect.right) / 2f;

            float ey = (point.yCoord.rect.left + point.yCoord.rect.right) / 2f;

            canvas.drawLine(sx, sy, ex, ey, mPaint);

        }

        //判断最后一个点是否在最后一条线上

        if (mPoints.size() > 0) {

            MyPoint lastPoint = mPoints.get(mPoints.size() - 1);

            if (lastPoint.line.index == mLines.size()) {//在最后一条线

            } else {//不在最后一条线，则由最后一点延申到最后一条线处

                float sx = (lastPoint.line.rect.left + lastPoint.line.rect.right) / 2f;

                float sy = (lastPoint.yCoord.rect.left + lastPoint.yCoord.rect.right) / 2f;

                MyLine lastLine = mLines.get(mLines.size() - 1);

                float ex = (lastLine.rect.left + lastLine.rect.right) / 2f;

                canvas.drawLine(sx, sy, ex, sy, mPaint);

            }

        }

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
                mMovePointDistanceX = 0;
                mMovePointDistanceY = 0;
                if (isNeedRemovePoint && mCurrentTapDown != null) {//移动后的点占据了原先点的位置，在松手时更新为移动过来的点
                    isNeedRemovePoint = false;
                    if (mCurrentTapDown != mCurrentTapDown.line.points) {
                        mPoints.remove(mCurrentTapDown.line.points);
                    }
                }
                if (mCurrentTapDown != null) {
                    mCurrentTapDown.line.movePoint = null;
                    mCurrentTapDown.line.points = mCurrentTapDown;
                }
                mCurrentTapDown = null;
                invalidate();
                break;
            }

        }

        return super.onTouchEvent(event);
    }


}

package com.example.wifidemo1.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.ViewGroupUtils;
import androidx.customview.widget.ViewDragHelper;

import com.example.wifidemo1.R;
import com.example.wifidemo1.log.MyLog;
import com.google.android.material.internal.ViewUtils;

/**
 * @author: fuhejian
 * @date: 2023/3/9
 */
public class TrochalDiskBackground extends FrameLayout {

    private ViewDragHelper mViewDragHelper;

    private DragListener mDragListener;

    private ListenerHandle mListenerHandle = new ListenerHandle();


    /**
     * x:初始left坐标
     * y:初始top坐标
     */
    private PointF mCenterPointF = new PointF();

    float thicknessBackGround;
    Paint mPaint = new Paint();

    public TrochalDiskBackground(Context context) {
        this(context, null);
    }

    public TrochalDiskBackground(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TrochalDiskBackground(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
        setWillNotDraw(false);//ViewGroup默认不绘制，重新打开
    }

    @SuppressLint("ResourceType")
    public TrochalDiskBackground(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TrochalDiskBackground, defStyleAttr, defStyleRes);
        thicknessBackGround = a.getDimension(R.styleable.TrochalDiskBackground_thicknessBackGround, 10f);
        a.recycle();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mViewDragHelper = ViewDragHelper.create(this, 2f, new ViewDragHelper.Callback() {
            private boolean canScrollHorizontal = true;
            private boolean updateScrollOrientation = false;

            private int oldDx, oldDy, oldLeft, oldTop;

            private boolean needSchedule = true;

            @Override
            public boolean tryCaptureView(@NonNull View child, int pointerId) {
                return child.getClass() == TrochalDiskView.class;
            }

            public int getPolarisSpeed(int distance,int base){
                return distance * 2000/base;
            }

            @SuppressLint("RestrictedApi")
            @Override
            public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {

/*                if (!canScrollHorizontal && isOrigin(child)) {
                    updateScrollOrientation = false;
                }*/
                //优化滑动
                if (!updateScrollOrientation) {
                    if (Math.abs(oldDy) >= Math.abs(oldDx)) {
                        if (oldDx == 0) {
                            oldDx = dx;
                            oldLeft = left;
                        }
                        if (oldDy == 0) {
                            return left - dx;
                        } else if (Math.abs(oldDy) > Math.abs(oldDx)) {
                            clampViewPositionVertical(child, oldTop, oldDy);
                        } else if (Math.abs(dx) <= mViewDragHelper.getTouchSlop()) {
                            canScrollHorizontal = true;
                            updateScrollOrientation = true;
                        }
                    } else if (Math.abs(dx) >= 0) {
                        canScrollHorizontal = true;
                        updateScrollOrientation = true;
                    }
                }

                if (canScrollHorizontal) {
                    if (mDragListener != null && needSchedule) {
                        needSchedule = false;
                        MyTimer.INSTANCE.schedule(50,new Runnable() {
                            @Override
                            public void run() {
                                float distance = child.getLeft() - mCenterPointF.x;
                                int base = TrochalDiskBackground.this.getWidth()/2 - child.getWidth()/2;
                                mDragListener.onDrag(0,getPolarisSpeed((int)-distance,base));
                            }
                        });
                    }
                    if (left < 0) return 0;
                    else if (left > TrochalDiskBackground.this.getWidth() - child.getWidth()) {
                        return TrochalDiskBackground.this.getWidth() - child.getWidth();
                    } else {
                        return left;
                    }
                } else {
                    return left - dx;
                }
            }

            @Override
            public int clampViewPositionVertical(@NonNull View child, int top, int dy) {

/*                if (canScrollHorizontal && isOrigin(child)) {
                    updateScrollOrientation = false;
                }*/
                //优化滑动
                if (!updateScrollOrientation) {
                    if (Math.abs(oldDx) >= Math.abs(oldDy)) {
                        if (oldDy == 0) {
                            oldTop = top;
                            oldDy = dy;
                        }
                        if (oldDx == 0) {
                            return top - dy;
                        } else if (Math.abs(oldDx) > Math.abs(oldDy)) {
                            clampViewPositionHorizontal(child, oldLeft, oldDx);
                        } else {
                            if (Math.abs(dy) >= 0) {
                                canScrollHorizontal = false;
                                updateScrollOrientation = true;
                            }
                        }
                    } else {
                        if (Math.abs(dy) <= mViewDragHelper.getTouchSlop()) {
                            canScrollHorizontal = false;
                            updateScrollOrientation = true;
                        }
                    }
                }
                if (!canScrollHorizontal) {
                    if (mDragListener != null && needSchedule) {
                        needSchedule = false;
                        MyTimer.INSTANCE.schedule(50,new Runnable() {
                            @Override
                            public void run() {
                                float distance = mCenterPointF.y - child.getTop();
                                int base = TrochalDiskBackground.this.getHeight()/2 - child.getHeight()/2;
                                mDragListener.onDrag(1,getPolarisSpeed((int)-distance,base));
                                MyLog.printLog("当前类:TrochalDiskBackground,信息:" + distance);
                                MyLog.printLog("当前类:TrochalDiskBackground,信息:speed:" + getPolarisSpeed((int)-distance,base));
                            }
                        });
                    }
                    if (top < 0) return 0;
                    else if (top > TrochalDiskBackground.this.getHeight() - child.getHeight()) {
                        return TrochalDiskBackground.this.getHeight() - child.getHeight();
                    } else {
                        return top;
                    }
                } else {
                    return top - dy;
                }
            }

            @Override
            public int getViewHorizontalDragRange(@NonNull View child) {
                if (childInParent(child) && canScrollHorizontal) {
                    return 1;
                } else {
                    return 0;
                }
            }

            @Override
            public int getViewVerticalDragRange(@NonNull View child) {
                if (childInParent(child) && !canScrollHorizontal) {
                    return 1;
                } else {
                    return 0;
                }
            }

            @Override
            public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
                //恢复状态
                updateScrollOrientation = false;
                oldTop = 0;
                oldLeft = 0;
                oldDx = 0;
                oldDy = 0;
                needSchedule = true;
                //停止未完成的任务
                MyTimer.INSTANCE.stopAll();
                //恢复到原点
                mViewDragHelper.smoothSlideViewTo(releasedChild, (int) mCenterPointF.x, (int) mCenterPointF.y);
                //开始恢复动画
                postInvalidate();

            }

            public boolean isOrigin(@NonNull View child) {
                return Math.abs(child.getLeft() - mCenterPointF.x) < mViewDragHelper.getTouchSlop()
                        &&
                        Math.abs(child.getTop() - mCenterPointF.y) < mViewDragHelper.getTouchSlop();
            }

            @SuppressLint("RestrictedApi")
            public boolean childInParent(@NonNull View child) {
                Rect out;
                out = new Rect(0, 0, getWidth(), getHeight());
                return out.contains(child.getLeft(), child.getTop()) && out.contains(child.getBottom(), child.getRight());
            }

        });
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mViewDragHelper.continueSettling(true)) {
            postInvalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mCenterPointF.x == 0) {
            if (getChildCount() != 0) {
                View child = getChildAt(0);
                mCenterPointF.x = child.getLeft();
                mCenterPointF.y = child.getTop();
            }
        }
        LinearGradient linearGradient = new LinearGradient(
                0, getHeight() / 2f, getWidth() / 2f, getHeight() / 2f,
                Color.parseColor("#001C252F"),
                Color.parseColor("#AD1C252F"),
                Shader.TileMode.MIRROR);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setShader(linearGradient);
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(thicknessBackGround);
        canvas.drawLine(0, getHeight() / 2f, getWidth(), getHeight() / 2f, mPaint);
        linearGradient = new LinearGradient(
                getHeight() / 2f, 0, getHeight() / 2f, getHeight() / 2f,
                Color.parseColor("#001C252F"),
                Color.parseColor("#AD1C252F"),
                Shader.TileMode.MIRROR);
        mPaint.setShader(linearGradient);
        canvas.drawLine(getHeight() / 2f, 0, getHeight() / 2f, getHeight(), mPaint);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        //防止viewpager2嵌套滑动
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                requestDisallowInterceptTouchEvent(true);
            }
        }
        mViewDragHelper.processTouchEvent(event);
        if (getChildCount() != 0 && canTouchEvent(event)) {
            View child = getChildAt(0);
            child.onTouchEvent(event);
        }
        return true;//不拦截事件
    }

    @SuppressLint("RestrictedApi")
    private boolean canTouchEvent(MotionEvent event) {
        if (getChildCount() != 0) {
            View child = getChildAt(0);
            Rect rect = ViewUtils.calculateRectFromBounds(child);
            return rect.contains((int) event.getX(), (int) event.getY());
        }
        return false;
    }


    public void setDragListener(DragListener listener) {
        if (listener != null) {
            mDragListener = listener;
        }
    }

    /**
     * 滑杆滑动监听
     */
    public static interface DragListener {
        /**
         * 正在滑动
         *
         * @param orientation 滑动方向 0 水平方向，1 竖直方向
         * @param d           滑动距离 负数反方向，正数正方向
         */
        void onDrag(int orientation, int d);

        /**
         * ACTION_UP事件
         */
        void onRelease();
    }

    private static class ListenerHandle extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            int action = msg.what;
            switch (action) {
                case 0: {
                    ((DragListener) msg.obj).onDrag(msg.arg1, msg.arg2);
                }
                case 1: {
                    ((DragListener) msg.obj).onRelease();
                }
            }
        }
    }

}

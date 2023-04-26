package com.example.wifidemo1.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.customview.widget.ViewDragHelper;

import com.example.wifidemo1.R;
import com.google.android.material.tooltip.TooltipDrawable;

/**
 * @author: fuhejian
 * @date: 2023/4/20
 */
public class MyProgressBar extends ConstraintLayout {
    Paint mBackGroundPaint = new Paint();
    float minValue;
    float maxValue;

    private ScrollListener mScrollListener;

    public MyProgressBar(Context context) {
        this(context, null);
    }

    public MyProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @SuppressLint("RestrictedApi")
    public MyProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        setWillNotDraw(false);

        setClipChildren(false);

        maxTooltipDrawable = TooltipDrawable.createFromAttributes(getContext(), null, 0, R.style.MyToolTip);

        tooltipDrawable = TooltipDrawable.createFromAttributes(getContext(), null, 0, R.style.MyToolTip);

        mViewDragHelper = ViewDragHelper.create(this, new ViewDragHelper.Callback() {

            @Override
            public boolean tryCaptureView(@NonNull View child, int pointerId) {
                return true;
            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {

                if (mScrollListener == null || dx == 0) return;
                int childWidth = getChildAt(0).getWidth() + getChildAt(1).getWidth();
                if (changedView.equals(getChildAt(0))) {
                    minValue = (changedView.getRight() - (float) getChildAt(0).getWidth()) / (getWidth() - childWidth);
                    mScrollListener.onMinPositionChanged(minValue);
                    tooltipDrawable.setBounds(changedView.getLeft(), changedView.getTop() - tooltipDrawable.getIntrinsicHeight(), changedView.getLeft() + tooltipDrawable.getIntrinsicWidth(), changedView.getTop());
                    MyProgressBar.this.invalidate();
                } else {
                    maxValue = (left - (float) getChildAt(1).getWidth()) / (getWidth() - childWidth);
                    mScrollListener.onMaxPositionChanged(maxValue);
                    maxTooltipDrawable.setBounds(changedView.getLeft(), changedView.getTop() - maxTooltipDrawable.getIntrinsicHeight(), changedView.getLeft() + maxTooltipDrawable.getIntrinsicWidth(), changedView.getTop());
                    MyProgressBar.this.invalidate();
                }

            }

            @Override
            public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
                if (getChildAt(1).getLeft() >= (getChildAt(0).getRight())) {

                    //处理滑过的问题
                    if (getChildAt(0).equals(child)) {
                        if (child.getRight() + dx >= getChildAt(1).getLeft() && child.getRight() <= getChildAt(1).getLeft()) {
                            return child.getLeft() + getChildAt(1).getLeft() - child.getRight();
                        }
                    } else {
                        if (child.getLeft() + dx <= getChildAt(0).getRight() && child.getLeft() >= getChildAt(0).getRight()) {
                            return left - (left - getChildAt(0).getRight());
                        }
                    }

                    //处理边界
                    if (left < 0) {
                        return 0;
                    } else if (left > (getWidth() - child.getWidth())) {
                        return getWidth() - child.getWidth();
                    }

                    return left;
                }
                return left - dx;
            }

            @Override
            public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
                return top - dy;
            }

            @Override
            public int getViewHorizontalDragRange(@NonNull View child) {
                return getWidth();
            }
        });

    }

    ViewDragHelper mViewDragHelper;

    public void setScrollListener(ScrollListener mScrollListener) {
        this.mScrollListener = mScrollListener;
    }

    TooltipDrawable tooltipDrawable;
    TooltipDrawable maxTooltipDrawable;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        tooltipDrawable.setRelativeToView(getChildAt(0));
        View _child = getChildAt(0);
        tooltipDrawable.setBounds(_child.getLeft(), _child.getTop() - tooltipDrawable.getIntrinsicHeight(), _child.getLeft() + tooltipDrawable.getIntrinsicWidth(), _child.getTop());

        View _child2 = getChildAt(1);
        maxTooltipDrawable.setRelativeToView(_child2);
        maxTooltipDrawable.setBounds(1000, _child2.getTop() - maxTooltipDrawable.getIntrinsicHeight(), 1000 + maxTooltipDrawable.getIntrinsicWidth(), _child2.getTop());
//        maxTooltipDrawable.setBounds(_child2.getLeft(), _child2.getTop() - maxTooltipDrawable.getIntrinsicHeight(), _child2.getLeft() + maxTooltipDrawable.getIntrinsicWidth(), _child2.getTop());
    }

    public TooltipDrawable getMinTooltip() {
        return tooltipDrawable;
    }

    public TooltipDrawable getMaxTooltip() {
        return maxTooltipDrawable;
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int left = getChildAt(0).getRight();
        int right = getChildAt(1).getLeft();
        mBackGroundPaint.setColor(Color.parseColor("#FF00FF00"));
        mBackGroundPaint.setAntiAlias(true);
        mBackGroundPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(left, 0, right, getHeight(), mBackGroundPaint);
        mBackGroundPaint.reset();
        if (mScrollListener != null) {
            mScrollListener.onDrawMinValue(canvas, minValue);
            mScrollListener.onDrawMaxValue(canvas, maxValue);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        requestDisallowInterceptTouchEvent(true);
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    public static interface ScrollListener {

        void onMinPositionChanged(float value);

        void onMaxPositionChanged(float value);

        void onDrawMinValue(Canvas canvas, float value);

        void onDrawMaxValue(Canvas canvas, float value);

    }

}

package com.example.wifidemo1.customview;

import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewConfigurationCompat;

import com.google.android.material.internal.ViewUtils;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * com.example.wifidemo1.customview
 */
@SuppressLint("RestrictedApi")
public class RecordButton extends View {
    public RecordButton(Context context) {
        this(context, null);
    }

    public RecordButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public RecordButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    private int mInnerRadius = (int) ViewUtils.dpToPx(getContext(), 27);

    private final int mInnerDefaultRadius = mInnerRadius;

    private int mRecordPauseRadius = (int) ViewUtils.dpToPx(getContext(), 15);
    private int mRecordPauseRadius_Animation = mInnerRadius;
    private int mRecordPauseCornerRadius = (int) ViewUtils.dpToPx(getContext(), 5);
    private int mRecordPauseCornerRadius_Animation = mInnerRadius;
    private int mInnerRadiusMinus = (int) ViewUtils.dpToPx(getContext(), 5);
    private int mExternalRadius = (int) ViewUtils.dpToPx(getContext(), 33);
    private int mTrackBolderWidth = (int) ViewUtils.dpToPx(getContext(), 4);

    //拍照模式样式
    private int mCountingCircularWidth = (int) ViewUtils.dpToPx(getContext(), 3);
    private int mTrackHeight = (int) ViewUtils.dpToPx(getContext(), 150);
    private int mInnerColor = Color.parseColor("#FFFFFFFF");
    private int mExternalColor = Color.parseColor("#CC0000FF");

    private int mCountingCircularColor = Color.parseColor("#CCFF0000");

    private int mTrackColor = Color.parseColor("#CC00FF00");
    private int mTrackBolderColor = Color.parseColor("#CCFFFFFF");

    //录像模式样式
    private int mInnerColor_Video = Color.parseColor("#CCFF0000");
    private int mBolderBaseColor_Video = Color.parseColor("#55FFFFFF");
    private int mBolderColor_Video = Color.parseColor("#CCFFFFFF");
    private int mExternalColor_Video = Color.parseColor("#DD0000FF");

    private int mCountingCircularColor_Video = Color.parseColor("#CCFF0000");

    private int mTextSize = (int) ViewUtils.dpToPx(getContext(), 14);

    private int mTextColor = Color.parseColor("#CCFFFFFF");

    private boolean mIsScrolling = false;

    private Paint mPaint;

    private int mScaledTouchSlop;

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        setPadding(mTrackBolderWidth, mTrackBolderWidth, mTrackBolderWidth, mTrackBolderWidth);
        mScaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        setMode(MODE_VIDEO);

        mPauseScaleSets.setDuration(500);
        mPauseScaleSets.playTogether(mPauseScaleRadiusAnimator, mPauseScaleCornerRadiusAnimator);

        mPauseScaleRadiusAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                Integer animatedValue = (Integer) animation.getAnimatedValue();
                mRecordPauseRadius_Animation = animatedValue;
                System.out.println("半径：" + animatedValue);
                invalidate();
            }
        });

        mPauseScaleCornerRadiusAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                Integer animatedValue = (Integer) animation.getAnimatedValue();
                mRecordPauseCornerRadius_Animation = animatedValue;
                System.out.println("圆角：" + animatedValue);
                invalidate();
            }
        });

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(mTextSize);


        setCountingDownDuration(Duration.of(10, ChronoUnit.SECONDS));

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = 0;
        int height = 0;
        int horizonPadding = getPaddingLeft() + getPaddingRight();
        int verticalPadding = getPaddingTop() + getPaddingBottom();

        width = horizonPadding + mExternalRadius * 2 + 2 * mTrackBolderWidth;
        if (mIsScrolling) {
            height = verticalPadding + mTrackHeight + 2 * mTrackBolderWidth;
        } else {
            height = verticalPadding + mExternalRadius * 2 + 2 * mTrackBolderWidth;
        }

        setMeasuredDimension(width, height);

    }

    private float mMoveLength = 0;
    private int mHeight = 0;
    private int mWidth = 0;
    private int mStartX = 0;

    private int mEndX = 0;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (mMode) {
            case 0: {
                if (mIsScrolling) {
                    drawTrack(canvas);
                }
                drawSlider(canvas);
                break;
            }
            case 2:
            case 1: {
                drawVideoButton(canvas);
                break;
            }
        }

    }

    private void drawSlider(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        float x = mStartX + mExternalRadius;
        float y = mHeight - mExternalRadius - getPaddingBottom() + mMoveLength;
        if (!mIsScrolling) {
            mPaint.setColor(mExternalColor);
            canvas.drawCircle(x, y, mExternalRadius, mPaint);
            mPaint.setColor(mInnerColor);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(mTrackBolderWidth);
            canvas.drawCircle(x, y, mExternalRadius, mPaint);
        }
        mPaint.setColor(mInnerColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(x, y, mInnerRadius, mPaint);
    }

    private Path mCountingDownPath = new Path();
    private Path mCountingDownPath_segment = new Path();
    PathMeasure mCountingDownPathMeasure = new PathMeasure();

    private boolean mIsRecord = false;
    private boolean mIsCountingDown = false;

    private Duration mCountingDownDuration = Duration.of(0, ChronoUnit.SECONDS);

    public void setCountingDownDuration(Duration countingDownDuration) {
        if (countingDownDuration != null) {
            this.mCountingDownDuration = countingDownDuration;
            mCountingDownSeconds = mCountingDownDuration.getSeconds();
            mIsCountingDown = true;
        }
    }

    private Rect mTextBounds = new Rect();

    private void drawVideoButton(Canvas canvas) {

        mPaint.setStyle(Paint.Style.FILL);
        float x = mStartX + mExternalRadius;
        float y = mHeight - mExternalRadius - getPaddingBottom();
        mPaint.setColor(mExternalColor_Video);
        canvas.drawCircle(x, y, mExternalRadius, mPaint);

        mPaint.setColor(mInnerColor_Video);
        canvas.drawRoundRect(x - mRecordPauseRadius_Animation, y - mRecordPauseRadius_Animation, x + mRecordPauseRadius_Animation, y + mRecordPauseRadius_Animation, mRecordPauseCornerRadius_Animation, mRecordPauseCornerRadius_Animation, mPaint);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mTrackBolderWidth);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        if (!mIsCountingDown) {
            mPaint.setColor(mBolderColor_Video);
            canvas.drawPath(mCountingDownPath, mPaint);
        } else {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(mBolderBaseColor_Video);
            canvas.drawPath(mCountingDownPath, mPaint);
            mPaint.setColor(mBolderColor_Video);
            mCountingDownPathMeasure.getSegment(0, mCountingDownPathMeasure.getLength() * getTimeOffset(), mCountingDownPath_segment, true);
            canvas.drawPath(mCountingDownPath_segment, mPaint);

            String time = getCurrentDuration();
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setTextSize(mTextSize);
            mPaint.setColor(mTextColor);
            mPaint.getTextBounds(time, 0, time.length(), mTextBounds);

            canvas.drawText(time, x - mTextBounds.width() / 2f, y + mTextBounds.height() / 2f, mPaint);
        }

    }

    private long mCountingDownSeconds;

    private String getCurrentDuration() {
        return String.format("%02d", mCountingDownDuration.toMinutes()) + ":" + String.format("%02d", mCountingDownDuration.getSeconds() % 60);
    }

    private Scheduler.Worker mCountingDownWorker;

    private void startCountDown() {
        if (mCountingDownWorker == null) {
            mCountingDownWorker = Schedulers.io().createWorker();

            mCountingDownWorker.schedulePeriodically(new Runnable() {
                @Override
                public void run() {
                    mCountingDownDuration = mCountingDownDuration.minus(1, ChronoUnit.SECONDS);
                    if (mCountingDownDuration.getSeconds() <= 0) {
                        mIsCountingDown = false;
                        mCountingDownWorker.dispose();
                    }
                    invalidate();
                }
            }, 0, 1, TimeUnit.SECONDS);

        }

        if (mCountingDownWorker.isDisposed()) {
            mCountingDownWorker.schedulePeriodically(new Runnable() {
                @Override
                public void run() {
                    mCountingDownDuration = mCountingDownDuration.minus(1, ChronoUnit.SECONDS);
                    if (mCountingDownDuration.getSeconds() < 0) {
                        mIsCountingDown = false;
                        mCountingDownWorker.dispose();
                        invalidate();
                    } else {
                        invalidate();
                    }
                }
            }, 1, 1, TimeUnit.SECONDS);
        }

    }

    public float getTimeOffset() {
        return (mCountingDownSeconds - mCountingDownDuration.getSeconds()) / (mCountingDownSeconds * 1f);
    }

    private Path mPath = new Path();

    private void drawTrack(Canvas canvas) {
        mPath.rewind();
        float sX = mStartX;
        float sY = mHeight / 2;
        float arcRadius = (mExternalRadius);
        mPath.moveTo(sX, sY);
        mPath.lineTo(mStartX, mHeight - getPaddingBottom() - arcRadius);
        float arcTop_1 = mHeight - getPaddingBottom() - arcRadius * 2;
        float arcBottom_1 = mHeight - getPaddingBottom();
        mPath.arcTo(mStartX, arcTop_1, mEndX, arcBottom_1, -180, -180, false);
        mPath.lineTo(mEndX, getPaddingTop() + arcRadius);
        mPath.arcTo(mStartX, getPaddingTop(), mEndX, getPaddingTop() + arcRadius * 2, 0, -180, false);
        mPath.lineTo(sX, sY);

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mExternalColor);
        canvas.drawPath(mPath, mPaint);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mInnerColor);
        mPaint.setStrokeWidth(mTrackBolderWidth);
        canvas.drawPath(mPath, mPaint);

    }


    private float mSliderStartY;
    private float mSliderEndY;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = getHeight();
        mWidth = getWidth();
        mStartX = getPaddingLeft();
        mEndX = mStartX + 2 * mExternalRadius;
        mSliderStartY = mHeight - mExternalRadius - getPaddingBottom();
        mSliderEndY = getPaddingTop() + mExternalRadius;

        float x = mStartX + mExternalRadius;
        float y = mHeight - mExternalRadius - getPaddingBottom();
        mCountingDownPath.moveTo(x, y - mExternalRadius);
        mCountingDownPath.arcTo(x - mExternalRadius, y - mExternalRadius, x + mExternalRadius, y + mExternalRadius, 270, 359, false);
//        mCountingDownPath.addCircle(x, y, mExternalRadius, Path.Direction.CCW);
        mCountingDownPath.close();
        mCountingDownPathMeasure.setPath(mCountingDownPath, false);

    }

    private boolean mIsCompleteScroll = false;

    private float mTouchY;

    private AnimatorSet mPauseScaleSets = new AnimatorSet();
    private ValueAnimator mPauseScaleRadiusAnimator = new ValueAnimator();
    private ValueAnimator mPauseScaleCornerRadiusAnimator = new ValueAnimator();
    private GestureDetector mGestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapUp(@NonNull MotionEvent e) {

            setIsRecord(!mIsRecord);

            startCountDown();

            return true;
        }
    });

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (mMode) {
            case 0: {
                switch (event.getActionMasked()) {

                    case MotionEvent.ACTION_DOWN: {
                        mInnerRadius = mInnerRadius - mInnerRadiusMinus;
                        mIsCompleteScroll = false;
                        mTouchY = event.getY();
                        invalidate();
                        break;
                    }

                    case MotionEvent.ACTION_MOVE: {
                        float dy = event.getY() - mTouchY;
                        mTouchY = event.getX();
                        if (Math.abs(dy) < mScaledTouchSlop && !mIsScrolling) {
                            return true;
                        } else {
                            mInnerRadius = mInnerDefaultRadius;
                            if (!mIsCompleteScroll) {
                                float d = event.getY() - mHeight + (mExternalRadius + mTrackBolderWidth);
                                if (d > 0) {
                                    d = 0;
                                }
                                mMoveLength = d;
                                if (d < mSliderEndY - mSliderStartY && mIsScrolling) {
                                    setCompleteScroll();
                                } else {
                                    setIsScrolling(true);
                                    invalidate();
                                }
                            }
                        }

                        break;
                    }

                    case MotionEvent.ACTION_UP: {
                        if (!mIsCompleteScroll) {
                            mMoveLength = 0;
                            setIsScrolling(false);
                            mInnerRadius = mInnerDefaultRadius;
                            invalidate();
                        }
                        break;
                    }
                }
                break;
            }
            case 2:
            case 1: {
                mGestureDetector.onTouchEvent(event);
                break;
            }
            default: {

            }
        }

        return true;
    }

    private void setIsScrolling(boolean scrolling) {
        if (mIsScrolling != scrolling) {
            mIsScrolling = scrolling;
            requestLayout();
        }
    }

    private void setIsRecord(boolean isRecord) {
        if (!mIsCountingDown) {
            mIsRecord = isRecord;
            if (isRecord) {
                mPauseScaleRadiusAnimator.setIntValues(mInnerRadius, mRecordPauseRadius);
                mPauseScaleCornerRadiusAnimator.setIntValues(mInnerRadius, mRecordPauseCornerRadius);
            } else {
                mPauseScaleRadiusAnimator.setIntValues(mRecordPauseRadius, mInnerRadius);
                mPauseScaleCornerRadiusAnimator.setIntValues(mRecordPauseCornerRadius, mInnerRadius);
            }
            mPauseScaleSets.start();
        }

        invalidate();
    }

    private void setCompleteScroll() {
        mIsCompleteScroll = true;
        setIsScrolling(false);
        mMoveLength = 0;
        mInnerRadius = mInnerDefaultRadius;
        if (mListener != null) {
            mListener.completeRecord();
        }
    }

    private RecordListener mListener;

    public void setListener(RecordListener listener) {
        this.mListener = listener;
    }

    public static interface RecordListener {

        void completeRecord();

    }

    public static int MODE_PHOTO = 0;
    public static int MODE_VIDEO = 1;

    public static int MODE_COUNTING_DOWN = 2;

    private int mMode = MODE_PHOTO;

    public void setMode(int mode) {
        mMode = mode;
    }

}

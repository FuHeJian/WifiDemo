package com.example.wifidemo1.customview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;

import androidx.annotation.Nullable;

import com.example.wifidemo1.log.MyLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * 可以滑动且可相互限制的滑块
 *
 * @author: fuhejian
 * @date: 2023/5/5
 */
public class BasePickVerticalScrollBar extends View {


    public BasePickVerticalScrollBar(Context context) {
        this(context, null);
    }

    public BasePickVerticalScrollBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BasePickVerticalScrollBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public BasePickVerticalScrollBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    /**
     * 刻度线
     */
    public class TickMark {
        /**
         * 中点
         */
        public float x = 0;
        /**
         * 将rawValue解析后的值
         */
        public float value = 0;

        /**
         * 在tickMarks中的位置
         */
        public int index = 0;
        /**
         * 原始值
         */
        public String rawValue;
        /**
         * 当前刻度的位置
         */
        public Rect rect = new Rect();
        /**
         * 当前刻度下的slider，由于slider可以表示相同值，所以可能不止一个
         */
        public ArrayList<Slider> currentSliders = new ArrayList<>();

    }

    private ArrayList<TickMark> tickMarks = new ArrayList<>();

    /**
     * 滑块
     */
    public class Slider {
        boolean notInit = true;
        /**
         * 表示在View中的位置
         */
        public Rect rect;

        /**
         * 相当于tickMark.index
         */
        public int index;
        /**
         * 当前的值
         */
        public TickMark tickMark;

        /**
         * 在sliders中的位置
         */
        public int indexInSliders;

        /**
         * 通过oldTickMark.index判断是从那边移到当前tickMark的
         */
        public TickMark oldTickMark;

        public float offsetX = 0;//相对于刻度线位置的x偏移
    }

    public static int ORIENTATION_HORIZONTAL = 0;

    public static int ORIENTATION_VERTICAL = 1;

    /**
     * 滑动方向
     */
    public int mOrientation = ORIENTATION_HORIZONTAL;

    private ArrayList<Slider> sliders = new ArrayList<>();

    private float lineMargin = 0;

    private float mPaddingTop;
    private float mPaddingBottom;
    private float mVisibleWidth;
    private float mVisibleHeight;

    private float midY;

    /**
     * 停止平滑移动，立即移动到目标位置
     */
    Runnable mFinishSmoothToPosition;

    /**
     * 平滑移动的值动画
     */
    private ValueAnimator mSmoothToTickMarkAnimator = new ValueAnimator();

    public void init() {
        //初始化slider
        for (int i = 0; i < 4; i++) {
            Slider slider = new Slider();
            slider.indexInSliders = i;
            sliders.add(slider);
        }

        mPaint.setAntiAlias(true);

        mPaddingTop = getPaddingTop();

        mPaddingBottom = getPaddingBottom();

        mSmoothToTickMarkAnimator.setDuration(200);

        mSmoothToTickMarkAnimator.setRepeatCount(0);

        mSmoothToTickMarkAnimator.setFloatValues(0f, 1f);

        mSmoothToTickMarkAnimator.setInterpolator(new BounceInterpolator());

        mFinishSmoothToPosition = new Runnable() {
            @Override
            public void run() {
                if (mAnimateFromSliderIndex != null && mAnimateToPosition != null) {
                    mSmoothToTickMarkAnimator.cancel();
                    setSliderValue(mAnimateFromSliderIndex, mAnimateToPosition);
                    mAnimateFromSliderIndex = null;
                    mAnimateToPosition = null;
                }
            }
        };

        mSmoothToTickMarkAnimator.addUpdateListener(animation -> {

            if (mAnimateFromSliderIndex != null && mAnimateToPosition != null) {

                float value = ((Float) animation.getAnimatedValue());

                if (tickMarks.get(mAnimateToPosition).currentSliders.size() != 0) {
                    mSmoothToTickMarkAnimator.cancel();
                    value = 1f;
                } else {
                    float length = tickMarks.get(mAnimateToPosition).rect.left - sliders.get(mAnimateFromSliderIndex).rect.right;
                    sliders.get(mAnimateFromSliderIndex).rect.offset((int) (length * value), 0);
                }

                if (value >= 1f) {
                    mFinishSmoothToPosition.run();
                }
                invalidate();
            }

        });

        ArrayList<String> strs = new ArrayList<>();
        ArrayList<Float> values = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            strs.add(String.valueOf(i));
            values.add((float) i);
        }

        OnLayoutChangeListener onLayoutChangeListener = new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                setDataList(strs, values);
                for (int i = 0; i < 4; i++) {
                    setSliderValue(i, 2 * i);
                }
                removeOnLayoutChangeListener(this);
            }
        };

        addOnLayoutChangeListener(onLayoutChangeListener);


    }

    /**
     * 平滑回弹到的位置
     */
    private Integer mAnimateToPosition;

    /**
     * 需要平滑回弹的Slider的下标
     */
    private Integer mAnimateFromSliderIndex;

    private int mSliderNum = 2;

    /**
     * 设置滑块的数量
     *
     * @param num
     */
    public void setSliderNum(int num) {
        if (num < 0) return;
        mSliderNum = num;

        sliders.clear();
        //初始化slider
        for (int i = 0; i < mSliderNum; i++) {
            Slider slider = new Slider();
            slider.indexInSliders = i;
            sliders.add(slider);
        }
        invalidate();
    }

    public boolean isLayout = false;

    private float mStartX;

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        mVisibleHeight = getHeight() - mPaddingTop - mPaddingBottom;

        mVisibleWidth = getWidth();

        mStartX = 0;

        midY = mVisibleHeight / 2 + mPaddingTop;

        isLayout = true;

    }

    private void setSliderValue(int sliderIndex, int tickIndex) {

        if (tickIndex == -1 || sliderIndex >= sliders.size() || sliderIndex < 0 || tickIndex >= tickMarks.size())
            return;

        try {
            //更新位置
            setSliderValue(sliderIndex, tickMarks.get(tickIndex));
        } catch (Exception e) {
            MyLog.printError("当前类:BasePickVerticalScrollBar,当前方法：setSliderValue,当前线程:" + Thread.currentThread().getName() + ",信息:" + e);
        }

    }

    private void setSliderValue(int sliderIndex, TickMark tickMark) {

        if (tickMark == null || sliderIndex >= sliders.size() || sliderIndex < 0 || tickMark.currentSliders.contains(sliders.get(sliderIndex)))
            return;

        //当前位置已经有值则偏移一段距离
//      if (sliders.get(sliderIndex).tickMark != tickMark) {}
        TickMark oldTickMark = sliders.get(sliderIndex).tickMark;
        minusSlider(oldTickMark, sliders.get(sliderIndex));
        //更新新位置
        plusSlider(tickMark, sliders.get(sliderIndex));
        sliders.get(sliderIndex).tickMark = tickMark;
        sliders.get(sliderIndex).index = tickMark.index;
        sliders.get(sliderIndex).notInit = false;

        if (mListener != null) {
            mListener.onValueSelect(sliders.get(sliderIndex));
        }
        if (sliderIndex == sliders.size() - 1 && !hasReset) {
            clearCacheAndResetPosition();
        }

        invalidate();
    }

    private boolean hasReset = false;

    public void setResetPoint(boolean reset) {
        hasReset = reset;
    }

    /**
     * 重新按当前值放置Slider，不会改变Slider的值
     *
     * <p>
     * 作用：当外部通过{@link #setDataList(ArrayList, ArrayList)}设置值时，由于存在默认值和默认位置，
     * 在从左到右通过{@link #plusSlider(TickMark, Slider)}设置滑块值时，由于存在旧数据的位置限制，导致先放置的slider的位置不正确。
     * 所以哦通过这个函数清除一下旧的限制，但不改变slider的值
     *
     * </p>
     */
    private void clearCacheAndResetPosition() {

        if (hasReset) return;
        hasReset = true;

        for (int i = 0; i < tickMarks.size(); i++) {

            TickMark tickMark = tickMarks.get(i);

            tickMark.x = 0.5f * (int) mVisibleHeight + i * lineMargin;
            Rect rect = tickMark.rect;
            rect.left = (int) (tickMark.x + 0.5f * (int) mVisibleHeight);//就是加号
            rect.right = (int) (tickMark.x - 0.5f * (int) mVisibleHeight);//就是减号
            rect.top = (int) mPaddingTop;
            rect.bottom = (int) (getHeight() - mPaddingBottom);

            tickMark.currentSliders.clear();

        }

        for (int i = 0; i < sliders.size(); i++) {

            Slider slider = sliders.get(i);

            slider.rect = null;

        }

        for (int i = 0; i < sliders.size(); i++) {
            Slider slider = sliders.get(i);
            setSliderValue(slider.indexInSliders, slider.tickMark.index);
        }

    }

    /**
     * tickMark - slider;
     */
    private void minusSlider(TickMark tickMark, Slider slider) {

        if (tickMark == null || slider == null) return;

        tickMark.currentSliders.remove(slider);
        Rect tickMarkRect = tickMark.rect;

        if (tickMark.rect == null) return;

        if (tickMark.currentSliders.size() == 0) {
            tickMarkRect.left = (int) (tickMark.x + mVisibleHeight / 2);
            tickMarkRect.right = (int) (tickMark.x - mVisibleHeight / 2);
        } else {
            for (int i = 0; i < tickMark.currentSliders.size(); i++) {
                Slider slider1 = tickMark.currentSliders.get(i);
                Rect sliderRect = slider1.rect;

                if (i == 0) {
                    tickMarkRect.right = sliderRect.right;
                    tickMarkRect.left = sliderRect.left;
                }

                tickMarkRect.right = Math.max(sliderRect.right, tickMarkRect.right);

                tickMarkRect.left = Math.min(sliderRect.left, tickMarkRect.left);

            }
        }

    }

    /**
     * rect1 - rect2;
     */
    private void plusSlider(TickMark tickMark, Slider slider) {

        if (tickMark == null || slider == null) return;
        Rect tickMarkRect = tickMark.rect;

        if (tickMarkRect == null) return;
        if (slider.rect == null) {
            //初始化 slider.rect
            Rect rect = new Rect();
            rect.top = (int) mPaddingTop;
            rect.bottom = (int) (getHeight() - mPaddingBottom);
            slider.rect = rect;
            slider.notInit = true;
        }

        tickMark.currentSliders.add(slider);

        if (tickMark.currentSliders.size() == 1) {

            slider.rect.left = tickMarkRect.right;
            slider.rect.right = tickMarkRect.left;

            tickMarkRect.right = slider.rect.right;

            tickMarkRect.left = slider.rect.left;

        } else {

            for (int i = tickMark.currentSliders.size() - 1; i < tickMark.currentSliders.size(); i++) {

                slider = tickMark.currentSliders.get(i);

                Rect sliderRect = slider.rect;
                if (slider.oldTickMark == null || slider.oldTickMark.index > tickMark.index || slider.notInit) {//右边

                    sliderRect.left = tickMarkRect.right;
                    sliderRect.right = tickMarkRect.right + (int) mVisibleHeight;

/*                    if (sliderRect.right > getWidth()) {//超出范围改为左边
                        sliderRect.left = tickMarkRect.left - (int) mVisibleHeight;
                        sliderRect.right = tickMarkRect.left;
                    }*/

                } else {//左边

                    sliderRect.left = tickMarkRect.left - (int) mVisibleHeight;
                    sliderRect.right = tickMarkRect.left;

/*                    if (sliderRect.left < 0) {//超出范围改为右边
                        sliderRect.left = tickMarkRect.right;
                        sliderRect.right = tickMarkRect.right + (int) mVisibleHeight;
                    }*/

                }

                tickMarkRect.right = Math.max(sliderRect.right, tickMarkRect.right);

                tickMarkRect.left = Math.min(sliderRect.left, tickMarkRect.left);

            }

        }

        restrictSlider(slider);

    }

    /**
     * 限制滑块间不能覆盖
     */
    private void restrictSlider(Slider slider) {

        if (slider.indexInSliders < sliders.size() - 1) {//如果后面有滑块
            Slider foreSlider = sliders.get(slider.indexInSliders + 1);
            if (foreSlider.rect != null && slider.rect.right >= foreSlider.rect.left) {
                slider.rect.right = foreSlider.rect.left;
                slider.rect.left = slider.rect.right - (int) mVisibleHeight;
            }
        }

        if (slider.indexInSliders > 0) {//如果前面有滑块
            Slider backSlider = sliders.get(slider.indexInSliders - 1);
            if (backSlider.rect != null && slider.rect.left <= backSlider.rect.right) {
                slider.rect.left = backSlider.rect.right;
                slider.rect.right = slider.rect.left + (int) mVisibleHeight;
            }
        }

        //越界处理
        Slider referenceSlider = slider;
        if (referenceSlider.indexInSliders == sliders.size() - 1 && referenceSlider.rect.right >= mVisibleWidth) {
            //超过了右边界，移动slider
            int offset = (int) (referenceSlider.rect.right - mVisibleWidth);
            referenceSlider.rect.offset(-offset, 0);
            for (int i = sliders.size() - 2; i >= 0; i--) {//处理移动后slider覆盖导致的冲突
                Slider moveSlider = sliders.get(i);
                if (moveSlider.rect != null) {
                    if (moveSlider.rect.right > referenceSlider.rect.left) {
                        int moveL = (int) (moveSlider.rect.right - referenceSlider.rect.right + mVisibleHeight);
                        moveSlider.rect.offset(-moveL, 0);
                    }
                }
                referenceSlider = moveSlider;
            }
        }

        referenceSlider = slider;
        if (referenceSlider.indexInSliders == 0 && referenceSlider.rect.left <= mStartX) {

            //超过了右边界，后面的Slider都向右平移
            int offset = (int) (referenceSlider.rect.left - mStartX);
            referenceSlider.rect.offset(-offset, 0);
            for (int i = 1; i < sliders.size(); i++) {//处理移动后slider覆盖导致的冲突
                Slider moveSlider = sliders.get(i);
                if (moveSlider.rect != null) {
                    if (moveSlider.rect.left < referenceSlider.rect.right) {
                        int moveL = (int) (moveSlider.rect.left - referenceSlider.rect.left - mVisibleHeight);
                        moveSlider.rect.offset(-moveL, 0);
                    }
                }
                referenceSlider = moveSlider;
            }

        }


    }

    public int findTickIndex(float value) {

        for (int i = 0; i < tickMarks.size(); i++) {
            if (tickMarks.get(i).value == value) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @param index slider的下标
     * @return
     */
    public String getRawValue(int index) {
        if (index < 0 || index >= sliders.size()) return "";
        return sliders.get(index).tickMark.rawValue;
    }

    /**
     * @param value 需要寻找的value值
     * @return
     */
    public String getRawValueForValue(float value) {

        if (dataList != null) {
            for (int i = 0; i < dataList.size(); i++) {
                if (dataList.get(i).value == value) {
                    return dataList.get(i).raw;
                } else if (dataList.get(i).value > value) {
                    return dataList.get(i).raw;
                }
            }
        }

        return "0";
    }

    private Paint mPaint = new Paint();

    private int color1 = Color.parseColor("#308C4F");
    private int color2 = Color.parseColor("#30798C");

    private int[] mColors = null;

    /**
     * 设置两点间的颜色值
     *
     * @param colors 颜色序列
     */
    public void setColorList(int... colors) {

        mColors = colors;

        postInvalidate();

    }

    /**
     * 轨道的颜色
     */
    private int mTrackColor = Color.parseColor("#1C252F");

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        lineMargin = dataList.size() == 1 ? 0 : (getWidth() - (int) mVisibleHeight) / (dataList.size() - 1f);

        //绘制slider之间的颜色
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mTrackColor);
        canvas.drawRoundRect(mStartX, mPaddingTop, mVisibleWidth, getHeight() - mPaddingBottom, 8, 8, mPaint);

        mPaint.setStyle(Paint.Style.STROKE);
        for (int i = 0, j = sliders.size() - 1; i < j; i++, j--) {

            if (mColors != null && i < mColors.length) {
                mPaint.setColor(mColors[i]);
            } else {
                mPaint.setColor(color1);
            }

            if (sliders.get(i).rect != null) {
                canvas.drawLine(sliders.get(i).rect.left + mVisibleHeight / 2, midY, sliders.get(j).rect.left + mVisibleHeight / 2, midY, mPaint);
            }

        }

        if (sliders.size() == 1) {
            if (mColors != null && mColors.length >= 1) {
                mPaint.setColor(mColors[0]);
            } else {
                mPaint.setColor(color1);
            }

            if (sliders.get(0).rect != null) {
                canvas.drawLine(mStartX, midY, sliders.get(0).rect.left + mVisibleHeight / 2, midY, mPaint);
            }
        }

        //绘制slider
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);
        for (int i = 0; i < sliders.size(); i++) {
            Slider slider = sliders.get(i);
            if (slider.tickMark != null) {
                canvas.drawRoundRect(slider.rect.left, slider.rect.top, slider.rect.right, slider.rect.bottom, 15, 15, mPaint);
            }
        }

        mPaint.reset();

    }

    private ArrayList<V> dataList = new ArrayList<>();

    public class V {
        public float value;
        public String raw;
    }

    private boolean mHasInitialDataList = false;

    /**
     * 设置数据
     *
     * @param dataList 原始字符串数据
     * @param values   浮点值
     */
    public void setDataList(ArrayList<String> dataList, ArrayList<Float> values) {

        if (!(dataList != null && values != null && dataList.size() <= values.size())) {
            throw new IllegalArgumentException("parameters are wrong");
        }

        ArrayList<V> vList = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            V v = new V();
            v.value = values.get(i);
            v.raw = dataList.get(i);
            vList.add(v);
        }

        Collections.sort(vList, new Comparator<V>() {
            @Override
            public int compare(V o1, V o2) {
                if (o1.value < o2.value) {
                    return -1;
                } else if (o1.value == o2.value) {
                    return 0;
                } else if (o1.value > o2.value) {
                    return 1;
                }
                return 0;
            }
        });

        this.dataList = vList;

//      lineMargin = dataList.size() == 1 ? 0 : (getWidth() - getHeight() * vList.size()) / (vList.size() - 1f);
        lineMargin = dataList.size() == 1 ? 0 : (getWidth() - (int) mVisibleHeight) / (vList.size() - 1f);
        tickMarks.clear();

        if (vList.size() != 0) {
            mHasInitialDataList = true;
        } else {
            mHasInitialDataList = false;
        }
        for (int i = 0; i < vList.size(); i++) {
            //更新刻度线位置
            TickMark tickMark = new TickMark();

            try {

                tickMark.value = vList.get(i).value;

            } catch (Exception e) {

                tickMark.value = 0;

            }

            tickMark.rawValue = vList.get(i).raw;
            tickMark.index = i;

            tickMark.x = 0.5f * (int) mVisibleHeight + i * lineMargin;
            Rect rect = tickMark.rect;
            rect.left = (int) (tickMark.x + 0.5f * (int) mVisibleHeight);
            rect.right = (int) (tickMark.x - 0.5f * (int) mVisibleHeight);
            rect.top = (int) mPaddingTop;
            rect.bottom = (int) (getHeight() - mPaddingBottom);

            tickMarks.add(tickMark);
        }


        hasReset = false;

        invalidate();
    }

    /**
     * 待实现
     *
     * @param orientation 滑动方向
     */
    public void setOrientation(int orientation) {
        mOrientation = orientation;
    }

    public ArrayList<String> getSlidersAt() {

        ArrayList<String> times = new ArrayList<>();

        if (sliders.get(1).tickMark != null) {
            times.add(String.format("%d", sliders.get(1).tickMark.value));
        }

        if (sliders.get(2).tickMark != null) {
            times.add(String.format("%d", sliders.get(2).tickMark.value));
        }

        if (sliders.get(0).tickMark != null) {
            times.add(String.format("%d", sliders.get(0).tickMark.value));
        }

        if (sliders.get(3).tickMark != null) {
            times.add(String.format("%d", sliders.get(3).tickMark.value));
        }

        return times;
    }

    public String getOrderRawValue() {
        return pendValueString;
    }

    private String pendValueString = null;

    /**
     * 设置滑块的值
     *
     * @param values
     */
    public void setSlidersValue(float... values) {

        if (values.length > mSliderNum) return;
        ArrayList<Float> arrayList = new ArrayList<>();

        for (int i = 0; i < values.length; i++) {
            float value = values[i];
            arrayList.add(value);
        }

        Collections.sort(arrayList);

        ArrayList<Integer> a2 = new ArrayList<>();

        post(new Runnable() {
            @Override
            public void run() {

                for (int i = 0; i < arrayList.size(); i++) {
                    int tickIndex = findTickIndex(arrayList.get(i));
                    if (tickIndex == -1) {
                        if (i == 0) {
                            tickIndex = 0;
                        } else {
                            if (a2.get(i - 1) < tickMarks.size() - 1) {
                                tickIndex = a2.get(i - 1) + 1;
                            } else {
                                tickIndex = a2.get(i - 1);
                            }
                        }
                    }
                    a2.add(tickIndex);
                    setSliderValue(i, tickIndex);
                }

            }
        });

    }

    public boolean hasExecuteRunnable = false;

    float touchX;
    float touchX_Event;

    float moveLength;

    float moveLengthValue;

    float oldMoveLengthValue;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mCanNotTouch || !mHasInitialDataList) return true;

        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN: {
                touchX = event.getX();
                touchX_Event = event.getRawX();

                findTouchSlider();

                if (mTouchId != -1 && mFinishSmoothToPosition != null) {
                    mFinishSmoothToPosition.run();
                }

                if (mTouchId != -1) {
                    Slider slider = sliders.get(mTouchId);
                    mAnimateToPosition = slider.index;

                    if (mListener != null) {
                        mListener.onValueSelect(slider);
                    }
                }
                getParent().requestDisallowInterceptTouchEvent(true);
                break;

            }

            case MotionEvent.ACTION_MOVE: {

                float d = event.getRawX() - touchX_Event;
                moveLength = canMoveLength(d, mTouchId);
                moveLength = d;

                if (dataList != null && dataList.size() > 0) {
                    moveLengthValue += (moveLength / (lineMargin));
                }

                if (mTouchId != -1) {

                    Slider slider = sliders.get(mTouchId);

                    minusSlider(slider.tickMark, slider);

                    //(((int) (moveLengthValue / 0.5f) != 0 && Math.abs(moveLengthValue % 1) >= 0.5) ? (int) (d / Math.abs(d)) : 0);
                    // 0. 6 ,1.6 ,2.6 ,3.6 ,4.6
                    slider.rect.offset((int) (moveLength), 0);
                    int resultPosition = 0;
                    float indexPosition = (event.getX() - mStartX - 0.5f * mVisibleHeight) / lineMargin;
//                    MyLog.printLog("当前类:BasePickVerticalScrollBar,当前方法：onTouchEvent,当前线程:" + Thread.currentThread().getName() + ",信息:" + indexPosition);
//                    MyLog.printLog("BasePickVerticalScrollBar,信息:滑块的位置" + slider.rect.centerX());

                    int relativeAdd = 0;

                    if (slider.rect.centerX() <= 0.5f * mVisibleHeight || slider.rect.centerX() <= (mStartX + 0.5f * mVisibleHeight + (int) indexPosition * lineMargin) + lineMargin / 2f) {
                        resultPosition = (int) (indexPosition);
                    } else {
                        resultPosition = (int) (indexPosition) + 1;
                    }

                    resultPosition = slider.tickMark.index + relativeAdd;

                    if (resultPosition != slider.tickMark.index && checkTickMarkIndex(resultPosition) && ((slider.indexInSliders == 0 || (resultPosition >= sliders.get(slider.indexInSliders - 1).tickMark.index)) && (slider.indexInSliders == sliders.size() - 1 || resultPosition <= sliders.get(slider.indexInSliders + 1).tickMark.index))) {
                        if (slider.tickMark != slider.oldTickMark && slider.tickMark.index != resultPosition) {
                            slider.oldTickMark = slider.tickMark;
                        }
                        slider.tickMark = tickMarks.get(resultPosition);
                        mAnimateToPosition = resultPosition;
                        if (mListener != null) {
//                            mListener.onValueSelect(slider.index + (int) moveLengthValue, slider);
                            mListener.onValueSelect(slider);
                        }
                        oldMoveLengthValue = moveLengthValue;
                        moveLengthValue = moveLengthValue % 1f;
                    }

                } else {
                    moveLengthValue = 0;
                }

                touchX_Event = event.getRawX();

                invalidate();

                break;

            }

            case MotionEvent.ACTION_UP: {
                moveLength = 0;
                moveLengthValue = 0;
                //恢复到触摸的slider最近的位置
                smoothToClosetPosition(mTouchId);
                mTouchId = -1;
                touchX = 0;
                break;
            }

        }

        return true;

    }

    void smoothToClosetPosition(int index) {

        if (checkSliderIndex(index)) {

            Slider slider = sliders.get(index);

            int centerX = slider.rect.centerX();
            int centerY = slider.rect.centerY();

            TickMark tickMark = slider.tickMark;

            int resultPosition;

            int ti = tickMark.index > 0 ? tickMark.index - 1 : tickMark.index;

            resultPosition = tickMarks.get(ti).rect.contains(centerX, centerY) ? ti : tickMark.index;

            ti = tickMark.index < tickMarks.size() - 1 ? tickMark.index + 1 : tickMark.index;

            resultPosition = tickMarks.get(ti).rect.contains(centerX, centerY) ? ti : resultPosition;

//            mAnimateToPosition = resultPosition;

            mAnimateFromSliderIndex = index;

            if (mSmoothToTickMarkAnimator.isRunning()) {
                mSmoothToTickMarkAnimator.cancel();
            }
            mSmoothToTickMarkAnimator.start();

        }

    }


    boolean checkSliderIndex(int index) {
        return index >= 0 && index < sliders.size();
    }

    boolean checkTickMarkIndex(int index) {
        return index >= 0 && index < tickMarks.size();
    }

    /**
     * 返回下标
     *
     * @return
     */
    int mTouchId = -1;

    public int findTouchSlider() {
        if (mTouchId != -1) return mTouchId;
        if (touchX == 0) return -1;
        for (int i = 0; i < sliders.size(); i++) {
            Rect rect = sliders.get(i).rect;
            if (rect != null) {
                if (rect.contains((int) touchX, (int) midY)) {
                    mTouchId = i;
                    return i;
                } else if (rect.contains((int) touchX - (int) mVisibleHeight / 3, (int) midY)) {
                    mTouchId = i;
                    return i;
                } else if (rect.contains((int) touchX + (int) mVisibleHeight / 3, (int) midY)) {
                    mTouchId = i;
                    return i;
                }
            }
        }
        return -1;
    }

    public float canMoveLength(float dx, int index) {

        if (!(index >= 0 && index < sliders.size())) return 0;

        float right = 0;
        float left = 0;

        float result = 0;
        if (dx > 0) {//向右滑
            if (index == sliders.size() - 1) {
                right = mVisibleWidth;
            } else {
                right = sliders.get(index + 1).rect.right;
            }
            float d = right - sliders.get(index).rect.right;
            result = Math.abs(dx) > Math.abs(d) ? d : dx;
        } else {//向左滑

            if (index == 0) {
                left = 0;
            } else {
                left = sliders.get(index - 1).rect.left;
            }
            float d = left - sliders.get(index).rect.left;
            result = Math.abs(dx) > Math.abs(d) ? d : dx;
        }

        return result;
    }

    private boolean mCanNotTouch = false;

    public void setDisallowTouch(boolean cantouch) {
        mCanNotTouch = cantouch;
    }

    private SelectListener mListener = null;

    public void setSelectListener(SelectListener selectListener) {
        mListener = selectListener;
    }

    public interface SelectListener {
        /**
         * @param slider 改变值的slider
         */
        void onValueSelect(Slider slider);
    }

}
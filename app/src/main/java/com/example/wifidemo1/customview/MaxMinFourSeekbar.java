package com.example.wifidemo1.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import org.apache.commons.lang3.math.Fraction;

import java.util.ArrayList;

/**
 * @author: fuhejian
 * @date: 2023/5/5
 */
public class MaxMinFourSeekbar extends View {


    public MaxMinFourSeekbar(Context context) {
        this(context, null);
    }

    public MaxMinFourSeekbar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaxMinFourSeekbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MaxMinFourSeekbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    /**
     * 刻度线
     */
    public class TickMark {
        public float x = 0;
        public float value = 0;

        public int index = 0;
        public String rawValue;
        public Rect rect = new Rect();

        public ArrayList<Slider> currentSliders = new ArrayList<>();

    }

    private ArrayList<TickMark> tickMarks = new ArrayList<>();

    /**
     * 滑块
     */
    public class Slider {
        public Rect rect = new Rect();

        public int index;
        public TickMark tickMark;
        public float offsetX = 0;//相对于刻度线位置的x偏移
    }

    private ArrayList<Slider> sliders = new ArrayList<>();

    private float lineMargin = 0;

    public void init() {
        for (int i = 0; i < 4; i++) {
            sliders.add(new Slider());
        }
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        for (int i = 0; i < sliders.size(); i++) {
            Slider slider = sliders.get(i);
            Rect rect = slider.rect;
            rect.top = 0;
            rect.bottom = getHeight();
            rect.left = 0;
            rect.right = getHeight();
        }

    }

    public void setSliderValue(int sliderIndex, int tickIndex) {

        if (tickIndex == -1 || sliderIndex >= sliders.size() || sliderIndex < 0 || tickIndex >= tickMarks.size())
            return;

        //更新位置
        setSliderValue(sliderIndex, tickMarks.get(tickIndex));

    }

    public void setSliderValue(int sliderIndex, TickMark tickMark) {

        if (tickMark == null || sliderIndex >= sliders.size() || sliderIndex < 0) return;

        //当前位置已经有值则偏移一段距离
//        if (sliders.get(sliderIndex).tickMark != tickMark) {}
            TickMark oldTickMark = sliders.get(sliderIndex).tickMark;
            sliders.get(sliderIndex).tickMark = tickMark;
            minusSlider(oldTickMark, sliders.get(sliderIndex));
            sliders.get(sliderIndex).index = tickMark.index;
            //更新新位置
            plusSlider(tickMark, sliders.get(sliderIndex));
    }

    /**
     * rect1 - rect2;
     */
    private void minusSlider(TickMark tickMark, Slider slider) {

        if (tickMark == null || slider == null) return;

        tickMark.currentSliders.remove(slider);
        Rect tickMarkRect = tickMark.rect;

        if(tickMark.currentSliders.size() == 0){
            tickMarkRect.left = (int)(tickMark.x + getHeight()/2);
            tickMarkRect.right = (int)(tickMark.x - getHeight()/2);
        }else {
            for (int i = 0; i < tickMark.currentSliders.size(); i++) {
                Slider slider1 = tickMark.currentSliders.get(i);
                Rect sliderRect = slider1.rect;

                if (i == 0) {
                    sliderRect.right = (int) (tickMark.x + getHeight() / 2);
                    sliderRect.left = (int) (tickMark.x - getHeight() / 2);

                    tickMarkRect.right = sliderRect.right;
                    tickMarkRect.left = sliderRect.left;

                } else {
                    //判断当前slider是在第一个的哪一边
                    if (sliderRect.left <= tickMarkRect.left) {//左边

                        sliderRect.right = tickMarkRect.left;


                        sliderRect.left = tickMarkRect.left - getHeight();

                    } else {//右边

                        sliderRect.left = tickMarkRect.right;

                        sliderRect.right = tickMarkRect.right + getHeight();

                    }
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

        tickMark.currentSliders.add(slider);

        if(tickMark.currentSliders.size() == 1){

            slider.rect.left = tickMarkRect.right;
            slider.rect.right = tickMarkRect.left;

            tickMarkRect.right = slider.rect.right;

            tickMarkRect.left = slider.rect.left;

        }else {
            for (int i = 1; i < tickMark.currentSliders.size(); i++){

                slider = tickMark.currentSliders.get(i);

                Rect sliderRect = slider.rect;
                if(sliderRect.left>=tickMarkRect.right){//右边

                    sliderRect.left = tickMarkRect.right;
                    sliderRect.right = tickMarkRect.right + getHeight();

                }else {//左边

                    sliderRect.left = tickMarkRect.left - getHeight();
                    sliderRect.right = tickMarkRect.left;

                }

                tickMarkRect.right = Math.max(sliderRect.right, tickMarkRect.right);

                tickMarkRect.left = Math.min(sliderRect.left, tickMarkRect.left);

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

    private Paint mPaint = new Paint();

    private int color1 = Color.parseColor("#308C4F");
    private int color2 = Color.parseColor("#30798C");

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        lineMargin = dataList.size() == 1 ? 0 : (getWidth() - getHeight() * sliders.size()) / (dataList.size() - 1);
        if (dataChanged) {
            tickMarks.clear();
            for (int i = 0; i < dataList.size(); i++) {
                //更新刻度线位置
                TickMark tickMark = new TickMark();

                try {
                    tickMark.value = Fraction.getFraction(dataList.get(i)).floatValue();
                } catch (Exception e) {
                    tickMark.value = 0;
                }

                tickMark.rawValue = dataList.get(i);
                tickMark.index = i;

                tickMark.x = (0.5f + i) * getHeight() + i * lineMargin;
                Rect rect = tickMark.rect;
/*              rect.left = (int) (tickMark.x + 0.5f * getHeight());
                rect.right = (int) (tickMark.x - 0.5f * getHeight());*/
                rect.top = 0;
                rect.bottom = getHeight();

                tickMarks.add(tickMark);
                dataChanged = false;
            }
        }

        //更新slider的位置
        int touchSlider = findTouchSlider();
        if (touchSlider != -1) {
            Slider tSlider = sliders.get(touchSlider);

            int enfIndex = tSlider.index + moveLengthValue;

            if (enfIndex != tSlider.index && enfIndex >= 0 && enfIndex < dataList.size()) {

                if (touchSlider >= 2) {
                    if (enfIndex >= sliders.get(0).index && enfIndex <= sliders.get(1).index) {
                        setSliderValue(touchSlider, enfIndex);
                    }
                } else {
                    if (!(enfIndex > sliders.get(2).index && enfIndex < sliders.get(3).index)){
                        setSliderValue(touchSlider, enfIndex);
                    }
                }
            }

        }

        //绘制slider之间的颜色

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(getHeight());
        mPaint.setColor(Color.parseColor("#1C252F"));
        canvas.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2, mPaint);

        mPaint.setColor(color2);
        for (int i = 0; i < sliders.size(); i += 2) {
            int i2 = i + 1;
            if (i2 < sliders.size()) {
                canvas.drawLine(sliders.get(i).rect.left + getHeight() / 2, getHeight() / 2, sliders.get(i2).rect.left + getHeight() / 2, getHeight() / 2, mPaint);
            }
            mPaint.setColor(color1);
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

/*        mPaint.setTextSize(20);
        mPaint.setColor(Color.BLACK);
        for (int i = 0; i < tickMarks.size(); i++) {
            canvas.drawText(tickMarks.get(i).rawValue,tickMarks.get(i).x,getHeight()/2,mPaint);
        }*/


        mPaint.reset();

    }

    private ArrayList<String> dataList = new ArrayList<>();

    private boolean dataChanged = true;

    public void setDataList(ArrayList<String> dataList) {
        this.dataList = dataList;
        lineMargin = dataList.size() == 1 ? 0 : (getWidth() - getHeight() * sliders.size()) / (dataList.size() - 1);
        tickMarks.clear();

        for (int i = 0; i < dataList.size(); i++) {
            //更新刻度线位置
            TickMark tickMark = new TickMark();

            try {
                tickMark.value = Fraction.getFraction(dataList.get(i)).floatValue();
            } catch (Exception e) {
                tickMark.value = 0;
            }

            tickMark.rawValue = dataList.get(i);
            tickMark.index = i;

            tickMark.x = (0.5f + i) * getHeight() + i * lineMargin;
            Rect rect = tickMark.rect;
            rect.left = (int) (tickMark.x + 0.5f * getHeight());
            rect.right = (int) (tickMark.x - 0.5f * getHeight());
            rect.top = 0;
            rect.bottom = getHeight();

            tickMarks.add(tickMark);
            dataChanged = false;
        }

        for (int i = 0; i < sliders.size(); i++) {
            Slider slider = sliders.get(i);
            slider.index = 0;
            slider.tickMark = tickMarks.size()>0?tickMarks.get(0):null;
            slider.rect.left = 0;
            slider.rect.right = getHeight()/2;
        }

        dataChanged = false;

        invalidate();
    }

    private float safeValueMin = 0;
    private float safeValueMax = 0;
    private float normalValueMin = 0;
    private float normalValueMax = 0;

    public void setCurrentValue(float v1, float v2, float v3, float v4) {
        safeValueMin = v1;
        setSliderValue(0, findTickIndex(v1));
        safeValueMax = v2;
        setSliderValue(1, findTickIndex(v2));
        normalValueMin = v3;
        setSliderValue(2, findTickIndex(v3));
        normalValueMax = v4;
        setSliderValue(3, findTickIndex(v4));

        invalidate();
    }

    float touchX;
    float touchX_Event;

    float moveLength;

    int moveLengthValue;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN: {
                touchX = event.getX();
                touchX_Event = event.getX();
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            }

            case MotionEvent.ACTION_MOVE: {

                moveLength += event.getX() - touchX_Event;

                if (dataList != null || dataList.size() > 0) {
                    moveLengthValue = (int) (moveLength / ((getWidth() / dataList.size()) / 2));
                }

                touchX_Event = event.getX();
                if(moveLengthValue!=0){
                    invalidate();
                }
                break;
            }

            case MotionEvent.ACTION_UP: {
                moveLength = 0;
                break;
            }
        }
        return true;
    }

    /**
     * 返回下标
     *
     * @return
     */
    public int findTouchSlider() {
        for (int i = 0; i < sliders.size(); i++) {
            Rect rect = sliders.get(i).rect;
            if (rect.contains((int) touchX, 0)) {
                return i;
            }
        }
        return -1;
    }


}

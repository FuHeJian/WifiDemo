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
import java.util.Collections;
import java.util.Comparator;

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
        //初始化slider
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
     * tickMark - slider;
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

            for (int i = tickMark.currentSliders.size()-1; i < tickMark.currentSliders.size(); i++){

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

        lineMargin = dataList.size() == 1 ? 0 : (getWidth() - getHeight() * dataList.size()) / (dataList.size() - 1);
        if (dataChanged) {
            tickMarks.clear();
            for (int i = 0; i < dataList.size(); i++) {
                //更新刻度线位置
                TickMark tickMark = new TickMark();

                try {
                    tickMark.value = Fraction.getFraction(dataList.get(i).value).floatValue();
                } catch (Exception e) {
                    tickMark.value = 0;
                }

                tickMark.rawValue = dataList.get(i).raw;
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

            int endIndex = tSlider.index + moveLengthValue;

            boolean canSlideToEnd = true;
            if (endIndex != tSlider.index && endIndex >= 0 && endIndex < dataList.size()) {

                if(moveLengthValue>0){

                    for (int i = touchSlider+1; i < sliders.size(); i++) {
                        if(endIndex<=sliders.get(i).index){

                        }else {
                            canSlideToEnd = false;
                            break;
                        }
                    }
                    if(canSlideToEnd){
                        setSliderValue(touchSlider, endIndex);
                        if(mListener!=null){
                            mListener.onValueSelect(touchSlider,sliders.get(touchSlider));
                        }
                    }

                }else if (moveLengthValue<0){
                    for (int i = touchSlider-1; i < sliders.size() && i>=0; i--) {
                        if(endIndex<sliders.get(i).index){
                            canSlideToEnd = false;
                            break;
                        }
                    }
                    if(canSlideToEnd){
                        setSliderValue(touchSlider, endIndex);
                        if(mListener!=null){
                            mListener.onValueSelect(touchSlider,sliders.get(touchSlider));
                        }
                    }
                }
            }

            moveLengthValue = 0;

        }

        //绘制slider之间的颜色

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(getHeight());
        mPaint.setColor(Color.parseColor("#1C252F"));
        canvas.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2, mPaint);

        mPaint.setColor(color2);

        for(int i=0,j = sliders.size()-1;i<j;i++,j--){
            canvas.drawLine(sliders.get(i).rect.left + getHeight() / 2, getHeight() / 2, sliders.get(j).rect.left + getHeight() / 2, getHeight() / 2, mPaint);
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

    private ArrayList<V> dataList = new ArrayList<>();

    private boolean dataChanged = true;

    public class V{
        public float value;
        public String raw;
    }
    public void setDataList(ArrayList<String> dataList) {



        ArrayList<V> vList = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            V v = new V();

            if(dataList.get(i).contains("/")){
                v.value = Fraction.getFraction(dataList.get(i)).floatValue();
            }else {
                v.value = Float.parseFloat(dataList.get(i));
            }
            v.raw = dataList.get(i);
            vList.add(v);
        }

        Collections.sort(vList, new Comparator<V>() {
            @Override
            public int compare(V o1, V o2) {
                if(o1.value<o2.value){
                    return -1;
                }else if(o1.value==o2.value){
                    return 0;
                } else if (o1.value>o2.value) {
                    return 1;
                }
                return 0;
            }
        });

        this.dataList = vList;

        lineMargin = dataList.size() == 1 ? 0 : (getWidth() - getHeight() * vList.size()) / (dataList.size() - 1);
        tickMarks.clear();

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
            slider.rect.right = getHeight();
        }

        dataChanged = false;

        invalidate();
    }

    public ArrayList<String> getSlidersAt(){

        ArrayList<String> times = new ArrayList<>();

        ArrayList<Slider> _sliders = new ArrayList<>(sliders);
        Collections.sort(_sliders, new Comparator<Slider>() {
            @Override
            public int compare(Slider o1, Slider o2) {
                if(o1.tickMark.value<o2.tickMark.value)return -1;
                if(o1.tickMark.value==o2.tickMark.value)return 0;
                if(o1.tickMark.value>o2.tickMark.value)return 1;
                return 0;
            }
        });

        for (int i = 0; i < _sliders.size(); i++) {
            if(_sliders.get(i).tickMark!=null){
                times.add(_sliders.get(i).tickMark.rawValue);
            }
        }

        return times;
    }

    public void setCurrentValue(float v1, float v2, float v3, float v4) {

        ArrayList<Float> arrayList = new ArrayList<>();
        arrayList.add(v1);
        arrayList.add(v2);
        arrayList.add(v3);
        arrayList.add(v4);
        Collections.sort(arrayList);

        for (int i = 0; i < arrayList.size(); i++) {
            setSliderValue(i, findTickIndex(arrayList.get(i)));
        }

        invalidate();

    }

    float touchX;
    float touchX_Event;

    float moveLength;

    int moveLengthValue;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        if(mCanNotTouch)return true;
        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN: {
                touchX = event.getX();
                touchX_Event = event.getX();
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            }

            case MotionEvent.ACTION_MOVE: {

                moveLength += event.getX() - touchX_Event;

                if (dataList != null && dataList.size() > 0 && moveLengthValue==0) {
                    moveLengthValue = (int)(moveLength / (lineMargin+getHeight()));
                }

                touchX_Event = event.getX();
                if(moveLengthValue!=0){
                    moveLength = 0;
                    invalidate();
                }
                break;
            }

            case MotionEvent.ACTION_UP: {
                moveLength = 0;
                moveLengthValue = 0;
                mTouchId = -1;
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
    int mTouchId = -1;
    public int findTouchSlider() {
        if(mTouchId!=-1)return mTouchId;
        for (int i = 0; i < sliders.size(); i++) {
            Rect rect = sliders.get(i).rect;
            if (rect.contains((int) touchX, 0)) {
                mTouchId = i;
                return i;
            }
        }
        return -1;
    }

    private boolean mCanNotTouch = false;
    public void setDisallowTouch(boolean cantouch){
        mCanNotTouch = cantouch;
    }

    private SelectListener mListener = null;
    public void setSelectListener(SelectListener selectListener){
        mListener = selectListener;
    }
    public interface SelectListener{
        void onValueSelect(int position,Slider slider);
    }

}

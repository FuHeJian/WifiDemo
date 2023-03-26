package com.example.wifidemo1.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.wifidemo1.R;


/**
 * @author: fuhejian
 * @date: 2023/3/21
 */
public class Indicator extends LinearLayout {


    public Indicator(Context context) {
        this(context, null);
    }

    private int lastSelected = 0;

    public Indicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Indicator(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public Indicator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void select(int position) {
        if (position < getChildCount()) {
            getChildAt(lastSelected).setSelected(false);
            getChildAt(position).setSelected(true);
            lastSelected = position;
        }
    }

    public void addIndicator() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.indicator_item, this, true);
    }

    public int getPosition(){
        return lastSelected;
    }

}
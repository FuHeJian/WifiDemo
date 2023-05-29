package com.example.wifidemo1.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * @author: fuhejian
 * @date: 2023/5/23
 */
public class TestView extends View {

    public TestView(Context context) {
        this(context, null);
    }

    public TestView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TestView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public TestView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {



    }

}

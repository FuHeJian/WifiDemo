package com.example.wifidemo1.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public abstract class AbsTextView extends AppCompatTextView {

    protected boolean frameShow;
    protected boolean languageChange;
    protected int frameSize = 3;
    protected int frameColor = Color.TRANSPARENT;
    protected TextView borderText = null;///用于描边的TextView
    protected Typeface typeface;

    public AbsTextView(Context context) {
        super(context);
        init(null);
        if (frameShow) {
            borderText = new TextView(context);
            initOutlineText();
        }
    }


    public AbsTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
        if (frameShow) {
            borderText = new TextView(context, attrs);
            initOutlineText();
        }
    }

    public AbsTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
        if (frameShow) {
            borderText = new TextView(context, attrs, defStyleAttr);
            initOutlineText();
        }
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
        if (borderText != null)
            borderText.setLayoutParams(params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (borderText == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            CharSequence tt = borderText.getText();
            //两个TextView上的文字必须一致
            if (tt == null || !tt.equals(this.getText())) {
                borderText.setText(getText());
                this.postInvalidate();
            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            borderText.measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (borderText != null)
            borderText.layout(left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (borderText != null)
            borderText.draw(canvas);
        super.onDraw(canvas);
    }


    public abstract void init(AttributeSet attrs);

    protected void initOutlineText() {
        TextPaint tp1 = borderText.getPaint();
        tp1.setStrokeWidth(frameSize);                                  //设置描边宽度
        tp1.setStyle(Paint.Style.STROKE);                             //对文字只描边
        borderText.setTextColor(frameColor);  //设置描边颜色
        borderText.setGravity(getGravity());
        if (typeface != null)
            borderText.setTypeface(typeface);
    }
}

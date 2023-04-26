package com.example.wifidemo1.customview.helper.scrollerview;

/**
 * Created by aq on 2018/3/12.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;

import com.example.wifidemo1.R;
import com.example.wifidemo1.utils.UtilFunction;
import java.util.ArrayList;
import java.util.List;


/**
 * 字符串滚动选择器
 * Created by huangziwei on 16-12-6.
 */
public class FocusScrollPicker extends ScrollPickerView<ScorllPickerFocusItem> {


    private TextPaint mOutlinePaint;
    private boolean outline;
    private int outlineColor = Color.WHITE;
    private int outlineSize = 3;
    private String typefaceName;

    private TextPaint mPaint;
    private int mMinTextSize = 24; // 最小的字体
    private int mMaxTextSize = 32; // 最大的字体
    // 字体渐变颜色
    private int mStartColor = Color.BLACK; // 中间选中ｉｔｅｍ的颜色
    private int mEndColor = Color.GRAY; // 上下两边的颜色
    private int mEndColorA = Color.GRAY; // 上下两边的颜色

    private int mMaxLineWidth = -1; // 最大的行宽,默认为itemWidth.超过后文字自动换行
    private Layout.Alignment mAlignment = Layout.Alignment.ALIGN_CENTER; // 对齐方式,默认居中


    private Paint paintLine;
    private boolean hasLine;

    public FocusScrollPicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private Bitmap delay_icon_unlimited;

    private Bitmap delay_icon_unlimited_s;

    public FocusScrollPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.BLACK);

        mOutlinePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        init(attrs);
        mOutlinePaint.setStrokeWidth(outlineSize);
        mOutlinePaint.setStyle(Paint.Style.STROKE);
        mOutlinePaint.setColor(outlineColor);


        List<ScorllPickerFocusItem> list = new ArrayList<>();
        list.add(new ScorllPickerFocusItem("noon", 0, 0, 0));
        list.add(new ScorllPickerFocusItem("noon", 0, 0, 0));
        list.add(new ScorllPickerFocusItem("noon", 0, 0, 0));
        list.add(new ScorllPickerFocusItem("noon", 0, 0, 0));
        list.add(new ScorllPickerFocusItem("noon", 0, 0, 0));
        list.add(new ScorllPickerFocusItem("noon", 0, 0, 0));


        setData(list);
        paintLine = new Paint();
        paintLine.setStyle(Paint.Style.STROKE);
        paintLine.setStrokeWidth(UtilFunction.dp2px(getContext(), 1));
        paintLine.setAntiAlias(true);
        paintLine.setColor(Color.parseColor("#0fffffff"));
    }

    public void setTypeface(Typeface typeface) {
        mPaint.setTypeface(typeface);
        mOutlinePaint.setTypeface(typeface);
        postInvalidate();
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.StringScrollPicker);
            mMinTextSize = typedArray.getDimensionPixelSize(R.styleable.StringScrollPicker_spv_min_text_size, mMinTextSize);
            mMaxTextSize = typedArray.getDimensionPixelSize(R.styleable.StringScrollPicker_spv_max_text_size, mMaxTextSize);
            mStartColor = typedArray.getColor(R.styleable.StringScrollPicker_spv_start_color, mStartColor);
            mEndColor = typedArray.getColor(R.styleable.StringScrollPicker_spv_end_color, mEndColor);
            mEndColorA = Color.parseColor("#FFFFFF");
            mMaxLineWidth = typedArray.getDimensionPixelSize(R.styleable.StringScrollPicker_spv_max_line_width, mMaxLineWidth);
            hasLine = typedArray.getBoolean(R.styleable.StringScrollPicker_hasLine, false);
            int align = typedArray.getInt(R.styleable.StringScrollPicker_spv_alignment, 1);
            if (align == 2) {
                mAlignment = Layout.Alignment.ALIGN_NORMAL;
            } else if (align == 3) {
                mAlignment = Layout.Alignment.ALIGN_OPPOSITE;
            } else {
                mAlignment = Layout.Alignment.ALIGN_CENTER;
            }

            outline = typedArray.getBoolean(R.styleable.StringScrollPicker_outline_p, false);
            outlineColor = typedArray.getColor(R.styleable.StringScrollPicker_outlineColor_p, outlineColor);
            outlineSize = typedArray.getDimensionPixelSize(R.styleable.StringScrollPicker_outline_size_p, outlineSize);
            typefaceName = typedArray.getString(R.styleable.StringScrollPicker_typeface_p);

            if (typefaceName != null) {
                try {
                    Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), typefaceName);
                    setTypeface(typeface);
                } catch (Exception e) {
                }
            }
            typedArray.recycle();
        }
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mMaxLineWidth < 0) {
            mMaxLineWidth = getItemWidth();
        }
    }

    @Override
    public void drawItem(Canvas canvas, List<ScorllPickerFocusItem> data, int position, int relative, float moveLength, float top) {
        String text = data.get(position).string;


        int itemSize = getItemSize();

        // 设置文字大小
        if (relative == -1) { // 上一个
            if (moveLength < 0) { // 向上滑动
                mPaint.setTextSize(mMinTextSize);
                mOutlinePaint.setTextSize(mMinTextSize);
            } else { // 向下滑动
                mPaint.setTextSize(mMinTextSize + (mMaxTextSize - mMinTextSize) * moveLength / itemSize);
                mOutlinePaint.setTextSize(mMinTextSize + (mMaxTextSize - mMinTextSize) * moveLength / itemSize);
            }
        } else if (relative == 0) { // 中间item,当前选中
            mPaint.setTextSize(mMinTextSize + (mMaxTextSize - mMinTextSize) * (itemSize - Math.abs(moveLength)) / itemSize);
            mOutlinePaint.setTextSize(mMinTextSize + (mMaxTextSize - mMinTextSize) * (itemSize - Math.abs(moveLength)) / itemSize);
        } else if (relative == 1) { // 下一个
            if (moveLength > 0) { // 向下滑动
                mPaint.setTextSize(mMinTextSize);
                mOutlinePaint.setTextSize(mMinTextSize);
            } else { // 向上滑动
                mPaint.setTextSize(mMinTextSize + (mMaxTextSize - mMinTextSize) * -moveLength / itemSize);
                mOutlinePaint.setTextSize(mMinTextSize + (mMaxTextSize - mMinTextSize) * -moveLength / itemSize);
            }
        } else { // 其他
            mPaint.setTextSize(mMinTextSize);
            mOutlinePaint.setTextSize(mMinTextSize);
        }

        StaticLayout layout = new StaticLayout(text, 0, text.length(), mPaint, mMaxLineWidth, mAlignment, 1.0F, 0.0F, true, null, 0);

        StaticLayout layout1 = null;
        if (outline)
            layout1 = new StaticLayout(text, 0, text.length(), mOutlinePaint, mMaxLineWidth, mAlignment, 1.0F, 0.0F, true, null, 0);
        float x = 0;
        float y = 0;
        float lineWidth = layout.getWidth();

        if (isHorizontal()) { // 水平滚动
            x = top + (getItemWidth() - lineWidth) / 2;
            y = (getItemHeight() - layout.getHeight()) / 2;
        } else { // 垂直滚动
            x = (getItemWidth() - lineWidth) / 2;
            y = top + (getItemHeight() - layout.getHeight()) / 2;
        }
        // 计算渐变颜色
        computeColor(relative, itemSize, moveLength);
        y = y - getHeight() * 5.0f / 80;

        canvas.save();
        canvas.translate(x, y);


        if (layout1 != null)
            layout1.draw(canvas);
        layout.draw(canvas);


        canvas.restore();

        canvas.save();
        canvas.translate(x, 0);
        mPaint.setColor(Color.parseColor("C4C4C4"));
        canvas.drawRect(getItemWidth() / 2 - 3, getHeight() * 13.0f / 80, getItemWidth() / 2 + 3, getHeight() * 13.0f / 80 + 12, mPaint);
        canvas.restore();
    }

    /**
     * 计算字体颜色，渐变
     *
     * @param relative 　相对中间item的位置
     */
    private void computeColor(int relative, int itemSize, float moveLength) {

        int color = mEndColor; // 　其他默认为ｍEndColor

        if (relative == -2 || relative == 2) { // 上一个或下一个
            // 处理上一个item且向上滑动　或者　处理下一个item且向下滑动　，颜色为mEndColor
            if ((relative == -2 && moveLength < 0) || (relative == 2 && moveLength > 0)) {
                color = mEndColorA;
            } else { // 计算渐变的颜色
                float rate = (itemSize - Math.abs(moveLength)) / itemSize;
                color = ColorUtil.computeGradientColor(mEndColor, mEndColorA, rate);
            }
        } else if (relative == -1 || relative == 1) { // 上一个或下一个
            // 处理上一个item且向上滑动　或者　处理下一个item且向下滑动　，颜色为mEndColor
            if ((relative == -1 && moveLength < 0) || (relative == 1 && moveLength > 0)) {
                color = mEndColor;
            } else { // 计算渐变的颜色
                float rate = (itemSize - Math.abs(moveLength)) / itemSize;
                color = ColorUtil.computeGradientColor(mStartColor, mEndColor, rate);
            }
        } else if (relative == 0) { // 中间item
            float rate = Math.abs(moveLength) / itemSize;
            color = ColorUtil.computeGradientColor(mStartColor, mEndColor, rate);
        }

        mPaint.setColor(color);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (hasLine)
            canvas.drawLine(0, getHeight(), getWidth(), getHeight(), paintLine);
    }
}

package com.example.wifidemo1.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.PointerIcon;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.PathInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.utils.Easing;
import androidx.core.view.PointerIconCompat;
import androidx.core.view.animation.PathInterpolatorCompat;
import androidx.customview.widget.ViewDragHelper;

import com.example.wifidemo1.R;
import com.google.android.material.canvas.CanvasCompat;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.math.MathUtils;
import com.google.android.material.progressindicator.LinearProgressIndicator;

/**
 * @author: fuhejian
 * @date: 2023/3/9
 */
public class TrochalDiskView extends MaterialCardView {

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

    }

    Paint mPaint = new Paint();

    public TrochalDiskView(Context context) {
        this(context,null);
    }

    public TrochalDiskView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TrochalDiskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

/*        if(处理点击事件){
            return true;
        }
        else {
            return false;
        }*/

        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }



}

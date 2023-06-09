package com.example.wifidemo1.customview;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.appbar.CollapsingToolbarLayout;

/**
 * @author: fuhejian
 * @date: 2023/5/30
 */
public class MyCollapsingLayout extends CollapsingToolbarLayout {

    public MyCollapsingLayout(@NonNull Context context) {
        this(context, null);
    }

    public MyCollapsingLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyCollapsingLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void setScrimsShown(boolean shown) {
        super.setScrimsShown(shown);

    }

    private boolean mShown = false;

    @Override
    public void setScrimsShown(boolean shown, boolean animate) {
        super.setScrimsShown(shown, animate);
        if (scrimsShowListener != null && animate && mShown != shown) {
            scrimsShowListener.onScrimsShowChange(this, shown);
            mShown = shown;
        }
    }

    private OnScrimsShowListener scrimsShowListener;

    public void setOnScrimsShowListener(OnScrimsShowListener listener) {
        scrimsShowListener = listener;
    }

    public interface OnScrimsShowListener {
        void onScrimsShowChange(
                MyCollapsingLayout collapsingToolbarLayout,
                boolean isScrimesShow
        );
    }

}

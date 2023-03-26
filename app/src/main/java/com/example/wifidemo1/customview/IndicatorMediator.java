package com.example.wifidemo1.customview;

import android.annotation.SuppressLint;
import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.wifidemo1.R;
import com.example.wifidemo1.log.MyLog;
import com.google.android.material.internal.ViewUtils;

import java.lang.ref.WeakReference;

import io.reactivex.rxjava3.internal.operators.maybe.MaybeDoAfterSuccess;
import kotlin.jvm.internal.Reflection;

/**
 * @author: fuhejian
 * @date: 2023/3/21
 */
public class IndicatorMediator {

    @Nullable
    private RecyclerView.Adapter<?> adapter;

    private ViewPager2 viewPager2;

    private static int originalWidth;

    private IndicatorPageChangeCallback indicatorPageChangeCallback;

    private ViewPager2.PageTransformer pageTransformer;

    private Indicator indicator;

    public IndicatorMediator(Indicator _indicator, ViewPager2 _viewPager2) {
        viewPager2 = _viewPager2;
        indicator = _indicator;
        adapter = _viewPager2.getAdapter();
    }

    public void attach() {
        if (adapter != null && viewPager2 != null && indicator != null) {
            int count = adapter.getItemCount();
            for (int i = 0; i < count; i++) {
                indicator.addIndicator();
            }
            indicatorPageChangeCallback = new IndicatorPageChangeCallback(indicator);
            LinearLayoutManager layoutManager = null;

            pageTransformer = new IndicatorPageTransformer(indicator);
            viewPager2.registerOnPageChangeCallback(indicatorPageChangeCallback);
            ViewPager2Util.addPageTransformer(viewPager2,pageTransformer);
        }
    }

    public void detach() {
        if (viewPager2 != null && indicatorPageChangeCallback != null && indicator != null) {
            viewPager2.unregisterOnPageChangeCallback(indicatorPageChangeCallback);
            ViewPager2Util.removePageTransformer(viewPager2,pageTransformer);
            indicator.removeAllViews();
        }
    }

    private static class IndicatorPageChangeCallback extends ViewPager2.OnPageChangeCallback {

        WeakReference<Indicator> mIndicator;


        public IndicatorPageChangeCallback(Indicator indicator) {
            mIndicator = new WeakReference<>(indicator);
        }

        @SuppressLint("ResourceType")
        @Override
        public void onPageSelected(int position) {
            Indicator indicator1 = mIndicator.get();
            if (indicator1 != null) {
                indicator1.select(position);
                if (originalWidth == 0) {

                    try {

                        int[] att = {R.attr.indicatorWidth};

                        TypedArray a = mIndicator.get().getContext().getTheme().obtainStyledAttributes(att);

                        originalWidth = (int) a.getDimension(0, 7);

                        a.recycle();

                        View view = mIndicator.get().getChildAt(position);

                        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams((LinearLayout.LayoutParams) view.getLayoutParams());

                        layoutParams1.width = 2 * originalWidth;

                        //不能使用setLayoutParams 因为layout期间 recyclerView会拦截测量布局请求，使用post加入事件队列，当这个runnable执行时，已完成layout，就不会拦截。
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                view.setLayoutParams(layoutParams1);
                            }
                        });

                    } catch (Exception e) {
                        View view = mIndicator.get().getChildAt(position);
                        originalWidth = view.getWidth() / 2;
                    }
                }
            }
        }

    }

    private static class IndicatorPageTransformer implements ViewPager2.PageTransformer {

        WeakReference<Indicator> mIndicator;

        public IndicatorPageTransformer(Indicator indicator) {
            mIndicator = new WeakReference<>(indicator);
        }

        @Override
        public void transformPage(@NonNull View page, float position) {

            Indicator indicator = mIndicator.get();

            if (indicator == null) return;

            RecyclerView.LayoutParams pageLayoutParams = (RecyclerView.LayoutParams) page.getLayoutParams();

            int cPosition = pageLayoutParams.getViewLayoutPosition();

            if (position < 0 && position > -1) {//中间页面向左面，0->-1 和 左面向中间-1->0

                MyLog.printLog("当前类:IndicatorPageTransformer,当前方法：transformPage,当前线程:" + Thread.currentThread().getName() + ",信息:" + position);

                View view = indicator.getChildAt(cPosition);

                LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams((LinearLayout.LayoutParams) view.getLayoutParams());

                layoutParams2.width = originalWidth + (int) ((1 + position) * originalWidth);

                view.setLayoutParams(layoutParams2);

            } else if (position > 0 && position < 1) {//右面的页面向中间，1->0 和 中间向右面0->1

                MyLog.printLog("当前类:IndicatorPageTransformer,当前方法：transformPage,当前线程:" + Thread.currentThread().getName() + ",信息:" + position);

                if (mIndicator.get().getPosition() == mIndicator.get().getChildCount() - 1) return;

                View view = mIndicator.get().getChildAt(cPosition);

                LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams((LinearLayout.LayoutParams) view.getLayoutParams());

                layoutParams1.width = originalWidth + (int) ((1 - position) * originalWidth);

                view.setLayoutParams(layoutParams1);
            }

        }
    }

}
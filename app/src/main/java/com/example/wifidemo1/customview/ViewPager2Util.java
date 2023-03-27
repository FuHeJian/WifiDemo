package com.example.wifidemo1.customview;

import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import java.lang.reflect.Field;

/**
 * @author: fuhejian
 * @date: 2023/3/22
 */
public class ViewPager2Util {

    public static ViewPager2.PageTransformer getPageTransform(ViewPager2 viewPager2) {
        try {
            Class<?> aClass = Class.forName(viewPager2.getClass().getName());
            Field mPageTransformerAdapter = aClass.getDeclaredField("mPageTransformerAdapter");
            mPageTransformerAdapter.setAccessible(true);
            Object pageTransformerAdapter = mPageTransformerAdapter.get(viewPager2);
            Class<?> bClass = Class.forName("androidx.viewpager2.widget.PageTransformerAdapter");
            Field mPageTransformer = bClass.getDeclaredField("mPageTransformer");
            mPageTransformer.setAccessible(true);
            ViewPager2.PageTransformer pageTransformer = (ViewPager2.PageTransformer) mPageTransformer.get(pageTransformerAdapter);
            return pageTransformer;
        } catch (Exception e) {
            return null;
        }
    }

    public static void addPageTransformer(ViewPager2 viewPager2, ViewPager2.PageTransformer pageTransformer){

        if(pageTransformer == null) return;

        ViewPager2.PageTransformer pageTransformer1 = getPageTransform(viewPager2);
        if(pageTransformer1 == null){
            CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
            compositePageTransformer.addTransformer(pageTransformer);
            viewPager2.setPageTransformer(compositePageTransformer);
        }else {
            if(pageTransformer1 instanceof CompositePageTransformer){
                ((CompositePageTransformer) pageTransformer1).addTransformer(pageTransformer);
            }
        }
    }

    public static void removePageTransformer(ViewPager2 viewPager2, ViewPager2.PageTransformer pageTransformer){
        if(pageTransformer == null) return;

        ViewPager2.PageTransformer pageTransformer1 = getPageTransform(viewPager2);
        if(!(pageTransformer1 instanceof CompositePageTransformer)) return;

        ((CompositePageTransformer) pageTransformer1).removeTransformer(pageTransformer);

    }

}

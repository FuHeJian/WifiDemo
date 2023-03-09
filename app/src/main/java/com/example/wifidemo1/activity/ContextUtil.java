package com.example.wifidemo1.activity;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.FragmentActivity;

/**
 * @author: fuhejian
 * @date: 2023/3/8
 */
public class ContextUtil {
    public static ContextUtil INSTANCE;

    static {
        INSTANCE = new ContextUtil();
    }

    public FragmentActivity findAppCompatActivity(Context context) {
        if (context instanceof FragmentActivity) {
            return (FragmentActivity) context;
        }
        else {
            return null;
        }
    }
}

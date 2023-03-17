package com.example.wifidemo1.activity.theme;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;

/**
 * @author: fuhejian
 * @date: 2023/3/9
 */
public class ThemeUtil {
    public static ThemeUtil INSTANCE;

    static {
        INSTANCE = new ThemeUtil();
    }

    public void setSystemStatusBar(AppCompatActivity activity, boolean fullScreen, boolean trans) {
        if (activity.getLifecycle().getCurrentState() == Lifecycle.State.INITIALIZED) {
            int flag = 0;
            if (fullScreen) {
                activity.getWindow().getDecorView().setFitsSystemWindows(false);
                if (Build.VERSION_CODES.R <= Build.VERSION.SDK_INT) {
//                  activity.getWindow().getDecorView().getWindowInsetsController().hide(WindowInsets.Type.systemBars());
                    activity.getWindow().getDecorView().getWindowInsetsController().hide(WindowInsets.Type.navigationBars());
                } else {
                    flag = flag | View.SYSTEM_UI_FLAG_FULLSCREEN;
                }
            }

            if (trans) {
                activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
                if (Build.VERSION_CODES.R <= Build.VERSION.SDK_INT) {
                    activity.getWindow().getDecorView().getWindowInsetsController().setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
                    activity.getWindow().getDecorView().getWindowInsetsController().setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                } else {
                    flag = flag | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }
            }

            if (Build.VERSION_CODES.R > Build.VERSION.SDK_INT) {
                activity.getWindow().getDecorView().setSystemUiVisibility(flag);
            }
        }
    }

}

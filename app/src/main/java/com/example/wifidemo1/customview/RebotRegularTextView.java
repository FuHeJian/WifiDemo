package com.example.wifidemo1.customview;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

public class RebotRegularTextView extends AbsTextView {

    public RebotRegularTextView(Context context) {
        super(context);
        
    }

    public RebotRegularTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

    }

    public RebotRegularTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    public void init(AttributeSet attrs) {
        if (attrs != null) {
/*            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.AbsTextView);
            frameShow = typedArray.getBoolean(R.styleable.AbsTextView_frameShow, false);
            languageChange = typedArray.getBoolean(R.styleable.AbsTextView_languageChange, false);
            frameColor = typedArray.getColor(R.styleable.AbsTextView_frameColor, frameColor);
            frameSize = typedArray.getDimensionPixelSize(R.styleable.AbsTextView_frameSize, frameSize);*/
//            typedArray.recycle();
        }


/*        if (languageChange) {
            Locale locale = getContext().getResources().getConfiguration().locale;
            String language = locale.getLanguage();
            language = language.toLowerCase();

            if (!language.contains("zh")) {
                typeface = Typeface.createFromAsset(getContext().getAssets(), "roboto_regular.ttf");
            }
        } else {
            typeface = Typeface.createFromAsset(getContext().getAssets(), "roboto_regular.ttf");
        }*/

        if(typeface!=null)
            setTypeface(typeface);
    }
}

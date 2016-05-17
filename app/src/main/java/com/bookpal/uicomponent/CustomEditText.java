package com.bookpal.uicomponent;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.LruCache;
import android.widget.EditText;


public class CustomEditText extends EditText {

    private final static String NAME = "FONTAWESOME";
    private static LruCache<String, Typeface> sTypefaceCache = new LruCache<String, Typeface>(12);

    public CustomEditText(Context context) {
        super(context);
        init();

    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init() {

        Typeface typeface = sTypefaceCache.get(NAME);

        if (typeface == null) {

            typeface = Typeface.createFromAsset(getContext().getAssets(), "fontawesome-webfont.ttf");
            sTypefaceCache.put(NAME, typeface);

        }

        setTypeface(typeface);

    }

}



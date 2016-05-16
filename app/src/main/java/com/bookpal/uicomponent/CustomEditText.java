package com.bookpal.uicomponent;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

import com.bookpal.utility.FontManager;


/**
 * Created by asif on 30/12/14.
 */
public class CustomEditText extends EditText {
    private KeyImeChange keyImeChangeListener;

    public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CustomEditText(Context context) {
        super(context);
        init(context, null);
    }

    public void setKeyImeChangeListener(KeyImeChange listener) {
        keyImeChangeListener = listener;
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyImeChangeListener != null) {
            keyImeChangeListener.onKeyIme(keyCode, event);
        }
        return false;
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            Typeface myTypeface = FontManager.getTypeface(context, FontManager.FONTAWESOME);
            setTypeface(myTypeface);
        }
    }

    public interface KeyImeChange {
        public void onKeyIme(int keyCode, KeyEvent event);
    }
}
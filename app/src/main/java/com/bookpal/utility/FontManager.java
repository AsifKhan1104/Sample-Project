package com.bookpal.utility;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by asif on 16/5/16.
 */
public class FontManager {

    public static final String ROOT = "fonts/",
            FONTAWESOME = ROOT + "fontawesome-webfont.ttf";

    public static Typeface getTypeface(Context context, String font) {
        return Typeface.createFromAsset(context.getAssets(), font);
    }

}

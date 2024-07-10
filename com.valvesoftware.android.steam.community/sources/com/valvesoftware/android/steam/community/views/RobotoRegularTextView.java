package com.valvesoftware.android.steam.community.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/* loaded from: classes.dex */
public class RobotoRegularTextView extends TypefaceTextView {
    protected String fontName() {
        return "fonts/Roboto-Regular.ttf";
    }

    public RobotoRegularTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        if (isInEditMode()) {
            return;
        }
        setTypeface(Typeface.createFromAsset(context.getAssets(), fontName()));
    }

    public RobotoRegularTextView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }
}

package com.valvesoftware.android.steam.community.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;
import com.valvesoftware.android.steam.community.R;

/* loaded from: classes.dex */
public class TypefaceTextView extends TextView {
    public TypefaceTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        if (isInEditMode()) {
            return;
        }
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.TypefaceTextView);
        String string = obtainStyledAttributes.getString(0);
        obtainStyledAttributes.recycle();
        if (string != null) {
            setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/" + string));
        }
    }

    public TypefaceTextView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }
}

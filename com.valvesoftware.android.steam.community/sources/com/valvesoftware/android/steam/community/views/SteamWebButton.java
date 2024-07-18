package com.valvesoftware.android.steam.community.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.valvesoftware.android.steam.community.R;

/* loaded from: classes.dex */
public class SteamWebButton extends LinearLayout {
    private TextView detailTextView;

    public SteamWebButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.SteamWebButton, 0, 0);
        int resourceId = obtainStyledAttributes.getResourceId(2, 0);
        int resourceId2 = obtainStyledAttributes.getResourceId(1, 0);
        boolean z = obtainStyledAttributes.getBoolean(3, false);
        boolean z2 = obtainStyledAttributes.getBoolean(0, false);
        obtainStyledAttributes.recycle();
        setOrientation(1);
        setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.steam_web_button, (ViewGroup) this, true);
        TextView textView = (TextView) findViewById(R.id.fake_button_text);
        this.detailTextView = (TextView) findViewById(R.id.fake_button_detail);
        View findViewById = findViewById(R.id.top_border);
        View findViewById2 = findViewById(R.id.bottom_border);
        findViewById.setVisibility(z ? 0 : 4);
        findViewById2.setVisibility(z2 ? 0 : 4);
        if (resourceId > 0) {
            textView.setText(getResources().getString(resourceId));
        }
        if (resourceId2 > 0) {
            this.detailTextView.setText(getResources().getString(resourceId2));
        }
    }

    @Override // android.view.View
    public void setOnClickListener(View.OnClickListener onClickListener) {
        if (onClickListener == null) {
            return;
        }
        setClickable(true);
        super.setOnClickListener(onClickListener);
    }

    public void setDetailText(String str) {
        TextView textView = this.detailTextView;
        if (textView == null) {
            return;
        }
        textView.setText(str);
    }
}

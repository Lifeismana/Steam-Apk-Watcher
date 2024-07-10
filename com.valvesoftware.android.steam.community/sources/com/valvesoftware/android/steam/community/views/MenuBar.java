package com.valvesoftware.android.steam.community.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.valvesoftware.android.steam.community.R;

/* loaded from: classes.dex */
public class MenuBar extends LinearLayout {
    private ImageButton extraMenuButton;
    private Button hamburgerButton;
    private ProgressBar progressBar;
    private Button refreshButton;
    private View.OnClickListener refreshClickedListener;
    private Button searchButton;
    private View.OnClickListener searchClickedListener;
    private TextView titleTextView;

    public MenuBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.menu_bar, (ViewGroup) this, true);
        this.hamburgerButton = (Button) findViewById(R.id.hamburgerButton);
        this.titleTextView = (TextView) findViewById(R.id.titleLabel);
        this.searchButton = (Button) findViewById(R.id.titleNavSearchButton);
        this.refreshButton = (Button) findViewById(R.id.titleNavRefreshButton);
        this.progressBar = (ProgressBar) findViewById(R.id.titleProgressBar);
        this.extraMenuButton = (ImageButton) findViewById(R.id.titleExtraButton);
    }

    public void setSearchClickedListener(View.OnClickListener onClickListener) {
        this.searchClickedListener = onClickListener;
        if (this.searchClickedListener == null) {
            this.searchButton.setVisibility(4);
        } else {
            this.searchButton.setOnClickListener(onClickListener);
            this.searchButton.setVisibility(0);
        }
    }

    public void setRefreshClickedListener(View.OnClickListener onClickListener) {
        this.refreshClickedListener = onClickListener;
        if (this.refreshClickedListener == null) {
            this.refreshButton.setVisibility(4);
        } else {
            this.refreshButton.setOnClickListener(onClickListener);
            this.refreshButton.setVisibility(0);
        }
    }

    public void setHamburgerClickedListener(View.OnClickListener onClickListener) {
        this.hamburgerButton.setOnClickListener(onClickListener);
    }

    public void showProgressIndicator() {
        this.refreshButton.setVisibility(8);
        this.progressBar.setVisibility(0);
    }

    public void hideProgressIndicator() {
        this.progressBar.setVisibility(8);
        if (this.refreshClickedListener != null) {
            this.refreshButton.setVisibility(0);
        }
    }

    public void setTitle(CharSequence charSequence) {
        this.titleTextView.setText(charSequence);
    }

    public void setExtraMenuItem(SteamMenuItem steamMenuItem) {
        if (steamMenuItem != null) {
            this.extraMenuButton.setVisibility(0);
            this.extraMenuButton.setImageResource(steamMenuItem.iconResourceId);
            this.extraMenuButton.setOnClickListener(steamMenuItem.onClickListener);
        } else {
            this.extraMenuButton.setVisibility(8);
            this.extraMenuButton.setOnClickListener(null);
        }
    }
}

package com.valvesoftware.android.steam.community.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.valvesoftware.android.steam.community.PagingHorizontalScrollView;
import com.valvesoftware.android.steam.community.R;
import com.valvesoftware.android.steam.community.SteamguardState;
import com.valvesoftware.android.steam.community.TimeCorrector;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class TwoFactorCodeListView extends FrameLayout {
    private boolean invisibleIfNoCodes;
    private PagingHorizontalScrollView pagingHorizontalScrollView;
    private BroadcastReceiver receiver;
    private boolean stopUpdatingTime;
    private Runnable timeCorrectorTask;
    private View twoFactorPlaceholder;
    private Handler updateHandler;

    public void setInvisibleIfNoCodes(boolean z) {
        this.invisibleIfNoCodes = z;
    }

    public TwoFactorCodeListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.stopUpdatingTime = false;
        this.updateHandler = new Handler();
        this.timeCorrectorTask = new Runnable() { // from class: com.valvesoftware.android.steam.community.fragment.TwoFactorCodeListView.1
            @Override // java.lang.Runnable
            public void run() {
                if (TwoFactorCodeListView.this.stopUpdatingTime) {
                    return;
                }
                TimeCorrector.getInstance().update();
                TwoFactorCodeListView.this.updateHandler.postDelayed(this, 1000L);
            }
        };
        View inflate = ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.twofactor_code_list_fragment, (ViewGroup) this, true);
        this.pagingHorizontalScrollView = (PagingHorizontalScrollView) inflate.findViewById(R.id.twofactor_code_fragment_horizontalscroller);
        this.pagingHorizontalScrollView.init();
        this.twoFactorPlaceholder = inflate.findViewById(R.id.two_factor_placeholder);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.TwoFactorCodeListFragment, 0, 0);
        this.invisibleIfNoCodes = obtainStyledAttributes.getBoolean(0, false);
        obtainStyledAttributes.recycle();
        this.receiver = new BroadcastReceiver() { // from class: com.valvesoftware.android.steam.community.fragment.TwoFactorCodeListView.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                TwoFactorCodeListView.this.syncFragments();
            }
        };
        if (isInEditMode()) {
            TwoFactorCodeView twoFactorCodeView = new TwoFactorCodeView(getContext(), null);
            twoFactorCodeView.setSteamguardState(null);
            this.pagingHorizontalScrollView.addView(twoFactorCodeView, "test");
            return;
        }
        gatherFragments(this);
    }

    public void syncFragments() {
        ArrayList<SteamguardState> sortedTwoFactorSteamGuardStates = SteamguardState.getSortedTwoFactorSteamGuardStates();
        this.pagingHorizontalScrollView.clear();
        String str = "sgStates size: " + String.valueOf(sortedTwoFactorSteamGuardStates.size());
        int i = 0;
        int i2 = 0;
        while (i < sortedTwoFactorSteamGuardStates.size()) {
            SteamguardState steamguardState = sortedTwoFactorSteamGuardStates.get(i);
            TwoFactorCodeView twoFactorCodeView = new TwoFactorCodeView(getContext(), null);
            this.pagingHorizontalScrollView.addView(twoFactorCodeView, steamguardState.getTokenGID());
            twoFactorCodeView.setSteamguardState(steamguardState);
            i2++;
            int i3 = i + 1;
            boolean z = true;
            twoFactorCodeView.enableForwardArrow(i3 < sortedTwoFactorSteamGuardStates.size());
            if (i <= 0) {
                z = false;
            }
            twoFactorCodeView.enableBackArrow(z);
            i = i3;
        }
        String str2 = "number of code views: " + String.valueOf(i2);
        if (i2 == 0) {
            showPlaceholder();
            if (this.invisibleIfNoCodes) {
                setVisibility(8);
                return;
            } else {
                setVisibility(0);
                return;
            }
        }
        removePlaceholder();
        setVisibility(0);
    }

    private void gatherFragments(View view) {
        ArrayList<SteamguardState> sortedTwoFactorSteamGuardStates = SteamguardState.getSortedTwoFactorSteamGuardStates();
        int i = 0;
        if (sortedTwoFactorSteamGuardStates.size() > 0) {
            while (i < sortedTwoFactorSteamGuardStates.size()) {
                SteamguardState steamguardState = sortedTwoFactorSteamGuardStates.get(i);
                TwoFactorCodeView twoFactorCodeView = new TwoFactorCodeView(getContext(), null);
                twoFactorCodeView.setSteamguardState(steamguardState);
                int i2 = i + 1;
                if (i2 < sortedTwoFactorSteamGuardStates.size()) {
                    twoFactorCodeView.enableForwardArrow(true);
                }
                if (i > 0) {
                    twoFactorCodeView.enableBackArrow(true);
                }
                this.pagingHorizontalScrollView.addView(twoFactorCodeView, steamguardState.getTokenGID());
                i = i2;
            }
            return;
        }
        if (!this.invisibleIfNoCodes) {
            view.setVisibility(0);
            showPlaceholder();
        } else {
            view.setVisibility(8);
        }
    }

    private void showPlaceholder() {
        this.pagingHorizontalScrollView.clear();
        this.pagingHorizontalScrollView.setVisibility(8);
        this.twoFactorPlaceholder.setVisibility(0);
    }

    private void removePlaceholder() {
        this.pagingHorizontalScrollView.setVisibility(0);
        this.twoFactorPlaceholder.setVisibility(8);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getContext().getApplicationContext().registerReceiver(this.receiver, new IntentFilter("TWOFACTORCODES_CHANGED"));
        startTimeCorrectorChecking();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getContext().getApplicationContext().unregisterReceiver(this.receiver);
        stopTimeCorrectorChecking();
    }

    public void stop() {
        stopTimeCorrectorChecking();
    }

    private void startTimeCorrectorChecking() {
        this.stopUpdatingTime = false;
        this.updateHandler.postDelayed(this.timeCorrectorTask, 1000L);
    }

    private void stopTimeCorrectorChecking() {
        this.stopUpdatingTime = true;
        this.updateHandler.removeCallbacks(this.timeCorrectorTask);
    }
}

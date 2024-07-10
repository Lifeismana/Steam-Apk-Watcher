package com.valvesoftware.android.steam.community.fragment;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.valvesoftware.android.steam.community.CloseableView;
import com.valvesoftware.android.steam.community.R;
import com.valvesoftware.android.steam.community.SteamguardState;
import com.valvesoftware.android.steam.community.TimeCorrector;
import com.valvesoftware.android.steam.community.TwoFactorCodeThermometerView;
import com.valvesoftware.android.steam.community.TwoFactorToken;

/* loaded from: classes.dex */
public class TwoFactorCodeView extends LinearLayout implements CloseableView {
    private boolean bEnableBackArrow;
    private boolean bEnableForwardArrow;
    private TextView mAccountName;
    private TextView mCode;
    private int mDangerColor;
    private int mNormalColor;
    private SteamguardState mSteamguardState;
    private TwoFactorCodeThermometerView mThermometer;
    private final TextView mValveTime;
    private boolean stopUpdating;
    private Handler updateHandler;
    private Runnable updateRunnable;

    public TwoFactorCodeView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.stopUpdating = true;
        this.updateHandler = new Handler();
        this.updateRunnable = new Runnable() { // from class: com.valvesoftware.android.steam.community.fragment.TwoFactorCodeView.1
            @Override // java.lang.Runnable
            public void run() {
                if (TwoFactorCodeView.this.stopUpdating) {
                    return;
                }
                TwoFactorCodeView.this.update();
                TwoFactorCodeView.this.updateHandler.postDelayed(this, 1000L);
            }
        };
        ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.twofactor_code_fragment, (ViewGroup) this, true);
        setLayoutParams(new LinearLayout.LayoutParams(getScreenWidth(), -1));
        this.mNormalColor = getResources().getColor(R.color.twofactorcode_normal);
        this.mDangerColor = getResources().getColor(R.color.twofactorcode_alert);
        this.mCode = (TextView) findViewById(R.id.twofactorcode_code);
        this.mAccountName = (TextView) findViewById(R.id.twofactorcode_account_name);
        this.mThermometer = (TwoFactorCodeThermometerView) findViewById(R.id.twofactorcode_thermometer);
        this.mValveTime = (TextView) findViewById(R.id.twofactorcode_valvetime);
    }

    public void setSteamguardState(SteamguardState steamguardState) {
        if (isInEditMode()) {
            TextView textView = this.mAccountName;
            if (textView != null) {
                textView.setText("EditModeTest");
                update();
                startContinuousUpdates();
                return;
            }
            return;
        }
        this.mSteamguardState = steamguardState;
        TextView textView2 = this.mAccountName;
        if (textView2 != null) {
            textView2.setText(this.mSteamguardState.getAccountName());
            update();
            startContinuousUpdates();
        } else {
            Log.e("twofactor", "TwoFactorCodeView.java, setSteamguardState, else clause, for sgState: " + steamguardState.getAccountName() + " " + steamguardState.getTokenGID());
        }
    }

    public void enableForwardArrow(boolean z) {
        this.bEnableForwardArrow = z;
    }

    public void enableBackArrow(boolean z) {
        this.bEnableBackArrow = z;
    }

    private void enableArrow(int i, boolean z) {
        View findViewById = findViewById(i);
        if (findViewById != null) {
            findViewById.setVisibility(z ? 0 : 4);
        }
    }

    public void startContinuousUpdates() {
        if (this.stopUpdating) {
            this.stopUpdating = false;
            this.updateHandler.postDelayed(this.updateRunnable, 1000L);
        }
    }

    public void stopContinuousUpdates() {
        this.stopUpdating = true;
    }

    private static String kernString(String str) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < str.length()) {
            sb.append(str.charAt(i));
            i++;
            if (i < str.length()) {
                sb.append((char) 8202);
            }
        }
        return sb.toString();
    }

    public void update() {
        if (isInEditMode()) {
            this.mCode.setText("12345");
            this.mCode.setTextColor(this.mNormalColor);
            this.mThermometer.setValue(10, false);
            this.mValveTime.setVisibility(0);
            return;
        }
        TwoFactorToken twoFactorToken = this.mSteamguardState.getTwoFactorToken();
        if (twoFactorToken != null) {
            int secondsToNextChange = twoFactorToken.secondsToNextChange();
            boolean z = secondsToNextChange < 7;
            enableArrow(R.id.twofactor_back, this.bEnableBackArrow);
            enableArrow(R.id.twofactor_forward, this.bEnableForwardArrow);
            String generateSteamGuardCode = twoFactorToken.generateSteamGuardCode();
            String str = "code is: " + generateSteamGuardCode + " end code";
            this.mCode.setText(kernString(generateSteamGuardCode));
            this.mCode.setTextColor(z ? this.mDangerColor : this.mNormalColor);
            this.mThermometer.setValue(secondsToNextChange, z);
            this.mValveTime.setVisibility(TimeCorrector.getInstance().bUsingAdjustedTime() ? 0 : 4);
            return;
        }
        Log.e("twofactor", "token is null");
        this.stopUpdating = true;
    }

    private int getScreenWidth() {
        return getContext().getResources().getDisplayMetrics().widthPixels;
    }

    private void adjustWidth(int i) {
        setLayoutParams(new LinearLayout.LayoutParams(i, -1));
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startContinuousUpdates();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopContinuousUpdates();
    }

    @Override // android.view.View
    public void onConfigurationChanged(Configuration configuration) {
        adjustWidth(getScreenWidth());
    }

    @Override // com.valvesoftware.android.steam.community.CloseableView
    public void close() {
        stopContinuousUpdates();
    }
}

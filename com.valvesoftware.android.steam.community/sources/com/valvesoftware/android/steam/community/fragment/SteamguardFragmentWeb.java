package com.valvesoftware.android.steam.community.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.valvesoftware.android.steam.community.R;
import com.valvesoftware.android.steam.community.SteamAppUri;
import com.valvesoftware.android.steam.community.TimeCorrector;
import com.valvesoftware.android.steam.community.activity.ActivityHelper;
import com.valvesoftware.android.steam.community.views.SteamWebView;

/* loaded from: classes.dex */
public class SteamguardFragmentWeb extends Fragment implements IBackButtonSupport {
    private SteamWebView m_webView;
    private BroadcastReceiver receiver;
    private TwoFactorCodeListView twoFactorCodeListView;
    private FrameLayout twoFactorContainer;

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        View inflate = layoutInflater.inflate(R.layout.steamguard_fragment_web, viewGroup, false);
        this.twoFactorContainer = (FrameLayout) inflate.findViewById(R.id.twofactor_container);
        this.twoFactorCodeListView = (TwoFactorCodeListView) inflate.findViewById(R.id.two_factor_code_view);
        LinearLayout linearLayout = (LinearLayout) inflate.findViewById(R.id.webview_container);
        this.m_webView = new SteamWebView(getActivity());
        this.m_webView.setOwner(this);
        linearLayout.addView(this.m_webView);
        Bundle arguments = getArguments();
        String string = arguments != null ? arguments.getString("defaultUrl") : null;
        if (string != null) {
            this.m_webView.loadUrl(string);
        } else {
            this.m_webView.loadUrl(SteamAppUri.STEAMGUARD_PRECHANGE);
        }
        return inflate;
    }

    @Override // android.support.v4.app.Fragment
    public void onResume() {
        super.onResume();
        this.receiver = new BroadcastReceiver() { // from class: com.valvesoftware.android.steam.community.fragment.SteamguardFragmentWeb.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                SteamguardFragmentWeb.this.twoFactorCodeListView.syncFragments();
            }
        };
        TimeCorrector.getInstance().hintSync();
        if (ActivityHelper.fragmentIsActive(this)) {
            getActivity().setTitle(R.string.steamguard_steam_guard);
        }
        this.twoFactorCodeListView.syncFragments();
    }

    @Override // android.support.v4.app.Fragment
    public void onPause() {
        super.onPause();
        this.twoFactorCodeListView.stop();
        unregister();
    }

    @Override // com.valvesoftware.android.steam.community.fragment.IBackButtonSupport
    public boolean canGoBack() {
        return this.m_webView.canGoBack();
    }

    @Override // com.valvesoftware.android.steam.community.fragment.IBackButtonSupport
    public void goBack() {
        this.m_webView.goBack();
    }

    public void setTwoFactorVisible(boolean z) {
        this.twoFactorContainer.setVisibility(z ? 0 : 8);
        this.twoFactorCodeListView.setInvisibleIfNoCodes(!z);
        this.twoFactorCodeListView.syncFragments();
        if (z) {
            TimeCorrector.getInstance().hintSync();
        }
    }

    private void unregister() {
        try {
            getActivity().getApplicationContext().unregisterReceiver(this.receiver);
        } catch (IllegalArgumentException unused) {
        }
    }
}

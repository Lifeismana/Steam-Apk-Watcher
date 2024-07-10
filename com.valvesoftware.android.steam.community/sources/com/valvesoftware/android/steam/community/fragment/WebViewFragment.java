package com.valvesoftware.android.steam.community.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.valvesoftware.android.steam.community.R;
import com.valvesoftware.android.steam.community.activity.MainActivity;
import com.valvesoftware.android.steam.community.views.SteamWebView;

/* loaded from: classes.dex */
public class WebViewFragment extends Fragment implements IBackButtonSupport {
    private boolean inMiddleOfProcessing = false;
    private boolean m_bWebViewPaused = false;
    protected SteamWebView m_webView;

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        LinearLayout linearLayout = (LinearLayout) layoutInflater.inflate(R.layout.webview_fragment, viewGroup, false);
        this.m_webView = (SteamWebView) linearLayout.findViewById(R.id.webView);
        this.m_webView.setOwner(this);
        return linearLayout;
    }

    @Override // android.support.v4.app.Fragment
    public void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT >= 11) {
            this.m_webView.onPause();
            this.m_bWebViewPaused = true;
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onResume() {
        super.onResume();
        if (this.m_bWebViewPaused) {
            if (Build.VERSION.SDK_INT >= 11) {
                this.m_webView.onResume();
            }
            this.m_bWebViewPaused = false;
        }
        Bundle arguments = getArguments();
        String string = arguments != null ? arguments.getString("url") : null;
        if (string != null && !this.inMiddleOfProcessing && this.m_webView.getURL() == null) {
            this.m_webView.loadUrl(string);
        }
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setRefreshButtonClickListener(new View.OnClickListener() { // from class: com.valvesoftware.android.steam.community.fragment.WebViewFragment.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    WebViewFragment.this.m_webView.reload();
                }
            });
        }
    }

    @Override // com.valvesoftware.android.steam.community.fragment.IBackButtonSupport
    public boolean canGoBack() {
        boolean canGoBack = this.m_webView.canGoBack();
        String str = "canGoBack is returning " + canGoBack + " for URL " + this.m_webView.getUrl();
        return canGoBack;
    }

    @Override // com.valvesoftware.android.steam.community.fragment.IBackButtonSupport
    public void goBack() {
        this.m_webView.goBack();
    }

    public void loadUrl(String str) {
        if (str == null) {
            return;
        }
        this.m_webView.loadUrl(str);
    }

    public void setInMiddleOfProcessing(boolean z) {
        this.inMiddleOfProcessing = z;
    }

    public boolean refreshConfirmationsPageIfActive() {
        if (!this.m_webView.isMobileConfirmationPage()) {
            return false;
        }
        this.m_webView.reload();
        return true;
    }
}

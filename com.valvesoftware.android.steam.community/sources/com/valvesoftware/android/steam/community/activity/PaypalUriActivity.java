package com.valvesoftware.android.steam.community.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;
import com.valvesoftware.android.steam.community.R;
import com.valvesoftware.android.steam.community.SteamUriHandler;
import com.valvesoftware.android.steam.community.fragment.WebViewFragment;

/* loaded from: classes.dex */
public class PaypalUriActivity extends FragmentActivity {
    private static int s_NextActivityID;
    private String m_url = null;
    private String m_CategoriesUrl = null;
    protected int m_residActivityLayout = R.layout.webview_activity;
    private TextView m_BrowserLocationBar = null;

    public void SetBrowserBarLocation(String str) {
        TextView textView = this.m_BrowserLocationBar;
        if (textView != null) {
            textView.setText(str);
        }
    }

    @Override // android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = getIntent();
        if ("android.intent.action.VIEW".equals(intent.getAction())) {
            SteamUriHandler.Result HandleSteamURI = SteamUriHandler.HandleSteamURI(intent.getData());
            if (HandleSteamURI.command == SteamUriHandler.Command.openurl) {
                this.m_url = HandleSteamURI.getProperty(SteamUriHandler.CommandProperty.url);
            }
        }
        setContentView(this.m_residActivityLayout);
        ((WebViewFragment) getSupportFragmentManager().findFragmentById(R.id.webview)).loadUrl(this.m_url);
        this.m_BrowserLocationBar = (TextView) findViewById(R.id.webview_browser_location);
        SetBrowserBarLocation(this.m_url);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        if (intent == null) {
            return;
        }
        String stringExtra = intent.getStringExtra(SteamUriHandler.CommandProperty.call.toString());
        if (stringExtra != null && stringExtra.length() > 0) {
            ((WebViewFragment) getSupportFragmentManager().findFragmentById(R.id.webview)).loadUrl("javascript:(function(){" + stringExtra + ";})()");
            return;
        }
        String stringExtra2 = intent.getStringExtra(SteamUriHandler.CommandProperty.url.toString());
        if (stringExtra2 != null && stringExtra2.length() > 0) {
            ((WebViewFragment) getSupportFragmentManager().findFragmentById(R.id.webview)).loadUrl(stringExtra2);
            return;
        }
        String stringExtra3 = intent.getStringExtra("dialogtext");
        if (stringExtra3 == null || stringExtra3.length() <= 0) {
            return;
        }
        new AlertDialog.Builder(this).setMessage(stringExtra3).show();
    }
}

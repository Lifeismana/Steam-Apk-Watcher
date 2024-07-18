package com.valvesoftware.android.steam.community.fragment;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.valvesoftware.android.steam.community.Config;
import com.valvesoftware.android.steam.community.LoggedInUserAccountInfo;
import com.valvesoftware.android.steam.community.R;
import com.valvesoftware.android.steam.community.SteamCommunityApplication;
import com.valvesoftware.android.steam.community.SteamUriHandler;
import com.valvesoftware.android.steam.community.SteamguardState;
import com.valvesoftware.android.steam.community.activity.LoginChangedListener;
import com.valvesoftware.android.steam.community.views.SteamWebView;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class LoginFragment extends Fragment {
    private static final String loginUrl = Config.URL_COMMUNITY_BASE + "/mobilelogin?oauth_client_id=" + Config.WebAPI.OAUTH_CLIENT_ID + "&oauth_scope=read_profile%20write_profile%20read_client%20write_client";
    private LoginChangedListener loginChangedListener;
    private SteamWebView steamWebView;
    private TwoFactorCodeListView twoFactorCodeListView;
    private boolean hidingTwoFactorCode = false;
    private Handler twoFactorCodeHandler = new Handler();

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        View inflate = layoutInflater.inflate(R.layout.login, viewGroup, false);
        this.twoFactorCodeListView = (TwoFactorCodeListView) inflate.findViewById(R.id.login_twofactor_view);
        this.steamWebView = (SteamWebView) inflate.findViewById(R.id.webview);
        this.steamWebView.setOwner(this);
        if (Build.VERSION.SDK_INT >= 11) {
            this.steamWebView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() { // from class: com.valvesoftware.android.steam.community.fragment.LoginFragment.1
                @Override // android.view.View.OnLayoutChangeListener
                public void onLayoutChange(View view, int i, final int i2, int i3, final int i4, int i5, int i6, int i7, int i8) {
                    LoginFragment.this.twoFactorCodeHandler.post(new Runnable() { // from class: com.valvesoftware.android.steam.community.fragment.LoginFragment.1.1
                        @Override // java.lang.Runnable
                        public void run() {
                            int i9 = i4 - i2;
                            if (i9 <= 50) {
                                if (LoginFragment.this.twoFactorCodeListView.getVisibility() == 0) {
                                    LoginFragment.this.twoFactorCodeListView.setVisibility(8);
                                    LoginFragment.this.hidingTwoFactorCode = true;
                                    return;
                                }
                                return;
                            }
                            if (!LoginFragment.this.hidingTwoFactorCode || i9 <= LoginFragment.this.twoFactorCodeListView.getHeight() + 50) {
                                return;
                            }
                            LoginFragment.this.hidingTwoFactorCode = false;
                            LoginFragment.this.twoFactorCodeListView.setVisibility(0);
                            LoginFragment.this.twoFactorCodeListView.syncFragments();
                        }
                    });
                }
            });
        }
        loadPage();
        return inflate;
    }

    public void OnMobileLoginSucceeded(SteamUriHandler.Result result) {
        boolean z;
        SteamguardState steamguardStateForSteamID;
        try {
            JSONObject jSONObject = new JSONObject();
            String property = result.getProperty(SteamUriHandler.CommandProperty.steamid);
            jSONObject.put("x_steamid", property);
            jSONObject.put("access_token", result.getProperty(SteamUriHandler.CommandProperty.oauth_token));
            jSONObject.put("wgtoken", result.getProperty(SteamUriHandler.CommandProperty.wgtoken));
            jSONObject.put("wgtoken_secure", result.getProperty(SteamUriHandler.CommandProperty.wgtoken_secure));
            String property2 = result.getProperty(SteamUriHandler.CommandProperty.webcookie, null);
            if (property2 != null) {
                jSONObject.put("x_webcookie", property2);
            }
            z = HandleLoginDocument(jSONObject);
            if (z && (steamguardStateForSteamID = SteamguardState.steamguardStateForSteamID(property)) != null) {
                steamguardStateForSteamID.startGetTwoFactorStatus();
            }
        } catch (Exception unused) {
            z = false;
        }
        if (z) {
            return;
        }
        loadPage();
    }

    private void loadPage() {
        this.steamWebView.loadUrl(loginUrl);
    }

    private boolean HandleLoginDocument(JSONObject jSONObject) throws JSONException {
        if (!jSONObject.has("access_token") || !jSONObject.has("x_steamid")) {
            return false;
        }
        if (LoggedInUserAccountInfo.isLoggedIn()) {
            LoggedInUserAccountInfo.logOut();
        }
        LoggedInUserAccountInfo.LoginInformation loginInformation = LoggedInUserAccountInfo.getLoginInformation();
        loginInformation.loginState = LoggedInUserAccountInfo.LoginState.LoggedIn;
        loginInformation.accessToken = jSONObject.getString("access_token");
        loginInformation.steamId = jSONObject.getString("x_steamid");
        loginInformation.wgtoken = jSONObject.getString("wgtoken");
        loginInformation.wgtokenSecure = jSONObject.getString("wgtoken_secure");
        LoggedInUserAccountInfo.setLoginInformation(loginInformation);
        SteamCommunityApplication.GetInstance().UpdateCachedLoginInformation();
        dispatchOnLoginChangedSuccessfully();
        return true;
    }

    private void dispatchOnLoginChangedSuccessfully() {
        LoginChangedListener loginChangedListener = this.loginChangedListener;
        if (loginChangedListener != null) {
            loginChangedListener.onLoginChangedSuccessfully();
        }
    }

    public void setLoginChangedListener(LoginChangedListener loginChangedListener) {
        this.loginChangedListener = loginChangedListener;
    }
}

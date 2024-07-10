package com.valvesoftware.android.steam.community.views;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslCertificate;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.valvesoftware.android.steam.community.Config;
import com.valvesoftware.android.steam.community.LoggedInUserAccountInfo;
import com.valvesoftware.android.steam.community.NetErrorTranslator;
import com.valvesoftware.android.steam.community.R;
import com.valvesoftware.android.steam.community.SettingInfo;
import com.valvesoftware.android.steam.community.SteamAppIntents;
import com.valvesoftware.android.steam.community.SteamAppUri;
import com.valvesoftware.android.steam.community.SteamCommunityApplication;
import com.valvesoftware.android.steam.community.SteamUriHandler;
import com.valvesoftware.android.steam.community.SteamguardState;
import com.valvesoftware.android.steam.community.TwoFactorToken;
import com.valvesoftware.android.steam.community.activity.ActivityHelper;
import com.valvesoftware.android.steam.community.activity.BaseActivity;
import com.valvesoftware.android.steam.community.activity.MainActivity;
import com.valvesoftware.android.steam.community.activity.PaypalUriActivity;
import com.valvesoftware.android.steam.community.fragment.LoginFragment;
import com.valvesoftware.android.steam.community.fragment.SettingsFragment;
import com.valvesoftware.android.steam.community.fragment.SteamguardFragmentWeb;
import com.valvesoftware.android.steam.community.fragment.WebViewFragment;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

/* loaded from: classes.dex */
public class SteamWebView extends WebView {
    private String mJavascriptAsyncReturnCode;
    private String mJavascriptAsyncReturnStatus;
    private String mJavascriptAsyncReturnValue;
    private SteamguardJavascriptHandler m_steamguardJavascriptHandler;
    private Object owner;
    public static ArrayList<String> m_arrayConsoleLog = new ArrayList<>();
    public static ValueCallback<Uri> m_FilePathCallback = null;
    public static ValueCallback<Uri[]> m_MultiFilePathCallback = null;

    protected void setTitle(String str) {
    }

    public SteamWebView(Context context) {
        this(context, null);
    }

    public SteamWebView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public SteamWebView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setupView();
    }

    public void setOwner(Object obj) {
        this.owner = obj;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Activity getActivity() {
        return (Activity) getContext();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isLoginPage() {
        String url = getUrl();
        if (url == null) {
            return false;
        }
        return url.startsWith(Config.URL_COMMUNITY_BASE + "/mobilelogin");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isSteamguardPage() {
        String url = getUrl();
        if (url == null) {
            return false;
        }
        return url.startsWith(Config.URL_COMMUNITY_BASE + "/steamguard");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isManageTwofactorPage() {
        String url = getUrl();
        if (url == null) {
            return false;
        }
        return url.startsWith(Config.URL_STORE_BASE + "/twofactor/");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isManagePhonePage() {
        String url = getUrl();
        if (url == null) {
            return false;
        }
        String str = url + "startsWith " + Config.URL_STORE_BASE + "/phone/";
        return url.startsWith(Config.URL_STORE_BASE + "/phone/");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isCommunityPage() {
        String url = getUrl();
        if (url == null) {
            return false;
        }
        return url.startsWith(Config.URL_COMMUNITY_BASE + '/');
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isStoreLaunchAuthPage(String str) {
        return str.startsWith(Config.URL_STORE_BASE) && str.contains("/paypal/launchauth");
    }

    public boolean isMobileConfirmationPage() {
        String url = getUrl();
        if (url == null) {
            return false;
        }
        return url.startsWith(Config.URL_COMMUNITY_BASE + "/mobileconf/");
    }

    public String getURL() {
        return super.getUrl();
    }

    public void setBlackBackground() {
        if (Build.VERSION.SDK_INT < 16) {
            setBackgroundColor(0);
        } else {
            setBackgroundColor(Color.argb(1, 0, 0, 0));
        }
    }

    protected void setupView() {
        setBlackBackground();
        setWebViewClient(new SteamWebViewClient());
        setWebChromeClient(new SteamWebChromeClient());
        if (isInEditMode()) {
            return;
        }
        getSettings().setJavaScriptEnabled(true);
        getSettings().setDomStorageEnabled(true);
        getSettings().setSupportZoom(true);
        getSettings().setBuiltInZoomControls(true);
        if (Build.VERSION.SDK_INT >= 11) {
            getSettings().setDisplayZoomControls(false);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            getSettings().setMixedContentMode(2);
        }
        if (Config.STEAM_UNIVERSE_WEBPHP != Config.SteamUniverse.Public && Build.VERSION.SDK_INT >= 19) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        setScrollBarStyle(0);
        setHorizontalScrollBarEnabled(false);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setOnTouchListener(new View.OnTouchListener() { // from class: com.valvesoftware.android.steam.community.views.SteamWebView.1
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case 0:
                    case 1:
                        if (view.hasFocus()) {
                            return false;
                        }
                        if (view instanceof SteamWebView) {
                            ((SteamWebView) view).requestFocusWrapper();
                            return false;
                        }
                        view.requestFocus();
                        return false;
                    default:
                        return false;
                }
            }
        });
        this.m_steamguardJavascriptHandler = new SteamguardJavascriptHandler();
        addJavascriptInterface(this.m_steamguardJavascriptHandler, "SGHandler");
        requestFocusWrapper();
    }

    @Override // android.webkit.WebView
    public void reload() {
        showProgressIndicator();
        super.reload();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void requestFocusWrapper() {
        Object obj = this.owner;
        if (obj == null || ActivityHelper.fragmentOrActivityIsActive(obj)) {
            requestFocus();
        }
    }

    /* loaded from: classes.dex */
    public class SteamguardJavascriptHandler {
        public SteamguardJavascriptHandler() {
        }

        @JavascriptInterface
        public String getResultStatus() {
            return SteamWebView.this.mJavascriptAsyncReturnStatus;
        }

        @JavascriptInterface
        public String getResultValue() {
            String str = SteamWebView.this.mJavascriptAsyncReturnValue;
            setJavascriptResultBusy();
            return str;
        }

        @JavascriptInterface
        public String getResultCode() {
            return SteamWebView.this.mJavascriptAsyncReturnCode;
        }

        @JavascriptInterface
        public void setJavascriptResultBusy() {
            SteamWebView.this.mJavascriptAsyncReturnValue = "";
            SteamWebView.this.mJavascriptAsyncReturnStatus = "busy";
        }

        @JavascriptInterface
        public void setJavascriptResultOkay(String str) {
            if (str == null) {
                str = "";
            }
            SteamWebView.this.mJavascriptAsyncReturnValue = str;
            SteamWebView.this.mJavascriptAsyncReturnStatus = "ok";
        }

        @JavascriptInterface
        public void setJavascriptResultError(String str, int i) {
            if (str == null) {
                str = "";
            }
            SteamWebView.this.mJavascriptAsyncReturnValue = str;
            SteamWebView.this.mJavascriptAsyncReturnStatus = "error";
            SteamWebView.this.mJavascriptAsyncReturnCode = String.format(Locale.US, "%d", Integer.valueOf(i));
        }
    }

    /* loaded from: classes.dex */
    private class ReportErrorTask extends AsyncTask<String, Void, Void> {
        private ReportErrorTask() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(String... strArr) {
            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection) new URL("https://steamcommunity.com/steamguard/reporterror").openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(httpURLConnection.getOutputStream());
                bufferedOutputStream.write(String.format(Locale.US, "op=%s&e=%s", URLEncoder.encode("AndroidLogin", "UTF-8"), URLEncoder.encode(strArr[0], "UTF-8")).getBytes("UTF-8"));
                bufferedOutputStream.flush();
                httpURLConnection.getInputStream();
                bufferedOutputStream.close();
                httpURLConnection.disconnect();
                return null;
            } catch (Exception e) {
                String str = "Failed to report console messages: " + e.toString();
                return null;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SteamWebViewClient extends WebViewClient {
        private Handler m_backgroundHandler;
        private SteamUriHandler.Result m_loginContext;
        private WeakReference<WebView> m_urlWebView;

        private SteamWebViewClient() {
            this.m_backgroundHandler = new Handler();
        }

        private void launchPayPalAuth(String str) {
            Activity activity = SteamWebView.this.getActivity();
            if ((activity instanceof MainActivity) && ActivityHelper.fragmentOrActivityIsActive(activity)) {
                if (SteamWebView.this.owner instanceof WebViewFragment) {
                    ((WebViewFragment) SteamWebView.this.owner).setInMiddleOfProcessing(true);
                }
                String str2 = "Launching PayPal auth intent " + str;
                ((MainActivity) activity).readyForPaypalComplete(SteamWebView.this);
                SteamWebView.this.getActivity().startActivityForResult(new Intent().setData(SteamAppUri.createSteamAppWebUri(str)).setAction("android.intent.action.VIEW").setClass(SteamWebView.this.getActivity(), PaypalUriActivity.class), 1000);
            }
        }

        @Override // android.webkit.WebViewClient
        public boolean shouldOverrideUrlLoading(WebView webView, String str) {
            if (str.startsWith("steammobile://") && SteamWebView.this.getContext() == null) {
                return true;
            }
            if (str.startsWith("otpauth://")) {
                return false;
            }
            if (str.startsWith("steammobile://")) {
                SteamUriHandler.Result HandleSteamURI = SteamUriHandler.HandleSteamURI(Uri.parse(str));
                if (HandleSteamURI.handled) {
                    try {
                        switch (HandleSteamURI.command) {
                            case opencategoryurl:
                            case openurl:
                                String decode = Uri.decode(HandleSteamURI.getProperty(SteamUriHandler.CommandProperty.url));
                                if (SteamWebView.isStoreLaunchAuthPage(decode)) {
                                    launchPayPalAuth(decode);
                                    break;
                                } else {
                                    SteamWebView.this.getContext().startActivity(SteamAppIntents.viewWebPage(SteamWebView.this.getContext(), decode));
                                    break;
                                }
                            case openexternalurl:
                                SteamWebView.this.getActivity().startActivity(new Intent("android.intent.action.VIEW", Uri.parse(HandleSteamURI.getProperty(SteamUriHandler.CommandProperty.url))));
                                break;
                            case mobileloginsucceeded:
                                if (SteamWebView.this.owner instanceof LoginFragment) {
                                    ((LoginFragment) SteamWebView.this.owner).OnMobileLoginSucceeded(HandleSteamURI);
                                    break;
                                }
                                break;
                            case reloadpage:
                                SteamWebView.this.reload();
                                break;
                            case login:
                                this.m_urlWebView = new WeakReference<>(webView);
                                this.m_loginContext = HandleSteamURI;
                                SteamWebView.this.getActivity().startActivity(SteamAppIntents.loginIntent(SteamWebView.this.getActivity()));
                                break;
                            case closethis:
                                Intent intent = SteamWebView.this.getActivity().getIntent();
                                intent.putExtra(SteamUriHandler.CommandProperty.call.toString(), HandleSteamURI.getProperty(SteamUriHandler.CommandProperty.call));
                                intent.setAction("com.valvesoftware.android.steam.community.intent.action.WEBVIEW_RESULT");
                                SteamWebView.this.getActivity().setResult(-1, intent);
                                SteamWebView.this.getActivity().finish();
                                break;
                            case notfound:
                                SteamWebView.this.setViewContentsShowFailure("steammobile://" + SteamUriHandler.Command.reloadpage.toString(), SteamCommunityApplication.GetInstance().getString(R.string.Web_Error_Reload));
                                break;
                            case settitle:
                                String property = HandleSteamURI.getProperty(SteamUriHandler.CommandProperty.title);
                                if ((SteamWebView.this.owner == null || !(SteamWebView.this.owner instanceof Fragment) || ActivityHelper.fragmentIsActive((Fragment) SteamWebView.this.owner)) && property != null) {
                                    SteamWebView.this.getActivity().setTitle(URLDecoder.decode(property));
                                    break;
                                }
                                break;
                            case chat:
                                SteamWebView.this.getContext().startActivity(SteamAppIntents.chatIntent(SteamWebView.this.getContext(), HandleSteamURI.getProperty(SteamUriHandler.CommandProperty.steamid)));
                                break;
                            case twofactorcode:
                                String str2 = "Page wants two-factor code for GID " + HandleSteamURI.getProperty(SteamUriHandler.CommandProperty.gid);
                                if (SteamWebView.this.isLoginPage() || SteamWebView.this.isManagePhonePage() || SteamWebView.this.isManageTwofactorPage()) {
                                    SteamguardState steamguardStateForGID = SteamguardState.steamguardStateForGID(HandleSteamURI.getProperty(SteamUriHandler.CommandProperty.gid));
                                    if (steamguardStateForGID == null) {
                                        SteamWebView.this.m_steamguardJavascriptHandler.setJavascriptResultError(null, -1);
                                        break;
                                    } else {
                                        TwoFactorToken twoFactorToken = steamguardStateForGID.getTwoFactorToken();
                                        if (twoFactorToken == null) {
                                            SteamWebView.this.m_steamguardJavascriptHandler.setJavascriptResultError(null, -1);
                                            break;
                                        } else {
                                            String generateSteamGuardCode = twoFactorToken.generateSteamGuardCode();
                                            String str3 = "Returning two-factor code " + generateSteamGuardCode;
                                            SteamWebView.this.m_steamguardJavascriptHandler.setJavascriptResultOkay(generateSteamGuardCode);
                                            break;
                                        }
                                    }
                                }
                                break;
                            case steamguardset:
                                if (SteamWebView.this.isSteamguardPage()) {
                                    String property2 = HandleSteamURI.getProperty(SteamUriHandler.CommandProperty.scheme);
                                    SteamguardState steamguardStateForLoggedInUser = SteamguardState.steamguardStateForLoggedInUser();
                                    SteamguardState.Scheme stringToScheme = SteamguardState.stringToScheme(property2);
                                    String property3 = HandleSteamURI.getProperty(SteamUriHandler.CommandProperty.ph);
                                    if (stringToScheme == null) {
                                        SteamWebView.this.m_steamguardJavascriptHandler.setJavascriptResultError("Internal error, bad Steamguard token type", -1);
                                        break;
                                    } else {
                                        steamguardStateForLoggedInUser.startSetScheme(stringToScheme, property3, new SteamguardState.Completion() { // from class: com.valvesoftware.android.steam.community.views.SteamWebView.SteamWebViewClient.1
                                            @Override // com.valvesoftware.android.steam.community.SteamguardState.Completion
                                            public void success() {
                                                SteamWebView.this.m_steamguardJavascriptHandler.setJavascriptResultOkay(null);
                                            }

                                            @Override // com.valvesoftware.android.steam.community.SteamguardState.Completion
                                            public void failure(int i, String str4) {
                                                SteamWebView.this.m_steamguardJavascriptHandler.setJavascriptResultError(str4, i);
                                            }
                                        });
                                        break;
                                    }
                                }
                                break;
                            case steamguardvalidate:
                                if (SteamWebView.this.isSteamguardPage()) {
                                    SteamguardState.steamguardStateForLoggedInUser().finalizeAddTwoFactor(HandleSteamURI.getProperty(SteamUriHandler.CommandProperty.code), new SteamguardState.Completion() { // from class: com.valvesoftware.android.steam.community.views.SteamWebView.SteamWebViewClient.2
                                        @Override // com.valvesoftware.android.steam.community.SteamguardState.Completion
                                        public void success() {
                                            SteamWebView.this.m_steamguardJavascriptHandler.setJavascriptResultOkay(null);
                                        }

                                        @Override // com.valvesoftware.android.steam.community.SteamguardState.Completion
                                        public void failure(int i, String str4) {
                                            SteamWebView.this.m_steamguardJavascriptHandler.setJavascriptResultError(str4, i);
                                        }
                                    });
                                    break;
                                }
                                break;
                            case steamguardsendemail:
                                if (SteamWebView.this.isSteamguardPage()) {
                                    SteamguardState.steamguardStateForLoggedInUser().sendActivationCodeEmail();
                                    SteamWebView.this.m_steamguardJavascriptHandler.setJavascriptResultOkay(null);
                                    break;
                                }
                                break;
                            case steamguardgetgid:
                                if (SteamWebView.this.isSteamguardPage() || SteamWebView.this.isLoginPage()) {
                                    SteamguardState steamguardStateForLoggedInUser2 = SteamguardState.steamguardStateForLoggedInUser();
                                    if (steamguardStateForLoggedInUser2 != null) {
                                        SteamWebView.this.m_steamguardJavascriptHandler.setJavascriptResultOkay(steamguardStateForLoggedInUser2.getTokenGID());
                                        break;
                                    } else {
                                        SteamWebView.this.m_steamguardJavascriptHandler.setJavascriptResultOkay(null);
                                        break;
                                    }
                                }
                                break;
                            case steamguardsuppresstwofactorgid:
                                if (SteamWebView.this.isSteamguardPage() || SteamWebView.this.isLoginPage()) {
                                    String property4 = HandleSteamURI.getProperty(SteamUriHandler.CommandProperty.gid);
                                    if ((property4.equals("hide") || property4.equals("show")) && SteamWebView.this.owner != null && (SteamWebView.this.owner instanceof SteamguardFragmentWeb)) {
                                        ((SteamguardFragmentWeb) SteamWebView.this.owner).setTwoFactorVisible(property4.equals("show"));
                                    }
                                    SteamWebView.this.m_steamguardJavascriptHandler.setJavascriptResultOkay(null);
                                    break;
                                }
                                break;
                            case steamguardgetrevocation:
                                if (SteamWebView.this.isSteamguardPage()) {
                                    SteamWebView.this.m_steamguardJavascriptHandler.setJavascriptResultOkay(SteamguardState.steamguardStateForLoggedInUser().getRevocationCode());
                                    break;
                                }
                                break;
                            case steamguardconfrefresh:
                                if (SteamWebView.this.isCommunityPage()) {
                                    SteamWebView.this.m_steamguardJavascriptHandler.setJavascriptResultOkay(null);
                                    break;
                                }
                                break;
                            case steamguardconfcount:
                                if (SteamWebView.this.isCommunityPage()) {
                                    SteamWebView.this.m_steamguardJavascriptHandler.setJavascriptResultOkay("0");
                                    break;
                                }
                                break;
                            case currentuser:
                                if (SteamWebView.this.isCommunityPage()) {
                                    SteamWebView.this.m_steamguardJavascriptHandler.setJavascriptResultOkay("0");
                                    break;
                                }
                                break;
                            case logout:
                                if (SteamWebView.this.isCommunityPage()) {
                                    SteamWebView.this.m_steamguardJavascriptHandler.setJavascriptResultOkay(null);
                                    break;
                                }
                                break;
                            case livetokens:
                                if (SteamWebView.this.isCommunityPage()) {
                                    SteamWebView.this.m_steamguardJavascriptHandler.setJavascriptResultOkay(SteamguardState.hasLiveSteamguardStates() ? "1" : "0");
                                    break;
                                }
                                break;
                            case steamguard:
                                if (SteamWebView.this.isLoginPage() || SteamWebView.this.isCommunityPage()) {
                                    String property5 = HandleSteamURI.getProperty(SteamUriHandler.CommandProperty.op);
                                    if (property5 != null && property5.compareTo("setsecret") == 0) {
                                        String property6 = HandleSteamURI.getProperty(SteamUriHandler.CommandProperty.arg1);
                                        int installSecret = property6 != null ? SteamguardState.installSecret(property6) : -1;
                                        if (installSecret == 0) {
                                            SteamWebView.this.m_steamguardJavascriptHandler.setJavascriptResultOkay("");
                                            break;
                                        } else {
                                            SteamWebView.this.m_steamguardJavascriptHandler.setJavascriptResultError("", installSecret);
                                            break;
                                        }
                                    } else if (property5 == null || property5.compareTo("conftag") != 0 || !SteamWebView.this.isMobileConfirmationPage()) {
                                        SteamWebView.this.m_steamguardJavascriptHandler.setJavascriptResultError("", -1);
                                        break;
                                    } else {
                                        String HandleGetConfirmationTag = HandleGetConfirmationTag(HandleSteamURI.getProperty(SteamUriHandler.CommandProperty.arg1));
                                        if (HandleGetConfirmationTag != null) {
                                            SteamWebView.this.m_steamguardJavascriptHandler.setJavascriptResultOkay(HandleGetConfirmationTag);
                                            break;
                                        } else {
                                            SteamWebView.this.m_steamguardJavascriptHandler.setJavascriptResultError("", -1);
                                            break;
                                        }
                                    }
                                }
                                break;
                            case lostauth:
                                LoggedInUserAccountInfo.reacquireWGTokenFromServer(SteamWebView.this);
                                break;
                        }
                    } catch (Exception unused) {
                    }
                }
                return true;
            }
            if (str.startsWith(Config.URL_COMMUNITY_BASE) || str.startsWith(Config.URL_STORE_BASE) || str.startsWith(Config.URL_HELP_BASE)) {
                SteamWebView.this.requestFocusWrapper();
                webView.loadUrl(str);
                SteamWebView.this.showProgressIndicator();
                return true;
            }
            if (SteamWebView.this.getActivity() instanceof PaypalUriActivity) {
                if (SteamWebView.isStoreLaunchAuthPage(str)) {
                    String str4 = "Launching PayPal auth " + str;
                    launchPayPalAuth(str);
                }
                return true;
            }
            try {
                String str5 = "Opening URL " + str + " in external browser.";
                SteamWebView.this.getActivity().startActivity(new Intent("android.intent.action.VIEW", Uri.parse(str)));
            } catch (Exception unused2) {
            }
            return true;
        }

        private String HandleGetConfirmationTag(String str) {
            SteamguardState steamguardStateForLoggedInUser = SteamguardState.steamguardStateForLoggedInUser();
            if (steamguardStateForLoggedInUser == null || str == null || str.length() == 0) {
                return null;
            }
            return steamguardStateForLoggedInUser.getTaggedConfirmationUrlParams(str);
        }

        @Override // android.webkit.WebViewClient
        public void onReceivedError(WebView webView, int i, String str, String str2) {
            String str3 = "onReceivedError: " + str + " : " + str2;
            if (SteamWebView.this.getContext() == null) {
                return;
            }
            SteamWebView.this.setViewContentsShowFailure(str2, str);
        }

        @Override // android.webkit.WebViewClient
        public void onReceivedSslError(WebView webView, final SslErrorHandler sslErrorHandler, SslError sslError) {
            Certificate certificate;
            Certificate certificate2;
            Certificate certificate3;
            String str = "onReceivedSslError: " + sslError.toString();
            if (SteamWebView.m_arrayConsoleLog.size() < 100) {
                SteamWebView.m_arrayConsoleLog.add(sslError.toString());
            }
            if (Config.STEAM_UNIVERSE_WEBPHP != Config.SteamUniverse.Public) {
                sslErrorHandler.proceed();
                return;
            }
            if (sslError.getCertificate().getIssuedBy().getOName().equals("Symantec Corporation")) {
                try {
                    CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                    InputStream openRawResource = SteamWebView.this.getResources().openRawResource(R.raw.a248eakamainet);
                    certificate = certificateFactory.generateCertificate(openRawResource);
                    openRawResource.close();
                } catch (Exception unused) {
                    certificate = null;
                }
                try {
                    CertificateFactory certificateFactory2 = CertificateFactory.getInstance("X.509");
                    InputStream openRawResource2 = SteamWebView.this.getResources().openRawResource(R.raw.symantec);
                    certificate2 = certificateFactory2.generateCertificate(openRawResource2);
                    openRawResource2.close();
                } catch (Exception unused2) {
                    certificate2 = null;
                }
                byte[] byteArray = SslCertificate.saveState(sslError.getCertificate()).getByteArray("x509-certificate");
                if (byteArray != null) {
                    try {
                        certificate3 = CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(byteArray));
                    } catch (CertificateException unused3) {
                        certificate3 = null;
                    }
                    if (certificate3 != null) {
                        if (certificate != null && certificate3.getPublicKey().equals(certificate.getPublicKey())) {
                            sslErrorHandler.proceed();
                            SteamWebView.m_arrayConsoleLog.add("Matched public key exactly, proceeding.");
                            return;
                        }
                        if (certificate2 != null) {
                            try {
                                certificate3.verify(certificate2.getPublicKey());
                                sslErrorHandler.proceed();
                                SteamWebView.m_arrayConsoleLog.add("Signed by Symantec, proceeding.");
                                return;
                            } catch (Exception e) {
                                if (SteamWebView.m_arrayConsoleLog.size() < 100) {
                                    SteamWebView.m_arrayConsoleLog.add("Caught exception: " + e.toString());
                                }
                                super.onReceivedSslError(webView, sslErrorHandler, sslError);
                                return;
                            }
                        }
                    }
                }
            } else if (SteamWebView.m_arrayConsoleLog.size() < 100) {
                SteamWebView.m_arrayConsoleLog.add("Not Symantec. Not proceeding.");
            }
            SettingInfo settingInfo = SteamCommunityApplication.GetInstance().GetSettingInfoDB().m_settingSslUntrustedPrompt;
            if (settingInfo.m_access != SettingInfo.AccessRight.NONE) {
                if (settingInfo.getRadioSelectorItemValue(SteamCommunityApplication.GetInstance().getApplicationContext()).value == -1) {
                    sslErrorHandler.proceed();
                    return;
                } else {
                    new SettingsFragment.RadioSelectorItemOnClickListener(SteamWebView.this.getActivity(), settingInfo, null) { // from class: com.valvesoftware.android.steam.community.views.SteamWebView.SteamWebViewClient.3
                        private SslErrorHandler m_hdlrDelayed;

                        {
                            this.m_hdlrDelayed = sslErrorHandler;
                        }

                        @Override // com.valvesoftware.android.steam.community.fragment.SettingsFragment.RadioSelectorItemOnClickListener
                        public void onSettingChanged(SettingInfo.RadioSelectorItem radioSelectorItem) {
                            if (radioSelectorItem.value != 1) {
                                this.m_hdlrDelayed.proceed();
                            } else {
                                this.m_hdlrDelayed.cancel();
                            }
                        }
                    }.onClick(null);
                    return;
                }
            }
            sslErrorHandler.cancel();
        }

        @Override // android.webkit.WebViewClient
        public void onPageStarted(WebView webView, String str, Bitmap bitmap) {
            SteamWebView.this.setBlackBackground();
            if (str.startsWith(Config.URL_COMMUNITY_BASE + "/mobileconf/")) {
                ((NotificationManager) SteamWebView.this.getContext().getSystemService("notification")).cancel(4);
            }
        }

        @Override // android.webkit.WebViewClient
        public void onPageFinished(WebView webView, String str) {
            this.m_backgroundHandler.postDelayed(new Runnable() { // from class: com.valvesoftware.android.steam.community.views.SteamWebView.SteamWebViewClient.4
                @Override // java.lang.Runnable
                public void run() {
                    SteamWebView.this.setBackgroundColor(-1);
                }
            }, 500L);
            SteamWebView.this.hideProgressIndicator();
            if (SteamWebView.this.isLoginPage() && SteamWebView.m_arrayConsoleLog.size() > 0) {
                String str2 = "Console messages (" + SteamWebView.m_arrayConsoleLog.size() + "): ";
                Iterator<String> it = SteamWebView.m_arrayConsoleLog.iterator();
                while (it.hasNext()) {
                    str2 = str2 + it.next() + "\n";
                }
                if (str2.length() > 65535) {
                    str2 = str2.substring(0, 65535);
                }
                try {
                    new ReportErrorTask().execute(str2);
                } catch (Exception e) {
                    String str3 = "Failed to report console messages: " + e.toString();
                }
                String str4 = "Page finished with " + SteamWebView.m_arrayConsoleLog.size() + " console messages.";
            }
            SteamWebView.m_arrayConsoleLog.clear();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SteamWebChromeClient extends WebChromeClient {
        private SteamWebChromeClient() {
        }

        @Override // android.webkit.WebChromeClient
        public void onProgressChanged(WebView webView, int i) {
            super.onProgressChanged(webView, i);
            String str = "onProgressChanged: " + i;
            if (i > 70) {
                SteamWebView.this.requestFocusWrapper();
            }
            if (i > 99) {
                SteamWebView.this.hideProgressIndicator();
            }
        }

        @Override // android.webkit.WebChromeClient
        public boolean onJsAlert(WebView webView, String str, String str2, JsResult jsResult) {
            return super.onJsAlert(webView, str, str2, jsResult);
        }

        @Override // android.webkit.WebChromeClient
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            if (SteamWebView.m_arrayConsoleLog.size() < 100) {
                SteamWebView.m_arrayConsoleLog.add(consoleMessage.message());
            }
            consoleMessage.message();
            return true;
        }

        public void openFileChooser(ValueCallback<Uri> valueCallback) {
            openFileChooser(valueCallback, null, null);
        }

        public void openFileChooser(ValueCallback<Uri> valueCallback, String str) {
            openFileChooser(valueCallback, str, null);
        }

        public void openFileChooser(ValueCallback<Uri> valueCallback, String str, String str2) {
            if (SteamWebView.m_FilePathCallback != null) {
                SteamWebView.m_FilePathCallback.onReceiveValue(null);
            }
            SteamWebView.m_FilePathCallback = valueCallback;
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.addCategory("android.intent.category.OPENABLE");
            intent.setType("image/*");
            try {
                SteamWebView.this.getActivity().startActivityForResult(Intent.createChooser(intent, "File Chooser"), 1001);
            } catch (Exception unused) {
            }
        }

        @Override // android.webkit.WebChromeClient
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback, WebChromeClient.FileChooserParams fileChooserParams) {
            if (Build.VERSION.SDK_INT >= 21) {
                if (SteamWebView.m_MultiFilePathCallback != null) {
                    SteamWebView.m_MultiFilePathCallback.onReceiveValue(null);
                }
                SteamWebView.m_MultiFilePathCallback = valueCallback;
                try {
                    SteamWebView.this.getActivity().startActivityForResult(Build.VERSION.SDK_INT >= 21 ? fileChooserParams.createIntent() : null, 1002);
                    return true;
                } catch (Exception unused) {
                    return false;
                }
            }
            return super.onShowFileChooser(webView, valueCallback, fileChooserParams);
        }
    }

    @Override // android.webkit.WebView
    public void loadUrl(String str) {
        if (str == null) {
            return;
        }
        String str2 = "loadUrl: " + str;
        if (str.startsWith("javascript:")) {
            super.loadUrl(str);
            return;
        }
        Object obj = this.owner;
        if (obj == null || ActivityHelper.fragmentOrActivityIsActive(obj)) {
            requestFocusWrapper();
            if (getActivity() instanceof PaypalUriActivity) {
                ((PaypalUriActivity) getActivity()).SetBrowserBarLocation(str);
            }
            super.loadUrl(str);
            if (str != null) {
                showProgressIndicator();
            }
        }
    }

    public void setViewContentsShowFailure(String str, String str2) {
        loadData("<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head><body bgcolor=\"#181818\" text=\"#D6D6D6\" link=\"#FFFFFF\" alink=\"#FFFFFF\" vlink=\"#FFFFFF\"><br/><h2 align=\"center\">" + SteamCommunityApplication.GetInstance().getString(R.string.Web_Error_Title) + "</h2><p align=\"left\">" + NetErrorTranslator.translateError(str2) + "</p><p align=\"left\"><small>" + str2 + "</small></p><p align=\"center\"><a href=\"" + str + "\">" + SteamCommunityApplication.GetInstance().getString(R.string.Web_Error_Retry_Now) + "</a></p></body></html>", "text/html; charset=UTF-8", "utf-8");
    }

    public static String extractUrlFromOpenUrlUri(Uri uri) {
        String uri2 = uri.toString();
        if (uri2.indexOf("openurl?url=") != -1) {
            return uri2.substring(uri2.indexOf("openurl?url=") + 12);
        }
        return null;
    }

    protected void showProgressIndicator() {
        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).showProgressIndicator();
        }
    }

    protected void hideProgressIndicator() {
        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).hideProgressIndicator();
        }
    }
}

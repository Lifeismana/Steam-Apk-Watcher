package com.valvesoftware.android.steam.community;

import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import com.valvesoftware.android.steam.community.SettingInfo;
import com.valvesoftware.android.steam.community.views.SteamWebView;
import com.valvesoftware.android.steam.community.webrequests.Endpoints;
import com.valvesoftware.android.steam.community.webrequests.JsonRequestBuilder;
import com.valvesoftware.android.steam.community.webrequests.RequestErrorInfo;
import com.valvesoftware.android.steam.community.webrequests.ResponseListener;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class LoggedInUserAccountInfo {
    private static boolean dontLoginToChat = false;
    private static LoginInformation loginInformation = new LoginInformation();
    private static final Map<String, Map<String, CWebCookie>> s_cookiesConfiguration = new HashMap();
    private static boolean s_bCookiesAreOutOfDate = false;
    private static String s_strLanguage = "";
    private static long s_lLastWGTokenFailureMS = 0;

    /* loaded from: classes.dex */
    public enum LoginState {
        RequireUsernameAndPassword,
        RequireSteamGuardEmailToken,
        LoggedIn
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class CWebCookie {
        public boolean m_httponly;
        public boolean m_secure;
        public String m_value;

        CWebCookie(String str, boolean z, boolean z2) {
            this.m_secure = false;
            this.m_httponly = false;
            this.m_value = str;
            this.m_secure = z;
            this.m_httponly = z2;
        }

        public boolean equals(CWebCookie cWebCookie) {
            String str = this.m_value;
            if (str == null) {
                if (cWebCookie.m_value != null) {
                    return false;
                }
            } else if (!str.equals(cWebCookie.m_value)) {
                return false;
            }
            return this.m_secure == cWebCookie.m_secure && this.m_httponly == cWebCookie.m_httponly;
        }
    }

    /* loaded from: classes.dex */
    public static class LoginInformation {
        public String accessToken;
        public LoginState loginState;
        public String steamId;
        public String wgtoken;
        public String wgtokenSecure;

        public LoginInformation() {
            LogOut();
        }

        public LoginInformation(JSONObject jSONObject) {
            DeserializeFromJSONDoc(jSONObject);
        }

        void LogOut() {
            this.loginState = LoginState.RequireUsernameAndPassword;
            this.steamId = null;
            this.accessToken = null;
            this.wgtoken = null;
            this.wgtokenSecure = null;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void SerializeToJSONDoc(JSONObject jSONObject) throws JSONException {
            if (this.loginState == LoginState.LoggedIn) {
                jSONObject.put("access_token", this.accessToken);
                jSONObject.put("x_steamid", this.steamId);
                jSONObject.put("wgtoken", this.wgtoken);
                jSONObject.put("wgtoken_secure", this.wgtokenSecure);
            }
        }

        void DeserializeFromJSONDoc(JSONObject jSONObject) {
            LogOut();
            this.accessToken = jSONObject.optString("access_token");
            this.steamId = jSONObject.optString("x_steamid");
            this.wgtoken = jSONObject.optString("wgtoken");
            this.wgtokenSecure = jSONObject.optString("wgtoken_secure");
            String str = this.accessToken;
            if (str == null || str.length() == 0) {
                return;
            }
            this.loginState = LoginState.LoggedIn;
        }
    }

    public static boolean isLoggedIn() {
        return loginInformation.loginState == LoginState.LoggedIn;
    }

    public static String getLoginSteamID() {
        return loginInformation.steamId;
    }

    public static String getAccessToken() {
        return loginInformation.accessToken;
    }

    public static void logOut() {
        SteamCommunityApplication.GetInstance().GetDiskCacheIndefinite().Delete("login.json");
        loginInformation.LogOut();
        setLoginInformation(loginInformation);
    }

    public static LoginInformation getLoginInformation() {
        return loginInformation;
    }

    public static void setLoginInformation(LoginInformation loginInformation2) {
        loginInformation = loginInformation2;
        resetAllCookies();
        setCookie2("steamLogin", loginInformation.steamId + "||" + loginInformation.wgtoken, false, true);
        setCookie2("steamLoginSecure", loginInformation.steamId + "||" + loginInformation.wgtokenSecure, true, true);
        SettingInfo settingInfo = SteamCommunityApplication.GetInstance().GetSettingInfoDB().m_settingDOB;
        String str = null;
        String value = settingInfo == null ? null : settingInfo.getValue(SteamCommunityApplication.GetInstance().getApplicationContext());
        if (value != null && !value.equals("")) {
            str = SettingInfo.DateConverter.makeUnixTime(value);
        }
        setCookie2("dob", str);
        syncAllCookies();
    }

    public static void updateWGToken(String str, String str2) {
        LoginInformation loginInformation2 = loginInformation;
        loginInformation2.wgtoken = str;
        loginInformation2.wgtokenSecure = str2;
        setCookie2("steamLogin", loginInformation.steamId + "||" + loginInformation.wgtoken, false, true);
        setCookie2("steamLoginSecure", loginInformation.steamId + "||" + loginInformation.wgtokenSecure, true, true);
        SteamCommunityApplication.GetInstance().UpdateCachedLoginInformation();
        s_bCookiesAreOutOfDate = true;
        syncAllCookies();
    }

    public static void setCookie2(String str, String str2) {
        setCookie2(str, str2, false, false);
    }

    public static void setCookie2(String str, String str2, boolean z, boolean z2) {
        String str3 = Config.URL_COMMUNITY_BASE;
        int indexOf = str3.indexOf("://", 0) + 3;
        int indexOf2 = str3.indexOf("/", indexOf);
        if (indexOf2 >= 0) {
            str3 = str3.substring(0, indexOf2);
        }
        str3.substring(indexOf);
        if (!s_cookiesConfiguration.containsKey("")) {
            s_cookiesConfiguration.put("", new HashMap());
        }
        CWebCookie cWebCookie = new CWebCookie(str2, z, z2);
        Map<String, CWebCookie> map = s_cookiesConfiguration.get("");
        if (!map.containsKey(str)) {
            if (str2 != null) {
                s_bCookiesAreOutOfDate = true;
            }
            map.put(str, cWebCookie);
            return;
        }
        if (str2 == null) {
            if (map.get(str) != null) {
                s_bCookiesAreOutOfDate = true;
            }
        } else {
            CWebCookie cWebCookie2 = map.get(str);
            if (cWebCookie2 == null || !cWebCookie.equals(cWebCookie2)) {
                s_bCookiesAreOutOfDate = true;
            }
        }
        map.remove(str);
        map.put(str, cWebCookie);
    }

    public static void resetAllCookies() {
        s_cookiesConfiguration.clear();
        setCookie2("mobileClient", "android");
        setCookie2("mobileClientVersion", "" + Config.APP_VERSION_ID + " (" + Config.APP_VERSION + ")");
        s_strLanguage = SteamCommunityApplication.GetInstance().getString(R.string.DO_NOT_LOCALIZE_COOKIE_Steam_Language);
        setCookie2("Steam_Language", s_strLanguage);
    }

    public static String getLanguage() {
        return s_strLanguage;
    }

    public static void updateLanguage() {
        String string = SteamCommunityApplication.GetInstance().getString(R.string.DO_NOT_LOCALIZE_COOKIE_Steam_Language);
        if (s_strLanguage.equals(string)) {
            return;
        }
        s_strLanguage = string;
        setCookie2("Steam_Language", string);
        syncAllCookies();
    }

    public static void syncAllCookies() {
        if (s_bCookiesAreOutOfDate) {
            s_bCookiesAreOutOfDate = false;
            CookieManager cookieManager = CookieManager.getInstance();
            Iterator<Map.Entry<String, Map<String, CWebCookie>>> it = s_cookiesConfiguration.entrySet().iterator();
            while (it.hasNext()) {
                for (Map.Entry<String, CWebCookie> entry : it.next().getValue().entrySet()) {
                    CWebCookie value = entry.getValue();
                    try {
                        String encode = value.m_value != null ? URLEncoder.encode(value.m_value, "ISO-8859-1") : "";
                        StringBuilder sb = new StringBuilder();
                        sb.append(entry.getKey());
                        sb.append("=");
                        sb.append(encode);
                        if (value.m_secure) {
                            sb.append("; secure");
                        }
                        if (value.m_httponly) {
                            sb.append("; HttpOnly");
                        }
                        for (String str : Config.WG_AUTH_DOMAINS) {
                            cookieManager.setCookie(str, sb.toString());
                        }
                    } catch (UnsupportedEncodingException unused) {
                    }
                }
            }
            CookieSyncManager.getInstance().sync();
        }
    }

    public static boolean dontLoginToChat() {
        return dontLoginToChat;
    }

    public static void setDontLoginToChat(boolean z) {
        if (z != dontLoginToChat) {
            dontLoginToChat = z;
        }
    }

    public static void reacquireWGTokenFromServer(final SteamWebView steamWebView) {
        if (isLoggedIn()) {
            long j = s_lLastWGTokenFailureMS;
            if (j <= 0 || j + 5000 <= System.currentTimeMillis()) {
                s_lLastWGTokenFailureMS = System.currentTimeMillis();
                JsonRequestBuilder wGToken = Endpoints.getWGToken();
                wGToken.setResponseListener(new ResponseListener() { // from class: com.valvesoftware.android.steam.community.LoggedInUserAccountInfo.1
                    @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
                    public void onError(RequestErrorInfo requestErrorInfo) {
                    }

                    @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
                    public void onSuccess(JSONObject jSONObject) {
                        String optString = jSONObject.optString("token", "");
                        String optString2 = jSONObject.optString("token_secure", "");
                        if (optString.length() == 0 || optString2.length() == 0) {
                            LoggedInUserAccountInfo.logOut();
                            long unused = LoggedInUserAccountInfo.s_lLastWGTokenFailureMS = 0L;
                        } else {
                            LoggedInUserAccountInfo.updateWGToken(optString, optString2);
                            long unused2 = LoggedInUserAccountInfo.s_lLastWGTokenFailureMS = System.currentTimeMillis();
                            SteamWebView.this.reload();
                        }
                    }
                });
                SteamCommunityApplication.GetInstance().sendRequest(wGToken);
            }
        }
    }
}

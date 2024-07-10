package com.valvesoftware.android.steam.community.activity;

import android.app.NotificationManager;
import android.content.ComponentCallbacks;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import com.valvesoftware.android.steam.community.GcmRegistrar;
import com.valvesoftware.android.steam.community.LoggedInUserAccountInfo;
import com.valvesoftware.android.steam.community.R;
import com.valvesoftware.android.steam.community.SettingInfoDB;
import com.valvesoftware.android.steam.community.SteamAppIntents;
import com.valvesoftware.android.steam.community.SteamAppUri;
import com.valvesoftware.android.steam.community.SteamCommunityApplication;
import com.valvesoftware.android.steam.community.SteamUriHandler;
import com.valvesoftware.android.steam.community.SteamguardState;
import com.valvesoftware.android.steam.community.UmqCommunicator;
import com.valvesoftware.android.steam.community.fragment.ChatFragment;
import com.valvesoftware.android.steam.community.fragment.FriendSearchFragment;
import com.valvesoftware.android.steam.community.fragment.FriendsListFragment;
import com.valvesoftware.android.steam.community.fragment.GroupListFragment;
import com.valvesoftware.android.steam.community.fragment.GroupSearchFragment;
import com.valvesoftware.android.steam.community.fragment.IBackButtonSupport;
import com.valvesoftware.android.steam.community.fragment.LoginFragment;
import com.valvesoftware.android.steam.community.fragment.SettingsFragment;
import com.valvesoftware.android.steam.community.fragment.SteamguardFragmentWeb;
import com.valvesoftware.android.steam.community.fragment.TabbedSteamWebViewFragment;
import com.valvesoftware.android.steam.community.fragment.WebViewFragment;
import com.valvesoftware.android.steam.community.model.Persona;
import com.valvesoftware.android.steam.community.views.SteamWebView;
import com.valvesoftware.android.steam.community.webrequests.Endpoints;
import com.valvesoftware.android.steam.community.webrequests.JsonRequestBuilder;
import com.valvesoftware.android.steam.community.webrequests.RequestErrorInfo;
import com.valvesoftware.android.steam.community.webrequests.ResponseListener;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class MainActivity extends BaseActivity {
    private long lastLoginFragmentLoadTime = 0;
    private final long loginFragmentRefreshDelay = 120000;
    public final SteamData steamData = new SteamData();
    private SteamWebView steamWebViewClient;

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onBackPressed() {
        if (closeDrawer()) {
            return;
        }
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
            return;
        }
        clearTitleLabel();
        hideSearchBar();
        clearRefreshButtonListener();
        clearSearchButtonListener();
        clearExtraMenuItems();
        super.onBackPressed();
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onNewIntent(Intent intent) {
        String str = "onNewIntent: " + intent.getAction();
        if (!intent.hasCategory("android.intent.category.LAUNCHER") || getIntent() == null) {
            if (!isSpecialNonLoggedInUri(intent) && !LoggedInUserAccountInfo.isLoggedIn()) {
                loadLoginFragment(intent);
                return;
            }
            super.onNewIntent(intent);
            setIntent(intent);
            doIntent(intent);
        }
    }

    private void loadLoginFragment() {
        loadLoginFragment(null);
    }

    private void loadLoginFragment(final Intent intent) {
        String str = "loadLoginFragment: " + getCurrentBackStackEntryName() + " " + this.lastLoginFragmentLoadTime;
        if (!getCurrentBackStackEntryName().equals(LoginFragment.class.getSimpleName()) || System.currentTimeMillis() - this.lastLoginFragmentLoadTime >= 120000) {
            new GcmRegistrar().unregister(getApplicationContext());
            getSupportFragmentManager().popBackStack((String) null, 1);
            this.lastLoginFragmentLoadTime = System.currentTimeMillis();
            hideMenuAndActionBar();
            LoginFragment loginFragment = new LoginFragment();
            loginFragment.setLoginChangedListener(new LoginChangedListener() { // from class: com.valvesoftware.android.steam.community.activity.MainActivity.1
                @Override // com.valvesoftware.android.steam.community.activity.LoginChangedListener
                public void onLoginChangedSuccessfully() {
                    new GcmRegistrar().registerWithGcm();
                    MainActivity.this.userNotificationCounts.Clear();
                    MainActivity.this.refreshUserNotificationCounts();
                    MainActivity.this.refreshNavDrawer();
                    MainActivity.this.getSupportFragmentManager().popBackStack();
                    MainActivity.this.showMenuAndActionBar();
                    UmqCommunicator.getInstance().start();
                    Intent intent2 = intent;
                    if (intent2 == null) {
                        MainActivity.this.loadUserDefaultFragment();
                    } else {
                        MainActivity.this.onNewIntent(intent2);
                    }
                }
            });
            loadFragment(loginFragment, LoginFragment.class.getSimpleName(), true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshUserNotificationCounts() {
        JsonRequestBuilder userNotificationCounts = Endpoints.getUserNotificationCounts();
        userNotificationCounts.setResponseListener(new ResponseListener() { // from class: com.valvesoftware.android.steam.community.activity.MainActivity.2
            @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
            public void onError(RequestErrorInfo requestErrorInfo) {
            }

            @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
            public void onSuccess(JSONObject jSONObject) {
                MainActivity.this.userNotificationCounts.Clear();
                JSONArray optJSONArray = jSONObject.optJSONArray("notifications");
                if (optJSONArray != null) {
                    for (int i = 0; i < optJSONArray.length(); i++) {
                        JSONObject optJSONObject = optJSONArray.optJSONObject(i);
                        if (optJSONObject != null) {
                            MainActivity.this.userNotificationCounts.SetNotificationCount(optJSONObject.optInt("user_notification_type", 0), optJSONObject.optInt("count", 0));
                        }
                    }
                }
                MainActivity.this.onNotificationCountsChanged();
            }
        });
        SteamCommunityApplication.GetInstance().sendRequest(userNotificationCounts);
    }

    private boolean isSpecialNonLoggedInUri(Intent intent) {
        Uri data;
        if (intent == null || (data = intent.getData()) == null || data.getHost() == null || !data.getHost().equals("openurl") || data.getQuery() == null) {
            return false;
        }
        return data.getQuery().indexOf(SteamAppUri.steamHelpUriPrefix()) == 4 || data.getQuery().indexOf(SteamAppUri.steamSubscriberAgreementUriPrefix()) == 4 || data.getQuery().indexOf(SteamAppUri.steamPrivacyPolicyUriPrefix()) == 4;
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        SteamCommunityApplication.switchingToForeground(this);
    }

    @Override // android.support.v4.app.FragmentActivity
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (!isSpecialNonLoggedInUri(getIntent()) && !LoggedInUserAccountInfo.isLoggedIn()) {
            loadLoginFragment();
            return;
        }
        if (this.loggedInUser == null) {
            refreshNavDrawer();
        }
        SteamCommunityApplication.GetInstance().getLocalDb().clearNotifications();
        if (LoggedInUserAccountInfo.isLoggedIn()) {
            refreshUserNotificationCounts();
        }
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            if (getIntent() != null) {
                doIntent(getIntent());
            } else {
                loadUserDefaultFragment();
            }
        }
        UmqCommunicator umqCommunicator = UmqCommunicator.getInstance();
        if (LoggedInUserAccountInfo.dontLoginToChat() || !LoggedInUserAccountInfo.isLoggedIn()) {
            return;
        }
        umqCommunicator.start();
    }

    private String getCurrentBackStackEntryName() {
        return getSupportFragmentManager().getBackStackEntryCount() > 0 ? getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName() : "";
    }

    private void loadFragment(Fragment fragment, String str) {
        loadFragment(fragment, str, false);
    }

    private void loadFragment(Fragment fragment, String str, boolean z) {
        String str2 = "loadFragment: " + str;
        if (getSupportFragmentManager().getBackStackEntryCount() > 0 && !z) {
            FragmentManager.BackStackEntry backStackEntryAt = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1);
            String str3 = "loadFragment has backstack " + backStackEntryAt.getName();
            if (str != null && str.equals(backStackEntryAt.getName())) {
                return;
            }
        }
        clearTitleLabel();
        hideProgressIndicator(0);
        hideSearchBar();
        clearRefreshButtonListener();
        clearSearchButtonListener();
        clearExtraMenuItems();
        invalidateOptionsMenu();
        FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
        beginTransaction.replace(R.id.fragment_placeholder, fragment);
        beginTransaction.addToBackStack(str);
        beginTransaction.commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    private void doIntent(Intent intent) {
        hideKeyboard();
        Uri data = intent.getData();
        if (data == null) {
            loadUserDefaultFragment();
            return;
        }
        String host = data.getHost();
        String str = "doIntent: " + host;
        if (host == null) {
            loadUserDefaultFragment();
            return;
        }
        if (host.equals("appsettings")) {
            loadSettingsFragment();
            return;
        }
        if (host.equals("login")) {
            loadLoginFragment();
            return;
        }
        if (host.equals("deletenotification")) {
            ((NotificationManager) SteamCommunityApplication.GetInstance().getSystemService("notification")).cancel(intent.getIntExtra(SteamAppIntents.notificationId, 0));
            return;
        }
        if (host.equals("friends")) {
            String queryParameter = data.getQueryParameter("search");
            if (queryParameter == null) {
                loadFriendsFragment();
                return;
            } else {
                loadFriendsSearchFragment(queryParameter);
                return;
            }
        }
        if (host.equals("chat")) {
            String queryParameter2 = data.getQueryParameter("steamid");
            if (queryParameter2 != null) {
                loadChatFragment(queryParameter2, intent);
                return;
            }
            return;
        }
        if (host.equals("groups")) {
            String queryParameter3 = data.getQueryParameter("search");
            if (queryParameter3 == null) {
                loadGroupsFragment();
                return;
            } else {
                loadGroupsSearchFragment(queryParameter3);
                return;
            }
        }
        if (host.equals("openurl")) {
            String extractUrlFromOpenUrlUri = SteamWebView.extractUrlFromOpenUrlUri(data);
            if (extractUrlFromOpenUrlUri != null) {
                loadWebViewFragment(extractUrlFromOpenUrlUri);
                return;
            }
            return;
        }
        if (host.equals("opencategoryurl")) {
            String queryParameter4 = data.getQueryParameter("url");
            if (queryParameter4 != null) {
                loadTabbedWebViewFragment(queryParameter4);
                return;
            }
            return;
        }
        if (host.equals("steamguard")) {
            loadSteamGuardWebFragment(SteamAppUri.STEAMGUARD_PRECHANGE);
            return;
        }
        if (host.equals("steamguardweb")) {
            loadSteamGuardWebFragment();
            return;
        }
        if (host.equals("steamguardviewrcode")) {
            loadSteamGuardWebFragment(SteamAppUri.STEAMGUARD_RCODE);
            return;
        }
        if (host.equals("confirmation")) {
            SteamguardState steamguardStateForLoggedInUser = SteamguardState.steamguardStateForLoggedInUser();
            if (steamguardStateForLoggedInUser != null && steamguardStateForLoggedInUser.getTwoFactorToken() != null) {
                String confirmationUrl = steamguardStateForLoggedInUser.getConfirmationUrl();
                if (confirmationUrl != null) {
                    loadWebViewFragment(confirmationUrl);
                    return;
                }
                return;
            }
            loadWebViewFragment(String.format(Locale.US, "%s?p=%s&a=%s&m=android", SteamAppUri.CONFIRMATION_WEB, SteamguardState.getUniqueIdForPhone(), LoggedInUserAccountInfo.getLoginSteamID()));
            return;
        }
        loadUserDefaultFragment();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadUserDefaultFragment() {
        switch (SettingInfoDB.StartScreen.fromInt(SteamCommunityApplication.GetInstance().GetSettingInfoDB().m_startScreen.getIntegerValue(this))) {
            case Catalog:
                startActivity(SteamAppIntents.viewCatalog(this));
                return;
            case Groups:
                startActivity(SteamAppIntents.viewGroupsList(this));
                return;
            case SteamGuard:
                startActivity(SteamAppIntents.viewSteamGuard(this));
                return;
            case SteamNews:
                startActivity(SteamAppIntents.viewSteamNews(this));
                return;
            case FriendActivity:
                startActivity(SteamAppIntents.viewFriendActivity(this));
                return;
            case Library:
                startActivity(SteamAppIntents.viewLibrary(this));
                return;
            default:
                startActivity(SteamAppIntents.viewFriendsList(this));
                return;
        }
    }

    private void loadSettingsFragment() {
        loadFragment(new SettingsFragment(), SettingsFragment.class.getSimpleName());
    }

    private void loadSteamGuardWebFragment() {
        loadFragment(new SteamguardFragmentWeb(), SteamguardFragmentWeb.class.getSimpleName(), true);
    }

    private void loadSteamGuardWebFragment(String str) {
        SteamguardFragmentWeb steamguardFragmentWeb = new SteamguardFragmentWeb();
        Bundle bundle = new Bundle();
        bundle.putString("defaultUrl", str);
        steamguardFragmentWeb.setArguments(bundle);
        loadFragment(steamguardFragmentWeb, SteamguardFragmentWeb.class.getSimpleName(), true);
    }

    private void loadWebViewFragment(String str) {
        loadFragment(getWebViewFragment(str), WebViewFragment.class.getSimpleName() + str, true);
    }

    private void loadTabbedWebViewFragment(String str) {
        Bundle bundle = new Bundle();
        bundle.putString("category", str);
        TabbedSteamWebViewFragment tabbedSteamWebViewFragment = new TabbedSteamWebViewFragment();
        tabbedSteamWebViewFragment.setArguments(bundle);
        loadFragment(tabbedSteamWebViewFragment, WebViewFragment.class.getSimpleName() + str);
    }

    private WebViewFragment getWebViewFragment(String str) {
        Bundle bundle = new Bundle();
        bundle.putString("url", str);
        WebViewFragment webViewFragment = new WebViewFragment();
        webViewFragment.setArguments(bundle);
        return webViewFragment;
    }

    private void loadGroupsSearchFragment(String str) {
        loadFragment(getGroupsSearchFragment(str), GroupSearchFragment.class.getSimpleName() + str);
    }

    private void loadGroupsFragment() {
        loadFragment(new GroupListFragment(), GroupListFragment.class.getSimpleName());
    }

    private void loadFriendsSearchFragment(String str) {
        FriendSearchFragment friendSearchFragment = new FriendSearchFragment();
        Bundle bundle = new Bundle();
        bundle.putString("friends", str);
        friendSearchFragment.setArguments(bundle);
        loadFragment(friendSearchFragment, FriendSearchFragment.class.getSimpleName() + str);
    }

    private Fragment getGroupsSearchFragment(String str) {
        GroupSearchFragment groupSearchFragment = new GroupSearchFragment();
        Bundle bundle = new Bundle();
        bundle.putString("groups", str);
        groupSearchFragment.setArguments(bundle);
        return groupSearchFragment;
    }

    private void loadChatFragment(String str, Intent intent) {
        ChatFragment chatFragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putString("chatPartnerSteamIdKey", str);
        bundle.putString("chatPartnerAvatarUrl", intent.getStringExtra(SteamAppIntents.chatPartnerAvatarUrlKey));
        bundle.putString("chatPartnerPersonaNameKey", intent.getStringExtra(SteamAppIntents.chatPartnerPersonaNameKey));
        bundle.putString("loggedInUserAvatarUrl", this.loggedInUser != null ? this.loggedInUser.mediumAvatarUrl : null);
        chatFragment.setArguments(bundle);
        loadFragment(chatFragment, ChatFragment.class.getSimpleName() + str);
    }

    private void loadFriendsFragment() {
        loadFragment(new FriendsListFragment(), FriendsListFragment.class.getSimpleName());
    }

    public void readyForPaypalComplete(SteamWebView steamWebView) {
        this.steamWebViewClient = steamWebView;
    }

    @Override // com.valvesoftware.android.steam.community.activity.BaseActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        String stringExtra;
        if (i == 1000) {
            if (intent != null && (stringExtra = intent.getStringExtra(SteamUriHandler.CommandProperty.call.toString())) != null && stringExtra.length() > 0) {
                this.steamWebViewClient.loadUrl("javascript:(function(){" + stringExtra + ";})()");
                return;
            }
        } else {
            if (i == 1001) {
                if (SteamWebView.m_FilePathCallback != null) {
                    SteamWebView.m_FilePathCallback.onReceiveValue((intent == null || i2 != -1) ? null : intent.getData());
                    SteamWebView.m_FilePathCallback = null;
                    return;
                }
                return;
            }
            if (i == 1002 && Build.VERSION.SDK_INT >= 21) {
                if (SteamWebView.m_MultiFilePathCallback != null) {
                    SteamWebView.m_MultiFilePathCallback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(i2, intent));
                    SteamWebView.m_MultiFilePathCallback = null;
                    return;
                }
                return;
            }
        }
        super.onActivityResult(i, i2, intent);
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onPause() {
        super.onPause();
        SteamCommunityApplication.switchingToBackground();
        UmqCommunicator.getInstance().stop();
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        ComponentCallbacks findFragmentById;
        if (i == 4 && (findFragmentById = getSupportFragmentManager().findFragmentById(R.id.fragment_placeholder)) != null && (findFragmentById instanceof IBackButtonSupport)) {
            IBackButtonSupport iBackButtonSupport = (IBackButtonSupport) findFragmentById;
            if (iBackButtonSupport.canGoBack()) {
                iBackButtonSupport.goBack();
                return true;
            }
        }
        return super.onKeyDown(i, keyEvent);
    }

    public boolean refreshConfirmationsPageIfActive() {
        Fragment findFragmentById = getSupportFragmentManager().findFragmentById(R.id.fragment_placeholder);
        if (findFragmentById == null || !(findFragmentById instanceof WebViewFragment)) {
            return false;
        }
        return ((WebViewFragment) findFragmentById).refreshConfirmationsPageIfActive();
    }

    /* loaded from: classes.dex */
    public static class SteamData {
        private final Map<String, Persona> steamIdToFriendsMap = new HashMap();

        public Map<String, Persona> getSteamIdToFriendsMap() {
            return this.steamIdToFriendsMap;
        }

        public void saveFriends(Map<String, Persona> map) {
            this.steamIdToFriendsMap.clear();
            this.steamIdToFriendsMap.putAll(map);
        }
    }
}

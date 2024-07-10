package com.valvesoftware.android.steam.community.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SearchViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.gms.security.ProviderInstaller;
import com.valvesoftware.android.steam.community.AndroidUtils;
import com.valvesoftware.android.steam.community.GcmRegistrar;
import com.valvesoftware.android.steam.community.LoggedInUserAccountInfo;
import com.valvesoftware.android.steam.community.NotificationCountUpdateListener;
import com.valvesoftware.android.steam.community.PersonaRepository;
import com.valvesoftware.android.steam.community.R;
import com.valvesoftware.android.steam.community.RepositoryCallback;
import com.valvesoftware.android.steam.community.SettingInfoDB;
import com.valvesoftware.android.steam.community.SteamAppIntents;
import com.valvesoftware.android.steam.community.SteamCommunityApplication;
import com.valvesoftware.android.steam.community.UmqCommunicator;
import com.valvesoftware.android.steam.community.fragment.NavDrawerItem;
import com.valvesoftware.android.steam.community.fragment.NavDrawerListAdapter;
import com.valvesoftware.android.steam.community.fragment.NavDrawerNotificationItem;
import com.valvesoftware.android.steam.community.fragment.SearchBarFragment;
import com.valvesoftware.android.steam.community.model.Persona;
import com.valvesoftware.android.steam.community.model.UserNotificationCounts;
import com.valvesoftware.android.steam.community.views.MenuBar;
import com.valvesoftware.android.steam.community.views.SteamMenuItem;
import com.valvesoftware.android.steam.community.webrequests.Endpoints;
import com.valvesoftware.android.steam.community.webrequests.ImageRequestBuilder;
import com.valvesoftware.android.steam.community.webrequests.ImageResponseListener;
import com.valvesoftware.android.steam.community.webrequests.RequestErrorInfo;
import com.valvesoftware.android.steam.community.webrequests.ResponseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.json.JSONObject;

/* loaded from: classes.dex */
public abstract class BaseActivity extends ActionBarActivity {
    private DrawerLayout drawerLayout;
    private ExpandableListView expandableListView;
    private SteamMenuItem extraMenuItem;
    protected Persona loggedInUser;
    private MenuBar menuBar;
    private NavDrawerListAdapter navDrawerListAdapter;
    private LinearLayout navigationHeadersLayout;
    private View progressView;
    private View.OnClickListener refreshClickListener;
    private MenuItem refreshItem;
    private TextWatcher searchTextWatcher;
    protected int activityLayoutId = R.layout.main_activity;
    private String limitedMenuBarSettingKey = "limitedMenuBarNeeded";
    private Handler signOutHandler = new Handler();
    protected final UserNotificationCounts userNotificationCounts = new UserNotificationCounts();
    private Boolean useLimitedMenuBar = null;
    List<Pair<UserNotificationCounts.EUserNotification, NavDrawerNotificationItem>> m_listNotificationNavItems = new ArrayList();

    /* JADX INFO: Access modifiers changed from: private */
    public boolean useLimitedMenuBar() {
        if (this.useLimitedMenuBar == null) {
            this.useLimitedMenuBar = Boolean.valueOf(getSharedPreferences(BaseActivity.class.getSimpleName(), 0).getBoolean(this.limitedMenuBarSettingKey, false));
        }
        return this.useLimitedMenuBar.booleanValue();
    }

    private void setUseLimitedMenuBar(boolean z) {
        SharedPreferences.Editor edit = getSharedPreferences(BaseActivity.class.getSimpleName(), 0).edit();
        edit.putBoolean(this.limitedMenuBarSettingKey, z);
        edit.commit();
        this.useLimitedMenuBar = Boolean.valueOf(z);
    }

    private void installSslUpdate() {
        try {
            ProviderInstaller.installIfNeeded(getApplicationContext());
        } catch (Exception e) {
            e.toString();
        }
    }

    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Configuration configuration = getResources().getConfiguration();
        if (configuration.locale == null) {
            configuration.locale = Locale.getDefault();
        }
        if (!isTaskRoot()) {
            finish();
            return;
        }
        installSslUpdate();
        UmqCommunicator.getInstance().setNotificationCountUpdateListener(new NotificationCountUpdateListener() { // from class: com.valvesoftware.android.steam.community.activity.BaseActivity.1
            @Override // com.valvesoftware.android.steam.community.NotificationCountUpdateListener
            public void notificationCountsChanged(UserNotificationCounts userNotificationCounts) {
                BaseActivity.this.userNotificationCounts.UpdateNotificationCounts(userNotificationCounts);
                BaseActivity.this.onNotificationCountsChanged();
            }
        });
        LoggedInUserAccountInfo.updateLanguage();
        setContentView(this.activityLayoutId);
        this.navigationHeadersLayout = new LinearLayout(this);
        this.navigationHeadersLayout.setOrientation(1);
        setupView();
        if (LoggedInUserAccountInfo.getLoginSteamID() == null || LoggedInUserAccountInfo.getLoginSteamID().length() <= 0) {
            return;
        }
        new GcmRegistrar().registerWithGcm();
    }

    @SuppressLint({"NewApi"})
    protected void setupView() {
        this.drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        this.expandableListView = (ExpandableListView) findViewById(R.id.navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.steam_toolbar);
        this.menuBar = (MenuBar) findViewById(R.id.menu_bar);
        this.progressView = ((LayoutInflater) getSystemService("layout_inflater")).inflate(R.layout.progress_layout, (ViewGroup) null);
        ((ProgressBar) this.progressView.findViewById(R.id.progressBar)).getIndeterminateDrawable().setColorFilter(R.color.holo_gray_light, PorterDuff.Mode.DST);
        if (useLimitedMenuBar()) {
            toolbar.setVisibility(8);
            this.menuBar.setVisibility(0);
            this.menuBar.setHamburgerClickedListener(new View.OnClickListener() { // from class: com.valvesoftware.android.steam.community.activity.BaseActivity.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    BaseActivity.this.drawerLayout.openDrawer(8388611);
                    BaseActivity.this.closeKeyboard();
                }
            });
        } else {
            try {
                setSupportActionBar(toolbar);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar();
                ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, this.drawerLayout, toolbar, R.string.app_name, R.string.app_name) { // from class: com.valvesoftware.android.steam.community.activity.BaseActivity.3
                    @Override // android.support.v7.app.ActionBarDrawerToggle, android.support.v4.widget.DrawerLayout.DrawerListener
                    public void onDrawerOpened(View view) {
                        super.onDrawerOpened(view);
                        BaseActivity.this.closeKeyboard();
                    }

                    @Override // android.support.v7.app.ActionBarDrawerToggle, android.support.v4.widget.DrawerLayout.DrawerListener
                    public void onDrawerSlide(View view, float f) {
                        super.onDrawerSlide(view, 0.0f);
                    }
                };
                this.drawerLayout.setDrawerListener(actionBarDrawerToggle);
                actionBarDrawerToggle.syncState();
            } catch (Throwable th) {
                if (Build.VERSION.SDK_INT > 15) {
                    setUseLimitedMenuBar(true);
                    recreate();
                    return;
                }
                throw new RuntimeException(th);
            }
        }
        setupNavDrawer();
        View findViewById = findViewById(R.id.nav_header_view);
        if (findViewById != null) {
            findViewById.setOnClickListener(new View.OnClickListener() { // from class: com.valvesoftware.android.steam.community.activity.BaseActivity.4
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    if (BaseActivity.this.drawerLayout.isDrawerOpen(BaseActivity.this.expandableListView)) {
                        BaseActivity baseActivity = BaseActivity.this;
                        baseActivity.startActivity(SteamAppIntents.visitProfileIntent(baseActivity, LoggedInUserAccountInfo.getLoginSteamID()));
                        BaseActivity.this.drawerLayout.closeDrawers();
                    }
                }
            });
        }
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.class.getSimpleName(), 0);
        if (sharedPreferences.getBoolean("NavigationDrawerShown", false)) {
            return;
        }
        this.drawerLayout.openDrawer(8388611);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean("NavigationDrawerShown", true);
        edit.commit();
    }

    @Override // android.app.Activity
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (useLimitedMenuBar()) {
            TextWatcher textWatcher = this.searchTextWatcher;
            if (textWatcher != null) {
                setupSearchBarForLimitedMenuBar(textWatcher);
            } else {
                this.menuBar.setSearchClickedListener(null);
            }
            this.menuBar.setRefreshClickedListener(this.refreshClickListener);
            SteamMenuItem steamMenuItem = this.extraMenuItem;
            if (steamMenuItem != null) {
                this.menuBar.setExtraMenuItem(steamMenuItem);
            }
            return true;
        }
        menu.clear();
        if (this.searchTextWatcher != null) {
            MenuItem add = menu.add("");
            add.setIcon(R.drawable.ic_action_search);
            MenuItemCompat.setShowAsAction(add, 2);
            View newSearchView = SearchViewCompat.newSearchView(this);
            if (newSearchView != null) {
                View findViewById = newSearchView.findViewById(getResources().getIdentifier("android:id/search_button", null, null));
                if (findViewById != null && (findViewById instanceof ImageView)) {
                    ((ImageView) findViewById).setImageResource(R.drawable.ic_action_search);
                }
                SearchViewCompat.setOnQueryTextListener(newSearchView, new SearchViewCompat.OnQueryTextListenerCompat() { // from class: com.valvesoftware.android.steam.community.activity.BaseActivity.5
                    @Override // android.support.v4.widget.SearchViewCompat.OnQueryTextListenerCompat, android.support.v4.widget.SearchViewCompat.OnQueryTextListener
                    public boolean onQueryTextChange(String str) {
                        if (BaseActivity.this.searchTextWatcher == null) {
                            return true;
                        }
                        if (str == null) {
                            str = "";
                        }
                        BaseActivity.this.searchTextWatcher.onTextChanged(str, 0, 0, str.length());
                        return true;
                    }
                });
                SearchViewCompat.setOnCloseListener(newSearchView, new SearchViewCompat.OnCloseListenerCompat() { // from class: com.valvesoftware.android.steam.community.activity.BaseActivity.6
                    @Override // android.support.v4.widget.SearchViewCompat.OnCloseListenerCompat, android.support.v4.widget.SearchViewCompat.OnCloseListener
                    public boolean onClose() {
                        ActivityHelper.hideKeyboard(BaseActivity.this);
                        return false;
                    }
                });
                MenuItemCompat.setActionView(add, newSearchView);
            } else {
                final SearchBarFragment searchBar = getSearchBar();
                if (searchBar != null) {
                    add.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() { // from class: com.valvesoftware.android.steam.community.activity.BaseActivity.7
                        @Override // android.view.MenuItem.OnMenuItemClickListener
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            searchBar.setSearchTextChangedListener(BaseActivity.this.searchTextWatcher);
                            searchBar.openSearch();
                            return true;
                        }
                    });
                }
            }
        }
        if (this.refreshClickListener != null) {
            this.refreshItem = menu.add("");
            this.refreshItem.setIcon(R.drawable.ic_action_refresh);
            MenuItemCompat.setShowAsAction(this.refreshItem, 2);
            this.refreshItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() { // from class: com.valvesoftware.android.steam.community.activity.BaseActivity.8
                @Override // android.view.MenuItem.OnMenuItemClickListener
                public boolean onMenuItemClick(MenuItem menuItem) {
                    if (BaseActivity.this.refreshClickListener == null) {
                        return true;
                    }
                    BaseActivity.this.refreshClickListener.onClick(null);
                    return true;
                }
            });
        }
        if (this.extraMenuItem != null) {
            MenuItem add2 = menu.add("");
            add2.setIcon(this.extraMenuItem.iconResourceId);
            MenuItemCompat.setShowAsAction(add2, 2);
            add2.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() { // from class: com.valvesoftware.android.steam.community.activity.BaseActivity.9
                @Override // android.view.MenuItem.OnMenuItemClickListener
                public boolean onMenuItemClick(MenuItem menuItem) {
                    if (BaseActivity.this.extraMenuItem.onClickListener == null) {
                        return true;
                    }
                    BaseActivity.this.extraMenuItem.onClickListener.onClick(null);
                    return true;
                }
            });
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean closeDrawer() {
        if (!this.drawerLayout.isDrawerOpen(8388611)) {
            return false;
        }
        this.drawerLayout.closeDrawers();
        return true;
    }

    public void showProgressIndicator() {
        MenuBar menuBar;
        if (isFinishing()) {
            return;
        }
        if (useLimitedMenuBar() && (menuBar = this.menuBar) != null) {
            menuBar.showProgressIndicator();
            return;
        }
        MenuItem menuItem = this.refreshItem;
        if (menuItem == null || MenuItemCompat.getActionView(menuItem) == this.progressView) {
            return;
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.valvesoftware.android.steam.community.activity.BaseActivity.10
            @Override // java.lang.Runnable
            public void run() {
                MenuItemCompat.setActionView(BaseActivity.this.refreshItem, BaseActivity.this.progressView);
            }
        });
    }

    public void hideProgressIndicator() {
        hideProgressIndicator(500);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void hideProgressIndicator(int i) {
        if (isFinishing()) {
            return;
        }
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { // from class: com.valvesoftware.android.steam.community.activity.BaseActivity.11
            @Override // java.lang.Runnable
            public void run() {
                if (BaseActivity.this.isFinishing()) {
                    return;
                }
                if (!BaseActivity.this.useLimitedMenuBar() || BaseActivity.this.menuBar == null) {
                    if (BaseActivity.this.refreshItem != null) {
                        MenuItemCompat.setActionView(BaseActivity.this.refreshItem, (View) null);
                        return;
                    }
                    return;
                }
                BaseActivity.this.menuBar.hideProgressIndicator();
            }
        }, i);
    }

    public void setRefreshButtonClickListener(View.OnClickListener onClickListener) {
        MenuBar menuBar;
        if (useLimitedMenuBar() && (menuBar = this.menuBar) != null) {
            menuBar.setRefreshClickedListener(onClickListener);
        } else {
            this.refreshClickListener = onClickListener;
        }
        invalidateOptionsMenu();
    }

    public void setSearchTextListener(TextWatcher textWatcher) {
        this.searchTextWatcher = textWatcher;
        if (useLimitedMenuBar()) {
            setupSearchBarForLimitedMenuBar(textWatcher);
        } else {
            invalidateOptionsMenu();
        }
    }

    private void setupSearchBarForLimitedMenuBar(final TextWatcher textWatcher) {
        if (textWatcher == null) {
            this.menuBar.setSearchClickedListener(null);
            return;
        }
        final SearchBarFragment searchBar = getSearchBar();
        if (searchBar != null) {
            this.menuBar.setSearchClickedListener(new View.OnClickListener() { // from class: com.valvesoftware.android.steam.community.activity.BaseActivity.12
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    searchBar.setSearchTextChangedListener(textWatcher);
                    searchBar.openSearch();
                }
            });
        }
    }

    public void setExtraToolbarItem(SteamMenuItem steamMenuItem) {
        this.extraMenuItem = steamMenuItem;
        if (useLimitedMenuBar()) {
            this.menuBar.setExtraMenuItem(this.extraMenuItem);
        } else {
            invalidateOptionsMenu();
        }
    }

    @Override // android.app.Activity
    public void setTitle(CharSequence charSequence) {
        MenuBar menuBar;
        if (useLimitedMenuBar() && (menuBar = this.menuBar) != null) {
            menuBar.setTitle(charSequence);
        } else {
            super.setTitle(charSequence);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void clearExtraMenuItems() {
        this.extraMenuItem = null;
        if (useLimitedMenuBar()) {
            this.menuBar.setExtraMenuItem(null);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void clearTitleLabel() {
        setTitle("");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void clearSearchButtonListener() {
        this.searchTextWatcher = null;
        MenuBar menuBar = this.menuBar;
        if (menuBar != null) {
            menuBar.setSearchClickedListener(null);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void clearRefreshButtonListener() {
        setRefreshButtonClickListener(null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void hideKeyboard() {
        ((InputMethodManager) getSystemService("input_method")).hideSoftInputFromWindow(findViewById(android.R.id.content).getWindowToken(), 0);
    }

    protected SearchBarFragment getSearchBar() {
        Fragment findFragmentById = getSupportFragmentManager().findFragmentById(R.id.searchbar);
        if (findFragmentById instanceof SearchBarFragment) {
            return (SearchBarFragment) findFragmentById;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void hideSearchBar() {
        SearchBarFragment searchBar = getSearchBar();
        if (searchBar != null) {
            searchBar.closeSearch();
        }
    }

    private void setupNavHeader() {
        this.navigationHeadersLayout.removeAllViews();
        this.navigationHeadersLayout.addView(getNavigationDrawerHeader());
    }

    protected void setupNavDrawer() {
        setupNavHeader();
        this.expandableListView.addHeaderView(this.navigationHeadersLayout);
        refreshNavDrawer();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void refreshNavDrawer() {
        setupNavHeader();
        this.navDrawerListAdapter = new NavDrawerListAdapter(this, getNavigationItems());
        this.expandableListView.setAdapter(this.navDrawerListAdapter);
        ExpandableListView expandableListView = this.expandableListView;
        expandableListView.setOnGroupExpandListener(new CustomOnGroupExpandListener(expandableListView));
        this.expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() { // from class: com.valvesoftware.android.steam.community.activity.BaseActivity.13
            @Override // android.widget.ExpandableListView.OnChildClickListener
            public boolean onChildClick(ExpandableListView expandableListView2, View view, int i, int i2, long j) {
                view.setSelected(true);
                return false;
            }
        });
        this.expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() { // from class: com.valvesoftware.android.steam.community.activity.BaseActivity.14
            @Override // android.widget.ExpandableListView.OnGroupClickListener
            public boolean onGroupClick(ExpandableListView expandableListView2, View view, int i, long j) {
                view.setSelected(true);
                return false;
            }
        });
    }

    private View getNavigationDrawerHeader() {
        View inflate = View.inflate(this, R.layout.nav_header, null);
        ImageView imageView = (ImageView) inflate.findViewById(R.id.avatar);
        TextView textView = (TextView) inflate.findViewById(R.id.name);
        inflate.setOnClickListener(new View.OnClickListener() { // from class: com.valvesoftware.android.steam.community.activity.BaseActivity.15
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                BaseActivity baseActivity = BaseActivity.this;
                baseActivity.startActivity(SteamAppIntents.visitProfileIntent(baseActivity, LoggedInUserAccountInfo.getLoginSteamID()));
                BaseActivity.this.closeDrawer();
            }
        });
        loadUserInfo(imageView, textView);
        return inflate;
    }

    private List<NavDrawerItem> getNavigationItems() {
        SteamCommunityApplication GetInstance = SteamCommunityApplication.GetInstance();
        ArrayList arrayList = new ArrayList();
        arrayList.add(new NavDrawerItem(-1, R.drawable.ic_action_steamguard, R.string.Steam_Guard, SteamAppIntents.viewSteamGuard(GetInstance), this.drawerLayout));
        arrayList.add(new NavDrawerItem(-1, R.drawable.ic_action_steamguard, R.string.Confirmations, SteamAppIntents.viewConfirmations(GetInstance), this.drawerLayout));
        arrayList.add(new NavDrawerItem(R.string.menu_chat, 0, R.string.menu_chat, SteamAppIntents.viewFriendsList(GetInstance), this.drawerLayout));
        arrayList.add(getNotificationNavigationItems(this.drawerLayout));
        NavDrawerItem navDrawerItem = new NavDrawerItem(R.string.Store_Caps, 0, R.string.Store, null, this.drawerLayout);
        navDrawerItem.add(new NavDrawerItem(R.string.Store_Caps, 0, R.string.Catalog, SteamAppIntents.viewCatalog(GetInstance), this.drawerLayout));
        navDrawerItem.add(new NavDrawerItem(R.string.Store_Caps, 0, R.string.Cart, SteamAppIntents.viewShoppingCart(GetInstance), this.drawerLayout));
        navDrawerItem.add(new NavDrawerItem(R.string.Store_Caps, 0, R.string.Search, SteamAppIntents.searchSteam(GetInstance), this.drawerLayout));
        navDrawerItem.add(new NavDrawerItem(R.string.Store_Caps, 0, R.string.Wishlist, SteamAppIntents.viewWishList(GetInstance), this.drawerLayout));
        navDrawerItem.add(new NavDrawerItem(R.string.Store_Caps, 0, R.string.Steam_News, SteamAppIntents.viewSteamNews(GetInstance), this.drawerLayout));
        navDrawerItem.add(new NavDrawerItem(R.string.Settings_Caps, 0, R.string.Account_Details, SteamAppIntents.viewAccountDetails(GetInstance), this.drawerLayout));
        arrayList.add(navDrawerItem);
        NavDrawerItem navDrawerItem2 = new NavDrawerItem(R.string.Community_Caps, 0, R.string.menu_community, null, this.drawerLayout);
        navDrawerItem2.add(new NavDrawerItem(R.string.Community_Caps, 0, R.string.menu_community_home, SteamAppIntents.communityURLIntent(GetInstance, "/"), this.drawerLayout));
        navDrawerItem2.add(new NavDrawerItem(R.string.Community_Caps, 0, R.string.menu_community_discussions, SteamAppIntents.communityURLIntent(GetInstance, "/discussions/"), this.drawerLayout));
        navDrawerItem2.add(new NavDrawerItem(R.string.Community_Caps, 0, R.string.menu_community_market, SteamAppIntents.communityURLIntent(GetInstance, "/market/"), this.drawerLayout));
        navDrawerItem2.add(new NavDrawerItem(R.string.Community_Caps, 0, R.string.menu_community_broadcasts, SteamAppIntents.communityURLIntent(GetInstance, "/?subsection=broadcasts"), this.drawerLayout));
        arrayList.add(navDrawerItem2);
        NavDrawerItem navDrawerItem3 = new NavDrawerItem(R.string.menu_personal, 0, R.string.menu_personal, null, this.drawerLayout);
        navDrawerItem3.add(new NavDrawerItem(R.string.menu_personal, 0, R.string.menu_personal_activity, SteamAppIntents.profileURLIntent(GetInstance, "/home/"), this.drawerLayout));
        navDrawerItem3.add(new NavDrawerItem(R.string.menu_personal, 0, R.string.menu_personal_profile, SteamAppIntents.profileURLIntent(GetInstance, "/"), this.drawerLayout));
        navDrawerItem3.add(new NavDrawerItem(R.string.menu_personal, 0, R.string.menu_personal_friends, SteamAppIntents.profileURLIntent(GetInstance, "/friends/"), this.drawerLayout));
        navDrawerItem3.add(new NavDrawerItem(R.string.menu_personal, 0, R.string.menu_personal_groups, SteamAppIntents.profileURLIntent(GetInstance, "/groups/"), this.drawerLayout));
        navDrawerItem3.add(new NavDrawerItem(R.string.menu_personal, 0, R.string.menu_personal_content, SteamAppIntents.profileURLIntent(GetInstance, "/screenshots/"), this.drawerLayout));
        navDrawerItem3.add(new NavDrawerItem(R.string.menu_personal, 0, R.string.menu_personal_badges, SteamAppIntents.profileURLIntent(GetInstance, "/badges/"), this.drawerLayout));
        navDrawerItem3.add(new NavDrawerItem(R.string.menu_personal, 0, R.string.menu_personal_inventory, SteamAppIntents.profileURLIntent(GetInstance, "/inventory/"), this.drawerLayout));
        arrayList.add(navDrawerItem3);
        arrayList.add(new NavDrawerItem(R.string.Library, 0, R.string.Library, SteamAppIntents.viewLibrary(GetInstance), this.drawerLayout));
        arrayList.add(new NavDrawerItem(R.string.menu_support, 0, R.string.menu_support, SteamAppIntents.helpURLIntent(GetInstance, "/"), this.drawerLayout));
        NavDrawerItem navDrawerItem4 = new NavDrawerItem(R.string.Settings_Caps, 0, R.string.Settings, null, this.drawerLayout);
        navDrawerItem4.add(new NavDrawerItem(R.string.Settings_Caps, 0, R.string.ApplicationPreferences, SteamAppIntents.viewSettings(GetInstance), this.drawerLayout));
        int i = LoggedInUserAccountInfo.dontLoginToChat() ? R.string.chat_go_online : R.string.chat_go_offline;
        navDrawerItem4.add(new NavDrawerItem(R.string.Settings_Caps, 0, i, null, this.drawerLayout) { // from class: com.valvesoftware.android.steam.community.activity.BaseActivity.16
            @Override // com.valvesoftware.android.steam.community.fragment.NavDrawerItem
            public void onClick() {
                ResponseListener responseListener = new ResponseListener() { // from class: com.valvesoftware.android.steam.community.activity.BaseActivity.16.1
                    @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
                    public void onError(RequestErrorInfo requestErrorInfo) {
                    }

                    @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
                    public void onSuccess(JSONObject jSONObject) {
                        LoggedInUserAccountInfo.setDontLoginToChat(!LoggedInUserAccountInfo.dontLoginToChat());
                        if (BaseActivity.this.navDrawerListAdapter != null) {
                            BaseActivity.this.navDrawerListAdapter.notifyDataSetChanged();
                        }
                    }
                };
                UmqCommunicator umqCommunicator = UmqCommunicator.getInstance();
                if (LoggedInUserAccountInfo.dontLoginToChat()) {
                    umqCommunicator.loginToUmq(responseListener);
                    umqCommunicator.start();
                } else {
                    umqCommunicator.logOffFromUmq(responseListener);
                }
            }

            @Override // com.valvesoftware.android.steam.community.fragment.NavDrawerItem
            public int getNameId() {
                return LoggedInUserAccountInfo.dontLoginToChat() ? R.string.chat_go_online : R.string.chat_go_offline;
            }
        });
        navDrawerItem4.add(new C016217(R.string.Settings_Caps, 0, R.string.Sign_Out, null, this.drawerLayout));
        arrayList.add(navDrawerItem4);
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.valvesoftware.android.steam.community.activity.BaseActivity$17 */
    /* loaded from: classes.dex */
    public class C016217 extends NavDrawerItem {
        C016217(int i, int i2, int i3, Intent intent, DrawerLayout drawerLayout) {
            super(i, i2, i3, intent, drawerLayout);
        }

        @Override // com.valvesoftware.android.steam.community.fragment.NavDrawerItem
        public void onClick() {
            new AlertDialog.Builder(BaseActivity.this).setTitle(R.string.Sign_Out).setIcon(android.R.drawable.ic_dialog_alert).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() { // from class: com.valvesoftware.android.steam.community.activity.BaseActivity.17.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    new GcmRegistrar().unregister(BaseActivity.this.getApplicationContext());
                    BaseActivity.this.signOutHandler.postDelayed(new Runnable() { // from class: com.valvesoftware.android.steam.community.activity.BaseActivity.17.1.1
                        @Override // java.lang.Runnable
                        public void run() {
                            UmqCommunicator.getInstance().signOutOfAppCompletely();
                            LoggedInUserAccountInfo.logOut();
                            BaseActivity.this.loggedInUser = null;
                            BaseActivity.this.refreshNavDrawer();
                            BaseActivity.this.finish();
                            Intent loginIntent = SteamAppIntents.loginIntent(SteamCommunityApplication.GetInstance());
                            loginIntent.addFlags(268435456);
                            BaseActivity.this.startActivity(loginIntent);
                        }
                    }, 500L);
                }
            }).setNegativeButton(android.R.string.no, (DialogInterface.OnClickListener) null).show();
        }
    }

    protected NavDrawerItem getNotificationNavigationItems(DrawerLayout drawerLayout) {
        SteamCommunityApplication GetInstance = SteamCommunityApplication.GetInstance();
        NavDrawerNotificationItem navDrawerGroupItem = new NavDrawerNotificationItem.NavDrawerGroupItem(R.string.Notifications, R.string.Notifications, drawerLayout);
        registerNotificationDrawerItem(UserNotificationCounts.EUserNotification.k_EUserNotificationInvalid, navDrawerGroupItem);
        NavDrawerNotificationItem navDrawerNotificationItem = new NavDrawerNotificationItem(R.string.Notifications, R.string.notification_comments, SteamAppIntents.notificationCommentsIntent(GetInstance), drawerLayout, false);
        registerNotificationDrawerItem(UserNotificationCounts.EUserNotification.k_EUserNotificationComment, navDrawerNotificationItem);
        navDrawerGroupItem.add(navDrawerNotificationItem);
        NavDrawerNotificationItem navDrawerNotificationItem2 = new NavDrawerNotificationItem(R.string.Notifications, R.string.notification_items, SteamAppIntents.notificationItemsIntent(GetInstance), drawerLayout, false);
        registerNotificationDrawerItem(UserNotificationCounts.EUserNotification.k_EUserNotificationItem, navDrawerNotificationItem2);
        navDrawerGroupItem.add(navDrawerNotificationItem2);
        NavDrawerNotificationItem navDrawerNotificationItem3 = new NavDrawerNotificationItem(R.string.Notifications, R.string.notification_invites, SteamAppIntents.notificationInvitesIntent(GetInstance), drawerLayout, false);
        registerNotificationDrawerItem(UserNotificationCounts.EUserNotification.k_EUserNotificationFriendInvite, navDrawerNotificationItem3);
        navDrawerGroupItem.add(navDrawerNotificationItem3);
        NavDrawerNotificationItem navDrawerNotificationItem4 = new NavDrawerNotificationItem(R.string.Notifications, R.string.notification_gifts, SteamAppIntents.notificationGiftsIntent(GetInstance), drawerLayout, false);
        registerNotificationDrawerItem(UserNotificationCounts.EUserNotification.k_EUserNotificationGift, navDrawerNotificationItem4);
        navDrawerGroupItem.add(navDrawerNotificationItem4);
        NavDrawerNotificationItem navDrawerNotificationItem5 = new NavDrawerNotificationItem(R.string.Notifications, R.string.notification_trade, SteamAppIntents.notificationTradeOffersIntent(GetInstance), drawerLayout, true);
        registerNotificationDrawerItem(UserNotificationCounts.EUserNotification.k_EUserNotificationTradeOffer, navDrawerNotificationItem5);
        navDrawerGroupItem.add(navDrawerNotificationItem5);
        NavDrawerNotificationItem navDrawerNotificationItem6 = new NavDrawerNotificationItem(R.string.Notifications, R.string.notification_chat, SteamAppIntents.viewFriendsList(GetInstance), drawerLayout, true);
        registerNotificationDrawerItem(UserNotificationCounts.EUserNotification.k_EUserNotificationOfflineMessage, navDrawerNotificationItem6);
        navDrawerGroupItem.add(navDrawerNotificationItem6);
        NavDrawerNotificationItem navDrawerNotificationItem7 = new NavDrawerNotificationItem(R.string.Notifications, R.string.notification_asyncgameinvite, SteamAppIntents.notificationAsyncGameIntent(GetInstance), drawerLayout, true);
        registerNotificationDrawerItem(UserNotificationCounts.EUserNotification.k_EUserNotificationAsyncGameState, navDrawerNotificationItem7);
        navDrawerGroupItem.add(navDrawerNotificationItem7);
        NavDrawerNotificationItem navDrawerNotificationItem8 = new NavDrawerNotificationItem(R.string.Notifications, R.string.notification_moderatormessages, SteamAppIntents.notificationModeratorMessageIntent(GetInstance), drawerLayout, true);
        registerNotificationDrawerItem(UserNotificationCounts.EUserNotification.k_EUserNotificationModeratorMessage, navDrawerNotificationItem8);
        navDrawerGroupItem.add(navDrawerNotificationItem8);
        NavDrawerNotificationItem navDrawerNotificationItem9 = new NavDrawerNotificationItem(R.string.Notifications, R.string.notification_helprequestreply, SteamAppIntents.notificationHelpRequestReplyIntent(GetInstance), drawerLayout, true);
        registerNotificationDrawerItem(UserNotificationCounts.EUserNotification.k_EUserNotificationHelpRequestReply, navDrawerNotificationItem9);
        navDrawerGroupItem.add(navDrawerNotificationItem9);
        return navDrawerGroupItem;
    }

    protected void registerNotificationDrawerItem(UserNotificationCounts.EUserNotification eUserNotification, NavDrawerNotificationItem navDrawerNotificationItem) {
        if (eUserNotification == UserNotificationCounts.EUserNotification.k_EUserNotificationInvalid) {
            navDrawerNotificationItem.setNotificationCount(this.userNotificationCounts.GetTotalNotificationCount());
        } else {
            navDrawerNotificationItem.setNotificationCount(this.userNotificationCounts.GetNotificationCount(eUserNotification));
        }
        this.m_listNotificationNavItems.add(new Pair<>(eUserNotification, navDrawerNotificationItem));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onNotificationCountsChanged() {
        int GetTotalNotificationCount = this.userNotificationCounts.GetTotalNotificationCount();
        boolean z = false;
        for (Pair<UserNotificationCounts.EUserNotification, NavDrawerNotificationItem> pair : this.m_listNotificationNavItems) {
            int GetNotificationCount = pair.first == UserNotificationCounts.EUserNotification.k_EUserNotificationInvalid ? GetTotalNotificationCount : this.userNotificationCounts.GetNotificationCount((UserNotificationCounts.EUserNotification) pair.first);
            boolean isHidden = ((NavDrawerNotificationItem) pair.second).isHidden();
            ((NavDrawerNotificationItem) pair.second).setNotificationCount(GetNotificationCount);
            if (isHidden != ((NavDrawerNotificationItem) pair.second).isHidden()) {
                z = true;
            }
        }
        if (z) {
            this.expandableListView.setAdapter(this.navDrawerListAdapter);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void showMenuAndActionBar() {
        if (useLimitedMenuBar()) {
            this.menuBar.setVisibility(0);
        } else {
            ActionBar supportActionBar = getSupportActionBar();
            if (supportActionBar != null) {
                supportActionBar.show();
            }
        }
        this.drawerLayout.setDrawerLockMode(0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void hideMenuAndActionBar() {
        if (useLimitedMenuBar()) {
            this.menuBar.setVisibility(8);
        } else {
            ActionBar supportActionBar = getSupportActionBar();
            if (supportActionBar != null) {
                supportActionBar.hide();
            }
        }
        this.drawerLayout.setDrawerLockMode(1);
    }

    private void loadUserInfo(final ImageView imageView, final TextView textView) {
        Persona persona = this.loggedInUser;
        if (persona != null) {
            AndroidUtils.setTextViewText(textView, persona.personaName);
            loadAvatar(this.loggedInUser, imageView);
        }
        if (LoggedInUserAccountInfo.getLoginSteamID() == null || LoggedInUserAccountInfo.getLoginSteamID().length() <= 0) {
            return;
        }
        PersonaRepository.getDetailedPersonaInfo(LoggedInUserAccountInfo.getLoginSteamID(), new RepositoryCallback<Persona>() { // from class: com.valvesoftware.android.steam.community.activity.BaseActivity.18
            @Override // com.valvesoftware.android.steam.community.RepositoryCallback
            public void end() {
            }

            @Override // com.valvesoftware.android.steam.community.RepositoryCallback
            public void dataAvailable(Persona persona2) {
                BaseActivity baseActivity = BaseActivity.this;
                baseActivity.loggedInUser = persona2;
                AndroidUtils.setTextViewText(textView, baseActivity.loggedInUser.personaName);
                BaseActivity baseActivity2 = BaseActivity.this;
                baseActivity2.loadAvatar(baseActivity2.loggedInUser, imageView);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadAvatar(Persona persona, final ImageView imageView) {
        ImageRequestBuilder imageUrlRequestBuilder = Endpoints.getImageUrlRequestBuilder(persona.fullAvatarUrl);
        imageUrlRequestBuilder.setResponseListener(new ImageResponseListener() { // from class: com.valvesoftware.android.steam.community.activity.BaseActivity.19
            @Override // com.valvesoftware.android.steam.community.webrequests.ImageResponseListener
            public void onSuccess(Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
            }
        });
        SteamCommunityApplication.GetInstance().sendRequest(imageUrlRequestBuilder);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closeKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService("input_method");
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        Uri uri;
        if (intent == null || i != SettingInfoDB.ringToneSelectorRequestCode || (uri = (Uri) intent.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI")) == null) {
            return;
        }
        SteamCommunityApplication.GetInstance().GetSettingInfoDB().m_settingRing.setValueAndCommit(SteamCommunityApplication.GetInstance(), uri.toString());
    }
}

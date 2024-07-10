package com.valvesoftware.android.steam.community;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import com.valvesoftware.android.steam.community.SettingInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class SettingInfoDB extends BroadcastReceiver {
    public static final String URL_SETTINGS_CATEGORIES = Config.URL_COMMUNITY_BASE + "/mobilesettings/GetManifest/v0001";
    public static int ringToneSelectorRequestCode = 800;
    public SettingInfo m_settingChatsAlertLinks;
    public SettingInfo m_settingDOB;
    public SettingInfo m_settingRing;
    public SettingInfo m_settingSound;
    public SettingInfo m_settingSslUntrustedPrompt;
    public SettingInfo m_settingVibrate;
    public SettingInfo m_startScreen;
    public SettingInfo m_usePushInBackground;
    private ArrayList<SettingInfo> m_settingsList = new ArrayList<>();
    private final String m_className = getClass().getName();

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
    }

    /* loaded from: classes.dex */
    public enum StartScreen {
        Friends(0),
        Groups(1),
        SteamNews(2),
        Catalog(3),
        SteamGuard(4),
        FriendActivity(5),
        Library(6),
        Unknown(-1);

        private static final Map<Integer, StartScreen> intToTypeMap = new HashMap();
        public final int value;

        static {
            for (StartScreen startScreen : values()) {
                intToTypeMap.put(Integer.valueOf(startScreen.value), startScreen);
            }
        }

        StartScreen(int i) {
            this.value = i;
        }

        public static StartScreen fromInt(int i) {
            StartScreen startScreen = intToTypeMap.get(Integer.valueOf(i));
            return startScreen == null ? Unknown : startScreen;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SettingInfoDB() {
        SteamCommunityApplication.GetInstance().registerReceiver(this, new IntentFilter(this.m_className));
        SettingInfo settingInfo = new SettingInfo();
        settingInfo.m_resid = R.string.settings_chat_links_nonsteam;
        settingInfo.m_resid_detailed = R.string.settings_chat_links_nonsteam_details;
        settingInfo.m_key = "chats_links_nonsteam";
        settingInfo.m_defaultValue = "1";
        settingInfo.m_type = SettingInfo.SettingType.CHECK;
        settingInfo.m_access = SettingInfo.AccessRight.VALID_ACCOUNT;
        this.m_settingChatsAlertLinks = settingInfo;
        this.m_settingsList.add(settingInfo);
        SettingInfo settingInfo2 = new SettingInfo();
        settingInfo2.m_resid = R.string.settings_personal;
        settingInfo2.m_access = SettingInfo.AccessRight.VALID_ACCOUNT;
        this.m_settingsList.add(settingInfo2);
        SettingInfo settingInfo3 = new SettingInfo();
        settingInfo3.m_resid = R.string.settings_start_screen;
        r1[0].value = StartScreen.Friends.value;
        r1[0].resid_text = R.string.Friends;
        r1[1].value = StartScreen.Groups.value;
        r1[1].resid_text = R.string.Groups;
        r1[2].value = StartScreen.Catalog.value;
        r1[2].resid_text = R.string.Catalog;
        r1[3].value = StartScreen.SteamNews.value;
        r1[3].resid_text = R.string.Steam_News;
        r1[4].value = StartScreen.SteamGuard.value;
        r1[4].resid_text = R.string.Steam_Guard;
        r1[5].value = StartScreen.FriendActivity.value;
        r1[5].resid_text = R.string.Friend_Activity;
        SettingInfo.RadioSelectorItem[] radioSelectorItemArr = {new SettingInfo.RadioSelectorItem(), new SettingInfo.RadioSelectorItem(), new SettingInfo.RadioSelectorItem(), new SettingInfo.RadioSelectorItem(), new SettingInfo.RadioSelectorItem(), new SettingInfo.RadioSelectorItem(), new SettingInfo.RadioSelectorItem()};
        radioSelectorItemArr[6].value = StartScreen.Library.value;
        radioSelectorItemArr[6].resid_text = R.string.Library;
        settingInfo3.m_extraData = radioSelectorItemArr;
        settingInfo3.m_key = "start_screen";
        settingInfo3.m_defaultValue = String.valueOf(StartScreen.Friends.value);
        settingInfo3.m_type = SettingInfo.SettingType.RADIOSELECTOR;
        settingInfo3.m_access = SettingInfo.AccessRight.VALID_ACCOUNT;
        this.m_startScreen = settingInfo3;
        this.m_settingsList.add(settingInfo3);
        SettingInfo settingInfo4 = new SettingInfo();
        settingInfo4.m_resid = R.string.settings_personal_dob;
        settingInfo4.m_resid_detailed = R.string.settings_personal_dob_instr;
        settingInfo4.m_key = "personal_dob";
        settingInfo4.m_defaultValue = "";
        settingInfo4.m_type = SettingInfo.SettingType.DATE;
        settingInfo4.m_access = SettingInfo.AccessRight.VALID_ACCOUNT;
        settingInfo4.m_pUpdateListener = new SettingInfo.UpdateListener() { // from class: com.valvesoftware.android.steam.community.SettingInfoDB.1
            @Override // com.valvesoftware.android.steam.community.SettingInfo.UpdateListener
            public void OnSettingInfoValueUpdate(SettingInfo settingInfo5, String str, SettingInfo.Transaction transaction) {
                LoggedInUserAccountInfo.setCookie2("dob", SettingInfo.DateConverter.makeUnixTime(str));
                transaction.markCookiesForSync();
            }
        };
        this.m_settingDOB = settingInfo4;
        this.m_settingsList.add(settingInfo4);
        SettingInfo settingInfo5 = new SettingInfo();
        settingInfo5.m_resid = R.string.settings_notifications_ring;
        settingInfo5.m_key = "notifications_ringtone";
        settingInfo5.m_defaultValue = "android.resource://com.valvesoftware.android.steam.community/raw/m";
        settingInfo5.m_type = SettingInfo.SettingType.RINGTONESELECTOR;
        settingInfo5.m_access = SettingInfo.AccessRight.VALID_ACCOUNT;
        this.m_settingRing = settingInfo5;
        this.m_settingsList.add(settingInfo5);
        SettingInfo settingInfo6 = new SettingInfo();
        settingInfo6.m_resid = R.string.settings_notifications_sound;
        r1[0].value = -1;
        r1[0].resid_text = R.string.settings_notifications_sound_all;
        r1[1].value = 0;
        r1[1].resid_text = R.string.settings_notifications_sound_first;
        SettingInfo.RadioSelectorItem[] radioSelectorItemArr2 = {new SettingInfo.RadioSelectorItem(), new SettingInfo.RadioSelectorItem(), new SettingInfo.RadioSelectorItem()};
        radioSelectorItemArr2[2].value = 1;
        radioSelectorItemArr2[2].resid_text = R.string.settings_notifications_sound_never;
        settingInfo6.m_extraData = radioSelectorItemArr2;
        settingInfo6.m_key = "notifications_sound";
        settingInfo6.m_defaultValue = String.valueOf(-1);
        settingInfo6.m_type = SettingInfo.SettingType.RADIOSELECTOR;
        settingInfo6.m_access = SettingInfo.AccessRight.VALID_ACCOUNT;
        this.m_settingSound = settingInfo6;
        this.m_settingsList.add(settingInfo6);
        SettingInfo settingInfo7 = new SettingInfo();
        settingInfo7.m_resid = R.string.settings_notifications_vibrate;
        r1[0].value = -1;
        r1[0].resid_text = R.string.settings_notifications_vibrate_all;
        r1[1].value = 0;
        r1[1].resid_text = R.string.settings_notifications_vibrate_first;
        SettingInfo.RadioSelectorItem[] radioSelectorItemArr3 = {new SettingInfo.RadioSelectorItem(), new SettingInfo.RadioSelectorItem(), new SettingInfo.RadioSelectorItem()};
        radioSelectorItemArr3[2].value = 1;
        radioSelectorItemArr3[2].resid_text = R.string.settings_notifications_vibrate_never;
        settingInfo7.m_extraData = radioSelectorItemArr3;
        settingInfo7.m_key = "notifications_vibrate";
        settingInfo7.m_defaultValue = String.valueOf(-1);
        settingInfo7.m_type = SettingInfo.SettingType.RADIOSELECTOR;
        settingInfo7.m_access = SettingInfo.AccessRight.VALID_ACCOUNT;
        this.m_settingVibrate = settingInfo7;
        this.m_settingsList.add(settingInfo7);
        SettingInfo settingInfo8 = new SettingInfo();
        settingInfo8.m_resid = R.string.settings_notifications_im2;
        r1[0].value = 1;
        r1[0].resid_text = R.string.settings_notifications_im_detailed;
        SettingInfo.RadioSelectorItem[] radioSelectorItemArr4 = {new SettingInfo.RadioSelectorItem(), new SettingInfo.RadioSelectorItem()};
        radioSelectorItemArr4[1].value = 0;
        radioSelectorItemArr4[1].resid_text = R.string.settings_notifications_im_off;
        settingInfo8.m_extraData = radioSelectorItemArr4;
        settingInfo8.m_key = "notifications_im2";
        settingInfo8.m_defaultValue = "1";
        settingInfo8.m_type = SettingInfo.SettingType.RADIOSELECTOR;
        settingInfo8.m_access = SettingInfo.AccessRight.VALID_ACCOUNT;
        settingInfo8.m_pUpdateListener = new SettingInfo.UpdateListener() { // from class: com.valvesoftware.android.steam.community.SettingInfoDB.2
            @Override // com.valvesoftware.android.steam.community.SettingInfo.UpdateListener
            public void OnSettingInfoValueUpdate(SettingInfo settingInfo9, String str, SettingInfo.Transaction transaction) {
                UmqCommunicator.getInstance().setServerPushStateBasedOnUserPreference();
            }
        };
        this.m_usePushInBackground = settingInfo8;
        this.m_settingsList.add(this.m_usePushInBackground);
        SettingInfo settingInfo9 = new SettingInfo();
        settingInfo9.m_resid = R.string.settings_personal_steam_preferences;
        settingInfo9.m_resid_detailed = R.string.settings_personal_steam_preferences_detailed;
        settingInfo9.m_defaultValue = "steammobile://opencategoryurl?url=Settings";
        settingInfo9.m_type = SettingInfo.SettingType.URI;
        settingInfo9.m_access = SettingInfo.AccessRight.VALID_ACCOUNT;
        this.m_settingsList.add(settingInfo9);
        SettingInfo settingInfo10 = new SettingInfo();
        settingInfo10.m_resid = R.string.webview_ssl_untrusted_prompt;
        r1[0].value = 1;
        r1[0].resid_text = R.string.webview_ssl_untrusted_prompt_cancel;
        r1[1].value = 0;
        r1[1].resid_text = R.string.webview_ssl_untrusted_prompt_ok_once;
        SettingInfo.RadioSelectorItem[] radioSelectorItemArr5 = {new SettingInfo.RadioSelectorItem(), new SettingInfo.RadioSelectorItem(), new SettingInfo.RadioSelectorItem()};
        radioSelectorItemArr5[2].value = -1;
        radioSelectorItemArr5[2].resid_text = R.string.webview_ssl_untrusted_prompt_ok_always;
        settingInfo10.m_extraData = radioSelectorItemArr5;
        settingInfo10.m_key = "ssl_untrusted_prompt";
        settingInfo10.m_defaultValue = String.valueOf(1);
        settingInfo10.m_type = SettingInfo.SettingType.RADIOSELECTOR;
        settingInfo10.m_access = Build.VERSION.SDK_INT < 8 ? SettingInfo.AccessRight.CODE : SettingInfo.AccessRight.NONE;
        this.m_settingSslUntrustedPrompt = settingInfo10;
        this.m_settingsList.add(settingInfo10);
        SettingInfo settingInfo11 = new SettingInfo();
        settingInfo11.m_resid = R.string.Settings_About;
        settingInfo11.m_access = SettingInfo.AccessRight.USER;
        this.m_settingsList.add(settingInfo11);
        SettingInfo settingInfo12 = new SettingInfo();
        settingInfo12.m_resid = R.string.Settings_Version;
        settingInfo12.m_defaultValue = Config.APP_VERSION + " / " + Config.APP_VERSION_ID;
        settingInfo12.m_type = SettingInfo.SettingType.MARKET;
        settingInfo12.m_access = SettingInfo.AccessRight.USER;
        this.m_settingsList.add(settingInfo12);
    }

    public ArrayList<SettingInfo> GetSettingsList() {
        return this.m_settingsList;
    }

    public boolean usePushInBackground() {
        try {
            return this.m_usePushInBackground.getIntegerValue(SteamCommunityApplication.GetInstance().getApplicationContext()) == 1;
        } catch (RuntimeException unused) {
            return true;
        }
    }
}

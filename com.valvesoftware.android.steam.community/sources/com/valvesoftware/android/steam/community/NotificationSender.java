package com.valvesoftware.android.steam.community;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class NotificationSender {
    private static NotificationSender instance;
    private ChatNotification lastChatNotification;
    private SettingInfoDB settingInfoDB = SteamCommunityApplication.GetInstance().GetSettingInfoDB();
    private long lastRingOrVibrateTime = 0;
    private HashSet<String> chatPartnerNamesWithRecentlyMadeNotifications = new HashSet<>();
    private SteamCommunityApplication steamCommunityApplication = SteamCommunityApplication.GetInstance();
    private Context context = this.steamCommunityApplication.getApplicationContext();

    private String GetChannelForNotificationType(int i) {
        switch (i) {
            case 1:
                return "Chat";
            case 2:
                return "Wishlist";
            case 3:
                return "Promotion";
            case 4:
                return "Confirmation";
            case 5:
                return "SteamGuard";
            default:
                return null;
        }
    }

    private NotificationSender() {
    }

    public static NotificationSender getInstance() {
        if (instance == null) {
            synchronized (NotificationSender.class) {
                instance = new NotificationSender();
                instance.registerNotificationChannels();
            }
        }
        return instance;
    }

    private void registerNotificationChannels() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationManager notificationManager = (NotificationManager) this.context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel("Chat", this.context.getString(R.string.menu_chat), 4));
            NotificationChannel notificationChannel = new NotificationChannel("Wishlist", this.context.getString(R.string.Wishlist), 2);
            notificationChannel.setDescription(this.context.getString(R.string.notificationchannel_wishlist_desc));
            notificationManager.createNotificationChannel(notificationChannel);
            notificationManager.createNotificationChannel(new NotificationChannel("Confirmation", this.context.getString(R.string.Confirmations), 4));
            NotificationChannel notificationChannel2 = new NotificationChannel("SteamGuard", this.context.getString(R.string.Steam_Guard), 4);
            notificationChannel2.setDescription(this.context.getString(R.string.Steam_Guard_Notification));
            notificationManager.createNotificationChannel(notificationChannel2);
        }
    }

    public void clearRecentNotificationsTracking() {
        this.chatPartnerNamesWithRecentlyMadeNotifications.clear();
    }

    public void clearRecentNotificationsTrackingFor(String str) {
        this.chatPartnerNamesWithRecentlyMadeNotifications.remove(str);
    }

    public void sendConfirmationNotification(String str) {
        sendNotification(SteamCommunityApplication.GetInstance().getResources().getString(R.string.ConfirmationTitle), str, PendingIntent.getActivity(this.context, 4, SteamAppIntents.viewConfirmations(this.context), 0), 4, shouldRingForGenericNotification(), shouldVibrateForGenericNotification());
    }

    public void sendTwoFactorPromptNotification(String str) {
        SteamguardState steamguardStateForGID;
        if (str == null || (steamguardStateForGID = SteamguardState.steamguardStateForGID(str)) == null || steamguardStateForGID.getTwoFactorToken() == null) {
            return;
        }
        sendNotificationWithHeadsUp(this.context.getResources().getString(R.string.notification_newlogin).replaceAll("%accountname%", steamguardStateForGID.getAccountName()), this.context.getResources().getString(R.string.notification_newlogin_short_body).replaceAll("%code%", steamguardStateForGID.getTwoFactorToken().generateSteamGuardCode()), PendingIntent.getActivity(this.context, 5, SteamAppIntents.viewSteamGuard(this.context), 134217728), 5, false, false, null);
        Intent data = new Intent(this.context, (Class<?>) NotificationDeleteReceiver.class).setData(SteamAppUri.deleteNotification());
        data.putExtra(SteamAppIntents.notificationId, 5);
        ((AlarmManager) this.context.getSystemService("alarm")).set(1, System.currentTimeMillis() + 60000, PendingIntent.getBroadcast(this.context, 1000, data, 1207959552));
    }

    public void sendChatNotification(String str, String str2, boolean z) {
        Intent viewFriendsList;
        String str3;
        String str4;
        String str5 = str != null ? str : "";
        if (z) {
            str5 = str5 + "...";
        }
        ChatNotification chatNotification = this.lastChatNotification;
        if (chatNotification == null || !chatNotification.matches(str2, str5, System.currentTimeMillis())) {
            ChatNotification chatNotification2 = new ChatNotification();
            chatNotification2.from = str2;
            chatNotification2.message = str5;
            chatNotification2.timeProcessed = System.currentTimeMillis();
            this.lastChatNotification = chatNotification2;
            LocalDb localDb = SteamCommunityApplication.GetInstance().getLocalDb();
            String str6 = "";
            List<ChatNotification> arrayList = new ArrayList<>();
            if (localDb != null) {
                str6 = localDb.getSteamIdForPersonaName(str2);
                localDb.saveChatNotification(chatNotification2);
                arrayList = localDb.getNotifications();
            }
            Map<String, List<String>> groupNotificationsBySender = groupNotificationsBySender(arrayList);
            boolean hasRungOrVibratedRecently = hasRungOrVibratedRecently();
            boolean z2 = !hasRungOrVibratedRecently && shouldRingForChat(str2);
            boolean z3 = !hasRungOrVibratedRecently && shouldVibrateForChat(str2);
            if (str2 != null) {
                this.chatPartnerNamesWithRecentlyMadeNotifications.add(str2);
            }
            if (groupNotificationsBySender == null || groupNotificationsBySender.keySet().size() <= 1) {
                if (str6 != null && str6.length() > 0) {
                    viewFriendsList = SteamAppIntents.chatIntent(this.context, str6);
                    str3 = str5;
                    str4 = str2;
                } else {
                    viewFriendsList = SteamAppIntents.viewFriendsList(this.context);
                    str3 = str5;
                    str4 = str2;
                }
            } else {
                Intent viewFriendsList2 = SteamAppIntents.viewFriendsList(this.context);
                str4 = this.context.getResources().getString(R.string.Chats);
                str3 = TextUtils.join(", ", groupNotificationsBySender.keySet().toArray());
                viewFriendsList = viewFriendsList2;
            }
            viewFriendsList.addFlags(538968064);
            viewFriendsList.setAction(Long.toString(System.currentTimeMillis()));
            sendNotificationWithHeadsUp(str4, str3, PendingIntent.getActivity(this.context, 1, viewFriendsList, 134217728), 1, z2, z3, this.context.getResources().getString(R.string.notification_chat));
        }
    }

    public void sendWishlistNotification(String str, String str2) {
        sendNotification(str, str2, PendingIntent.getActivity(this.context, 2, SteamAppIntents.viewWishList(this.context), 0), 2, shouldRingForGenericNotification(), shouldVibrateForGenericNotification());
    }

    public void sendPromotionNotification(String str, String str2) {
        sendNotification(str, str2, PendingIntent.getActivity(this.context, 3, SteamAppIntents.viewCatalog(this.context), 0), 3, shouldRingForGenericNotification(), shouldVibrateForGenericNotification());
    }

    public void ringOrVibrateAsNeededForChat(String str) {
        if (hasRungOrVibratedRecently()) {
            return;
        }
        this.lastRingOrVibrateTime = System.currentTimeMillis();
        if (shouldVibrateForChat(str)) {
            vibrate();
        }
        if (shouldRingForChat(str)) {
            ringtone();
        }
        if (str != null) {
            this.chatPartnerNamesWithRecentlyMadeNotifications.add(str);
        }
    }

    private boolean hasRungOrVibratedRecently() {
        return System.currentTimeMillis() - this.lastRingOrVibrateTime < 1000;
    }

    private boolean shouldVibrateForChat(String str) {
        int integerValue = this.settingInfoDB.m_settingVibrate.getIntegerValue(SteamCommunityApplication.GetInstance().getApplicationContext());
        return integerValue == -1 || (integerValue == 0 && !this.chatPartnerNamesWithRecentlyMadeNotifications.contains(str));
    }

    private boolean shouldRingForChat(String str) {
        int integerValue = this.settingInfoDB.m_settingSound.getIntegerValue(SteamCommunityApplication.GetInstance().getApplicationContext());
        return integerValue == -1 || (integerValue == 0 && !this.chatPartnerNamesWithRecentlyMadeNotifications.contains(str));
    }

    private boolean shouldVibrateForGenericNotification() {
        return this.settingInfoDB.m_settingVibrate.getIntegerValue(SteamCommunityApplication.GetInstance().getApplicationContext()) != 1;
    }

    private boolean shouldRingForGenericNotification() {
        return this.settingInfoDB.m_settingSound.getIntegerValue(SteamCommunityApplication.GetInstance().getApplicationContext()) != 1;
    }

    private void ringtone() {
        Ringtone ringtone = RingtoneManager.getRingtone(this.steamCommunityApplication.getApplicationContext(), getRingToneUri());
        if (ringtone != null) {
            ringtone.play();
        }
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) this.steamCommunityApplication.getSystemService("vibrator");
        if (vibrator != null) {
            vibrator.vibrate(200L);
        }
    }

    private Uri getRingToneUri() {
        try {
            return Uri.parse(this.settingInfoDB.m_settingRing.getValue(this.steamCommunityApplication.getApplicationContext()));
        } catch (RuntimeException unused) {
            return Uri.parse(this.settingInfoDB.m_settingRing.m_defaultValue);
        }
    }

    private void sendNotificationWithHeadsUp(String str, String str2, PendingIntent pendingIntent, int i, boolean z, boolean z2, String str3) {
        if (str == null) {
            str = "";
        }
        if (str2 == null) {
            str2 = "";
        }
        NotificationCompat.Builder priority = new NotificationCompat.Builder(this.context, GetChannelForNotificationType(i)).setSmallIcon(R.drawable.notification_chat).setContentTitle(str).setContentText(str2).setContentIntent(pendingIntent).setCategory("msg").setVisibility(1).setPriority(1);
        if (str3 != null) {
            priority.setContentTitle(this.context.getResources().getString(R.string.app_name)).setContentText(str3).setPublicVersion(priority.build()).setVisibility(0).setContentTitle(str).setContentText(str2);
        }
        Notification build = priority.build();
        build.flags |= 16;
        if (z) {
            build.sound = getRingToneUri();
        }
        if (z2) {
            build.vibrate = new long[]{0, 200};
        }
        ((NotificationManager) this.context.getSystemService("notification")).notify(i, build);
    }

    private void sendNotification(String str, String str2, PendingIntent pendingIntent, int i, boolean z, boolean z2) {
        if (str == null) {
            str = "";
        }
        if (str2 == null) {
            str2 = "";
        }
        Notification build = new NotificationCompat.Builder(this.context, GetChannelForNotificationType(i)).setSmallIcon(R.drawable.notification_chat).setContentTitle(str).setContentText(str2).setContentIntent(pendingIntent).build();
        build.flags |= 16;
        if (z) {
            build.sound = getRingToneUri();
        }
        if (z2) {
            build.vibrate = new long[]{0, 200};
        }
        ((NotificationManager) this.context.getSystemService("notification")).notify(i, build);
    }

    private Map<String, List<String>> groupNotificationsBySender(List<ChatNotification> list) {
        HashMap hashMap = new HashMap();
        if (list == null) {
            return hashMap;
        }
        for (ChatNotification chatNotification : list) {
            if (chatNotification.from != null) {
                if (!hashMap.containsKey(chatNotification.from)) {
                    hashMap.put(chatNotification.from, new ArrayList());
                }
                ((List) hashMap.get(chatNotification.from)).add(chatNotification.message);
            }
        }
        return hashMap;
    }
}

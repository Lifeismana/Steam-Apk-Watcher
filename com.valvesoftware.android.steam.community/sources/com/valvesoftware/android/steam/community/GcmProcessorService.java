package com.valvesoftware.android.steam.community;

import android.os.Bundle;
import com.google.android.gms.gcm.GcmListenerService;

/* loaded from: classes.dex */
public class GcmProcessorService extends GcmListenerService {
    @Override // com.google.android.gms.gcm.GcmListenerService
    public void onMessageReceived(String str, Bundle bundle) {
        NotificationSender notificationSender = NotificationSender.getInstance();
        String string = bundle.getString("type");
        if (string != null && string.equals("tfpr")) {
            long j = 0;
            try {
                j = Long.parseLong(bundle.getString("timestamp"));
            } catch (Exception unused) {
            }
            if (System.currentTimeMillis() / 1000 <= j + 30) {
                notificationSender.sendTwoFactorPromptNotification(bundle.getString("gid"));
                return;
            }
            return;
        }
        if (LoggedInUserAccountInfo.isLoggedIn() && string != null) {
            if (string.equals("chat")) {
                if (SteamCommunityApplication.GetInstance().GetSettingInfoDB().usePushInBackground() && !UmqCommunicator.isUmqRunning()) {
                    notificationSender.sendChatNotification(bundle.getString("message"), bundle.getString("messageFrom"), "true".equals(bundle.getString("messageIsTruncated")));
                    return;
                }
                return;
            }
            if (string.equals("wishlist")) {
                notificationSender.sendWishlistNotification(bundle.getString("title"), bundle.getString("message"));
                return;
            }
            if (string.equals("promotion")) {
                notificationSender.sendPromotionNotification(bundle.getString("title"), bundle.getString("message"));
                return;
            }
            if (string.equals("conf")) {
                final String string2 = bundle.getString("alert");
                if (SteamCommunityApplication.isInForeground && SteamCommunityApplication.mMainActivity != null) {
                    SteamCommunityApplication.confirmationRefreshHandler.post(new Runnable() { // from class: com.valvesoftware.android.steam.community.GcmProcessorService.1
                        @Override // java.lang.Runnable
                        public void run() {
                            if (!SteamCommunityApplication.mMainActivity.refreshConfirmationsPageIfActive()) {
                                NotificationSender.getInstance().sendConfirmationNotification(string2);
                            }
                        }
                    });
                    return;
                } else {
                    notificationSender.sendConfirmationNotification(string2);
                    return;
                }
            }
            if (string.equals("rmtf")) {
                SteamguardState.handleTwoFactorRemovalNotification(bundle.getString("gid"), bundle.getString("scheme"));
            }
        }
    }
}

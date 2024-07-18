package com.valvesoftware.android.steam.community.model;

/* loaded from: classes.dex */
public class UserNotificationCounts {
    public static final int MAX_NOTIFICATION_TYPES = EUserNotification.k_EUserNotificationMax.value;
    private final int[] m_rgNotificationCounts = new int[MAX_NOTIFICATION_TYPES];

    /* loaded from: classes.dex */
    public enum EUserNotification {
        k_EUserNotificationInvalid(0, false),
        k_EUserNotificationTradeOffer(1, true),
        k_EUserNotificationAsyncGameState(2, true),
        k_EUserNotificationModeratorMessage(3, true),
        k_EUserNotificationComment(4, true),
        k_EUserNotificationItem(5, true),
        k_EUserNotificationFriendInvite(6, true),
        k_EUserNotificationGroupInvite(7, true),
        k_EUserNotificationGift(8, true),
        k_EUserNotificationOfflineMessage(9, true),
        k_EUserNotificationHelpRequestReply(10, true),
        k_EUserNotificationMax(11, false);

        public final boolean supported;
        public final int value;

        EUserNotification(int i, boolean z) {
            this.value = i;
            this.supported = z;
        }
    }

    public int GetTotalNotificationCount() {
        int i = 0;
        for (EUserNotification eUserNotification : EUserNotification.values()) {
            if (eUserNotification.supported) {
                i += this.m_rgNotificationCounts[eUserNotification.value];
            }
        }
        return i;
    }

    public void SetNotificationCount(int i, int i2) {
        if (i <= 0 || i >= MAX_NOTIFICATION_TYPES) {
            return;
        }
        this.m_rgNotificationCounts[i] = i2;
    }

    public int GetNotificationCount(EUserNotification eUserNotification) {
        if (eUserNotification == EUserNotification.k_EUserNotificationFriendInvite) {
            return this.m_rgNotificationCounts[EUserNotification.k_EUserNotificationFriendInvite.value] + this.m_rgNotificationCounts[EUserNotification.k_EUserNotificationGroupInvite.value];
        }
        return this.m_rgNotificationCounts[eUserNotification.value];
    }

    public void Clear() {
        for (int i = 0; i < MAX_NOTIFICATION_TYPES; i++) {
            this.m_rgNotificationCounts[i] = 0;
        }
    }

    public void UpdateNotificationCounts(UserNotificationCounts userNotificationCounts) {
        for (int i = 1; i < MAX_NOTIFICATION_TYPES; i++) {
            int[] iArr = userNotificationCounts.m_rgNotificationCounts;
            if (i < iArr.length) {
                this.m_rgNotificationCounts[i] = iArr[i];
            } else {
                this.m_rgNotificationCounts[i] = 0;
            }
        }
    }
}

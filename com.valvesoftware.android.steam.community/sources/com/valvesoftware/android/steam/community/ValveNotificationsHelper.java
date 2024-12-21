package com.valvesoftware.android.steam.community;

import android.util.Pair;
import com.google.firebase.messaging.RemoteMessage;
import java.util.ArrayList;
import java.util.HashMap;

/* loaded from: classes3.dex */
public class ValveNotificationsHelper {
    private static ValveNotificationsHelper sm_instance = new ValveNotificationsHelper();
    private HashMap<String, RemoteMessage> m_mapNotifications = new HashMap<>();
    private ArrayList<Pair<RemoteMessage, ENotificationAppState>> m_mapPendingNotifications = new ArrayList<>();
    private OnMessageReceivedListener m_listener = null;

    /* loaded from: classes3.dex */
    public interface OnMessageReceivedListener {
        void onMessageReceived(RemoteMessage remoteMessage);
    }

    public static ValveNotificationsHelper getInstance() {
        return sm_instance;
    }

    public void registerMessageReceivedListener(OnMessageReceivedListener onMessageReceivedListener) {
        this.m_listener = onMessageReceivedListener;
    }

    public void onMessageReceived(RemoteMessage remoteMessage) {
        this.m_mapNotifications.put(remoteMessage.getMessageId(), remoteMessage);
        OnMessageReceivedListener onMessageReceivedListener = this.m_listener;
        if (onMessageReceivedListener != null) {
            onMessageReceivedListener.onMessageReceived(remoteMessage);
        } else {
            this.m_mapPendingNotifications.add(new Pair<>(remoteMessage, ENotificationAppState.Inactive));
        }
    }

    public RemoteMessage getMessage(String str) {
        if (this.m_mapNotifications.containsKey(str)) {
            return this.m_mapNotifications.get(str);
        }
        return null;
    }

    /* loaded from: classes3.dex */
    public enum ENotificationAppState {
        Invalid(0),
        Pressed(1),
        Background(2),
        Foreground(3),
        Inactive(4);

        private final int value;

        ENotificationAppState(int i) {
            this.value = i;
        }

        public int getValue() {
            return this.value;
        }
    }

    public ArrayList getPendingNotifications() {
        return this.m_mapPendingNotifications;
    }

    public void clearPendingNotifications() {
        this.m_mapPendingNotifications.clear();
    }
}

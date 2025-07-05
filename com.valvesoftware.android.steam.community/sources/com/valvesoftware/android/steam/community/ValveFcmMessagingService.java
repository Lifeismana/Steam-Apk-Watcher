package com.valvesoftware.android.steam.community;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.HashMap;

/* loaded from: classes4.dex */
public class ValveFcmMessagingService extends FirebaseMessagingService {
    static HashMap<String, RemoteMessage> sm_notifications = new HashMap<>();

    @Override // com.google.firebase.messaging.FirebaseMessagingService
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        ValveNotificationsHelper.getInstance().onMessageReceived(remoteMessage);
    }
}

package com.valvesoftware.android.steam.community;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/* loaded from: classes.dex */
public class NotificationDeleteReceiver extends WakefulBroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        int intExtra = intent.getIntExtra(SteamAppIntents.notificationId, 0);
        String str = "Deleting notification " + intExtra;
        notificationManager.cancel(intExtra);
        setResultCode(-1);
    }
}

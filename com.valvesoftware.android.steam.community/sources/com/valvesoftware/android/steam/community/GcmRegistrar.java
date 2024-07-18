package com.valvesoftware.android.steam.community;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: classes.dex */
public class GcmRegistrar extends IntentService {
    private static final Handler gcmHandler = new Handler();
    private AtomicInteger numberOfFailedRegistrationAttempts;

    public GcmRegistrar() {
        super("GcmRegistrar");
        this.numberOfFailedRegistrationAttempts = new AtomicInteger(0);
    }

    @Override // android.app.IntentService
    public void onHandleIntent(Intent intent) {
        if (LoggedInUserAccountInfo.getLoginSteamID() == null) {
            return;
        }
        registerWithGcm();
    }

    public void registerWithGcm() {
        if (LoggedInUserAccountInfo.getLoginSteamID() == null) {
            return;
        }
        try {
            String token = InstanceID.getInstance(this).getToken("963091912489", "GCM", null);
            String str = "Device registered, registration ID=" + token;
            storeRegistrationIdAndSendToServer(SteamCommunityApplication.GetInstance().getApplicationContext(), token);
        } catch (Exception unused) {
        }
    }

    public String getStoredRegistrationId(Context context) {
        return getGcmPreferences(context).getString("registration_id", "");
    }

    public void storeRegistrationIdAndSendToServer(Context context, String str) {
        if (str == null || str.length() == 0) {
            return;
        }
        SharedPreferences gcmPreferences = getGcmPreferences(context);
        int appVersion = getAppVersion(context);
        SharedPreferences.Editor edit = gcmPreferences.edit();
        edit.putString("registration_id", str);
        edit.putInt("appVersion", appVersion);
        edit.putLong("lastRegTime", System.currentTimeMillis());
        edit.apply();
        UmqCommunicator.getInstance().setServerPushStateBasedOnUserPreference();
    }

    public void clearStoredRegistrationId(Context context) {
        SharedPreferences.Editor edit = getGcmPreferences(context).edit();
        edit.remove("registration_id");
        edit.apply();
    }

    private SharedPreferences getGcmPreferences(Context context) {
        return context.getSharedPreferences(GcmRegistrar.class.getSimpleName(), 0);
    }

    private static int getAppVersion(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException unused) {
            return 0;
        }
    }

    public void unregister(Context context) {
        unregister(context, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Type inference failed for: r0v0, types: [com.valvesoftware.android.steam.community.GcmRegistrar$1] */
    public void unregister(final Context context, final boolean z) {
        new AsyncTask() { // from class: com.valvesoftware.android.steam.community.GcmRegistrar.1
            @Override // android.os.AsyncTask
            protected Object doInBackground(Object[] objArr) {
                try {
                    GoogleCloudMessaging.getInstance(context).unregister();
                    GcmRegistrar.this.clearStoredRegistrationId(context);
                    return "";
                } catch (Exception unused) {
                    if (!z) {
                        return "Could not unregister with GCM";
                    }
                    GcmRegistrar.gcmHandler.postDelayed(new Runnable() { // from class: com.valvesoftware.android.steam.community.GcmRegistrar.1.1
                        @Override // java.lang.Runnable
                        public void run() {
                            GcmRegistrar.this.unregister(context, false);
                        }
                    }, 2000L);
                    return "";
                }
            }
        }.execute(null, null, null);
    }
}

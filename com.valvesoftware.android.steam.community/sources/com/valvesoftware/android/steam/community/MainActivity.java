package com.valvesoftware.android.steam.community;

import android.content.Intent;
import android.os.Bundle;
import com.facebook.react.ReactActivity;
import com.facebook.react.ReactActivityDelegate;
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint;
import com.facebook.react.defaults.DefaultReactActivityDelegate;
import com.google.firebase.messaging.Constants;
import com.google.firebase.messaging.RemoteMessage;
import expo.modules.ReactActivityDelegateWrapper;

/* loaded from: classes2.dex */
public class MainActivity extends ReactActivity {
    @Override // com.facebook.react.ReactActivity
    protected String getMainComponentName() {
        return "main";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.facebook.react.ReactActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(null);
        getWindow().setNavigationBarColor(getResources().getColor(C2256R.color.navigation_bar));
        if (getResources().getBoolean(C2256R.bool.portrait_only)) {
            setRequestedOrientation(1);
        }
        ProcessIntentFCMMessage(getIntent());
    }

    @Override // com.facebook.react.ReactActivity
    protected ReactActivityDelegate createReactActivityDelegate() {
        return new ReactActivityDelegateWrapper(this, new DefaultReactActivityDelegate(this, getMainComponentName(), DefaultNewArchitectureEntryPoint.getFabricEnabled()));
    }

    @Override // com.facebook.react.ReactActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ProcessIntentFCMMessage(intent);
    }

    private void ProcessIntentFCMMessage(Intent intent) {
        if (intent.hasExtra(Constants.MessagePayloadKeys.MSGID) && intent.hasExtra("type")) {
            ValveNotificationsHelper.getInstance().onMessageReceived(new RemoteMessage(intent.getExtras()));
        }
    }
}

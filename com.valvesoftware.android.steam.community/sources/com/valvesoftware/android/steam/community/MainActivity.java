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
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: MainActivity.kt */
@Metadata(m1519d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006J\b\u0010\u0007\u001a\u00020\bH\u0014J\b\u0010\t\u001a\u00020\nH\u0014J\u0012\u0010\u000b\u001a\u00020\u00042\b\u0010\f\u001a\u0004\u0018\u00010\rH\u0014J\u0010\u0010\u000e\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016¨\u0006\u000f"}, m1520d2 = {"Lcom/valvesoftware/android/steam/community/MainActivity;", "Lcom/facebook/react/ReactActivity;", "()V", "ProcessIntentFCMMessage", "", "intent", "Landroid/content/Intent;", "createReactActivityDelegate", "Lcom/facebook/react/ReactActivityDelegate;", "getMainComponentName", "", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onNewIntent", "app_release"}, m1521k = 1, m1522mv = {1, 9, 0}, m1524xi = 48)
/* loaded from: classes4.dex */
public final class MainActivity extends ReactActivity {
    @Override // com.facebook.react.ReactActivity
    protected String getMainComponentName() {
        return "main";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.facebook.react.ReactActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        Intent intent = getIntent();
        Intrinsics.checkNotNullExpressionValue(intent, "getIntent(...)");
        ProcessIntentFCMMessage(intent);
    }

    @Override // com.facebook.react.ReactActivity
    protected ReactActivityDelegate createReactActivityDelegate() {
        final String mainComponentName = getMainComponentName();
        final boolean fabricEnabled = DefaultNewArchitectureEntryPoint.getFabricEnabled();
        return new ReactActivityDelegateWrapper(this, false, new DefaultReactActivityDelegate(this, mainComponentName, fabricEnabled) { // from class: com.valvesoftware.android.steam.community.MainActivity$createReactActivityDelegate$1
            /* JADX INFO: Access modifiers changed from: package-private */
            {
                MainActivity mainActivity = this;
            }
        });
    }

    @Override // com.facebook.react.ReactActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onNewIntent(Intent intent) {
        Intrinsics.checkNotNullParameter(intent, "intent");
        super.onNewIntent(intent);
        ProcessIntentFCMMessage(intent);
    }

    public final void ProcessIntentFCMMessage(Intent intent) {
        Intrinsics.checkNotNullParameter(intent, "intent");
        if (intent.hasExtra(Constants.MessagePayloadKeys.MSGID) && intent.hasExtra("type")) {
            Bundle extras = intent.getExtras();
            Intrinsics.checkNotNull(extras);
            ValveNotificationsHelper.getInstance().onMessageReceived(new RemoteMessage(extras));
        }
    }
}

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
@Metadata(m995d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u0014J\b\u0010\b\u001a\u00020\tH\u0014J\b\u0010\n\u001a\u00020\u000bH\u0014J\u0010\u0010\f\u001a\u00020\u00052\u0006\u0010\r\u001a\u00020\u000eH\u0016J\u000e\u0010\u000f\u001a\u00020\u00052\u0006\u0010\r\u001a\u00020\u000e¨\u0006\u0010"}, m996d2 = {"Lcom/valvesoftware/android/steam/community/MainActivity;", "Lcom/facebook/react/ReactActivity;", "<init>", "()V", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "getMainComponentName", "", "createReactActivityDelegate", "Lcom/facebook/react/ReactActivityDelegate;", "onNewIntent", "intent", "Landroid/content/Intent;", "ProcessIntentFCMMessage", "app_release"}, m997k = 1, m998mv = {2, 0, 0}, m1000xi = 48)
/* loaded from: classes3.dex */
public final class MainActivity extends ReactActivity {
    @Override // com.facebook.react.ReactActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        Intent intent = getIntent();
        Intrinsics.checkNotNullExpressionValue(intent, "getIntent(...)");
        ProcessIntentFCMMessage(intent);
    }

    @Override // com.facebook.react.ReactActivity
    protected String getMainComponentName() {
        return "main";
    }

    @Override // com.facebook.react.ReactActivity
    protected ReactActivityDelegate createReactActivityDelegate() {
        return new ReactActivityDelegateWrapper(this, true, new DefaultReactActivityDelegate(this, getMainComponentName(), DefaultNewArchitectureEntryPoint.getFabricEnabled()) { // from class: com.valvesoftware.android.steam.community.MainActivity.createReactActivityDelegate.1
            {
                MainActivity mainActivity = this;
            }
        });
    }

    @Override // com.facebook.react.ReactActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onNewIntent(Intent intent) {
        Intrinsics.checkNotNullParameter(intent, "intent");
        super.onNewIntent(intent);
        setIntent(intent);
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

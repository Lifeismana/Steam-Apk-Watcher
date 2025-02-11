package com.valvesoftware.android.steam.friendsui;

import android.os.Build;
import android.os.Bundle;
import com.facebook.react.ReactActivity;
import com.facebook.react.ReactActivityDelegate;
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint;
import com.facebook.react.defaults.DefaultReactActivityDelegate;
import expo.modules.ReactActivityDelegateWrapper;

/* loaded from: classes2.dex */
public class MainActivity extends ReactActivity {
    @Override // com.facebook.react.ReactActivity
    protected String getMainComponentName() {
        return "main";
    }

    @Override // com.facebook.react.ReactActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        setTheme(C2285R.style.AppTheme);
        super.onCreate(null);
    }

    @Override // com.facebook.react.ReactActivity
    protected ReactActivityDelegate createReactActivityDelegate() {
        return new ReactActivityDelegateWrapper(this, false, new DefaultReactActivityDelegate(this, getMainComponentName(), DefaultNewArchitectureEntryPoint.getFabricEnabled()));
    }

    @Override // com.facebook.react.ReactActivity, com.facebook.react.modules.core.DefaultHardwareBackBtnHandler
    public void invokeDefaultOnBackPressed() {
        if (Build.VERSION.SDK_INT <= 30) {
            if (moveTaskToBack(false)) {
                return;
            }
            super.invokeDefaultOnBackPressed();
            return;
        }
        super.invokeDefaultOnBackPressed();
    }
}

package com.valvesoftware.android.steam.friendsui;

import android.os.Build;
import android.os.Bundle;
import com.facebook.react.ReactActivity;
import com.facebook.react.ReactActivityDelegate;
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint;
import com.facebook.react.defaults.DefaultReactActivityDelegate;
import expo.modules.ReactActivityDelegateWrapper;
import kotlin.Metadata;

/* compiled from: MainActivity.kt */
@Metadata(m693d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u0014J\b\u0010\b\u001a\u00020\tH\u0014J\b\u0010\n\u001a\u00020\u000bH\u0014J\b\u0010\f\u001a\u00020\u0005H\u0016¨\u0006\r"}, m694d2 = {"Lcom/valvesoftware/android/steam/friendsui/MainActivity;", "Lcom/facebook/react/ReactActivity;", "<init>", "()V", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "getMainComponentName", "", "createReactActivityDelegate", "Lcom/facebook/react/ReactActivityDelegate;", "invokeDefaultOnBackPressed", "app_release"}, m695k = 1, m696mv = {2, 0, 0}, m698xi = 48)
/* loaded from: classes3.dex */
public final class MainActivity extends ReactActivity {
    @Override // com.facebook.react.ReactActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(C2306R.style.AppTheme);
        super.onCreate(null);
    }

    @Override // com.facebook.react.ReactActivity
    protected String getMainComponentName() {
        return "main";
    }

    @Override // com.facebook.react.ReactActivity
    protected ReactActivityDelegate createReactActivityDelegate() {
        final String mainComponentName = getMainComponentName();
        final boolean fabricEnabled = DefaultNewArchitectureEntryPoint.getFabricEnabled();
        return new ReactActivityDelegateWrapper(this, true, new DefaultReactActivityDelegate(this, mainComponentName, fabricEnabled) { // from class: com.valvesoftware.android.steam.friendsui.MainActivity$createReactActivityDelegate$1
            {
                MainActivity mainActivity = this;
            }
        });
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

package com.valvesoftware.android.steam.friendsui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.view.MotionEvent;
import androidx.core.content.FileProvider;
import androidx.webkit.internal.AssetHelper;
import com.facebook.react.ReactApplication;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.network.ForwardingCookieHandler;
import expo.modules.imagepicker.MediaTypes;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;

/* loaded from: classes2.dex */
public class ValveHelpersModule extends ReactContextBaseJavaModule {
    private ForwardingCookieHandler m_CookieHandler;

    @Override // com.facebook.react.bridge.NativeModule
    public String getName() {
        return "ValveHelpers";
    }

    public ValveHelpersModule(ReactApplicationContext reactApplicationContext) {
        super(reactApplicationContext);
        this.m_CookieHandler = new ForwardingCookieHandler(reactApplicationContext);
    }

    @ReactMethod
    public void SynthesizeActionUp() {
        final Activity currentActivity = getCurrentActivity();
        if (currentActivity == null) {
            return;
        }
        currentActivity.runOnUiThread(new Runnable() { // from class: com.valvesoftware.android.steam.friendsui.ValveHelpersModule.1
            @Override // java.lang.Runnable
            public void run() {
                currentActivity.getWindow().getDecorView().getRootView().dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 1, 0.0f, 0.0f, 0));
            }
        });
    }

    @ReactMethod
    public void ShareImage(String str, String str2, String str3) {
        Activity currentActivity = getCurrentActivity();
        if (currentActivity == null) {
            return;
        }
        Uri uriForFile = FileProvider.getUriForFile(currentActivity, "com.valvesoftware.android.steam.friendsui.provider", new File(str2));
        Intent type = new Intent("android.intent.action.SEND").setType(MediaTypes.ImageAllMimeType);
        type.putExtra("android.intent.extra.STREAM", uriForFile);
        type.putExtra("android.intent.extra.TEXT", str);
        currentActivity.startActivity(Intent.createChooser(type, str3));
    }

    @ReactMethod
    public void ShareText(String str, String str2) {
        Activity currentActivity = getCurrentActivity();
        if (currentActivity == null) {
            return;
        }
        Intent type = new Intent("android.intent.action.SEND").setType(AssetHelper.DEFAULT_MIME_TYPE);
        type.putExtra("android.intent.extra.TEXT", str);
        currentActivity.startActivity(Intent.createChooser(type, str2));
    }

    @ReactMethod
    public void ShowReactNativeDevMenu() {
        final Activity currentActivity = getCurrentActivity();
        if (currentActivity == null) {
            return;
        }
        currentActivity.runOnUiThread(new Runnable() { // from class: com.valvesoftware.android.steam.friendsui.ValveHelpersModule.2
            @Override // java.lang.Runnable
            public void run() {
                ((ReactApplication) currentActivity.getApplication()).getReactNativeHost().getReactInstanceManager().showDevOptionsDialog();
            }
        });
    }

    @ReactMethod
    public void setCookie(String str, String str2, String str3) throws IOException, URISyntaxException {
        URI uri = new URI(str2);
        HashMap hashMap = new HashMap();
        hashMap.put("Set-cookie", Collections.singletonList(str + "=" + str3 + "; path=/"));
        this.m_CookieHandler.put(uri, hashMap);
    }
}

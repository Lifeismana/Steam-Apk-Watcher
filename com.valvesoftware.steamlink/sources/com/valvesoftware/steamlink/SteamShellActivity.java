package com.valvesoftware.steamlink;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import java.util.Locale;
import org.libsdl.app.HIDDeviceManager;
import org.libsdl.app.SDL;
import org.libsdl.app.SDLActivity;
import org.libsdl.app.SDLControllerManager;
import org.qtproject.qt5.android.bindings.QtActivity;
import org.qtproject.qt5.android.bindings.QtApplication;

/* loaded from: classes.dex */
public class SteamShellActivity extends QtActivity {
    public static final String TAG = "SteamShell";
    private HIDDeviceManager mHIDDeviceManager;
    private VirtualHere mVirtualHere;
    private boolean mStreamingInProgress = false;
    private Object mStreamingComplete = new Object();
    protected final int[] messageboxSelection = new int[1];

    public int messageboxShowMessageBox(int i, String str, String str2, int[] iArr, int[] iArr2, String[] strArr, int[] iArr3) {
        return -1;
    }

    @Override // org.qtproject.qt5.android.bindings.QtActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mHIDDeviceManager = HIDDeviceManager.acquire(this);
        this.mVirtualHere = VirtualHere.acquire(this);
        startActivity();
    }

    protected void startActivity() {
        SDL.initialize();
        SDL.setContext(this);
    }

    protected void stopActivity() {
        if (SDL.getContext() == this) {
            SDL.setContext(null);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.qtproject.qt5.android.bindings.QtActivity, android.app.Activity
    public void onPause() {
        super.onPause();
        HIDDeviceManager hIDDeviceManager = this.mHIDDeviceManager;
        if (hIDDeviceManager != null) {
            hIDDeviceManager.setFrozen(true);
        }
    }

    @Override // org.qtproject.qt5.android.bindings.QtActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        HIDDeviceManager hIDDeviceManager = this.mHIDDeviceManager;
        if (hIDDeviceManager != null) {
            hIDDeviceManager.setFrozen(false);
        }
        getWindow().getDecorView().setSystemUiVisibility(5638);
        getWindow().addFlags(128);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.qtproject.qt5.android.bindings.QtActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        stopActivity();
        HIDDeviceManager hIDDeviceManager = this.mHIDDeviceManager;
        if (hIDDeviceManager != null) {
            HIDDeviceManager.release(hIDDeviceManager);
            this.mHIDDeviceManager = null;
        }
        VirtualHere virtualHere = this.mVirtualHere;
        if (virtualHere != null) {
            VirtualHere.release(virtualHere);
            this.mVirtualHere = null;
        }
        if (this.mStreamingInProgress) {
            return;
        }
        QtApplication.invokeDelegate(new Object[0]);
    }

    @Override // org.qtproject.qt5.android.bindings.QtActivity, android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        InputDevice device;
        int deviceId = keyEvent.getDeviceId();
        if (keyEvent.getSource() == 0 && (device = InputDevice.getDevice(deviceId)) != null) {
            device.getSources();
        }
        if (SDLControllerManager.isDeviceSDLJoystick(deviceId) && SDLControllerManager.onNativePadDown(deviceId, i) == 0) {
            return true;
        }
        return super.onKeyDown(i, keyEvent);
    }

    @Override // org.qtproject.qt5.android.bindings.QtActivity, android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        InputDevice device;
        int deviceId = keyEvent.getDeviceId();
        if (keyEvent.getSource() == 0 && (device = InputDevice.getDevice(deviceId)) != null) {
            device.getSources();
        }
        if (SDLControllerManager.isDeviceSDLJoystick(deviceId) && SDLControllerManager.onNativePadUp(deviceId, i) == 0) {
            return true;
        }
        return super.onKeyUp(i, keyEvent);
    }

    @Override // org.qtproject.qt5.android.bindings.QtActivity, android.app.Activity
    public boolean onGenericMotionEvent(MotionEvent motionEvent) {
        int source = motionEvent.getSource();
        if (source == 513 || source == 1025 || source == 16777232) {
            return SDLControllerManager.handleJoystickMotionEvent(motionEvent);
        }
        return super.onGenericMotionEvent(motionEvent);
    }

    public void startStreaming(String[] strArr) {
        Runnable runnable = new Runnable() { // from class: com.valvesoftware.steamlink.SteamShellActivity.1
            @Override // java.lang.Runnable
            public void run() {
                SteamShellActivity.this.stopActivity();
                synchronized (this) {
                    notify();
                }
            }
        };
        try {
            synchronized (runnable) {
                runOnUiThread(runnable);
                runnable.wait();
            }
        } catch (InterruptedException unused) {
            Log.e(TAG, "Semaphore interrupted while we waited!");
        }
        this.mStreamingInProgress = true;
        Intent intent = new Intent();
        intent.setClassName(BuildConfig.APPLICATION_ID, "com.valvesoftware.steamlink.SteamLink");
        intent.putExtra("args", strArr);
        startActivity(intent);
        try {
            synchronized (this.mStreamingComplete) {
                this.mStreamingComplete.wait();
            }
        } catch (InterruptedException unused2) {
        }
        this.mStreamingInProgress = false;
        Runnable runnable2 = new Runnable() { // from class: com.valvesoftware.steamlink.SteamShellActivity.2
            @Override // java.lang.Runnable
            public void run() {
                SteamShellActivity.this.startActivity();
                synchronized (this) {
                    notify();
                }
            }
        };
        try {
            synchronized (runnable2) {
                runOnUiThread(runnable2);
                runnable2.wait();
            }
        } catch (InterruptedException unused3) {
            Log.e(TAG, "Semaphore interrupted while we waited!");
        }
    }

    public static void onStreamingResult(int i) {
        System.exit(0);
    }

    public static void onShellComplete() {
        System.exit(0);
    }

    public String getLocale() {
        Locale locale = Locale.getDefault();
        return locale.getLanguage() + "_" + locale.getCountry();
    }

    @Override // org.qtproject.qt5.android.bindings.QtActivity, android.app.Activity
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        if (iArr.length > 0 && iArr[0] == 0) {
            SDLActivity.nativePermissionResult(i, true);
        } else {
            SDLActivity.nativePermissionResult(i, false);
        }
    }
}

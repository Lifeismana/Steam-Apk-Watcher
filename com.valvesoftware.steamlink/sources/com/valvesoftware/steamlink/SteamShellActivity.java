package com.valvesoftware.steamlink;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.window.OnBackInvokedCallback;
import org.libsdl.app.HIDDeviceManager;
import org.libsdl.app.SDL;
import org.libsdl.app.SDLActivity;
import org.libsdl.app.SDLControllerManager;
import org.qtproject.qt5.android.bindings.QtActivity;
import org.qtproject.qt5.android.bindings.QtApplication;

/* JADX INFO: loaded from: classes.dex */
public class SteamShellActivity extends QtActivity {
    public static final String TAG = "SteamShell";
    OnBackInvokedCallback backButtonCallback;
    private HIDDeviceManager mHIDDeviceManager;
    private VirtualHere mVirtualHere;
    private boolean mStreamingInProgress = false;
    private Object mStreamingComplete = new Object();
    boolean mBackKeyTrapEnabled = false;
    protected final int[] messageboxSelection = new int[1];

    public int messageboxShowMessageBox(int i, String str, String str2, int[] iArr, int[] iArr2, String[] strArr, int[] iArr3) {
        return -1;
    }

    native void nativeSendEscape(boolean z);

    @Override // org.qtproject.qt5.android.bindings.QtActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mHIDDeviceManager = HIDDeviceManager.acquire(this);
        this.mVirtualHere = VirtualHere.acquire(this);
        setBackButtonTrapEnabled(true);
        startActivity();
    }

    protected void startActivity() {
        SDL.initialize();
        SDL.setContext(this);
        SDLControllerManager.initializeDeviceListener();
    }

    protected void stopActivity() {
        if (SDL.getContext() == this) {
            SDLControllerManager.shutdownDeviceListener();
            SDL.setContext(null);
        }
    }

    @Override // org.qtproject.qt5.android.bindings.QtActivity, android.app.Activity
    protected void onPause() {
        super.onPause();
        HIDDeviceManager hIDDeviceManager = this.mHIDDeviceManager;
        if (hIDDeviceManager != null) {
            hIDDeviceManager.setFrozen(true);
        }
    }

    @Override // org.qtproject.qt5.android.bindings.QtActivity, android.app.Activity, android.view.Window.Callback
    public void onWindowFocusChanged(boolean z) {
        HIDDeviceManager hIDDeviceManager;
        super.onWindowFocusChanged(z);
        if ((z || !SDLActivity.nativeGetHintBoolean("SDL_JOYSTICK_ALLOW_BACKGROUND_EVENTS", false)) && (hIDDeviceManager = this.mHIDDeviceManager) != null) {
            hIDDeviceManager.setFrozen(!z);
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

    @Override // org.qtproject.qt5.android.bindings.QtActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        if (!this.mStreamingInProgress) {
            QtApplication.invokeDelegate(new Object[0]);
        }
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
    }

    public void setBackButtonTrapEnabled(boolean z) {
        if (Build.VERSION.SDK_INT >= 33 && z != this.mBackKeyTrapEnabled) {
            if (this.backButtonCallback == null) {
                this.backButtonCallback = new OnBackInvokedCallback() { // from class: com.valvesoftware.steamlink.SteamShellActivity.1
                    Handler mBackKeyHandler;

                    @Override // android.window.OnBackInvokedCallback
                    public void onBackInvoked() {
                        if (this.mBackKeyHandler == null) {
                            this.mBackKeyHandler = new Handler(Looper.getMainLooper());
                        }
                        SteamShellActivity.this.nativeSendEscape(true);
                        this.mBackKeyHandler.postDelayed(new Runnable() { // from class: com.valvesoftware.steamlink.SteamShellActivity.1.1
                            @Override // java.lang.Runnable
                            public void run() {
                                SteamShellActivity.this.nativeSendEscape(false);
                            }
                        }, 500L);
                    }
                };
            }
            if (z) {
                getOnBackInvokedDispatcher().registerOnBackInvokedCallback(0, this.backButtonCallback);
            } else {
                getOnBackInvokedDispatcher().unregisterOnBackInvokedCallback(this.backButtonCallback);
            }
            this.mBackKeyTrapEnabled = z;
        }
    }

    @Override // org.qtproject.qt5.android.bindings.QtActivity, android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        InputDevice device;
        int deviceId = keyEvent.getDeviceId();
        if (keyEvent.getSource() == 0 && (device = InputDevice.getDevice(deviceId)) != null) {
            device.getSources();
        }
        if (SDLControllerManager.isDeviceSDLJoystick(deviceId) && keyEvent.getRepeatCount() == 0 && SDLControllerManager.onNativePadDown(deviceId, i, keyEvent.getScanCode())) {
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
        if (SDLControllerManager.isDeviceSDLJoystick(deviceId) && SDLControllerManager.onNativePadUp(deviceId, i, keyEvent.getScanCode())) {
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

    public void startStreaming(String[] strArr, int i) {
        Runnable runnable = new Runnable() { // from class: com.valvesoftware.steamlink.SteamShellActivity.2
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
        intent.putExtra("lowLatencyVideoMode", i);
        startActivity(intent);
        try {
            synchronized (this.mStreamingComplete) {
                this.mStreamingComplete.wait();
            }
        } catch (InterruptedException unused2) {
        }
        this.mStreamingInProgress = false;
        Runnable runnable2 = new Runnable() { // from class: com.valvesoftware.steamlink.SteamShellActivity.3
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

    public void startVRLink(String str, String str2) {
        this.mStreamingInProgress = true;
        Intent intent = new Intent();
        intent.addCategory("com.oculus.intent.category.VR");
        intent.setComponent(new ComponentName("com.valvesoftware.steamlinkvr", "android.app.NativeActivity"));
        Intent intentMakeRestartActivityTask = Intent.makeRestartActivityTask(intent.getComponent());
        intentMakeRestartActivityTask.putExtra("sOriginalPackage", "com.valvesoftware.steamlinkvr");
        intentMakeRestartActivityTask.putExtra("sOriginalActivity", getClass().getName());
        intentMakeRestartActivityTask.putExtra("sNetworkTest", str2);
        intentMakeRestartActivityTask.putExtra("sArgs", str);
        intentMakeRestartActivityTask.putExtra("sNetworkTestResults", str2);
        if (getPackageManager().hasSystemFeature("oculus.software.vr.app.hybrid")) {
            Log.v(TAG, "Hybrid support found. Launching activity.");
            intentMakeRestartActivityTask.addFlags(268435456);
            startActivity(intentMakeRestartActivityTask);
            return;
        }
        Log.v(TAG, "Hybrid support not found. Launching activity using legacy method.");
        intentMakeRestartActivityTask.addCategory("com.oculus.intent.category.VR");
        ((AlarmManager) getSystemService("alarm")).set(3, SystemClock.elapsedRealtime(), PendingIntent.getActivity(SDL.getContext(), 354678, intentMakeRestartActivityTask, 33554432));
        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

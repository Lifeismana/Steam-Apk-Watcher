package com.valvesoftware.steamlink;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import org.libsdl.app.HIDDeviceManager;
import org.libsdl.app.SDL;
import org.libsdl.app.SDLActivity;
import org.libsdl.app.SDLControllerManager;
import org.qtproject.qt5.android.bindings.QtActivity;

/* loaded from: classes.dex */
public class SteamShellActivity extends QtActivity {
    public static final String TAG = "SteamShell";
    private static int nPauseRemoveState = 0;
    private static boolean sStreamingInProgress = false;
    private HIDDeviceManager mHIDDeviceManager = null;
    private VirtualHere mVirtualHere = null;
    private ShellWifiInfo mWifiInfo = null;
    private Object mStreamingComplete = new Object();
    protected final int[] messageboxSelection = new int[1];

    public int messageboxShowMessageBox(int i, String str, String str2, int[] iArr, int[] iArr2, String[] strArr, int[] iArr3) {
        return -1;
    }

    public void streamingComplete(int i) {
    }

    /* loaded from: classes.dex */
    public class ShellWifiInfo extends ConnectivityManager.NetworkCallback {
        private Context mContext;
        public int m_nNetworkID = -1;
        public String m_sSSID = "";
        public int m_nFrequency = 0;
        public int m_nStrength = 0;

        public ShellWifiInfo(Context context) {
            this.mContext = context;
        }

        public void Start() {
            ((ConnectivityManager) this.mContext.getSystemService(ConnectivityManager.class)).registerNetworkCallback(new NetworkRequest.Builder().addTransportType(1).build(), this);
            Update();
        }

        public void Stop() {
            ((ConnectivityManager) this.mContext.getSystemService(ConnectivityManager.class)).unregisterNetworkCallback(this);
        }

        public void Update() {
            WifiInfo connectionInfo = ((WifiManager) this.mContext.getSystemService("wifi")).getConnectionInfo();
            int networkId = connectionInfo.getNetworkId();
            this.m_nNetworkID = networkId;
            if (networkId < 0) {
                this.m_sSSID = "";
            } else {
                this.m_sSSID = connectionInfo.getSSID();
            }
            this.m_nFrequency = connectionInfo.getFrequency();
            this.m_nStrength = WifiManager.calculateSignalLevel(connectionInfo.getRssi(), 3);
        }

        @Override // android.net.ConnectivityManager.NetworkCallback
        public void onAvailable(Network network) {
            Update();
        }

        @Override // android.net.ConnectivityManager.NetworkCallback
        public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
            Update();
        }

        @Override // android.net.ConnectivityManager.NetworkCallback
        public void onLost(Network network) {
            Update();
        }
    }

    @Override // org.qtproject.qt5.android.bindings.QtActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        nPauseRemoveState = 0;
        Log.v(TAG, "__onCreate, sStreamingInProgress = " + sStreamingInProgress);
        if (sStreamingInProgress) {
            Log.v(TAG, "Relaunching shell activity");
            ((AlarmManager) getSystemService("alarm")).set(3, SystemClock.elapsedRealtime(), PendingIntent.getActivity(this, 354678, getIntent(), 33554432));
            System.exit(0);
        }
        super.onCreate(bundle);
        this.mWifiInfo = new ShellWifiInfo(this);
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
        Log.v(TAG, "__onPause");
        super.onPause();
        this.mWifiInfo.Stop();
        HIDDeviceManager hIDDeviceManager = this.mHIDDeviceManager;
        if (hIDDeviceManager != null) {
            hIDDeviceManager.setFrozen(true);
        }
        int i = nPauseRemoveState;
        if (i == 1) {
            nPauseRemoveState = 2;
        } else if (i == 3) {
            nPauseRemoveState = 4;
        }
    }

    @Override // org.qtproject.qt5.android.bindings.QtActivity, android.app.Activity
    public void onResume() {
        Log.v(TAG, "__onResume");
        super.onResume();
        this.mWifiInfo.Start();
        HIDDeviceManager hIDDeviceManager = this.mHIDDeviceManager;
        if (hIDDeviceManager != null) {
            hIDDeviceManager.setFrozen(false);
        }
        getWindow().getDecorView().setSystemUiVisibility(5638);
        getWindow().addFlags(128);
        int i = nPauseRemoveState;
        if (i == 0) {
            nPauseRemoveState = 1;
        } else if (i == 2) {
            nPauseRemoveState = 3;
        } else if (i == 4) {
            nPauseRemoveState = 5;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.qtproject.qt5.android.bindings.QtActivity, android.app.Activity
    public void onStop() {
        Log.v(TAG, "__onStop");
        super.onStop();
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
        sStreamingInProgress = true;
        Intent intent = new Intent();
        intent.setClassName("com.valvesoftware.steamlink", "com.valvesoftware.steamlink.SteamLink");
        intent.putExtra("args", strArr);
        startActivity(intent);
        try {
            synchronized (this.mStreamingComplete) {
                this.mStreamingComplete.wait();
            }
        } catch (InterruptedException unused2) {
        }
        sStreamingInProgress = false;
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
        Log.v(TAG, "Streaming activity complete, exiting");
        System.exit(0);
    }

    public static void onShellComplete() {
        if (sStreamingInProgress) {
            Log.v(TAG, "Shell activity complete, streaming in progress");
        } else {
            Log.v(TAG, "Shell activity complete, exiting");
            System.exit(0);
        }
    }

    public ShellWifiInfo getWifiInfo() {
        return this.mWifiInfo;
    }

    public void openWifiSettings() {
        startActivity(new Intent("android.settings.WIFI_SETTINGS"));
    }

    public boolean canStartVRLink() {
        return nPauseRemoveState == 5;
    }

    public void startVRLink(String str) {
        sStreamingInProgress = true;
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(BuildConfig.APPLICATION_ID, "android.app.NativeActivity"));
        intent.addFlags(268435456);
        intent.putExtra("sOriginalPackage", BuildConfig.APPLICATION_ID);
        intent.putExtra("sOriginalActivity", getClass().getName());
        String[] split = str.split("~");
        if (split.length > 3) {
            intent.putExtra("sStartInfo", split[3]);
        }
        if (split.length < 1) {
            intent.putExtra("sGenInfo", "sArgs Is Empty");
        }
        intent.putExtra("sArgs", str);
        if (getPackageManager().hasSystemFeature("oculus.software.vr.app.hybrid")) {
            startActivity(intent);
        } else {
            Log.v(TAG, "Hybrid support not found. Launching activity using legacy method.");
            Intent.makeRestartActivityTask(intent.getComponent()).addCategory("com.oculus.intent.category.VR");
            ((AlarmManager) getSystemService("alarm")).set(3, SystemClock.elapsedRealtime(), PendingIntent.getActivity(this, 354678, intent, 33554432));
        }
        finishAndRemoveTask();
    }

    public boolean wasLaunchedFromVRLink() {
        String stringExtra = getIntent().getStringExtra("returnFrom");
        return stringExtra != null && stringExtra.equals("vrlink");
    }

    public String getMessageTitle() {
        return getIntent().getStringExtra("displayTitle");
    }

    public String getMessageText() {
        return getIntent().getStringExtra("displayMessage");
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

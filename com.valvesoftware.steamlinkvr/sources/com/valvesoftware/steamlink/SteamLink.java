package com.valvesoftware.steamlink;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import java.lang.reflect.Method;
import org.libsdl.app.SDL;
import org.libsdl.app.SDLActivity;

/* loaded from: classes.dex */
public class SteamLink extends SDLActivity {
    private static final String TAG = "SteamLink";
    VirtualHere mVirtualHere;
    ShellWifiInfo mWifiInfo = null;
    float m_flOverlayScale;
    View m_marginBottom;
    View m_marginLeft;
    View m_marginRight;
    View m_marginTop;
    int m_nOverlayHeight;
    int m_nOverlayWidth;
    SurfaceView m_overlaySurface;
    SurfaceView m_videoSurface;
    SurfaceTexture m_videoTexture;

    private native void freezeRendering();

    /* JADX INFO: Access modifiers changed from: private */
    public native void overlaySurfaceCreated(Surface surface);

    /* JADX INFO: Access modifiers changed from: private */
    public native void overlaySurfaceDestroyed();

    private native void thawRendering();

    private native boolean useVideoSurface();

    /* JADX INFO: Access modifiers changed from: private */
    public native void videoSurfaceCreated(Surface surface);

    /* JADX INFO: Access modifiers changed from: private */
    public native void videoSurfaceDestroyed();

    @Override // org.libsdl.app.SDLActivity
    protected String getMainFunction() {
        return "main";
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

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.libsdl.app.SDLActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setWindowStyle(true);
        if (useVideoSurface()) {
            createVideoSurface();
        }
        this.mVirtualHere = VirtualHere.acquire(this);
        this.mWifiInfo = new ShellWifiInfo(this);
        nativeSetenv("QT_PLUGIN_PATH", getApplicationInfo().nativeLibraryDir);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.libsdl.app.SDLActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        VirtualHere virtualHere = this.mVirtualHere;
        if (virtualHere != null) {
            VirtualHere.release(virtualHere);
            this.mVirtualHere = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.libsdl.app.SDLActivity, android.app.Activity
    public void onPause() {
        super.onPause();
        this.mWifiInfo.Stop();
    }

    @Override // org.libsdl.app.SDLActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        this.mWifiInfo.Start();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.libsdl.app.SDLActivity, android.app.Activity
    public void onStart() {
        super.onStart();
        thawRendering();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.libsdl.app.SDLActivity, android.app.Activity
    public void onStop() {
        super.onStop();
        freezeRendering();
    }

    @Override // org.libsdl.app.SDLActivity
    protected String getMainSharedObject() {
        return "libshell_" + Build.SUPPORTED_ABIS[0] + ".so";
    }

    @Override // org.libsdl.app.SDLActivity
    protected String[] getLibraries() {
        String str = Build.SUPPORTED_ABIS[0];
        return new String[]{"SDL3", "SDL3_image", "SDL3_mixer", "SDL3_ttf", "c++_shared", "Qt6Core_" + str, "Qt6Gui_" + str, "Qt6Network_" + str, "Qt6Widgets_" + str, "Qt6Svg_" + str, "shell_" + str, "h264bitstream", "hevcbitstream", "steamwebrtc"};
    }

    @Override // org.libsdl.app.SDLActivity
    protected String[] getArguments() {
        Uri data = getIntent().getData();
        return data != null ? new String[]{data.toString()} : new String[0];
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.libsdl.app.SDLActivity
    public boolean sendCommand(int i, Object obj) {
        if (i == 2 && ((Integer) obj).intValue() == 0) {
            return true;
        }
        return super.sendCommand(i, obj);
    }

    public ShellWifiInfo getWifiInfo() {
        return this.mWifiInfo;
    }

    public void openWifiSettings() {
        startActivity(new Intent("android.settings.WIFI_SETTINGS"));
    }

    public void startVRLink(String str) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(BuildConfig.APPLICATION_ID, "android.app.NativeActivity"));
        intent.addFlags(268468224);
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
            Log.v("SteamLink", "Hybrid support not found. Launching activity using legacy method.");
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

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class VideoSurfaceCallback implements SurfaceHolder.Callback {
        @Override // android.view.SurfaceHolder.Callback
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        }

        private VideoSurfaceCallback() {
        }

        @Override // android.view.SurfaceHolder.Callback
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            SteamLink.this.videoSurfaceCreated(surfaceHolder.getSurface());
        }

        @Override // android.view.SurfaceHolder.Callback
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            SteamLink.this.videoSurfaceDestroyed();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class OverlaySurfaceCallback implements SurfaceHolder.Callback {
        @Override // android.view.SurfaceHolder.Callback
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        }

        private OverlaySurfaceCallback() {
        }

        @Override // android.view.SurfaceHolder.Callback
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            SteamLink.this.overlaySurfaceCreated(surfaceHolder.getSurface());
        }

        @Override // android.view.SurfaceHolder.Callback
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            SteamLink.this.overlaySurfaceDestroyed();
        }
    }

    public void createVideoSurface() {
        SurfaceView surfaceView = new SurfaceView(getApplication());
        this.m_videoSurface = surfaceView;
        surfaceView.getHolder().addCallback(new VideoSurfaceCallback());
        mLayout.addView(this.m_videoSurface);
        this.m_nOverlayWidth = 1280;
        this.m_nOverlayHeight = 256;
        SurfaceView surfaceView2 = new SurfaceView(getApplication());
        this.m_overlaySurface = surfaceView2;
        surfaceView2.setZOrderOnTop(true);
        SurfaceHolder holder = this.m_overlaySurface.getHolder();
        holder.setFormat(1);
        holder.setFixedSize(this.m_nOverlayWidth, this.m_nOverlayHeight);
        holder.addCallback(new OverlaySurfaceCallback());
        mLayout.addView(this.m_overlaySurface);
        View view = new View(getApplication());
        this.m_marginLeft = view;
        view.setBackgroundColor(-16777216);
        mLayout.addView(this.m_marginLeft);
        View view2 = new View(getApplication());
        this.m_marginRight = view2;
        view2.setBackgroundColor(-16777216);
        mLayout.addView(this.m_marginRight);
        View view3 = new View(getApplication());
        this.m_marginTop = view3;
        view3.setBackgroundColor(-16777216);
        mLayout.addView(this.m_marginTop);
        View view4 = new View(getApplication());
        this.m_marginBottom = view4;
        view4.setBackgroundColor(-16777216);
        mLayout.addView(this.m_marginBottom);
        updateViewRects(0, 0, 0, 0);
        setVideoSurfaceVisible(false);
        setOverlaySurfaceVisible(false);
        try {
            Class<?> cls = Class.forName("android.view.PointerIcon");
            Method method = cls.getMethod("getSystemIcon", Context.class, Integer.TYPE);
            Method method2 = SurfaceView.class.getMethod("setPointerIcon", cls);
            method2.invoke(this.m_videoSurface, method.invoke(null, SDL.getContext(), 0));
            method2.invoke(this.m_overlaySurface, method.invoke(null, SDL.getContext(), 0));
        } catch (Exception unused) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setVideoSurfaceVisible(boolean z) {
        int i = z ? 0 : 8;
        this.m_videoSurface.setVisibility(i);
        this.m_marginLeft.setVisibility(i);
        this.m_marginRight.setVisibility(i);
        this.m_marginTop.setVisibility(i);
        this.m_marginBottom.setVisibility(i);
    }

    public void showVideoSurface() {
        Runnable runnable = new Runnable() { // from class: com.valvesoftware.steamlink.SteamLink.1
            @Override // java.lang.Runnable
            public void run() {
                SteamLink.this.setVideoSurfaceVisible(true);
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
            Log.e("SteamLink", "Semaphore interrupted while we waited!");
        }
    }

    public void hideVideoSurface() {
        Runnable runnable = new Runnable() { // from class: com.valvesoftware.steamlink.SteamLink.2
            @Override // java.lang.Runnable
            public void run() {
                SteamLink.this.setVideoSurfaceVisible(false);
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
            Log.e("SteamLink", "Semaphore interrupted while we waited!");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateViewRects(int i, int i2, int i3, int i4) {
        Display defaultDisplay = ((WindowManager) getApplication().getSystemService("window")).getDefaultDisplay();
        int width = defaultDisplay.getWidth();
        int height = defaultDisplay.getHeight();
        int i5 = (width - i3) - i;
        int i6 = (height - i4) - i2;
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(i3, i4);
        layoutParams.setMargins(i, i2, i5, i6);
        this.m_videoSurface.setLayoutParams(layoutParams);
        if (i < 0) {
            i = 0;
        }
        if (i5 < 0) {
            i5 = 0;
        }
        if (i2 < 0) {
            i2 = 0;
        }
        if (i6 < 0) {
            i6 = 0;
        }
        if (i > 0) {
            RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(i, height);
            layoutParams2.setMargins(0, 0, width - i, 0);
            this.m_marginLeft.setLayoutParams(layoutParams2);
            this.m_marginLeft.setVisibility(0);
        } else {
            this.m_marginLeft.setVisibility(8);
        }
        if (i5 > 0) {
            RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(i5, height);
            layoutParams3.setMargins(width - i5, 0, 0, 0);
            this.m_marginRight.setLayoutParams(layoutParams3);
            this.m_marginRight.setVisibility(0);
        } else {
            this.m_marginRight.setVisibility(8);
        }
        if (i2 > 0) {
            RelativeLayout.LayoutParams layoutParams4 = new RelativeLayout.LayoutParams(width, i2);
            layoutParams4.setMargins(0, 0, 0, height - i2);
            this.m_marginTop.setLayoutParams(layoutParams4);
            this.m_marginTop.setVisibility(0);
        } else {
            this.m_marginTop.setVisibility(8);
        }
        if (i6 > 0) {
            RelativeLayout.LayoutParams layoutParams5 = new RelativeLayout.LayoutParams(width, i6);
            layoutParams5.setMargins(0, height - i6, 0, 0);
            this.m_marginBottom.setLayoutParams(layoutParams5);
            this.m_marginBottom.setVisibility(0);
        } else {
            this.m_marginBottom.setVisibility(8);
        }
        int i7 = (width - i) - i5;
        int i8 = (height - i2) - i6;
        RelativeLayout.LayoutParams layoutParams6 = new RelativeLayout.LayoutParams(i7, i8);
        layoutParams6.setMargins(i, i2 + (i8 - ((int) (this.m_nOverlayHeight * (i7 / this.m_nOverlayWidth)))), i5, i6);
        this.m_overlaySurface.setLayoutParams(layoutParams6);
    }

    public void setVideoDisplayRect(final int i, final int i2, final int i3, final int i4) {
        Runnable runnable = new Runnable() { // from class: com.valvesoftware.steamlink.SteamLink.3
            @Override // java.lang.Runnable
            public void run() {
                SteamLink.this.updateViewRects(i, i2, i3, i4);
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
            Log.e("SteamLink", "Semaphore interrupted while we waited!");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setOverlaySurfaceVisible(boolean z) {
        this.m_overlaySurface.setVisibility(z ? 0 : 8);
    }

    public void showOverlaySurface() {
        Runnable runnable = new Runnable() { // from class: com.valvesoftware.steamlink.SteamLink.4
            @Override // java.lang.Runnable
            public void run() {
                SteamLink.this.setOverlaySurfaceVisible(true);
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
            Log.e("SteamLink", "Semaphore interrupted while we waited!");
        }
    }

    public void hideOverlaySurface() {
        Runnable runnable = new Runnable() { // from class: com.valvesoftware.steamlink.SteamLink.5
            @Override // java.lang.Runnable
            public void run() {
                SteamLink.this.setOverlaySurfaceVisible(false);
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
            Log.e("SteamLink", "Semaphore interrupted while we waited!");
        }
    }

    public Surface createVideoTexture(int i) {
        this.m_videoTexture = new SurfaceTexture(i);
        return new Surface(this.m_videoTexture);
    }

    public void updateVideoTexture() {
        this.m_videoTexture.updateTexImage();
    }

    public void releaseVideoTexture() {
        SurfaceTexture surfaceTexture = this.m_videoTexture;
        if (surfaceTexture != null) {
            surfaceTexture.release();
            this.m_videoTexture = null;
        }
    }
}

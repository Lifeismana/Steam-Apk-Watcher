package com.valvesoftware.steamlink;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import java.lang.reflect.Method;
import java.util.ArrayList;
import org.libsdl.app.SDL;
import org.libsdl.app.SDLActivity;

/* loaded from: classes.dex */
public class SteamLink extends SDLActivity {
    private static final String ARGS_KEY = "args";
    private static final String TAG = "SteamLink";
    VirtualHere mVirtualHere;
    WifiManager.WifiLock m_WiFiLock;
    boolean m_bLowLatencyAudio;
    float m_flOverlayScale;
    View m_marginBottom;
    View m_marginLeft;
    View m_marginRight;
    View m_marginTop;
    int m_nDisplayHeight;
    int m_nDisplayWidth;
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

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.libsdl.app.SDLActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        int checkSelfPermission;
        super.onCreate(bundle);
        setWindowStyle(true);
        if (mLayout != null && useVideoSurface()) {
            createVideoSurface();
        }
        if (Build.VERSION.SDK_INT >= 29) {
            checkSelfPermission = getApplication().checkSelfPermission("android.permission.WAKE_LOCK");
            if (checkSelfPermission == 0) {
                this.m_WiFiLock = ((WifiManager) getApplication().getSystemService("wifi")).createWifiLock(4, "Steam Link");
            }
        }
        this.mVirtualHere = VirtualHere.acquire(this);
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
        if (isFinishing()) {
            return;
        }
        finish();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.libsdl.app.SDLActivity, android.app.Activity
    public void onStart() {
        super.onStart();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.libsdl.app.SDLActivity, android.app.Activity
    public void onStop() {
        super.onStop();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.libsdl.app.SDLActivity, android.app.Activity
    public void onPause() {
        super.onPause();
        setLowLatencyAudio(false);
        disableWiFiLock();
        freezeRendering();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.libsdl.app.SDLActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        enableWiFiLock();
        if (SDLActivity.nativeGetHintBoolean("SDL_ANDROID_LOW_LATENCY_AUDIO", true)) {
            setLowLatencyAudio(true);
        }
        thawRendering();
    }

    @Override // org.libsdl.app.SDLActivity
    protected String[] getArguments() {
        Uri data = getIntent().getData();
        if (data != null) {
            return new String[]{data.toString()};
        }
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return new String[0];
        }
        String[] strArr = (String[]) extras.get(ARGS_KEY);
        if (strArr != null) {
            return strArr;
        }
        ArrayList<String> stringArrayList = extras.getStringArrayList(ARGS_KEY);
        return stringArrayList != null ? (String[]) stringArrayList.toArray(new String[stringArrayList.size()]) : new String[0];
    }

    private void enableWiFiLock() {
        if (this.m_WiFiLock != null) {
            Log.v(TAG, "Enabling low latency WiFi");
            this.m_WiFiLock.acquire();
        }
    }

    private void disableWiFiLock() {
        WifiManager.WifiLock wifiLock = this.m_WiFiLock;
        if (wifiLock == null || !wifiLock.isHeld()) {
            return;
        }
        Log.v(TAG, "Disabling low latency WiFi");
        this.m_WiFiLock.release();
    }

    private void setLowLatencyAudio(boolean z) {
        if (z == this.m_bLowLatencyAudio) {
            return;
        }
        AudioManager audioManager = (AudioManager) getApplication().getSystemService("audio");
        if (z) {
            Log.v(TAG, "Enabling low latency audio");
            audioManager.setParameters("set_audio_low_latency=1");
        } else {
            Log.v(TAG, "Disabling low latency audio");
            audioManager.setParameters("set_audio_low_latency=1");
        }
        this.m_bLowLatencyAudio = z;
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
        Display defaultDisplay = ((WindowManager) getApplication().getSystemService("window")).getDefaultDisplay();
        this.m_nDisplayWidth = defaultDisplay.getWidth();
        this.m_nDisplayHeight = defaultDisplay.getHeight();
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
        updateViewRects(0, 0, this.m_nDisplayWidth, this.m_nDisplayHeight);
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
            Log.e(TAG, "Semaphore interrupted while we waited!");
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
            Log.e(TAG, "Semaphore interrupted while we waited!");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateViewRects(int i, int i2, int i3, int i4) {
        int i5 = (this.m_nDisplayWidth - i3) - i;
        int i6 = (this.m_nDisplayHeight - i4) - i2;
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
            RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(i, this.m_nDisplayHeight);
            layoutParams2.setMargins(0, 0, this.m_nDisplayWidth - i, 0);
            this.m_marginLeft.setLayoutParams(layoutParams2);
            this.m_marginLeft.setVisibility(0);
        } else {
            this.m_marginLeft.setVisibility(8);
        }
        if (i5 > 0) {
            RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(i5, this.m_nDisplayHeight);
            layoutParams3.setMargins(this.m_nDisplayWidth - i5, 0, 0, 0);
            this.m_marginRight.setLayoutParams(layoutParams3);
            this.m_marginRight.setVisibility(0);
        } else {
            this.m_marginRight.setVisibility(8);
        }
        if (i2 > 0) {
            RelativeLayout.LayoutParams layoutParams4 = new RelativeLayout.LayoutParams(this.m_nDisplayWidth, i2);
            layoutParams4.setMargins(0, 0, 0, this.m_nDisplayHeight - i2);
            this.m_marginTop.setLayoutParams(layoutParams4);
            this.m_marginTop.setVisibility(0);
        } else {
            this.m_marginTop.setVisibility(8);
        }
        if (i6 > 0) {
            RelativeLayout.LayoutParams layoutParams5 = new RelativeLayout.LayoutParams(this.m_nDisplayWidth, i6);
            layoutParams5.setMargins(0, this.m_nDisplayHeight - i6, 0, 0);
            this.m_marginBottom.setLayoutParams(layoutParams5);
            this.m_marginBottom.setVisibility(0);
        } else {
            this.m_marginBottom.setVisibility(8);
        }
        int i7 = (this.m_nDisplayWidth - i) - i5;
        int i8 = (this.m_nDisplayHeight - i2) - i6;
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
            Log.e(TAG, "Semaphore interrupted while we waited!");
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
            Log.e(TAG, "Semaphore interrupted while we waited!");
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
            Log.e(TAG, "Semaphore interrupted while we waited!");
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

    public void streamingComplete(int i) {
        setLowLatencyAudio(false);
        disableWiFiLock();
        SteamShellActivity.onStreamingResult(i);
    }
}

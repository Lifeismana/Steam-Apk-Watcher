package com.valvesoftware.steamlink;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
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

/* JADX INFO: loaded from: classes.dex */
public class SteamLink extends SDLActivity {
    private static final String ARGS_KEY = "args";
    private static final int LOW_LATENCY_MODE_DISABLED = 2;
    private static final int LOW_LATENCY_MODE_FIND_BEST_CODEC = 1;
    private static final String LOW_LATENCY_MODE_KEY = "lowLatencyVideoMode";
    private static final int LOW_LATENCY_MODE_WITH_DEFAULT_CODEC = 0;
    private static final String TAG = "SteamLink";
    VirtualHere mVirtualHere;
    WifiManager.WifiLock m_WiFiLock;
    boolean m_bCursorUpdatePending;
    boolean m_bCursorVisible;
    boolean m_bLowLatencyAudio;
    Bitmap m_cursorBitmap;
    SurfaceView m_cursorSurface;
    float m_flCursorX;
    float m_flCursorY;
    float m_flOverlayScale;
    View m_marginBottom;
    View m_marginLeft;
    View m_marginRight;
    View m_marginTop;
    int m_nCursorHeight;
    int m_nCursorHotX;
    int m_nCursorHotY;
    int m_nCursorWidth;
    int m_nDisplayHeight;
    int m_nDisplayWidth;
    int m_nLowLatencyVideoMode;
    int m_nOverlayHeight;
    int m_nOverlayWidth;
    int m_nVideoH;
    int m_nVideoW;
    int m_nVideoX;
    int m_nVideoY;
    SurfaceView m_overlaySurface;
    SurfaceView m_videoSurface;
    SurfaceTexture m_videoTexture;
    float m_flCursorScale = 1.0f;
    final Object m_cursorLock = new Object();

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

    @Override // org.libsdl.app.SDLActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setWindowStyle(true);
        if (mLayout != null && useVideoSurface()) {
            createVideoSurface();
        }
        this.m_nLowLatencyVideoMode = 0;
        if (Build.VERSION.SDK_INT >= 29) {
            this.m_nLowLatencyVideoMode = 1;
            if (getApplication().checkSelfPermission("android.permission.WAKE_LOCK") == 0) {
                this.m_WiFiLock = ((WifiManager) getApplication().getSystemService("wifi")).createWifiLock(4, "Steam Link");
            }
        }
        this.mVirtualHere = VirtualHere.acquire(this);
    }

    @Override // org.libsdl.app.SDLActivity, android.app.Activity
    protected void onDestroy() {
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

    @Override // org.libsdl.app.SDLActivity, android.app.Activity
    protected void onStart() {
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(LOW_LATENCY_MODE_KEY)) {
            this.m_nLowLatencyVideoMode = extras.getInt(LOW_LATENCY_MODE_KEY);
        }
        super.onStart();
    }

    @Override // org.libsdl.app.SDLActivity, android.app.Activity
    protected void onStop() {
        super.onStop();
    }

    @Override // org.libsdl.app.SDLActivity, android.app.Activity
    protected void onPause() {
        super.onPause();
        setLowLatencyAudio(false);
        disableWiFiLock();
        freezeRendering();
    }

    @Override // org.libsdl.app.SDLActivity, android.app.Activity
    protected void onResume() {
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
        if (stringArrayList != null) {
            return (String[]) stringArrayList.toArray(new String[stringArrayList.size()]);
        }
        return new String[0];
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

    private class VideoSurfaceCallback implements SurfaceHolder.Callback {
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

    private class OverlaySurfaceCallback implements SurfaceHolder.Callback {
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
        surfaceView.setZOrderMediaOverlay(true);
        this.m_videoSurface.getHolder().addCallback(new VideoSurfaceCallback());
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
        SurfaceView surfaceView3 = new SurfaceView(getApplication());
        this.m_cursorSurface = surfaceView3;
        surfaceView3.setZOrderOnTop(true);
        this.m_cursorSurface.getHolder().setFormat(-3);
        this.m_cursorSurface.getHolder().addCallback(new CursorSurfaceCallback());
        this.m_cursorSurface.setVisibility(8);
        mLayout.addView(this.m_cursorSurface, new RelativeLayout.LayoutParams(1, 1));
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
            method2.invoke(this.m_cursorSurface, method.invoke(null, SDL.getContext(), 0));
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
        this.m_nVideoX = i;
        this.m_nVideoY = i2;
        this.m_nVideoW = i3;
        this.m_nVideoH = i4;
        updateCursorView();
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
        int i9 = i2 + (i8 - ((int) (this.m_nOverlayHeight * (i7 / this.m_nOverlayWidth))));
        RelativeLayout.LayoutParams layoutParams6 = new RelativeLayout.LayoutParams(i7, i8);
        layoutParams6.setMargins(i, i9, i5, i6);
        this.m_overlaySurface.setLayoutParams(layoutParams6);
    }

    class CursorSurfaceCallback implements SurfaceHolder.Callback {
        @Override // android.view.SurfaceHolder.Callback
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
        }

        @Override // android.view.SurfaceHolder.Callback
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        }

        CursorSurfaceCallback() {
        }

        @Override // android.view.SurfaceHolder.Callback
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
            SteamLink.this.drawCursorSurface();
        }
    }

    public Bitmap createCursorBitmap(int[] iArr, int i, int i2) {
        return Bitmap.createBitmap(iArr, i, i2, Bitmap.Config.ARGB_8888);
    }

    public void setCursor(final Bitmap bitmap, final int i, final int i2) {
        runOnUiThread(new Runnable() { // from class: com.valvesoftware.steamlink.SteamLink.3
            @Override // java.lang.Runnable
            public void run() {
                SteamLink.this.m_cursorBitmap = bitmap;
                synchronized (SteamLink.this.m_cursorLock) {
                    SteamLink.this.m_nCursorHotX = i;
                    SteamLink.this.m_nCursorHotY = i2;
                }
                if (SteamLink.this.m_cursorBitmap != null && SteamLink.this.m_cursorSurface != null) {
                    SteamLink.this.m_cursorSurface.getHolder().setFixedSize(SteamLink.this.m_cursorBitmap.getWidth(), SteamLink.this.m_cursorBitmap.getHeight());
                }
                SteamLink.this.drawCursorSurface();
                SteamLink.this.updateCursorView();
            }
        });
    }

    public void showCursor(final boolean z) {
        runOnUiThread(new Runnable() { // from class: com.valvesoftware.steamlink.SteamLink.4
            @Override // java.lang.Runnable
            public void run() {
                SteamLink.this.m_bCursorVisible = z;
                if (SteamLink.this.m_cursorSurface != null) {
                    SteamLink.this.m_cursorSurface.setVisibility(z ? 0 : 8);
                }
            }
        });
    }

    public void moveCursor(float f, float f2, float f3) {
        synchronized (this.m_cursorLock) {
            this.m_flCursorX = f;
            this.m_flCursorY = f2;
            this.m_flCursorScale = f3;
            if (this.m_bCursorUpdatePending) {
                return;
            }
            this.m_bCursorUpdatePending = true;
            runOnUiThread(new Runnable() { // from class: com.valvesoftware.steamlink.SteamLink.5
                @Override // java.lang.Runnable
                public void run() {
                    SteamLink.this.updateCursorView();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void drawCursorSurface() {
        Canvas canvasLockCanvas;
        SurfaceView surfaceView = this.m_cursorSurface;
        if (surfaceView == null || this.m_cursorBitmap == null) {
            return;
        }
        SurfaceHolder holder = surfaceView.getHolder();
        if (holder.getSurface().isValid() && (canvasLockCanvas = holder.lockCanvas()) != null) {
            canvasLockCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
            canvasLockCanvas.drawBitmap(this.m_cursorBitmap, 0.0f, 0.0f, (Paint) null);
            holder.unlockCanvasAndPost(canvasLockCanvas);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCursorView() {
        float f;
        float f2;
        float f3;
        int i;
        int i2;
        if (this.m_cursorSurface == null) {
            return;
        }
        synchronized (this.m_cursorLock) {
            f = this.m_flCursorX;
            f2 = this.m_flCursorY;
            f3 = this.m_flCursorScale;
            i = this.m_nCursorHotX;
            i2 = this.m_nCursorHotY;
            this.m_bCursorUpdatePending = false;
        }
        Bitmap bitmap = this.m_cursorBitmap;
        if (bitmap != null) {
            int iMax = Math.max(1, Math.round(bitmap.getWidth() * f3));
            int iMax2 = Math.max(1, Math.round(this.m_cursorBitmap.getHeight() * f3));
            if (iMax != this.m_nCursorWidth || iMax2 != this.m_nCursorHeight) {
                this.m_nCursorWidth = iMax;
                this.m_nCursorHeight = iMax2;
                this.m_cursorSurface.setLayoutParams(new RelativeLayout.LayoutParams(iMax, iMax2));
            }
        }
        this.m_cursorSurface.setTranslationX((this.m_nVideoX + (f * this.m_nVideoW)) - (i * f3));
        this.m_cursorSurface.setTranslationY((this.m_nVideoY + (f2 * this.m_nVideoH)) - (i2 * f3));
    }

    public void setVideoDisplayRect(final int i, final int i2, final int i3, final int i4) {
        Runnable runnable = new Runnable() { // from class: com.valvesoftware.steamlink.SteamLink.6
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
        Runnable runnable = new Runnable() { // from class: com.valvesoftware.steamlink.SteamLink.7
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
        Runnable runnable = new Runnable() { // from class: com.valvesoftware.steamlink.SteamLink.8
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

    public String findBestDecoder(String str) {
        if (this.m_nLowLatencyVideoMode == 1) {
            String strFindBestDecoder = SteamLinkUtils.findBestDecoder(str);
            if (strFindBestDecoder == null) {
                Log.v(TAG, "Could not find good codec for " + str + ", falling back to system default");
            }
            return strFindBestDecoder;
        }
        Log.v(TAG, "Using system default codec");
        return null;
    }

    public boolean shouldEnableLowLatencyOptions() {
        return this.m_nLowLatencyVideoMode != 2;
    }
}

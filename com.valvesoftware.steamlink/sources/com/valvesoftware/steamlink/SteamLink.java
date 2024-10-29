package com.valvesoftware.steamlink;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
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
    float m_flOverlayScale;
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
        super.onCreate(bundle);
        setWindowStyle(true);
        if (mLayout != null && useVideoSurface()) {
            createVideoSurface();
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
        freezeRendering();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.libsdl.app.SDLActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        thawRendering();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.libsdl.app.SDLActivity
    public String[] getArguments() {
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
        updateViewRects(0, 0, this.m_nDisplayWidth, this.m_nDisplayHeight);
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
        int i7 = (this.m_nDisplayWidth - i) - i5;
        int i8 = (this.m_nDisplayHeight - i2) - i6;
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(i7, i8);
        layoutParams2.setMargins(i, i2 + (i8 - ((int) (this.m_nOverlayHeight * (i7 / this.m_nOverlayWidth)))), i5, i6);
        this.m_overlaySurface.setLayoutParams(layoutParams2);
    }

    public void setVideoDisplayRect(final int i, final int i2, final int i3, final int i4) {
        Runnable runnable = new Runnable() { // from class: com.valvesoftware.steamlink.SteamLink.1
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
        SteamShellActivity.onStreamingResult(i);
    }
}

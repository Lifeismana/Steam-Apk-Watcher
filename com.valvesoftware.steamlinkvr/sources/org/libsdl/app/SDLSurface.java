package org.libsdl.app;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import org.libsdl.app.SDLActivity;

/* loaded from: classes.dex */
public class SDLSurface extends SurfaceView implements SurfaceHolder.Callback, View.OnKeyListener, View.OnTouchListener, SensorEventListener {
    protected Display mDisplay;
    protected float mHeight;
    public boolean mIsSurfaceReady;
    protected SensorManager mSensorManager;
    protected float mWidth;

    @Override // android.hardware.SensorEventListener
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public SDLSurface(Context context) {
        super(context);
        getHolder().addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        setOnKeyListener(this);
        setOnTouchListener(this);
        this.mDisplay = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
        this.mSensorManager = (SensorManager) context.getSystemService("sensor");
        setOnGenericMotionListener(SDLActivity.getMotionListener());
        this.mWidth = 1.0f;
        this.mHeight = 1.0f;
        this.mIsSurfaceReady = false;
    }

    public void handlePause() {
        enableSensor(1, false);
    }

    public void handleResume() {
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        setOnKeyListener(this);
        setOnTouchListener(this);
        enableSensor(1, true);
    }

    public Surface getNativeSurface() {
        return getHolder().getSurface();
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.v("SDL", "surfaceCreated()");
        SDLActivity.onNativeSurfaceCreated();
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.v("SDL", "surfaceDestroyed()");
        SDLActivity.mNextNativeState = SDLActivity.NativeState.PAUSED;
        SDLActivity.handleNativeState();
        this.mIsSurfaceReady = false;
        SDLActivity.onNativeSurfaceDestroyed();
    }

    /* JADX WARN: Removed duplicated region for block: B:15:0x0033 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    @Override // android.view.SurfaceHolder.Callback
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        int i4;
        int i5;
        float f;
        Log.v("SDL", "surfaceChanged()");
        if (SDLActivity.mSingleton == null) {
            return;
        }
        this.mWidth = i2;
        this.mHeight = i3;
        try {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            this.mDisplay.getRealMetrics(displayMetrics);
            i4 = displayMetrics.widthPixels;
            try {
                i5 = displayMetrics.heightPixels;
                try {
                    f = displayMetrics.densityDpi / 160.0f;
                } catch (Exception unused) {
                    f = 1.0f;
                    float f2 = f;
                    int i6 = i4;
                    int i7 = i5;
                    synchronized (SDLActivity.getContext()) {
                    }
                }
            } catch (Exception unused2) {
                i5 = i3;
                f = 1.0f;
                float f22 = f;
                int i62 = i4;
                int i72 = i5;
                synchronized (SDLActivity.getContext()) {
                }
            }
        } catch (Exception unused3) {
            i4 = i2;
        }
        float f222 = f;
        int i622 = i4;
        int i722 = i5;
        synchronized (SDLActivity.getContext()) {
            SDLActivity.getContext().notifyAll();
        }
        Log.v("SDL", "Window size: " + i2 + "x" + i3);
        Log.v("SDL", "Device size: " + i622 + "x" + i722);
        SDLActivity.nativeSetScreenResolution(i2, i3, i622, i722, f222, this.mDisplay.getRefreshRate());
        SDLActivity.onNativeResize();
        int requestedOrientation = SDLActivity.mSingleton.getRequestedOrientation();
        boolean z = requestedOrientation == 1 || requestedOrientation == 7 ? this.mWidth > this.mHeight : !(!(requestedOrientation == 0 || requestedOrientation == 6) || this.mWidth >= this.mHeight);
        if (z) {
            if (Math.max(this.mWidth, this.mHeight) / Math.min(this.mWidth, this.mHeight) < 1.2d) {
                Log.v("SDL", "Don't skip on such aspect-ratio. Could be a square resolution.");
                z = false;
            }
        }
        if (z) {
            z = false;
        }
        if (z) {
            Log.v("SDL", "Skip .. Surface is not ready.");
            this.mIsSurfaceReady = false;
        } else {
            SDLActivity.onNativeSurfaceChanged();
            this.mIsSurfaceReady = true;
            SDLActivity.mNextNativeState = SDLActivity.NativeState.RESUMED;
            SDLActivity.handleNativeState();
        }
    }

    @Override // android.view.View.OnKeyListener
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        return SDLActivity.handleKeyEvent(view, i, keyEvent, null);
    }

    private float getNormalizedX(float f) {
        float f2 = this.mWidth;
        if (f2 <= 1.0f) {
            return 0.5f;
        }
        return f / (f2 - 1.0f);
    }

    private float getNormalizedY(float f) {
        float f2 = this.mHeight;
        if (f2 <= 1.0f) {
            return 0.5f;
        }
        return f / (f2 - 1.0f);
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int i;
        Object invoke;
        int deviceId = motionEvent.getDeviceId();
        int pointerCount = motionEvent.getPointerCount();
        int actionMasked = motionEvent.getActionMasked();
        if (deviceId < 0) {
            deviceId--;
        }
        int i2 = 0;
        if (motionEvent.getSource() == 8194 || motionEvent.getSource() == 12290) {
            try {
                invoke = motionEvent.getClass().getMethod("getButtonState", new Class[0]).invoke(motionEvent, new Object[0]);
            } catch (Exception unused) {
            }
            if (invoke != null) {
                i = ((Integer) invoke).intValue();
                SDLGenericMotionListener_API12 motionListener = SDLActivity.getMotionListener();
                SDLActivity.onNativeMouse(i, actionMasked, motionListener.getEventX(motionEvent), motionListener.getEventY(motionEvent), motionListener.inRelativeMode());
            }
            i = 1;
            SDLGenericMotionListener_API12 motionListener2 = SDLActivity.getMotionListener();
            SDLActivity.onNativeMouse(i, actionMasked, motionListener2.getEventX(motionEvent), motionListener2.getEventY(motionEvent), motionListener2.inRelativeMode());
        } else {
            if (actionMasked != 0 && actionMasked != 1) {
                if (actionMasked == 2) {
                    for (int i3 = 0; i3 < pointerCount; i3++) {
                        int pointerId = motionEvent.getPointerId(i3);
                        float normalizedX = getNormalizedX(motionEvent.getX(i3));
                        float normalizedY = getNormalizedY(motionEvent.getY(i3));
                        float pressure = motionEvent.getPressure(i3);
                        SDLActivity.onNativeTouch(deviceId, pointerId, actionMasked, normalizedX, normalizedY, pressure > 1.0f ? 1.0f : pressure);
                    }
                } else if (actionMasked == 3) {
                    for (int i4 = 0; i4 < pointerCount; i4++) {
                        int pointerId2 = motionEvent.getPointerId(i4);
                        float normalizedX2 = getNormalizedX(motionEvent.getX(i4));
                        float normalizedY2 = getNormalizedY(motionEvent.getY(i4));
                        float pressure2 = motionEvent.getPressure(i4);
                        SDLActivity.onNativeTouch(deviceId, pointerId2, 1, normalizedX2, normalizedY2, pressure2 > 1.0f ? 1.0f : pressure2);
                    }
                } else if (actionMasked == 5 || actionMasked == 6) {
                    i2 = -1;
                }
            }
            if (i2 == -1) {
                i2 = motionEvent.getActionIndex();
            }
            int pointerId3 = motionEvent.getPointerId(i2);
            float normalizedX3 = getNormalizedX(motionEvent.getX(i2));
            float normalizedY3 = getNormalizedY(motionEvent.getY(i2));
            float pressure3 = motionEvent.getPressure(i2);
            SDLActivity.onNativeTouch(deviceId, pointerId3, actionMasked, normalizedX3, normalizedY3, pressure3 > 1.0f ? 1.0f : pressure3);
        }
        return true;
    }

    public void enableSensor(int i, boolean z) {
        if (z) {
            SensorManager sensorManager = this.mSensorManager;
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(i), 1, (Handler) null);
        } else {
            SensorManager sensorManager2 = this.mSensorManager;
            sensorManager2.unregisterListener(this, sensorManager2.getDefaultSensor(i));
        }
    }

    @Override // android.hardware.SensorEventListener
    public void onSensorChanged(SensorEvent sensorEvent) {
        float f;
        float f2;
        if (sensorEvent.sensor.getType() == 1) {
            int rotation = this.mDisplay.getRotation();
            int i = 0;
            if (rotation == 1) {
                f = -sensorEvent.values[1];
                f2 = sensorEvent.values[0];
                i = 90;
            } else if (rotation == 2) {
                f = -sensorEvent.values[0];
                f2 = -sensorEvent.values[1];
                i = 180;
            } else if (rotation != 3) {
                f = sensorEvent.values[0];
                f2 = sensorEvent.values[1];
            } else {
                f = sensorEvent.values[1];
                f2 = -sensorEvent.values[0];
                i = 270;
            }
            if (i != SDLActivity.mCurrentRotation) {
                SDLActivity.mCurrentRotation = i;
                SDLActivity.onNativeRotationChanged(i);
            }
            SDLActivity.onNativeAccel((-f) / 9.80665f, f2 / 9.80665f, sensorEvent.values[2] / 9.80665f);
        }
    }

    @Override // android.view.View
    public boolean onCapturedPointerEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 2 || actionMasked == 7) {
            SDLActivity.onNativeMouse(0, actionMasked, motionEvent.getX(0), motionEvent.getY(0), true);
            return true;
        }
        if (actionMasked == 8) {
            SDLActivity.onNativeMouse(0, actionMasked, motionEvent.getAxisValue(10, 0), motionEvent.getAxisValue(9, 0), false);
            return true;
        }
        if (actionMasked != 11 && actionMasked != 12) {
            return false;
        }
        SDLActivity.onNativeMouse(motionEvent.getButtonState(), actionMasked == 11 ? 0 : 1, motionEvent.getX(0), motionEvent.getY(0), true);
        return true;
    }
}

package org.libsdl.app;

import android.content.Context;
import android.graphics.Insets;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
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
import android.view.WindowInsets;
import android.view.WindowManager;
import org.libsdl.app.SDLActivity;

/* loaded from: classes.dex */
public class SDLSurface extends SurfaceView implements SurfaceHolder.Callback, View.OnApplyWindowInsetsListener, View.OnKeyListener, View.OnTouchListener, SensorEventListener {
    protected Display mDisplay;
    protected float mHeight;
    protected boolean mIsSurfaceReady;
    protected SensorManager mSensorManager;
    protected float mWidth;

    @Override // android.hardware.SensorEventListener
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    protected SDLSurface(Context context) {
        super(context);
        getHolder().addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        setOnApplyWindowInsetsListener(this);
        setOnKeyListener(this);
        setOnTouchListener(this);
        this.mDisplay = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
        this.mSensorManager = (SensorManager) context.getSystemService("sensor");
        setOnGenericMotionListener(SDLActivity.getMotionListener());
        this.mWidth = 1.0f;
        this.mHeight = 1.0f;
        this.mIsSurfaceReady = false;
    }

    protected void handlePause() {
        enableSensor(1, false);
    }

    protected void handleResume() {
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        setOnApplyWindowInsetsListener(this);
        setOnKeyListener(this);
        setOnTouchListener(this);
        enableSensor(1, true);
    }

    protected Surface getNativeSurface() {
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

    @Override // android.view.View.OnApplyWindowInsetsListener
    public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
        if (Build.VERSION.SDK_INT >= 30) {
            Insets insets = windowInsets.getInsets(WindowInsets.Type.systemBars() | WindowInsets.Type.systemGestures() | WindowInsets.Type.mandatorySystemGestures() | WindowInsets.Type.tappableElement() | WindowInsets.Type.displayCutout());
            SDLActivity.onNativeInsetsChanged(insets.left, insets.right, insets.top, insets.bottom);
        }
        return windowInsets;
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
        int i2;
        int i3;
        int deviceId = motionEvent.getDeviceId();
        int pointerCount = motionEvent.getPointerCount();
        int actionMasked = motionEvent.getActionMasked();
        int i4 = 6;
        int actionIndex = (actionMasked == 6 || actionMasked == 5) ? motionEvent.getActionIndex() : 0;
        while (true) {
            int toolType = motionEvent.getToolType(actionIndex);
            if (toolType == 3) {
                int buttonState = motionEvent.getButtonState();
                SDLGenericMotionListener_API14 motionListener = SDLActivity.getMotionListener();
                SDLActivity.onNativeMouse(buttonState, actionMasked, motionListener.getEventX(motionEvent, actionIndex), motionListener.getEventY(motionEvent, actionIndex), motionListener.inRelativeMode());
            } else {
                if (toolType == 2 || toolType == 4) {
                    int i5 = i4;
                    int pointerId = motionEvent.getPointerId(actionIndex);
                    float x = motionEvent.getX(actionIndex);
                    float y = motionEvent.getY(actionIndex);
                    float pressure = motionEvent.getPressure(actionIndex);
                    if (pressure > 1.0f) {
                        pressure = 1.0f;
                    }
                    int buttonState2 = (1 << (toolType == 2 ? 0 : 30)) | (motionEvent.getButtonState() >> 4);
                    i = actionIndex;
                    i2 = actionMasked;
                    i3 = i5;
                    SDLActivity.onNativePen(pointerId, buttonState2, i2, x, y, pressure);
                    if (i2 == i3 || i2 == 5 || (actionIndex = i + 1) >= pointerCount) {
                        break;
                    }
                    i4 = i3;
                    actionMasked = i2;
                } else {
                    int pointerId2 = motionEvent.getPointerId(actionIndex);
                    float normalizedX = getNormalizedX(motionEvent.getX(actionIndex));
                    float normalizedY = getNormalizedY(motionEvent.getY(actionIndex));
                    float pressure2 = motionEvent.getPressure(actionIndex);
                    SDLActivity.onNativeTouch(deviceId, pointerId2, actionMasked, normalizedX, normalizedY, pressure2 <= 1.0f ? pressure2 : 1.0f);
                }
            }
            i2 = actionMasked;
            i3 = i4;
            i = actionIndex;
            if (i2 == i3) {
                break;
            }
            break;
        }
        return true;
    }

    protected void enableSensor(int i, boolean z) {
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
        int pointerCount = motionEvent.getPointerCount();
        for (int i = 0; i < pointerCount; i++) {
            if (actionMasked == 2 || actionMasked == 7) {
                SDLActivity.onNativeMouse(0, actionMasked, motionEvent.getX(i), motionEvent.getY(i), true);
                return true;
            }
            if (actionMasked == 8) {
                SDLActivity.onNativeMouse(0, actionMasked, motionEvent.getAxisValue(10, i), motionEvent.getAxisValue(9, i), false);
                return true;
            }
            if (actionMasked == 11 || actionMasked == 12) {
                SDLActivity.onNativeMouse(motionEvent.getButtonState(), actionMasked != 11 ? 1 : 0, motionEvent.getX(i), motionEvent.getY(i), true);
                return true;
            }
        }
        return false;
    }
}

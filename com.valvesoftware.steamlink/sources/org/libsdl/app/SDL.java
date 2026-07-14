package org.libsdl.app;

import android.app.Activity;
import android.content.Context;

/* JADX INFO: loaded from: classes.dex */
public class SDL {
    public static final int SDL_INIT_AUDIO = 16;
    public static final int SDL_INIT_CAMERA = 65536;
    private static final int SDL_INIT_CONTROLLER = 12800;
    public static final int SDL_INIT_EVERYTHING = 111152;
    public static final int SDL_INIT_GAMEPAD = 8192;
    public static final int SDL_INIT_HAPTIC = 4096;
    public static final int SDL_INIT_JOYSTICK = 512;
    public static final int SDL_INIT_SENSOR = 32768;
    public static final int SDL_INIT_VIDEO = 32;
    private static int mCompiledSubsystems = 111152;
    protected static Activity mContext = null;
    private static int mInitializedSubsystems = 111152;

    public static void setupJNI() {
        setupJNI(SDL_INIT_EVERYTHING);
    }

    public static void setupJNI(int i) {
        SDLActivity.nativeSetupJNI();
        int iNativeGetCompiledSubsystems = SDLActivity.nativeGetCompiledSubsystems();
        mCompiledSubsystems = iNativeGetCompiledSubsystems;
        mInitializedSubsystems = i & iNativeGetCompiledSubsystems;
        if (isSubsystemCompiled(16)) {
            SDLAudioManager.nativeSetupJNI();
        }
        if (isSubsystemCompiled(SDL_INIT_CONTROLLER)) {
            SDLControllerManager.nativeSetupJNI();
        }
    }

    public static void initialize() {
        initialize(mInitializedSubsystems);
    }

    public static void initialize(int i) {
        setContext(null);
        SDLActivity.initialize();
        if (isSubsystemCompiled(16)) {
            SDLAudioManager.initialize();
        }
        if (isSubsystemCompiled(SDL_INIT_CONTROLLER)) {
            SDLControllerManager.initialize();
        }
    }

    static boolean isSubsystemInitialized(int i) {
        return (i & mInitializedSubsystems) != 0;
    }

    static boolean isSubsystemCompiled(int i) {
        return (i & mCompiledSubsystems) != 0;
    }

    static boolean isControllerManagerReady() {
        return isSubsystemInitialized(SDL_INIT_CONTROLLER);
    }

    public static void setContext(Activity activity) {
        if (isSubsystemCompiled(16)) {
            SDLAudioManager.setContext(activity);
        }
        mContext = activity;
    }

    public static Activity getContext() {
        return mContext;
    }

    static void loadLibrary(String str) throws SecurityException, UnsatisfiedLinkError, NullPointerException {
        loadLibrary(str, mContext);
    }

    static void loadLibrary(String str, Context context) throws SecurityException, UnsatisfiedLinkError, NullPointerException {
        if (str == null) {
            throw new NullPointerException("No library name provided.");
        }
        try {
            Class<?> clsLoadClass = context.getClassLoader().loadClass("com.getkeepsafe.relinker.ReLinker");
            Class<?> clsLoadClass2 = context.getClassLoader().loadClass("com.getkeepsafe.relinker.ReLinker$LoadListener");
            Class<?> clsLoadClass3 = context.getClassLoader().loadClass("android.content.Context");
            Class<?> clsLoadClass4 = context.getClassLoader().loadClass("java.lang.String");
            Object objInvoke = clsLoadClass.getDeclaredMethod("force", null).invoke(null, null);
            objInvoke.getClass().getDeclaredMethod("loadLibrary", clsLoadClass3, clsLoadClass4, clsLoadClass4, clsLoadClass2).invoke(objInvoke, context, str, null, null);
        } catch (Throwable unused) {
            System.loadLibrary(str);
        }
    }
}

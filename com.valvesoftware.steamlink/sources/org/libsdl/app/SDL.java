package org.libsdl.app;

import android.app.Activity;
import android.content.Context;

/* loaded from: classes.dex */
public class SDL {
    protected static Activity mContext;

    public static void setupJNI() {
        SDLActivity.nativeSetupJNI();
        SDLAudioManager.nativeSetupJNI();
        SDLControllerManager.nativeSetupJNI();
    }

    public static void initialize() {
        setContext(null);
        SDLActivity.initialize();
        SDLAudioManager.initialize();
        SDLControllerManager.initialize();
    }

    public static void setContext(Activity activity) {
        SDLAudioManager.setContext(activity);
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

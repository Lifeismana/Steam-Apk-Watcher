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

    static void loadLibrary(String str) throws UnsatisfiedLinkError, SecurityException, NullPointerException {
        loadLibrary(str, mContext);
    }

    static void loadLibrary(String str, Context context) throws UnsatisfiedLinkError, SecurityException, NullPointerException {
        if (str == null) {
            throw new NullPointerException("No library name provided.");
        }
        try {
            Class<?> loadClass = context.getClassLoader().loadClass("com.getkeepsafe.relinker.ReLinker");
            Class<?> loadClass2 = context.getClassLoader().loadClass("com.getkeepsafe.relinker.ReLinker$LoadListener");
            Class<?> loadClass3 = context.getClassLoader().loadClass("android.content.Context");
            Class<?> loadClass4 = context.getClassLoader().loadClass("java.lang.String");
            Object invoke = loadClass.getDeclaredMethod("force", new Class[0]).invoke(null, new Object[0]);
            invoke.getClass().getDeclaredMethod("loadLibrary", loadClass3, loadClass4, loadClass4, loadClass2).invoke(invoke, context, str, null, null);
        } catch (Throwable unused) {
            System.loadLibrary(str);
        }
    }
}

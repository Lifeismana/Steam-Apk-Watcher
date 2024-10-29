package org.libsdl.app;

import android.os.Process;
import android.util.Log;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: SDLActivity.java */
/* loaded from: classes.dex */
public class SDLMain implements Runnable {
    @Override // java.lang.Runnable
    public void run() {
        String mainSharedObject = SDLActivity.mSingleton.getMainSharedObject();
        String mainFunction = SDLActivity.mSingleton.getMainFunction();
        String[] arguments = SDLActivity.mSingleton.getArguments();
        try {
            Process.setThreadPriority(-4);
        } catch (Exception e) {
            Log.v("SDL", "modify thread properties failed " + e.toString());
        }
        Log.v("SDL", "Running main function " + mainFunction + " from library " + mainSharedObject);
        SDLActivity.nativeRunMain(mainSharedObject, mainFunction, arguments);
        Log.v("SDL", "Finished main function");
        if (SDLActivity.mSingleton == null || SDLActivity.mSingleton.isFinishing()) {
            return;
        }
        SDLActivity.mSDLThread = null;
        SDLActivity.mSDLMainFinished = true;
        SDLActivity.mSingleton.finish();
    }
}

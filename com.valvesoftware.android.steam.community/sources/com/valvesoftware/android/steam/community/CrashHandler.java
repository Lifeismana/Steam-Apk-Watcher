package com.valvesoftware.android.steam.community;

import android.os.Build;
import android.util.Log;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread;
import java.util.Calendar;

/* loaded from: classes.dex */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private Thread.UncaughtExceptionHandler m_defaultUEH = null;

    public void register() {
        Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        if (this != defaultUncaughtExceptionHandler) {
            this.m_defaultUEH = defaultUncaughtExceptionHandler;
            Thread.setDefaultUncaughtExceptionHandler(this);
        }
    }

    @Override // java.lang.Thread.UncaughtExceptionHandler
    public void uncaughtException(Thread thread, Throwable th) {
        StringWriter stringWriter = new StringWriter();
        th.printStackTrace(new PrintWriter(stringWriter));
        Calendar calendar = Calendar.getInstance();
        String str = "VERSION: " + Config.APP_VERSION_ID + "\nAPPNAME: com.valvesoftware.android.steam.community\nAPPVERSION: " + Config.APP_VERSION + "\nTIMESTAMP: " + (calendar.getTimeInMillis() / 1000) + "\nDATETIME: " + calendar.getTime().toGMTString() + "\nUSERID: " + Build.VERSION.CODENAME + Build.VERSION.INCREMENTAL + " (" + Build.DEVICE + "/" + Build.PRODUCT + ") " + Build.BRAND + " - " + Build.MANUFACTURER + " - " + Build.DISPLAY + "\nCONTACT: " + LoggedInUserAccountInfo.getLoginSteamID() + "\nSYSTEMVER: " + Build.VERSION.RELEASE + " : " + Build.VERSION.SDK + "\nSYSTEMOS: " + Build.MODEL + "\nSTACKTRACE: \n" + stringWriter.toString() + "\n//ENDOFSTACKTRACE//";
        Log.e("crash", str);
        DebugUtilRecord newDebugUtilRecord = SteamDebugUtil.newDebugUtilRecord(SteamDebugUtil.newDebugUtilRecord(null, null, str), null, (String) null);
        long currentTimeMillis = System.currentTimeMillis();
        while (newDebugUtilRecord.getId() == 0 && System.currentTimeMillis() - currentTimeMillis < 5000) {
            try {
                Thread.sleep(450L);
            } catch (InterruptedException unused) {
            }
        }
        Thread.UncaughtExceptionHandler uncaughtExceptionHandler = this.m_defaultUEH;
        if (uncaughtExceptionHandler != null) {
            uncaughtExceptionHandler.uncaughtException(thread, th);
        }
    }
}

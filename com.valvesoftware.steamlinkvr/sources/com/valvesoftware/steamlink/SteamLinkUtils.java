package com.valvesoftware.steamlink;

import android.app.Activity;
import android.os.Build;
import android.view.Display;
import org.libsdl.app.SDL;

/* loaded from: classes.dex */
public class SteamLinkUtils {
    public static final String TAG = "SteamLink";

    public static boolean canDisplay4KVideo() {
        Activity activity = (Activity) SDL.getContext();
        if (Build.MODEL.startsWith("BRAVIA") && activity.getPackageManager().hasSystemFeature("com.sony.dtv.hardware.panel.qfhd")) {
            return true;
        }
        Display.Mode mode = activity.getWindowManager().getDefaultDisplay().getMode();
        return mode.getPhysicalWidth() >= 3840 && mode.getPhysicalHeight() >= 2160;
    }
}

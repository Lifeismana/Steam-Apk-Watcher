package com.valvesoftware.steamlink;

import android.app.Activity;
import android.os.Build;
import android.view.Display;
import org.libsdl.app.SDL;

/* loaded from: classes.dex */
public class SteamLinkUtils {
    public static final String TAG = "SteamLinkShell";

    public static boolean canDisplay4KVideo() {
        Display.Mode mode;
        int physicalWidth;
        int physicalHeight;
        Activity activity = (Activity) SDL.getContext();
        if (Build.MODEL.startsWith("BRAVIA") && activity.getPackageManager().hasSystemFeature("com.sony.dtv.hardware.panel.qfhd")) {
            return true;
        }
        if (Build.VERSION.SDK_INT < 23) {
            return false;
        }
        mode = activity.getWindowManager().getDefaultDisplay().getMode();
        physicalWidth = mode.getPhysicalWidth();
        if (physicalWidth < 3840) {
            return false;
        }
        physicalHeight = mode.getPhysicalHeight();
        return physicalHeight >= 2160;
    }
}

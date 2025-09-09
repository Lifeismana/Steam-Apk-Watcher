package com.valvesoftware.steamlink;

import android.app.Activity;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
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

    /* JADX WARN: Code restructure failed: missing block: B:3:0x000d, code lost:
    
        r0 = r0.getWindowManager().getDefaultDisplay().getHdrCapabilities();
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static boolean canDisplayHDRVideo(boolean z, boolean z2) {
        Display.HdrCapabilities hdrCapabilities;
        int[] supportedHdrTypes;
        Activity activity = (Activity) SDL.getContext();
        if (Build.VERSION.SDK_INT >= 24 && hdrCapabilities != null) {
            supportedHdrTypes = hdrCapabilities.getSupportedHdrTypes();
            int length = supportedHdrTypes.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                }
                if (supportedHdrTypes[i] == 2) {
                    if (z && supportsHDRHEVC()) {
                        return true;
                    }
                    if (!z2 || !supportsHDRAV1()) {
                        break;
                    }
                    return true;
                }
                i++;
            }
        }
        return false;
    }

    private static MediaCodecInfo[] getCodecInfoList() {
        return new MediaCodecList(0).getCodecInfos();
    }

    private static MediaCodecInfo getDecoderInfo(String str) {
        boolean isAlias;
        for (MediaCodecInfo mediaCodecInfo : getCodecInfoList()) {
            if (!mediaCodecInfo.isEncoder()) {
                if (Build.VERSION.SDK_INT >= 29) {
                    isAlias = mediaCodecInfo.isAlias();
                    if (isAlias) {
                        continue;
                    }
                }
                for (String str2 : mediaCodecInfo.getSupportedTypes()) {
                    if (str2.equalsIgnoreCase(str)) {
                        return mediaCodecInfo;
                    }
                }
            }
        }
        return null;
    }

    private static boolean supportsHDRHEVC() {
        MediaCodecInfo decoderInfo = getDecoderInfo("video/hevc");
        if (decoderInfo != null) {
            for (MediaCodecInfo.CodecProfileLevel codecProfileLevel : decoderInfo.getCapabilitiesForType("video/hevc").profileLevels) {
                if (codecProfileLevel.profile == 4096) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean supportsHDRAV1() {
        MediaCodecInfo decoderInfo = getDecoderInfo("video/av01");
        if (decoderInfo != null) {
            for (MediaCodecInfo.CodecProfileLevel codecProfileLevel : decoderInfo.getCapabilitiesForType("video/av01").profileLevels) {
                if (codecProfileLevel.profile == 4096) {
                    return true;
                }
            }
        }
        return false;
    }
}

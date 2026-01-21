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
        Activity context = SDL.getContext();
        if (Build.MODEL.startsWith("BRAVIA") && context.getPackageManager().hasSystemFeature("com.sony.dtv.hardware.panel.qfhd")) {
            return true;
        }
        if (Build.VERSION.SDK_INT < 23) {
            return false;
        }
        Display.Mode mode = context.getWindowManager().getDefaultDisplay().getMode();
        return mode.getPhysicalWidth() >= 3840 && mode.getPhysicalHeight() >= 2160;
    }

    public static boolean canDisplayHDRVideo(boolean z, boolean z2) {
        Display.HdrCapabilities hdrCapabilities;
        Activity context = SDL.getContext();
        if (Build.VERSION.SDK_INT >= 24 && (hdrCapabilities = context.getWindowManager().getDefaultDisplay().getHdrCapabilities()) != null) {
            int[] supportedHdrTypes = hdrCapabilities.getSupportedHdrTypes();
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
        for (MediaCodecInfo mediaCodecInfo : getCodecInfoList()) {
            if (!mediaCodecInfo.isEncoder() && (Build.VERSION.SDK_INT < 29 || !mediaCodecInfo.isAlias())) {
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

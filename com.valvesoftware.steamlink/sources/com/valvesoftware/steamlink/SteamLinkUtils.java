package com.valvesoftware.steamlink;

import android.app.Activity;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import java.util.Iterator;
import java.util.LinkedList;
import org.libsdl.app.SDL;

/* JADX INFO: loaded from: classes.dex */
public class SteamLinkUtils {
    public static final String TAG = "SteamLinkShell";
    static LinkedList<String> knownVendorLowLatencyOptions;

    public static boolean canDisplay4KVideo() {
        Activity context = SDL.getContext();
        if (Build.MODEL.startsWith("BRAVIA") && context.getPackageManager().hasSystemFeature("com.sony.dtv.hardware.panel.qfhd")) {
            return true;
        }
        if (Build.VERSION.SDK_INT < 23) {
            return false;
        }
        Display.Mode mode = context.getWindowManager().getDefaultDisplay().getMode();
        if (mode.getPhysicalHeight() > mode.getPhysicalWidth()) {
            mode.getPhysicalHeight();
        }
        return mode.getPhysicalWidth() >= 3840 && mode.getPhysicalHeight() >= 2160;
    }

    public static boolean canDisplayHDRVideo(boolean z, boolean z2) {
        Display.HdrCapabilities hdrCapabilities;
        Activity context = SDL.getContext();
        if (Build.VERSION.SDK_INT >= 24 && (hdrCapabilities = context.getWindowManager().getDefaultDisplay().getHdrCapabilities()) != null) {
            for (int i : hdrCapabilities.getSupportedHdrTypes()) {
                if (i == 2) {
                    if (z && supportsHDRHEVC()) {
                        return true;
                    }
                    if (!z2 || !supportsHDRAV1()) {
                        break;
                    }
                    return true;
                }
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

    static {
        LinkedList<String> linkedList = new LinkedList<>();
        knownVendorLowLatencyOptions = linkedList;
        linkedList.add("vendor.qti-ext-dec-low-latency.enable");
        knownVendorLowLatencyOptions.add("vendor.hisi-ext-low-latency-video-dec.video-scene-for-low-latency-req");
        knownVendorLowLatencyOptions.add("vendor.rtc-ext-dec-low-latency.enable");
        knownVendorLowLatencyOptions.add("vendor.low-latency.enable");
    }

    private static boolean supportsKnownLowLatencyOption(MediaCodecInfo mediaCodecInfo, String str) {
        if (Build.VERSION.SDK_INT >= 30) {
            try {
                if (mediaCodecInfo.getCapabilitiesForType(str).isFeatureSupported("low-latency")) {
                    return true;
                }
            } catch (Exception e) {
                Log.w(TAG, "Codec " + mediaCodecInfo.getName() + " threw exception on checking capabilities: " + e.toString());
            }
        }
        if (Build.VERSION.SDK_INT < 31) {
            return false;
        }
        MediaCodec mediaCodecCreateByCodecName = null;
        try {
            mediaCodecCreateByCodecName = MediaCodec.createByCodecName(mediaCodecInfo.getName());
            for (String str2 : mediaCodecCreateByCodecName.getSupportedVendorParameters()) {
                Iterator<String> it = knownVendorLowLatencyOptions.iterator();
                while (it.hasNext()) {
                    if (str2.equalsIgnoreCase(it.next())) {
                        return true;
                    }
                }
            }
            if (mediaCodecCreateByCodecName == null) {
                return false;
            }
        } catch (Exception e2) {
            Log.w(TAG, "Codec " + mediaCodecInfo.getName() + " threw exception on checking capabilities: " + e2.toString());
            if (mediaCodecCreateByCodecName == null) {
                return false;
            }
        } finally {
            if (mediaCodecCreateByCodecName != null) {
                mediaCodecCreateByCodecName.release();
            }
        }
        return false;
    }

    public static String findBestDecoder(String str) {
        MediaCodecList mediaCodecList = new MediaCodecList(0);
        LinkedList<MediaCodecInfo> linkedList = new LinkedList();
        if (Build.VERSION.SDK_INT < 29) {
            Log.v(TAG, "Android version too old to find best decoder, just using system default.");
            return null;
        }
        Log.v(TAG, "Finding best decoder for " + str);
        for (MediaCodecInfo mediaCodecInfo : mediaCodecList.getCodecInfos()) {
            if (!mediaCodecInfo.isEncoder() && mediaCodecInfo.isHardwareAccelerated() && !mediaCodecInfo.isAlias()) {
                for (String str2 : mediaCodecInfo.getSupportedTypes()) {
                    if (str2.equalsIgnoreCase(str)) {
                        linkedList.add(mediaCodecInfo);
                    }
                }
            }
        }
        if (linkedList.isEmpty()) {
            Log.v(TAG, "No viable candidate decoders found, using system default.");
            return null;
        }
        if (Build.VERSION.SDK_INT >= 30) {
            for (MediaCodecInfo mediaCodecInfo2 : linkedList) {
                if (supportsKnownLowLatencyOption(mediaCodecInfo2, str)) {
                    Log.v(TAG, "Found codec with low-latency support: " + mediaCodecInfo2.getName());
                    return mediaCodecInfo2.getName();
                }
            }
        }
        String name = ((MediaCodecInfo) linkedList.getFirst()).getName();
        Log.v(TAG, "No 'best' option, falling back to first available codec: " + name);
        return name;
    }
}

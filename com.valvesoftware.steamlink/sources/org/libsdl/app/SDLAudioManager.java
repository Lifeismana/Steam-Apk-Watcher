package org.libsdl.app;

import android.content.Context;
import android.media.AudioDeviceCallback;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Build;
import android.os.Process;
import android.util.Log;

/* loaded from: classes.dex */
class SDLAudioManager {
    protected static final String TAG = "SDLAudio";
    private static AudioDeviceCallback mAudioDeviceCallback;
    protected static Context mContext;

    static native void nativeAddAudioDevice(boolean z, String str, int i);

    static native void nativeRemoveAudioDevice(boolean z, int i);

    static native void nativeSetupJNI();

    static void release(Context context) {
    }

    SDLAudioManager() {
    }

    static void initialize() {
        mAudioDeviceCallback = null;
        if (Build.VERSION.SDK_INT >= 24) {
            mAudioDeviceCallback = new AudioDeviceCallback() { // from class: org.libsdl.app.SDLAudioManager.1
                @Override // android.media.AudioDeviceCallback
                public void onAudioDevicesAdded(AudioDeviceInfo[] audioDeviceInfoArr) {
                    for (AudioDeviceInfo audioDeviceInfo : audioDeviceInfoArr) {
                        SDLAudioManager.nativeAddAudioDevice(audioDeviceInfo.isSink(), audioDeviceInfo.getProductName().toString(), audioDeviceInfo.getId());
                    }
                }

                @Override // android.media.AudioDeviceCallback
                public void onAudioDevicesRemoved(AudioDeviceInfo[] audioDeviceInfoArr) {
                    for (AudioDeviceInfo audioDeviceInfo : audioDeviceInfoArr) {
                        SDLAudioManager.nativeRemoveAudioDevice(audioDeviceInfo.isSink(), audioDeviceInfo.getId());
                    }
                }
            };
        }
    }

    static void setContext(Context context) {
        mContext = context;
    }

    private static AudioDeviceInfo getInputAudioDeviceInfo(int i) {
        if (Build.VERSION.SDK_INT < 24) {
            return null;
        }
        for (AudioDeviceInfo audioDeviceInfo : ((AudioManager) mContext.getSystemService("audio")).getDevices(1)) {
            if (audioDeviceInfo.getId() == i) {
                return audioDeviceInfo;
            }
        }
        return null;
    }

    private static AudioDeviceInfo getPlaybackAudioDeviceInfo(int i) {
        if (Build.VERSION.SDK_INT < 24) {
            return null;
        }
        for (AudioDeviceInfo audioDeviceInfo : ((AudioManager) mContext.getSystemService("audio")).getDevices(2)) {
            if (audioDeviceInfo.getId() == i) {
                return audioDeviceInfo;
            }
        }
        return null;
    }

    static void registerAudioDeviceCallback() {
        if (Build.VERSION.SDK_INT >= 24) {
            AudioManager audioManager = (AudioManager) mContext.getSystemService("audio");
            for (AudioDeviceInfo audioDeviceInfo : audioManager.getDevices(2)) {
                if (audioDeviceInfo.getType() != 18) {
                    nativeAddAudioDevice(audioDeviceInfo.isSink(), audioDeviceInfo.getProductName().toString(), audioDeviceInfo.getId());
                }
            }
            for (AudioDeviceInfo audioDeviceInfo2 : audioManager.getDevices(1)) {
                nativeAddAudioDevice(audioDeviceInfo2.isSink(), audioDeviceInfo2.getProductName().toString(), audioDeviceInfo2.getId());
            }
            audioManager.registerAudioDeviceCallback(mAudioDeviceCallback, null);
        }
    }

    static void unregisterAudioDeviceCallback() {
        if (Build.VERSION.SDK_INT >= 24) {
            ((AudioManager) mContext.getSystemService("audio")).unregisterAudioDeviceCallback(mAudioDeviceCallback);
        }
    }

    static void audioSetThreadPriority(boolean z, int i) throws SecurityException, IllegalArgumentException {
        try {
            if (z) {
                Thread.currentThread().setName("SDLAudioC" + i);
            } else {
                Thread.currentThread().setName("SDLAudioP" + i);
            }
            Process.setThreadPriority(-16);
        } catch (Exception e) {
            Log.v(TAG, "modify thread properties failed " + e.toString());
        }
    }
}

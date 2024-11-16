package org.libsdl.app;

import android.content.Context;
import android.media.AudioDeviceCallback;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Process;
import android.util.Log;

/* loaded from: classes.dex */
public class SDLAudioManager {
    protected static final String TAG = "SDLAudio";
    private static AudioDeviceCallback mAudioDeviceCallback;
    protected static Context mContext;

    public static native void addAudioDevice(boolean z, String str, int i);

    public static native int nativeSetupJNI();

    public static void release(Context context) {
    }

    public static native void removeAudioDevice(boolean z, int i);

    public static void initialize() {
        mAudioDeviceCallback = null;
        mAudioDeviceCallback = new AudioDeviceCallback() { // from class: org.libsdl.app.SDLAudioManager.1
            @Override // android.media.AudioDeviceCallback
            public void onAudioDevicesAdded(AudioDeviceInfo[] audioDeviceInfoArr) {
                for (AudioDeviceInfo audioDeviceInfo : audioDeviceInfoArr) {
                    SDLAudioManager.addAudioDevice(audioDeviceInfo.isSink(), audioDeviceInfo.getProductName().toString(), audioDeviceInfo.getId());
                }
            }

            @Override // android.media.AudioDeviceCallback
            public void onAudioDevicesRemoved(AudioDeviceInfo[] audioDeviceInfoArr) {
                for (AudioDeviceInfo audioDeviceInfo : audioDeviceInfoArr) {
                    SDLAudioManager.removeAudioDevice(audioDeviceInfo.isSink(), audioDeviceInfo.getId());
                }
            }
        };
    }

    public static void setContext(Context context) {
        mContext = context;
    }

    private static AudioDeviceInfo getInputAudioDeviceInfo(int i) {
        for (AudioDeviceInfo audioDeviceInfo : ((AudioManager) mContext.getSystemService("audio")).getDevices(1)) {
            if (audioDeviceInfo.getId() == i) {
                return audioDeviceInfo;
            }
        }
        return null;
    }

    private static AudioDeviceInfo getPlaybackAudioDeviceInfo(int i) {
        for (AudioDeviceInfo audioDeviceInfo : ((AudioManager) mContext.getSystemService("audio")).getDevices(2)) {
            if (audioDeviceInfo.getId() == i) {
                return audioDeviceInfo;
            }
        }
        return null;
    }

    public static void registerAudioDeviceCallback() {
        AudioManager audioManager = (AudioManager) mContext.getSystemService("audio");
        for (AudioDeviceInfo audioDeviceInfo : audioManager.getDevices(2)) {
            if (audioDeviceInfo.getType() != 18) {
                addAudioDevice(audioDeviceInfo.isSink(), audioDeviceInfo.getProductName().toString(), audioDeviceInfo.getId());
            }
        }
        for (AudioDeviceInfo audioDeviceInfo2 : audioManager.getDevices(1)) {
            addAudioDevice(audioDeviceInfo2.isSink(), audioDeviceInfo2.getProductName().toString(), audioDeviceInfo2.getId());
        }
        audioManager.registerAudioDeviceCallback(mAudioDeviceCallback, null);
    }

    public static void unregisterAudioDeviceCallback() {
        ((AudioManager) mContext.getSystemService("audio")).unregisterAudioDeviceCallback(mAudioDeviceCallback);
    }

    public static void audioSetThreadPriority(boolean z, int i) {
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

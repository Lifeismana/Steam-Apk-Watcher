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

    static native int nativeSetupJNI();

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
                    boolean isSink;
                    CharSequence productName;
                    int id;
                    for (AudioDeviceInfo audioDeviceInfo : audioDeviceInfoArr) {
                        isSink = audioDeviceInfo.isSink();
                        productName = audioDeviceInfo.getProductName();
                        String charSequence = productName.toString();
                        id = audioDeviceInfo.getId();
                        SDLAudioManager.nativeAddAudioDevice(isSink, charSequence, id);
                    }
                }

                @Override // android.media.AudioDeviceCallback
                public void onAudioDevicesRemoved(AudioDeviceInfo[] audioDeviceInfoArr) {
                    boolean isSink;
                    int id;
                    for (AudioDeviceInfo audioDeviceInfo : audioDeviceInfoArr) {
                        isSink = audioDeviceInfo.isSink();
                        id = audioDeviceInfo.getId();
                        SDLAudioManager.nativeRemoveAudioDevice(isSink, id);
                    }
                }
            };
        }
    }

    static void setContext(Context context) {
        mContext = context;
    }

    private static AudioDeviceInfo getInputAudioDeviceInfo(int i) {
        AudioDeviceInfo[] devices;
        int id;
        if (Build.VERSION.SDK_INT < 24) {
            return null;
        }
        devices = ((AudioManager) mContext.getSystemService("audio")).getDevices(1);
        for (AudioDeviceInfo audioDeviceInfo : devices) {
            id = audioDeviceInfo.getId();
            if (id == i) {
                return audioDeviceInfo;
            }
        }
        return null;
    }

    private static AudioDeviceInfo getPlaybackAudioDeviceInfo(int i) {
        AudioDeviceInfo[] devices;
        int id;
        if (Build.VERSION.SDK_INT < 24) {
            return null;
        }
        devices = ((AudioManager) mContext.getSystemService("audio")).getDevices(2);
        for (AudioDeviceInfo audioDeviceInfo : devices) {
            id = audioDeviceInfo.getId();
            if (id == i) {
                return audioDeviceInfo;
            }
        }
        return null;
    }

    static void registerAudioDeviceCallback() {
        AudioDeviceInfo[] devices;
        AudioDeviceInfo[] devices2;
        boolean isSink;
        CharSequence productName;
        int id;
        int type;
        boolean isSink2;
        CharSequence productName2;
        int id2;
        if (Build.VERSION.SDK_INT >= 24) {
            AudioManager audioManager = (AudioManager) mContext.getSystemService("audio");
            devices = audioManager.getDevices(2);
            for (AudioDeviceInfo audioDeviceInfo : devices) {
                type = audioDeviceInfo.getType();
                if (type != 18) {
                    isSink2 = audioDeviceInfo.isSink();
                    productName2 = audioDeviceInfo.getProductName();
                    String charSequence = productName2.toString();
                    id2 = audioDeviceInfo.getId();
                    nativeAddAudioDevice(isSink2, charSequence, id2);
                }
            }
            devices2 = audioManager.getDevices(1);
            for (AudioDeviceInfo audioDeviceInfo2 : devices2) {
                isSink = audioDeviceInfo2.isSink();
                productName = audioDeviceInfo2.getProductName();
                String charSequence2 = productName.toString();
                id = audioDeviceInfo2.getId();
                nativeAddAudioDevice(isSink, charSequence2, id);
            }
            audioManager.registerAudioDeviceCallback(mAudioDeviceCallback, null);
        }
    }

    static void unregisterAudioDeviceCallback() {
        if (Build.VERSION.SDK_INT >= 24) {
            ((AudioManager) mContext.getSystemService("audio")).unregisterAudioDeviceCallback(mAudioDeviceCallback);
        }
    }

    static void audioSetThreadPriority(boolean z, int i) {
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

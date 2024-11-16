package org.libsdl.app;

import android.content.Context;
import android.media.AudioDeviceCallback;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Process;
import android.util.Log;
import com.getkeepsafe.relinker.elf.Elf;

/* loaded from: classes.dex */
public class SDLAudioManager {
    protected static final String TAG = "SDLAudio";
    private static AudioDeviceCallback mAudioDeviceCallback;
    protected static AudioRecord mAudioRecord;
    protected static AudioTrack mAudioTrack;
    protected static Context mContext;

    public static native void addAudioDevice(boolean z, String str, int i);

    public static native int nativeSetupJNI();

    public static void release(Context context) {
    }

    public static native void removeAudioDevice(boolean z, int i);

    public static void initialize() {
        mAudioTrack = null;
        mAudioRecord = null;
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

    protected static String getAudioFormatString(int i) {
        return i != 2 ? i != 3 ? i != 4 ? Integer.toString(i) : "float" : "8-bit" : "16-bit";
    }

    protected static int[] open(boolean z, int i, int i2, int i3, int i4, int i5) {
        int i6;
        int i7;
        int minBufferSize;
        int[] iArr;
        String str;
        char c;
        char c2;
        int i8 = i3;
        Log.v(TAG, "Opening " + (z ? "recording" : "playback") + ", requested " + i4 + " frames of " + i8 + " channel " + getAudioFormatString(i2) + " audio at " + i + " Hz");
        int i9 = i2;
        if (i9 == 4) {
            if (Build.VERSION.SDK_INT < (z ? 23 : 21)) {
                i9 = 2;
            }
        }
        if (i9 == 2) {
            i6 = 2;
        } else if (i9 == 3) {
            i6 = 1;
        } else if (i9 != 4) {
            Log.v(TAG, "Requested format " + i9 + ", getting ENCODING_PCM_16BIT");
            i6 = 2;
            i9 = 2;
        } else {
            i6 = 4;
        }
        if (!z) {
            switch (i8) {
                case 1:
                    i7 = 4;
                    break;
                case 2:
                    i7 = 12;
                    break;
                case 3:
                    i7 = 28;
                    break;
                case 4:
                    i7 = 204;
                    break;
                case Elf.DynamicStructure.DT_STRTAB /* 5 */:
                    i7 = 220;
                    break;
                case 6:
                    i7 = 252;
                    break;
                case 7:
                    i7 = 1276;
                    break;
                case 8:
                    i7 = 6396;
                    break;
                default:
                    Log.v(TAG, "Requested " + i8 + " channels, getting stereo");
                    i7 = 12;
                    i8 = 2;
                    break;
            }
        } else if (i8 != 1) {
            if (i8 != 2) {
                Log.v(TAG, "Requested " + i8 + " channels, getting stereo");
                i7 = 12;
                i8 = 2;
            }
            i7 = 12;
        } else {
            i7 = 16;
        }
        int i10 = i6 * i8;
        if (z) {
            minBufferSize = AudioRecord.getMinBufferSize(i, i7, i9);
        } else {
            minBufferSize = AudioTrack.getMinBufferSize(i, i7, i9);
        }
        int max = Math.max(i4, ((minBufferSize + i10) - 1) / i10);
        int[] iArr2 = new int[4];
        if (z) {
            if (mAudioRecord == null) {
                iArr = iArr2;
                AudioRecord audioRecord = new AudioRecord(0, i, i7, i9, max * i10);
                mAudioRecord = audioRecord;
                if (audioRecord.getState() != 1) {
                    Log.e(TAG, "Failed during initialization of AudioRecord");
                    mAudioRecord.release();
                    mAudioRecord = null;
                    return null;
                }
                if (i5 != 0) {
                    mAudioRecord.setPreferredDevice(getPlaybackAudioDeviceInfo(i5));
                }
                mAudioRecord.startRecording();
            } else {
                iArr = iArr2;
            }
            iArr[0] = mAudioRecord.getSampleRate();
            iArr[1] = mAudioRecord.getAudioFormat();
            iArr[2] = mAudioRecord.getChannelCount();
            str = "recording";
            c = 3;
            c2 = 1;
        } else {
            iArr = iArr2;
            if (mAudioTrack == null) {
                str = "recording";
                c = 3;
                c2 = 1;
                AudioTrack audioTrack = new AudioTrack(3, i, i7, i9, max * i10, 1);
                mAudioTrack = audioTrack;
                if (audioTrack.getState() != 1) {
                    Log.e(TAG, "Failed during initialization of Audio Track");
                    mAudioTrack.release();
                    mAudioTrack = null;
                    return null;
                }
                if (i5 != 0) {
                    mAudioTrack.setPreferredDevice(getInputAudioDeviceInfo(i5));
                }
                mAudioTrack.play();
            } else {
                str = "recording";
                c = 3;
                c2 = 1;
            }
            iArr[0] = mAudioTrack.getSampleRate();
            iArr[c2] = mAudioTrack.getAudioFormat();
            iArr[2] = mAudioTrack.getChannelCount();
        }
        iArr[c] = max;
        Log.v(TAG, "Opening " + (z ? str : "playback") + ", got " + iArr[c] + " frames of " + iArr[2] + " channel " + getAudioFormatString(iArr[c2]) + " audio at " + iArr[0] + " Hz");
        return iArr;
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

    public static int[] audioOpen(int i, int i2, int i3, int i4, int i5) {
        return open(false, i, i2, i3, i4, i5);
    }

    public static void audioWriteFloatBuffer(float[] fArr) {
        if (mAudioTrack == null) {
            Log.e(TAG, "Attempted to make audio call with uninitialized audio!");
            return;
        }
        int i = 0;
        while (i < fArr.length) {
            int write = mAudioTrack.write(fArr, i, fArr.length - i, 0);
            if (write > 0) {
                i += write;
            } else if (write == 0) {
                try {
                    Thread.sleep(1L);
                } catch (InterruptedException unused) {
                }
            } else {
                Log.w(TAG, "SDL audio: error return from write(float)");
                return;
            }
        }
    }

    public static void audioWriteShortBuffer(short[] sArr) {
        if (mAudioTrack == null) {
            Log.e(TAG, "Attempted to make audio call with uninitialized audio!");
            return;
        }
        int i = 0;
        while (i < sArr.length) {
            int write = mAudioTrack.write(sArr, i, sArr.length - i);
            if (write > 0) {
                i += write;
            } else if (write == 0) {
                try {
                    Thread.sleep(1L);
                } catch (InterruptedException unused) {
                }
            } else {
                Log.w(TAG, "SDL audio: error return from write(short)");
                return;
            }
        }
    }

    public static void audioWriteByteBuffer(byte[] bArr) {
        if (mAudioTrack == null) {
            Log.e(TAG, "Attempted to make audio call with uninitialized audio!");
            return;
        }
        int i = 0;
        while (i < bArr.length) {
            int write = mAudioTrack.write(bArr, i, bArr.length - i);
            if (write > 0) {
                i += write;
            } else if (write == 0) {
                try {
                    Thread.sleep(1L);
                } catch (InterruptedException unused) {
                }
            } else {
                Log.w(TAG, "SDL audio: error return from write(byte)");
                return;
            }
        }
    }

    public static int[] recordingOpen(int i, int i2, int i3, int i4, int i5) {
        return open(true, i, i2, i3, i4, i5);
    }

    public static int recordingReadFloatBuffer(float[] fArr, boolean z) {
        return mAudioRecord.read(fArr, 0, fArr.length, !z ? 1 : 0);
    }

    public static int recordingReadShortBuffer(short[] sArr, boolean z) {
        return mAudioRecord.read(sArr, 0, sArr.length, !z ? 1 : 0);
    }

    public static int recordingReadByteBuffer(byte[] bArr, boolean z) {
        return mAudioRecord.read(bArr, 0, bArr.length, !z ? 1 : 0);
    }

    public static void audioClose() {
        AudioTrack audioTrack = mAudioTrack;
        if (audioTrack != null) {
            audioTrack.stop();
            mAudioTrack.release();
            mAudioTrack = null;
        }
    }

    public static void recordingClose() {
        AudioRecord audioRecord = mAudioRecord;
        if (audioRecord != null) {
            audioRecord.stop();
            mAudioRecord.release();
            mAudioRecord = null;
        }
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

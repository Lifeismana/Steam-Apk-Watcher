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
import java.util.ArrayList;
import org.qtproject.qt5.android.EditContextView;
import org.qtproject.qt5.android.QtNative;

/* loaded from: classes.dex */
public class SDLAudioManager {
    private static final int[] NO_DEVICES = new int[0];
    protected static final String TAG = "SDLAudio";
    private static AudioDeviceCallback mAudioDeviceCallback;
    protected static AudioRecord mAudioRecord;
    protected static AudioTrack mAudioTrack;
    protected static Context mContext;

    public static native void addAudioDevice(boolean z, int i);

    public static native int nativeSetupJNI();

    public static native void removeAudioDevice(boolean z, int i);

    public static void initialize() {
        mAudioTrack = null;
        mAudioRecord = null;
        mAudioDeviceCallback = null;
        if (Build.VERSION.SDK_INT >= 24) {
            mAudioDeviceCallback = new AudioDeviceCallback() { // from class: org.libsdl.app.SDLAudioManager.1
                @Override // android.media.AudioDeviceCallback
                public void onAudioDevicesAdded(AudioDeviceInfo[] audioDeviceInfoArr) {
                    boolean isSink;
                    int id;
                    for (AudioDeviceInfo audioDeviceInfo : audioDeviceInfoArr) {
                        isSink = audioDeviceInfo.isSink();
                        id = audioDeviceInfo.getId();
                        SDLAudioManager.addAudioDevice(isSink, id);
                    }
                }

                @Override // android.media.AudioDeviceCallback
                public void onAudioDevicesRemoved(AudioDeviceInfo[] audioDeviceInfoArr) {
                    boolean isSink;
                    int id;
                    for (AudioDeviceInfo audioDeviceInfo : audioDeviceInfoArr) {
                        isSink = audioDeviceInfo.isSink();
                        id = audioDeviceInfo.getId();
                        SDLAudioManager.removeAudioDevice(isSink, id);
                    }
                }
            };
        }
    }

    public static void setContext(Context context) {
        mContext = context;
        if (context != null) {
            registerAudioDeviceCallback();
        }
    }

    public static void release(Context context) {
        unregisterAudioDeviceCallback(context);
    }

    protected static String getAudioFormatString(int i) {
        return i != 2 ? i != 3 ? i != 4 ? Integer.toString(i) : "float" : "8-bit" : "16-bit";
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:11:0x0068  */
    /* JADX WARN: Removed duplicated region for block: B:19:0x0078  */
    /* JADX WARN: Removed duplicated region for block: B:25:0x00a4  */
    /* JADX WARN: Removed duplicated region for block: B:33:0x0118  */
    /* JADX WARN: Removed duplicated region for block: B:36:0x0134  */
    /* JADX WARN: Removed duplicated region for block: B:51:0x0200  */
    /* JADX WARN: Removed duplicated region for block: B:55:0x019c  */
    /* JADX WARN: Removed duplicated region for block: B:67:0x011d  */
    /* JADX WARN: Removed duplicated region for block: B:69:0x00c1  */
    /* JADX WARN: Removed duplicated region for block: B:83:0x009a  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    protected static int[] open(boolean z, int i, int i2, int i3, int i4, int i5) {
        int i6;
        int i7;
        int i8;
        int i9;
        int i10;
        int minBufferSize;
        String str;
        char c;
        char c2;
        int i11 = i3;
        StringBuilder sb = new StringBuilder("Opening ");
        sb.append(z ? "capture" : "playback");
        sb.append(", requested ");
        sb.append(i4);
        sb.append(" frames of ");
        sb.append(i11);
        sb.append(" channel ");
        sb.append(getAudioFormatString(i2));
        sb.append(" audio at ");
        sb.append(i);
        sb.append(" Hz");
        Log.v(TAG, sb.toString());
        if (Build.VERSION.SDK_INT < 22) {
            if (i < 8000) {
                i6 = 8000;
            } else if (i > 48000) {
                i6 = 48000;
            }
            i7 = i2;
            if (i7 == 4) {
                if (Build.VERSION.SDK_INT < (z ? 23 : 21)) {
                    i7 = 2;
                }
            }
            if (i7 == 2) {
                if (i7 == 3) {
                    i8 = i7;
                    i9 = 1;
                } else if (i7 != 4) {
                    Log.v(TAG, "Requested format " + i7 + ", getting ENCODING_PCM_16BIT");
                    i8 = 2;
                } else {
                    i8 = i7;
                    i9 = 4;
                }
                if (!z) {
                    switch (i11) {
                        case 1:
                            i10 = 4;
                            break;
                        case 2:
                            i10 = 12;
                            break;
                        case QtNative.IdRightHandle /* 3 */:
                            i10 = 28;
                            break;
                        case 4:
                            i10 = 204;
                            break;
                        case 5:
                            i10 = 220;
                            break;
                        case 6:
                            i10 = 252;
                            break;
                        case 7:
                            i10 = 1276;
                            break;
                        case EditContextView.SALL_BUTTON /* 8 */:
                            if (Build.VERSION.SDK_INT < 23) {
                                Log.v(TAG, "Requested " + i11 + " channels, getting 5.1 surround");
                                i11 = 6;
                                i10 = 252;
                                break;
                            } else {
                                i10 = 6396;
                                break;
                            }
                        default:
                            Log.v(TAG, "Requested " + i11 + " channels, getting stereo");
                            i11 = 2;
                            i10 = 12;
                            break;
                    }
                } else if (i11 != 1) {
                    if (i11 != 2) {
                        Log.v(TAG, "Requested " + i11 + " channels, getting stereo");
                        i11 = 2;
                    }
                    i10 = 12;
                } else {
                    i10 = 16;
                }
                int i12 = i9 * i11;
                if (z) {
                    minBufferSize = AudioRecord.getMinBufferSize(i6, i10, i8);
                } else {
                    minBufferSize = AudioTrack.getMinBufferSize(i6, i10, i8);
                }
                int max = Math.max(i4, ((minBufferSize + i12) - 1) / i12);
                int[] iArr = new int[4];
                if (z) {
                    if (mAudioRecord == null) {
                        str = "capture";
                        c = 1;
                        c2 = 2;
                        AudioRecord audioRecord = new AudioRecord(0, i6, i10, i8, i12 * max);
                        mAudioRecord = audioRecord;
                        if (audioRecord.getState() != 1) {
                            Log.e(TAG, "Failed during initialization of AudioRecord");
                            mAudioRecord.release();
                            mAudioRecord = null;
                            return null;
                        }
                        if (Build.VERSION.SDK_INT >= 24 && i5 != 0) {
                            mAudioRecord.setPreferredDevice(getOutputAudioDeviceInfo(i5));
                        }
                        mAudioRecord.startRecording();
                    } else {
                        str = "capture";
                        c = 1;
                        c2 = 2;
                    }
                    iArr[0] = mAudioRecord.getSampleRate();
                    iArr[c] = mAudioRecord.getAudioFormat();
                    iArr[c2] = mAudioRecord.getChannelCount();
                } else {
                    str = "capture";
                    c = 1;
                    c2 = 2;
                    if (mAudioTrack == null) {
                        AudioTrack audioTrack = new AudioTrack(3, i6, i10, i8, max * i12, 1);
                        mAudioTrack = audioTrack;
                        if (audioTrack.getState() != 1) {
                            Log.e(TAG, "Failed during initialization of Audio Track");
                            mAudioTrack.release();
                            mAudioTrack = null;
                            return null;
                        }
                        if (Build.VERSION.SDK_INT >= 24 && i5 != 0) {
                            mAudioTrack.setPreferredDevice(getInputAudioDeviceInfo(i5));
                        }
                        mAudioTrack.play();
                    }
                    iArr[0] = mAudioTrack.getSampleRate();
                    iArr[1] = mAudioTrack.getAudioFormat();
                    iArr[2] = mAudioTrack.getChannelCount();
                }
                iArr[3] = max;
                StringBuilder sb2 = new StringBuilder("Opening ");
                sb2.append(z ? str : "playback");
                sb2.append(", got ");
                sb2.append(iArr[3]);
                sb2.append(" frames of ");
                sb2.append(iArr[c2]);
                sb2.append(" channel ");
                sb2.append(getAudioFormatString(iArr[c]));
                sb2.append(" audio at ");
                sb2.append(iArr[0]);
                sb2.append(" Hz");
                Log.v(TAG, sb2.toString());
                return iArr;
            }
            i8 = i7;
            i9 = 2;
            if (!z) {
            }
            int i122 = i9 * i11;
            if (z) {
            }
            int max2 = Math.max(i4, ((minBufferSize + i122) - 1) / i122);
            int[] iArr2 = new int[4];
            if (z) {
            }
            iArr2[3] = max2;
            StringBuilder sb22 = new StringBuilder("Opening ");
            sb22.append(z ? str : "playback");
            sb22.append(", got ");
            sb22.append(iArr2[3]);
            sb22.append(" frames of ");
            sb22.append(iArr2[c2]);
            sb22.append(" channel ");
            sb22.append(getAudioFormatString(iArr2[c]));
            sb22.append(" audio at ");
            sb22.append(iArr2[0]);
            sb22.append(" Hz");
            Log.v(TAG, sb22.toString());
            return iArr2;
        }
        i6 = i;
        i7 = i2;
        if (i7 == 4) {
        }
        if (i7 == 2) {
        }
        i9 = 2;
        if (!z) {
        }
        int i1222 = i9 * i11;
        if (z) {
        }
        int max22 = Math.max(i4, ((minBufferSize + i1222) - 1) / i1222);
        int[] iArr22 = new int[4];
        if (z) {
        }
        iArr22[3] = max22;
        StringBuilder sb222 = new StringBuilder("Opening ");
        sb222.append(z ? str : "playback");
        sb222.append(", got ");
        sb222.append(iArr22[3]);
        sb222.append(" frames of ");
        sb222.append(iArr22[c2]);
        sb222.append(" channel ");
        sb222.append(getAudioFormatString(iArr22[c]));
        sb222.append(" audio at ");
        sb222.append(iArr22[0]);
        sb222.append(" Hz");
        Log.v(TAG, sb222.toString());
        return iArr22;
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

    private static AudioDeviceInfo getOutputAudioDeviceInfo(int i) {
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

    private static void registerAudioDeviceCallback() {
        if (Build.VERSION.SDK_INT >= 24) {
            ((AudioManager) mContext.getSystemService("audio")).registerAudioDeviceCallback(mAudioDeviceCallback, null);
        }
    }

    private static void unregisterAudioDeviceCallback(Context context) {
        if (Build.VERSION.SDK_INT >= 24) {
            ((AudioManager) context.getSystemService("audio")).unregisterAudioDeviceCallback(mAudioDeviceCallback);
        }
    }

    private static int[] ArrayListToArray(ArrayList<Integer> arrayList) {
        int size = arrayList.size();
        int[] iArr = new int[size];
        for (int i = 0; i < size; i++) {
            iArr[i] = arrayList.get(i).intValue();
        }
        return iArr;
    }

    public static int[] getAudioOutputDevices() {
        AudioDeviceInfo[] devices;
        int type;
        int id;
        if (Build.VERSION.SDK_INT >= 24) {
            AudioManager audioManager = (AudioManager) mContext.getSystemService("audio");
            ArrayList arrayList = new ArrayList();
            devices = audioManager.getDevices(2);
            for (AudioDeviceInfo audioDeviceInfo : devices) {
                type = audioDeviceInfo.getType();
                if (type != 18) {
                    id = audioDeviceInfo.getId();
                    arrayList.add(Integer.valueOf(id));
                }
            }
            return ArrayListToArray(arrayList);
        }
        return NO_DEVICES;
    }

    public static int[] getAudioInputDevices() {
        AudioDeviceInfo[] devices;
        int id;
        if (Build.VERSION.SDK_INT >= 24) {
            AudioManager audioManager = (AudioManager) mContext.getSystemService("audio");
            ArrayList arrayList = new ArrayList();
            devices = audioManager.getDevices(1);
            for (AudioDeviceInfo audioDeviceInfo : devices) {
                id = audioDeviceInfo.getId();
                arrayList.add(Integer.valueOf(id));
            }
            return ArrayListToArray(arrayList);
        }
        return NO_DEVICES;
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

    public static int[] captureOpen(int i, int i2, int i3, int i4, int i5) {
        return open(true, i, i2, i3, i4, i5);
    }

    public static int captureReadFloatBuffer(float[] fArr, boolean z) {
        int read;
        if (Build.VERSION.SDK_INT < 23) {
            return 0;
        }
        read = mAudioRecord.read(fArr, 0, fArr.length, !z ? 1 : 0);
        return read;
    }

    public static int captureReadShortBuffer(short[] sArr, boolean z) {
        int read;
        if (Build.VERSION.SDK_INT < 23) {
            return mAudioRecord.read(sArr, 0, sArr.length);
        }
        read = mAudioRecord.read(sArr, 0, sArr.length, !z ? 1 : 0);
        return read;
    }

    public static int captureReadByteBuffer(byte[] bArr, boolean z) {
        int read;
        if (Build.VERSION.SDK_INT < 23) {
            return mAudioRecord.read(bArr, 0, bArr.length);
        }
        read = mAudioRecord.read(bArr, 0, bArr.length, !z ? 1 : 0);
        return read;
    }

    public static void audioClose() {
        AudioTrack audioTrack = mAudioTrack;
        if (audioTrack != null) {
            audioTrack.stop();
            mAudioTrack.release();
            mAudioTrack = null;
        }
    }

    public static void captureClose() {
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

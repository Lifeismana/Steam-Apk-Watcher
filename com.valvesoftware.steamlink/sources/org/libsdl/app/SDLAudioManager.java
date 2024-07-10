package org.libsdl.app;

import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Process;
import android.util.Log;
import org.qtproject.qt5.android.EditContextView;
import org.qtproject.qt5.android.QtNative;

/* loaded from: classes.dex */
public class SDLAudioManager {
    protected static final String TAG = "SDLAudio";
    protected static AudioRecord mAudioRecord;
    protected static AudioTrack mAudioTrack;

    public static native int nativeSetupJNI();

    public static void initialize() {
        mAudioTrack = null;
        mAudioRecord = null;
    }

    protected static String getAudioFormatString(int i) {
        return i != 2 ? i != 3 ? i != 4 ? Integer.toString(i) : "float" : "8-bit" : "16-bit";
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:15:0x0072  */
    /* JADX WARN: Removed duplicated region for block: B:23:0x0082  */
    /* JADX WARN: Removed duplicated region for block: B:28:0x00b1  */
    /* JADX WARN: Removed duplicated region for block: B:36:0x0122  */
    /* JADX WARN: Removed duplicated region for block: B:39:0x013a  */
    /* JADX WARN: Removed duplicated region for block: B:50:0x01df  */
    /* JADX WARN: Removed duplicated region for block: B:53:0x0185  */
    /* JADX WARN: Removed duplicated region for block: B:61:0x0127  */
    /* JADX WARN: Removed duplicated region for block: B:63:0x00cf  */
    /* JADX WARN: Removed duplicated region for block: B:77:0x00a6  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    protected static int[] open(boolean z, int i, int i2, int i3, int i4) {
        int i5;
        int i6;
        int i7;
        int minBufferSize;
        int i8 = i3;
        StringBuilder sb = new StringBuilder();
        sb.append("Opening ");
        sb.append(z ? "capture" : "playback");
        sb.append(", requested ");
        sb.append(i4);
        sb.append(" frames of ");
        sb.append(i8);
        sb.append(" channel ");
        sb.append(getAudioFormatString(i2));
        sb.append(" audio at ");
        sb.append(i);
        sb.append(" Hz");
        Log.v(TAG, sb.toString());
        if (Build.VERSION.SDK_INT < 21 && i8 > 2) {
            i8 = 2;
        }
        int i9 = 8000;
        if (Build.VERSION.SDK_INT < 22) {
            if (i >= 8000) {
                if (i > 48000) {
                    i9 = 48000;
                }
            }
            i5 = i2;
            if (i5 == 4) {
                if (Build.VERSION.SDK_INT < (z ? 23 : 21)) {
                    i5 = 2;
                }
            }
            if (i5 != 2) {
                i6 = 2;
            } else if (i5 == 3) {
                i6 = 1;
            } else if (i5 != 4) {
                Log.v(TAG, "Requested format " + i5 + ", getting ENCODING_PCM_16BIT");
                i6 = 2;
                i5 = 2;
            } else {
                i6 = 4;
            }
            if (z) {
                switch (i8) {
                    case 1:
                        i7 = 4;
                        break;
                    case 2:
                        i7 = 12;
                        break;
                    case QtNative.IdRightHandle /* 3 */:
                        i7 = 28;
                        break;
                    case 4:
                        i7 = 204;
                        break;
                    case 5:
                        i7 = 220;
                        break;
                    case 6:
                        i7 = 252;
                        break;
                    case 7:
                        i7 = 1276;
                        break;
                    case EditContextView.SALL_BUTTON /* 8 */:
                        if (Build.VERSION.SDK_INT < 23) {
                            Log.v(TAG, "Requested " + i8 + " channels, getting 5.1 surround");
                            i8 = 6;
                            i7 = 252;
                            break;
                        } else {
                            i7 = 6396;
                            break;
                        }
                    default:
                        Log.v(TAG, "Requested " + i8 + " channels, getting stereo");
                        i8 = 2;
                        i7 = 12;
                        break;
                }
            } else if (i8 != 1) {
                if (i8 != 2) {
                    Log.v(TAG, "Requested " + i8 + " channels, getting stereo");
                    i8 = 2;
                }
                i7 = 12;
            } else {
                i7 = 16;
            }
            int i10 = i6 * i8;
            if (!z) {
                minBufferSize = AudioRecord.getMinBufferSize(i9, i7, i5);
            } else {
                minBufferSize = AudioTrack.getMinBufferSize(i9, i7, i5);
            }
            int max = Math.max(i4, ((minBufferSize + i10) - 1) / i10);
            int[] iArr = new int[4];
            if (!z) {
                if (mAudioRecord == null) {
                    AudioRecord audioRecord = new AudioRecord(0, i9, i7, i5, max * i10);
                    mAudioRecord = audioRecord;
                    if (audioRecord.getState() != 1) {
                        Log.e(TAG, "Failed during initialization of AudioRecord");
                        mAudioRecord.release();
                        mAudioRecord = null;
                        return null;
                    }
                    mAudioRecord.startRecording();
                }
                iArr[0] = mAudioRecord.getSampleRate();
                iArr[1] = mAudioRecord.getAudioFormat();
                iArr[2] = mAudioRecord.getChannelCount();
            } else {
                if (mAudioTrack == null) {
                    AudioTrack audioTrack = new AudioTrack(3, i9, i7, i5, max * i10, 1);
                    mAudioTrack = audioTrack;
                    if (audioTrack.getState() != 1) {
                        Log.e(TAG, "Failed during initialization of Audio Track");
                        mAudioTrack.release();
                        mAudioTrack = null;
                        return null;
                    }
                    mAudioTrack.play();
                }
                iArr[0] = mAudioTrack.getSampleRate();
                iArr[1] = mAudioTrack.getAudioFormat();
                iArr[2] = mAudioTrack.getChannelCount();
            }
            iArr[3] = max;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Opening ");
            sb2.append(z ? "capture" : "playback");
            sb2.append(", got ");
            sb2.append(iArr[3]);
            sb2.append(" frames of ");
            sb2.append(iArr[2]);
            sb2.append(" channel ");
            sb2.append(getAudioFormatString(iArr[1]));
            sb2.append(" audio at ");
            sb2.append(iArr[0]);
            sb2.append(" Hz");
            Log.v(TAG, sb2.toString());
            return iArr;
        }
        i9 = i;
        i5 = i2;
        if (i5 == 4) {
        }
        if (i5 != 2) {
        }
        if (z) {
        }
        int i102 = i6 * i8;
        if (!z) {
        }
        int max2 = Math.max(i4, ((minBufferSize + i102) - 1) / i102);
        int[] iArr2 = new int[4];
        if (!z) {
        }
        iArr2[3] = max2;
        StringBuilder sb22 = new StringBuilder();
        sb22.append("Opening ");
        sb22.append(z ? "capture" : "playback");
        sb22.append(", got ");
        sb22.append(iArr2[3]);
        sb22.append(" frames of ");
        sb22.append(iArr2[2]);
        sb22.append(" channel ");
        sb22.append(getAudioFormatString(iArr2[1]));
        sb22.append(" audio at ");
        sb22.append(iArr2[0]);
        sb22.append(" Hz");
        Log.v(TAG, sb22.toString());
        return iArr2;
    }

    public static int[] audioOpen(int i, int i2, int i3, int i4) {
        return open(false, i, i2, i3, i4);
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

    public static int[] captureOpen(int i, int i2, int i3, int i4) {
        return open(true, i, i2, i3, i4);
    }

    public static int captureReadFloatBuffer(float[] fArr, boolean z) {
        return mAudioRecord.read(fArr, 0, fArr.length, !z ? 1 : 0);
    }

    public static int captureReadShortBuffer(short[] sArr, boolean z) {
        if (Build.VERSION.SDK_INT < 23) {
            return mAudioRecord.read(sArr, 0, sArr.length);
        }
        return mAudioRecord.read(sArr, 0, sArr.length, !z ? 1 : 0);
    }

    public static int captureReadByteBuffer(byte[] bArr, boolean z) {
        if (Build.VERSION.SDK_INT < 23) {
            return mAudioRecord.read(bArr, 0, bArr.length);
        }
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

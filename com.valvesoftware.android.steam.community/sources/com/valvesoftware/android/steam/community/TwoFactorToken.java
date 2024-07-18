package com.valvesoftware.android.steam.community;

import android.util.Base64;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class TwoFactorToken {
    private static final byte[] s_rgchSteamguardCodeChars = {50, 51, 52, 53, 54, 55, 56, 57, 66, 67, 68, 70, 71, 72, 74, 75, 77, 78, 80, 81, 82, 84, 86, 87, 88, 89};
    byte[] mSecret;

    public TwoFactorToken(JSONObject jSONObject) {
        String optString = jSONObject.optString("shared_secret");
        if (optString == null || optString.length() <= 0) {
            return;
        }
        this.mSecret = Base64.decode(optString.getBytes(), 0);
    }

    public final String generateSteamGuardCode() {
        return generateSteamGuardCodeForTime(currentTime());
    }

    public final String generateSteamGuardCodeForTime(long j) {
        if (this.mSecret == null) {
            return "";
        }
        byte[] bArr = new byte[8];
        long j2 = j / 30;
        int i = 8;
        while (true) {
            int i2 = i - 1;
            if (i <= 0) {
                break;
            }
            bArr[i2] = (byte) j2;
            j2 >>>= 8;
            i = i2;
        }
        SecretKeySpec secretKeySpec = new SecretKeySpec(this.mSecret, "HmacSHA1");
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(secretKeySpec);
            byte[] doFinal = mac.doFinal(bArr);
            int i3 = doFinal[19] & 15;
            int i4 = (doFinal[i3 + 3] & 255) | ((doFinal[i3 + 2] & 255) << 8) | ((doFinal[i3] & Byte.MAX_VALUE) << 24) | ((doFinal[i3 + 1] & 255) << 16);
            byte[] bArr2 = new byte[5];
            for (int i5 = 0; i5 < 5; i5++) {
                byte[] bArr3 = s_rgchSteamguardCodeChars;
                bArr2[i5] = bArr3[i4 % bArr3.length];
                i4 /= bArr3.length;
            }
            return new String(bArr2);
        } catch (InvalidKeyException unused) {
            return null;
        } catch (NoSuchAlgorithmException unused2) {
            return null;
        }
    }

    public final long currentTime() {
        return TimeCorrector.getInstance().currentTimeSeconds();
    }

    public final int secondsToNextChange() {
        return (int) (30 - (currentTime() % 30));
    }
}

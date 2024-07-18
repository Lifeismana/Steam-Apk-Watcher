package com.valvesoftware.android.steam.community;

import android.widget.TextView;

/* loaded from: classes.dex */
public class AndroidUtils {
    public static void setTextViewText(TextView textView, String str) {
        if (str == null) {
            str = "";
        }
        try {
            textView.setText(str);
        } catch (Exception unused) {
            int length = str.length();
            if (length > 0) {
                try {
                    try {
                        char[] cArr = new char[length];
                        for (int i = 0; i < length; i++) {
                            char charAt = str.charAt(i);
                            if (charAt < 0 || charAt > 127) {
                                charAt = '?';
                            }
                            cArr[i] = charAt;
                        }
                        textView.setText(cArr, 0, length);
                    } catch (Exception unused2) {
                    }
                } catch (Exception unused3) {
                    textView.setText(" ");
                }
            }
        }
    }
}

package me.leolin.shortcutbadger.util;

import android.database.Cursor;
import java.io.Closeable;
import java.io.IOException;

/* loaded from: classes3.dex */
public class CloseHelper {
    public static void close(Cursor cursor) {
        if (cursor == null || cursor.isClosed()) {
            return;
        }
        cursor.close();
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException unused) {
            }
        }
    }
}

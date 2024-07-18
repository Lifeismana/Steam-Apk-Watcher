package com.valvesoftware.android.steam.community;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;
import com.android.volley.toolbox.ImageLoader;

/* loaded from: classes.dex */
public class LruBitmapCache extends LruCache<String, Bitmap> implements ImageLoader.ImageCache {
    public LruBitmapCache(int i) {
        super(i);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.util.LruCache
    public int sizeOf(String str, Bitmap bitmap) {
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    @Override // com.android.volley.toolbox.ImageLoader.ImageCache
    public Bitmap getBitmap(String str) {
        return get(str);
    }

    @Override // com.android.volley.toolbox.ImageLoader.ImageCache
    public void putBitmap(String str, Bitmap bitmap) {
        put(str, bitmap);
    }

    public static int getCacheSize(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels * displayMetrics.heightPixels * 4 * 3;
    }
}

package com.android.volley.toolbox;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.http.AndroidHttpClient;
import android.os.Build;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import java.io.File;

/* loaded from: classes.dex */
public class Volley {
    public static RequestQueue newRequestQueue(Context context, BaseHttpStack baseHttpStack) {
        BasicNetwork basicNetwork;
        if (baseHttpStack == null) {
            if (Build.VERSION.SDK_INT >= 9) {
                basicNetwork = new BasicNetwork((BaseHttpStack) new HurlStack());
            } else {
                String str = "volley/0";
                try {
                    String packageName = context.getPackageName();
                    str = packageName + "/" + context.getPackageManager().getPackageInfo(packageName, 0).versionCode;
                } catch (PackageManager.NameNotFoundException unused) {
                }
                basicNetwork = new BasicNetwork(new HttpClientStack(AndroidHttpClient.newInstance(str)));
            }
        } else {
            basicNetwork = new BasicNetwork(baseHttpStack);
        }
        return newRequestQueue(context, basicNetwork);
    }

    private static RequestQueue newRequestQueue(Context context, Network network) {
        RequestQueue requestQueue = new RequestQueue(new DiskBasedCache(new File(context.getCacheDir(), "volley")), network);
        requestQueue.start();
        return requestQueue;
    }

    public static RequestQueue newRequestQueue(Context context) {
        return newRequestQueue(context, (BaseHttpStack) null);
    }
}

package me.leolin.shortcutbadger.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Build;
import java.util.Collections;
import java.util.List;
import me.leolin.shortcutbadger.ShortcutBadgeException;
import me.leolin.shortcutbadger.impl.IntentConstants;

/* loaded from: classes5.dex */
public class BroadcastHelper {
    public static List<ResolveInfo> resolveBroadcast(Context context, Intent intent) {
        List<ResolveInfo> queryBroadcastReceivers = context.getPackageManager().queryBroadcastReceivers(intent, 0);
        return queryBroadcastReceivers != null ? queryBroadcastReceivers : Collections.emptyList();
    }

    public static void sendIntentExplicitly(Context context, Intent intent) throws ShortcutBadgeException {
        List<ResolveInfo> resolveBroadcast = resolveBroadcast(context, intent);
        if (resolveBroadcast.size() == 0) {
            throw new ShortcutBadgeException("unable to resolve intent: " + intent.toString());
        }
        for (ResolveInfo resolveInfo : resolveBroadcast) {
            Intent intent2 = new Intent(intent);
            if (resolveInfo != null) {
                intent2.setPackage(resolveInfo.resolvePackageName);
                context.sendBroadcast(intent2);
            }
        }
    }

    public static void sendDefaultIntentExplicitly(Context context, Intent intent) throws ShortcutBadgeException {
        if (Build.VERSION.SDK_INT >= 26) {
            Intent intent2 = new Intent(intent);
            intent2.setAction(IntentConstants.DEFAULT_OREO_INTENT_ACTION);
            try {
                sendIntentExplicitly(context, intent2);
                return;
            } catch (ShortcutBadgeException unused) {
            }
        }
        sendIntentExplicitly(context, intent);
    }
}

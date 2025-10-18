package me.leolin.shortcutbadger.impl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import java.util.Collections;
import java.util.List;
import me.leolin.shortcutbadger.Badger;
import me.leolin.shortcutbadger.ShortcutBadgeException;
import me.leolin.shortcutbadger.util.BroadcastHelper;

/* loaded from: classes3.dex */
public class NewHtcHomeBadger implements Badger {
    public static final String COUNT = "count";
    public static final String EXTRA_COMPONENT = "com.htc.launcher.extra.COMPONENT";
    public static final String EXTRA_COUNT = "com.htc.launcher.extra.COUNT";
    public static final String INTENT_SET_NOTIFICATION = "com.htc.launcher.action.SET_NOTIFICATION";
    public static final String INTENT_UPDATE_SHORTCUT = "com.htc.launcher.action.UPDATE_SHORTCUT";
    public static final String PACKAGENAME = "packagename";

    @Override // me.leolin.shortcutbadger.Badger
    public void executeBadge(Context context, ComponentName componentName, int i) throws ShortcutBadgeException {
        boolean z;
        Intent intent = new Intent(INTENT_SET_NOTIFICATION);
        intent.putExtra(EXTRA_COMPONENT, componentName.flattenToShortString());
        intent.putExtra(EXTRA_COUNT, i);
        Intent intent2 = new Intent(INTENT_UPDATE_SHORTCUT);
        intent2.putExtra(PACKAGENAME, componentName.getPackageName());
        intent2.putExtra(COUNT, i);
        boolean z2 = false;
        try {
            BroadcastHelper.sendIntentExplicitly(context, intent);
            z = true;
        } catch (ShortcutBadgeException unused) {
            z = false;
        }
        try {
            BroadcastHelper.sendIntentExplicitly(context, intent2);
            z2 = true;
        } catch (ShortcutBadgeException unused2) {
        }
        if (!z && !z2) {
            throw new ShortcutBadgeException("unable to resolve intent: " + intent2.toString());
        }
    }

    @Override // me.leolin.shortcutbadger.Badger
    public List<String> getSupportLaunchers() {
        return Collections.singletonList("com.htc.launcher");
    }
}

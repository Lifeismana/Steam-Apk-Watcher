package me.leolin.shortcutbadger.impl;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Build;
import expo.modules.notifications.service.NotificationsService;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import me.leolin.shortcutbadger.Badger;
import me.leolin.shortcutbadger.ShortcutBadgeException;
import me.leolin.shortcutbadger.util.BroadcastHelper;

@Deprecated
/* loaded from: classes3.dex */
public class XiaomiHomeBadger implements Badger {
    public static final String EXTRA_UPDATE_APP_COMPONENT_NAME = "android.intent.extra.update_application_component_name";
    public static final String EXTRA_UPDATE_APP_MSG_TEXT = "android.intent.extra.update_application_message_text";
    public static final String INTENT_ACTION = "android.intent.action.APPLICATION_MESSAGE_UPDATE";
    private ResolveInfo resolveInfo;

    @Override // me.leolin.shortcutbadger.Badger
    public void executeBadge(Context context, ComponentName componentName, int i) throws ShortcutBadgeException {
        Object valueOf;
        Object obj = "";
        try {
            Object newInstance = Class.forName("android.app.MiuiNotification").newInstance();
            Field declaredField = newInstance.getClass().getDeclaredField("messageCount");
            declaredField.setAccessible(true);
            if (i == 0) {
                valueOf = "";
            } else {
                try {
                    valueOf = Integer.valueOf(i);
                } catch (Exception unused) {
                    declaredField.set(newInstance, Integer.valueOf(i));
                }
            }
            declaredField.set(newInstance, String.valueOf(valueOf));
        } catch (Exception unused2) {
            Intent intent = new Intent(INTENT_ACTION);
            intent.putExtra(EXTRA_UPDATE_APP_COMPONENT_NAME, componentName.getPackageName() + "/" + componentName.getClassName());
            if (i != 0) {
                obj = Integer.valueOf(i);
            }
            intent.putExtra(EXTRA_UPDATE_APP_MSG_TEXT, String.valueOf(obj));
            try {
                BroadcastHelper.sendIntentExplicitly(context, intent);
            } catch (ShortcutBadgeException unused3) {
            }
        }
        if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")) {
            tryNewMiuiBadge(context, i);
        }
    }

    private void tryNewMiuiBadge(Context context, int i) throws ShortcutBadgeException {
        if (this.resolveInfo == null) {
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.HOME");
            this.resolveInfo = context.getPackageManager().resolveActivity(intent, 65536);
        }
        if (this.resolveInfo != null) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NotificationsService.NOTIFICATION_KEY);
            Notification build = new Notification.Builder(context).setContentTitle("").setContentText("").setSmallIcon(this.resolveInfo.getIconResource()).build();
            try {
                Object obj = build.getClass().getDeclaredField("extraNotification").get(build);
                obj.getClass().getDeclaredMethod("setMessageCount", Integer.TYPE).invoke(obj, Integer.valueOf(i));
                notificationManager.notify(0, build);
            } catch (Exception e) {
                throw new ShortcutBadgeException("not able to set badge", e);
            }
        }
    }

    @Override // me.leolin.shortcutbadger.Badger
    public List<String> getSupportLaunchers() {
        return Arrays.asList("com.miui.miuilite", "com.miui.home", "com.miui.miuihome", "com.miui.miuihome2", "com.miui.mihome", "com.miui.mihome2", "com.i.miui.launcher");
    }
}

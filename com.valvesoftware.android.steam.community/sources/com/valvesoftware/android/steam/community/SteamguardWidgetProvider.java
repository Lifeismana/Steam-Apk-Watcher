package com.valvesoftware.android.steam.community;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.widget.RemoteViews;
import com.valvesoftware.android.steam.community.activity.MainActivity;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public class SteamguardWidgetProvider extends AppWidgetProvider {
    private boolean mShowRefresh = false;
    private long mTimeShowRefresh = 0;
    private int mDesiredIndex = 0;

    @Override // android.appwidget.AppWidgetProvider
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] iArr) {
        int i;
        int i2;
        Paint paint;
        int[] iArr2 = iArr;
        Paint paint2 = new Paint();
        paint2.setColor(context.getResources().getColor(R.color.twofactorcode_thermometer_background));
        paint2.setStrokeWidth(3.0f);
        paint2.setStyle(Paint.Style.FILL);
        Paint paint3 = new Paint();
        paint3.setColor(context.getResources().getColor(R.color.twofactorcode_thermometer_normal));
        paint3.setStyle(Paint.Style.FILL);
        Paint paint4 = new Paint();
        paint4.setColor(context.getResources().getColor(R.color.twofactorcode_alert));
        paint4.setStyle(Paint.Style.FILL);
        int length = iArr2.length;
        int i3 = 0;
        int i4 = 0;
        boolean z = false;
        while (i4 < length) {
            int i5 = iArr2[i4];
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.steamguard_widget);
            if (this.mShowRefresh) {
                remoteViews.setTextViewText(R.id.twofactorcode_account_name, context.getResources().getString(R.string.widget_touch_to_refresh));
                Intent intent = new Intent(context, (Class<?>) SteamguardWidgetProvider.class);
                intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
                int[] iArr3 = new int[1];
                iArr3[i3] = i5;
                intent.putExtra("appWidgetIds", iArr3);
                intent.putExtra("widgetIndex", this.mDesiredIndex);
                PendingIntent broadcast = PendingIntent.getBroadcast(context, i5 << 17, intent, 1207959552);
                remoteViews.setOnClickPendingIntent(R.id.linearLayout, broadcast);
                remoteViews.setOnClickPendingIntent(R.id.widgetLogo, broadcast);
                i2 = 2;
                i = 0;
            } else {
                ArrayList<SteamguardState> sortedTwoFactorSteamGuardStates = SteamguardState.getSortedTwoFactorSteamGuardStates();
                Iterator<SteamguardState> it = sortedTwoFactorSteamGuardStates.iterator();
                i = 0;
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    SteamguardState next = it.next();
                    TwoFactorToken twoFactorToken = next.getTwoFactorToken();
                    if (i >= this.mDesiredIndex) {
                        remoteViews.setViewVisibility(R.id.twofactorcode_thermometer, i3);
                        remoteViews.setViewVisibility(R.id.twofactorcode_code, i3);
                        if (i == 0) {
                            remoteViews.setViewVisibility(R.id.twofactor_back, 4);
                        } else {
                            remoteViews.setViewVisibility(R.id.twofactor_back, i3);
                        }
                        String generateSteamGuardCode = twoFactorToken.generateSteamGuardCode();
                        String accountName = next.getAccountName();
                        remoteViews.setTextViewText(R.id.twofactorcode_code, generateSteamGuardCode);
                        remoteViews.setTextViewText(R.id.twofactorcode_account_name, accountName);
                        String str = "Widget " + i5 + " updating code to " + generateSteamGuardCode + " for account " + accountName;
                        Bitmap createBitmap = Bitmap.createBitmap(200, 10, Bitmap.Config.ARGB_8888);
                        int secondsToNextChange = ((int) (200 * (twoFactorToken.secondsToNextChange() / 30.0f))) / 2;
                        new Canvas(createBitmap).drawRect(100 - secondsToNextChange, 0.0f, 100 + secondsToNextChange, 10, twoFactorToken.secondsToNextChange() <= 7 ? paint4 : paint3);
                        remoteViews.setBitmap(R.id.twofactor_thermo_img, "setImageBitmap", createBitmap);
                        z = true;
                    } else {
                        i++;
                        i3 = 0;
                    }
                }
                if (i >= sortedTwoFactorSteamGuardStates.size() - 1) {
                    remoteViews.setViewVisibility(R.id.twofactor_forward, 4);
                } else {
                    remoteViews.setViewVisibility(R.id.twofactor_forward, 0);
                }
                if (!z) {
                    remoteViews.setTextViewText(R.id.twofactorcode_account_name, context.getResources().getString(R.string.widget_no_steamguard));
                }
                i2 = 2;
                PendingIntent activity = PendingIntent.getActivity(context, 2, new Intent(context, (Class<?>) MainActivity.class), 134217728);
                remoteViews.setOnClickPendingIntent(R.id.linearLayout, activity);
                remoteViews.setOnClickPendingIntent(R.id.widgetLogo, activity);
            }
            if (z) {
                if (Build.VERSION.SDK_INT >= 16) {
                    remoteViews.setTextViewTextSize(R.id.twofactorcode_account_name, i2, 11.0f);
                }
            } else {
                remoteViews.setViewVisibility(R.id.twofactor_back, 8);
                remoteViews.setViewVisibility(R.id.twofactor_forward, 8);
                remoteViews.setViewVisibility(R.id.twofactorcode_thermometer, 4);
                remoteViews.setViewVisibility(R.id.twofactorcode_code, 8);
                if (Build.VERSION.SDK_INT >= 16) {
                    remoteViews.setTextViewTextSize(R.id.twofactorcode_account_name, i2, 16.0f);
                }
                remoteViews.setTextViewText(R.id.twofactorcode_code, context.getResources().getString(R.string.widget_no_code));
            }
            Intent intent2 = new Intent(context, (Class<?>) SteamguardWidgetProvider.class);
            intent2.setAction("android.appwidget.action.APPWIDGET_UPDATE");
            intent2.putExtra("appWidgetIds", new int[]{i5});
            intent2.putExtra("widgetIndex", i - 1);
            remoteViews.setOnClickPendingIntent(R.id.twofactor_back, PendingIntent.getBroadcast(context, i5 << 19, intent2, 1207959552));
            Intent intent3 = new Intent(context, (Class<?>) SteamguardWidgetProvider.class);
            intent3.setAction("android.appwidget.action.APPWIDGET_UPDATE");
            intent3.putExtra("appWidgetIds", new int[]{i5});
            intent3.putExtra("widgetIndex", i + 1);
            remoteViews.setOnClickPendingIntent(R.id.twofactor_forward, PendingIntent.getBroadcast(context, i5 << 20, intent3, 1207959552));
            appWidgetManager.updateAppWidget(i5, remoteViews);
            if (z) {
                AlarmManager alarmManager = (AlarmManager) context.getSystemService("alarm");
                Intent intent4 = new Intent(context, (Class<?>) SteamguardWidgetProvider.class);
                paint = paint3;
                intent4.putExtra("timeShowRefresh", this.mTimeShowRefresh);
                intent4.putExtra("widgetIndex", i);
                intent4.setAction("android.appwidget.action.APPWIDGET_UPDATE");
                intent4.putExtra("appWidgetIds", new int[]{i5});
                long currentTimeMillis = System.currentTimeMillis() + 3000;
                intent4.putExtra("widgetShowRefreshButton", currentTimeMillis >= this.mTimeShowRefresh);
                alarmManager.set(1, currentTimeMillis, PendingIntent.getBroadcast(context, i5 << 21, intent4, 1207959552));
            } else {
                paint = paint3;
            }
            i4++;
            paint3 = paint;
            iArr2 = iArr;
            i3 = 0;
        }
        if (this.mShowRefresh) {
            this.mShowRefresh = false;
        }
    }

    @Override // android.appwidget.AppWidgetProvider, android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        this.mShowRefresh = intent.getBooleanExtra("widgetShowRefreshButton", false);
        this.mDesiredIndex = intent.getIntExtra("widgetIndex", 0);
        this.mTimeShowRefresh = intent.getLongExtra("timeShowRefresh", System.currentTimeMillis() + 30000);
        super.onReceive(context, intent);
    }

    @Override // android.appwidget.AppWidgetProvider
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int i, Bundle bundle) {
        onUpdate(context, appWidgetManager, new int[]{i});
    }
}

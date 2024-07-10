package com.valvesoftware.android.steam.community;

import android.util.Pair;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.valvesoftware.android.steam.community.ValveNotificationsHelper;
import expo.modules.notifications.service.NotificationsService;
import java.util.Iterator;
import java.util.Map;
import org.bouncycastle.i18n.MessageBundle;

/* loaded from: classes2.dex */
public class ValveNotificationsModule extends ReactContextBaseJavaModule {
    public static String MessageEventName = "NewNotification";
    public static String Name = "ValveNotifications";
    private ReactApplicationContext m_ctx;

    @ReactMethod
    public void addListener(String str) {
    }

    @ReactMethod
    public void removeListeners(Integer num) {
    }

    public ValveNotificationsModule(final ReactApplicationContext reactApplicationContext) {
        super(reactApplicationContext);
        this.m_ctx = reactApplicationContext;
        ValveNotificationsHelper.getInstance().registerMessageReceivedListener(new ValveNotificationsHelper.OnMessageReceivedListener() { // from class: com.valvesoftware.android.steam.community.ValveNotificationsModule.1
            @Override // com.valvesoftware.android.steam.community.ValveNotificationsHelper.OnMessageReceivedListener
            public void onMessageReceived(RemoteMessage remoteMessage) {
                this.onMessageReceived(new Pair<>(remoteMessage, reactApplicationContext.getLifecycleState() == LifecycleState.RESUMED ? ValveNotificationsHelper.ENotificationAppState.Foreground : ValveNotificationsHelper.ENotificationAppState.Background));
            }
        });
    }

    @Override // com.facebook.react.bridge.NativeModule
    public String getName() {
        return Name;
    }

    public void onMessageReceived(Pair<RemoteMessage, ValveNotificationsHelper.ENotificationAppState> pair) {
        sendMessageToJS(pair);
    }

    private WritableMap serializeMessageToWritableMap(Pair<RemoteMessage, ValveNotificationsHelper.ENotificationAppState> pair) {
        WritableMap createMap = Arguments.createMap();
        createMap.putInt("appState", ((ValveNotificationsHelper.ENotificationAppState) pair.second).getValue());
        RemoteMessage remoteMessage = (RemoteMessage) pair.first;
        Map<String, String> data = remoteMessage.getData();
        WritableMap createMap2 = Arguments.createMap();
        Iterator<Map.Entry<String, String>> it = data.entrySet().iterator();
        if (remoteMessage.getNotification() != null) {
            createMap2.putString("body", remoteMessage.getNotification().getBody());
            createMap2.putString(MessageBundle.TITLE_ENTRY, remoteMessage.getNotification().getTitle());
        }
        if (it != null) {
            while (it.hasNext()) {
                Map.Entry<String, String> next = it.next();
                createMap2.putString(next.getKey(), next.getValue());
            }
        }
        createMap.putMap(NotificationsService.NOTIFICATION_KEY, createMap2);
        return createMap;
    }

    private void sendMessageToJS(Pair<RemoteMessage, ValveNotificationsHelper.ENotificationAppState> pair) {
        try {
            ((DeviceEventManagerModule.RCTDeviceEventEmitter) this.m_ctx.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)).emit(MessageEventName, serializeMessageToWritableMap(pair));
        } catch (IllegalStateException unused) {
        }
    }

    @ReactMethod
    public void getFcmDeviceToken(final Promise promise) {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() { // from class: com.valvesoftware.android.steam.community.ValveNotificationsModule.2
            @Override // com.google.android.gms.tasks.OnCompleteListener
            public void onComplete(Task<String> task) {
                if (!task.isSuccessful()) {
                    promise.reject(ValveNotificationsModule.Name, task.getException().getMessage());
                } else {
                    promise.resolve(task.getResult());
                }
            }
        });
    }

    @ReactMethod
    public void getInitialNotifications(Promise promise) {
        WritableArray createArray = Arguments.createArray();
        Iterator it = ValveNotificationsHelper.getInstance().getPendingNotifications().iterator();
        while (it.hasNext()) {
            createArray.pushMap(serializeMessageToWritableMap((Pair) it.next()));
        }
        ValveNotificationsHelper.getInstance().clearPendingNotifications();
        promise.resolve(createArray);
    }
}

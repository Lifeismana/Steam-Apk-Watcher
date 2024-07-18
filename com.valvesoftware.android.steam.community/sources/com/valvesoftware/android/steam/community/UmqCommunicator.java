package com.valvesoftware.android.steam.community;

import android.os.Handler;
import com.valvesoftware.android.steam.community.jsontranslators.ActiveMessageSessionsTranslator;
import com.valvesoftware.android.steam.community.jsontranslators.UmqMessageHistoryTranslator;
import com.valvesoftware.android.steam.community.jsontranslators.UmqPollResultTranslator;
import com.valvesoftware.android.steam.community.model.MessageSession;
import com.valvesoftware.android.steam.community.model.PollStatus;
import com.valvesoftware.android.steam.community.model.UmqMessage;
import com.valvesoftware.android.steam.community.model.UmqPollResult;
import com.valvesoftware.android.steam.community.model.UserNotificationCounts;
import com.valvesoftware.android.steam.community.webrequests.Endpoints;
import com.valvesoftware.android.steam.community.webrequests.RequestBuilder;
import com.valvesoftware.android.steam.community.webrequests.RequestErrorInfo;
import com.valvesoftware.android.steam.community.webrequests.ResponseListener;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class UmqCommunicator {
    private static UmqCommunicator instance;
    private ChatStateListener chatStateListener;
    private long lastMessageNumber;
    private LoggedInStatusChangedListener loggedInStatusChangedListener;
    private NotificationCountUpdateListener notificationCountUpdateListener;
    private String umqId;
    private boolean canSendTypingNotification = true;
    private boolean stopPolling = true;
    private final AtomicBoolean pollInFlight = new AtomicBoolean(false);
    private final AtomicLong lastPollTime = new AtomicLong(0);
    private final Handler enqueueStopHandler = new Handler();
    private final Handler uiThreadHandler = new Handler();
    final AtomicInteger numSwitchToPushRetries = new AtomicInteger(0);
    private int consecutiveLoginAttemptsFailed = 0;
    private final SteamCommunityApplication steamCommunityApplication = SteamCommunityApplication.GetInstance();
    private final LocalDb umqdb = new LocalDb(this.steamCommunityApplication.getApplicationContext());

    static /* synthetic */ int access$1808(UmqCommunicator umqCommunicator) {
        int i = umqCommunicator.consecutiveLoginAttemptsFailed;
        umqCommunicator.consecutiveLoginAttemptsFailed = i + 1;
        return i;
    }

    private UmqCommunicator() {
    }

    public static boolean isUmqRunning() {
        UmqCommunicator umqCommunicator = instance;
        return umqCommunicator != null && umqCommunicator.isRunning();
    }

    public static UmqCommunicator getInstance() {
        if (instance == null) {
            synchronized (UmqCommunicator.class) {
                instance = new UmqCommunicator();
            }
        }
        return instance;
    }

    public void stop() {
        stop(60000);
    }

    public void stopImmediate() {
        stop(0);
    }

    public void stop(int i) {
        if (i == 0) {
            this.stopPolling = true;
        }
        this.enqueueStopHandler.postDelayed(new Runnable() { // from class: com.valvesoftware.android.steam.community.UmqCommunicator.1
            @Override // java.lang.Runnable
            public void run() {
                UmqCommunicator.this.stopPolling = true;
                UmqCommunicator.this.switchToPush();
            }
        }, i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.valvesoftware.android.steam.community.UmqCommunicator$2 */
    /* loaded from: classes.dex */
    public class RunnableC01462 implements Runnable {
        RunnableC01462() {
        }

        @Override // java.lang.Runnable
        public void run() {
            if (SteamCommunityApplication.GetInstance().GetSettingInfoDB().usePushInBackground()) {
                String storedRegistrationId = new GcmRegistrar().getStoredRegistrationId(UmqCommunicator.this.steamCommunityApplication.getApplicationContext());
                if (storedRegistrationId == null && storedRegistrationId.length() == 0) {
                    return;
                }
                UmqCommunicator.this.setPushInfoOnServer(true, new ResponseListener() { // from class: com.valvesoftware.android.steam.community.UmqCommunicator.2.1
                    @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
                    public void onSuccess(JSONObject jSONObject) {
                        RequestBuilder switchToPushRequestBuilder = Endpoints.getSwitchToPushRequestBuilder(UmqCommunicator.this.umqId);
                        UmqCommunicator.this.numSwitchToPushRetries.set(0);
                        switchToPushRequestBuilder.setResponseListener(new ResponseListener() { // from class: com.valvesoftware.android.steam.community.UmqCommunicator.2.1.1
                            @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
                            public void onSuccess(JSONObject jSONObject2) {
                                UmqCommunicator.this.numSwitchToPushRetries.set(0);
                            }

                            @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
                            public void onError(RequestErrorInfo requestErrorInfo) {
                                if (UmqCommunicator.this.numSwitchToPushRetries.getAndIncrement() < 3) {
                                    UmqCommunicator.this.switchToPush();
                                }
                            }
                        });
                        UmqCommunicator.this.steamCommunityApplication.sendRequest(switchToPushRequestBuilder);
                        UmqCommunicator.this.umqId = "0";
                    }

                    @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
                    public void onError(RequestErrorInfo requestErrorInfo) {
                        if (UmqCommunicator.this.numSwitchToPushRetries.getAndIncrement() < 3) {
                            UmqCommunicator.this.switchToPush();
                        }
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void switchToPush() {
        new Handler().postDelayed(new RunnableC01462(), this.numSwitchToPushRetries.get() * 2000);
    }

    public void start() {
        this.enqueueStopHandler.removeCallbacksAndMessages(null);
        if (this.stopPolling || !this.pollInFlight.get()) {
            this.stopPolling = false;
            pollUmqStatus();
        }
    }

    public boolean isRunning() {
        return !this.stopPolling;
    }

    public void sendMessage(UmqMessage umqMessage, String str) {
        if (umqMessage == null || umqMessage.isEmpty()) {
            return;
        }
        RequestBuilder sendUMQMessageRequestBuilder = Endpoints.getSendUMQMessageRequestBuilder(umqMessage.text, str, this.umqId);
        sendUMQMessageRequestBuilder.setResponseListener(new C01473(umqMessage, str));
        this.steamCommunityApplication.sendRequest(sendUMQMessageRequestBuilder);
    }

    /* renamed from: com.valvesoftware.android.steam.community.UmqCommunicator$3 */
    /* loaded from: classes.dex */
    class C01473 extends ResponseListener {
        final /* synthetic */ String val$chatPartnerSteamId;
        final /* synthetic */ UmqMessage val$message;

        C01473(UmqMessage umqMessage, String str) {
            this.val$message = umqMessage;
            this.val$chatPartnerSteamId = str;
        }

        @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
        public void onSuccess(final JSONObject jSONObject) {
            UmqCommunicator.this.steamCommunityApplication.runOnBackgroundThread(new Runnable() { // from class: com.valvesoftware.android.steam.community.UmqCommunicator.3.1
                @Override // java.lang.Runnable
                public void run() {
                    long optLong = jSONObject.optLong("utc_timestamp", 0L);
                    C01473.this.val$message.utcTimeStamp = optLong;
                    UmqCommunicator.this.umqdb.saveSentMessage(C01473.this.val$message.text, optLong, LoggedInUserAccountInfo.getLoginSteamID(), C01473.this.val$chatPartnerSteamId);
                    if (UmqCommunicator.this.chatStateListener != null) {
                        UmqCommunicator.this.uiThreadHandler.post(new Runnable() { // from class: com.valvesoftware.android.steam.community.UmqCommunicator.3.1.1
                            @Override // java.lang.Runnable
                            public void run() {
                                UmqCommunicator.this.chatStateListener.messageSent(C01473.this.val$message);
                            }
                        });
                    }
                }
            });
        }

        @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
        public void onError(RequestErrorInfo requestErrorInfo) {
            if (UmqCommunicator.this.chatStateListener != null) {
                UmqCommunicator.this.chatStateListener.messageSendFailed(this.val$message);
            }
        }
    }

    public void sendTypingNotification(String str) {
        if (this.canSendTypingNotification) {
            this.canSendTypingNotification = false;
            RequestBuilder sendUMQTypingNotificationRequestBuilder = Endpoints.getSendUMQTypingNotificationRequestBuilder(str, this.umqId);
            sendUMQTypingNotificationRequestBuilder.setResponseListener(new ResponseListener() { // from class: com.valvesoftware.android.steam.community.UmqCommunicator.4
                @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
                public void onSuccess(JSONObject jSONObject) {
                    UmqCommunicator.this.canSendTypingNotification = true;
                }

                @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
                public void onError(RequestErrorInfo requestErrorInfo) {
                    UmqCommunicator.this.canSendTypingNotification = true;
                }
            });
            this.steamCommunityApplication.sendRequest(sendUMQTypingNotificationRequestBuilder);
        }
    }

    public void updateOfflineChats() {
        RequestBuilder activeMessageSessions = Endpoints.getActiveMessageSessions();
        activeMessageSessions.setResponseListener(new ResponseListener() { // from class: com.valvesoftware.android.steam.community.UmqCommunicator.5
            @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
            public void onError(RequestErrorInfo requestErrorInfo) {
            }

            @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
            public void onSuccess(JSONObject jSONObject) {
                Iterator<MessageSession> it = ActiveMessageSessionsTranslator.translateList(jSONObject).iterator();
                while (it.hasNext()) {
                    UmqCommunicator.this.updateChatMessages(it.next().steamId);
                }
            }
        });
        this.steamCommunityApplication.sendRequest(activeMessageSessions);
    }

    public void setChatLoggedInStatusChangedListener(LoggedInStatusChangedListener loggedInStatusChangedListener) {
        this.loggedInStatusChangedListener = loggedInStatusChangedListener;
    }

    public void setChatStateListener(ChatStateListener chatStateListener) {
        this.chatStateListener = chatStateListener;
    }

    public void setNotificationCountUpdateListener(NotificationCountUpdateListener notificationCountUpdateListener) {
        this.notificationCountUpdateListener = notificationCountUpdateListener;
    }

    public void updateChatMessages(String str) {
        updateChatMessages(str, false, null);
    }

    public void updateChatMessages(String str, boolean z, CompleteCallback completeCallback) {
        RequestBuilder recentMessages = Endpoints.getRecentMessages(LoggedInUserAccountInfo.getLoginSteamID(), str);
        recentMessages.setResponseListener(new C01506(str, completeCallback, z));
        this.steamCommunityApplication.sendRequest(recentMessages);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.valvesoftware.android.steam.community.UmqCommunicator$6 */
    /* loaded from: classes.dex */
    public class C01506 extends ResponseListener {
        final /* synthetic */ String val$chatPartnerSteamId;
        final /* synthetic */ boolean val$enterNewMessagesAsUnread;
        final /* synthetic */ CompleteCallback val$onCompleteCallback;

        @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
        public void onError(RequestErrorInfo requestErrorInfo) {
        }

        C01506(String str, CompleteCallback completeCallback, boolean z) {
            this.val$chatPartnerSteamId = str;
            this.val$onCompleteCallback = completeCallback;
            this.val$enterNewMessagesAsUnread = z;
        }

        @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
        public void onSuccess(JSONObject jSONObject) {
            final List<UmqMessage> translateList = UmqMessageHistoryTranslator.translateList(jSONObject, this.val$chatPartnerSteamId);
            if (translateList == null || translateList.size() == 0) {
                return;
            }
            SteamCommunityApplication.GetInstance().runOnBackgroundThread(new Runnable() { // from class: com.valvesoftware.android.steam.community.UmqCommunicator.6.1
                @Override // java.lang.Runnable
                public void run() {
                    long mostRecentDeletionTime = UmqCommunicator.this.umqdb.getMostRecentDeletionTime(LoggedInUserAccountInfo.getLoginSteamID(), C01506.this.val$chatPartnerSteamId);
                    Iterator it = translateList.iterator();
                    while (it.hasNext()) {
                        if (((UmqMessage) it.next()).utcTimeStamp <= mostRecentDeletionTime) {
                            it.remove();
                        }
                    }
                    if (translateList.size() <= 0 || UmqCommunicator.this.umqdb.saveMessages(translateList, LoggedInUserAccountInfo.getLoginSteamID(), C01506.this.val$enterNewMessagesAsUnread) <= 0) {
                        return;
                    }
                    UmqCommunicator.this.uiThreadHandler.post(new Runnable() { // from class: com.valvesoftware.android.steam.community.UmqCommunicator.6.1.1
                        @Override // java.lang.Runnable
                        public void run() {
                            UmqCommunicator.this.sendMessagesSavedNotification(translateList);
                        }
                    });
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void pollUmqStatus() {
        if (this.stopPolling) {
            return;
        }
        if (this.umqId == null) {
            loginToUmq();
            return;
        }
        long currentTimeMillis = System.currentTimeMillis();
        if (!this.pollInFlight.get() || currentTimeMillis - this.lastPollTime.get() >= 30000) {
            this.lastPollTime.set(System.currentTimeMillis());
            this.pollInFlight.set(true);
            RequestBuilder uMQPollStatusRequestBuilder = Endpoints.getUMQPollStatusRequestBuilder(LoggedInUserAccountInfo.getLoginSteamID(), this.umqId, this.lastMessageNumber);
            uMQPollStatusRequestBuilder.setResponseListener(new ResponseListener() { // from class: com.valvesoftware.android.steam.community.UmqCommunicator.7
                @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
                public void onSuccess(JSONObject jSONObject) {
                    UmqPollResult translate = UmqPollResultTranslator.translate(jSONObject);
                    UmqCommunicator.this.pollInFlight.set(false);
                    if (translate.statusCode == PollStatus.NOT_LOGGED_ON) {
                        if (UmqCommunicator.this.stopPolling) {
                            return;
                        }
                        UmqCommunicator.this.loginToUmq();
                        return;
                    }
                    if (translate.statusCode == PollStatus.TIMEOUT) {
                        UmqCommunicator.this.pollUmqStatus();
                        return;
                    }
                    UmqCommunicator.this.lastMessageNumber = translate.lastMessageNumber;
                    if (translate.containsMessageText()) {
                        UmqCommunicator.this.pollUmqForMessageContents();
                        return;
                    }
                    UmqCommunicator.this.sendRefreshSteamIdsNotification(translate.steamIdsWithPersonaStateChange());
                    UmqCommunicator.this.sendRelationshipChangeNotification(translate.steamIdsWithRelationshipChanges());
                    if (translate.containsIsTypingNotification()) {
                        UmqCommunicator.this.sendIsTypingNotification(translate.getTypingNotificationMessages());
                    }
                    if (translate.containsNotificationCountUpdate()) {
                        UmqCommunicator.this.sendNotificationCountsUpdate(translate.getNotificationCountMessage().notificationCounts);
                    }
                    if (UmqCommunicator.this.stopPolling) {
                        return;
                    }
                    UmqCommunicator.this.pollUmqStatus();
                }

                @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
                public void onError(RequestErrorInfo requestErrorInfo) {
                    UmqCommunicator.this.pollInFlight.set(false);
                    UmqCommunicator.this.loginToUmq();
                }
            });
            this.steamCommunityApplication.sendRequest(uMQPollStatusRequestBuilder);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendRefreshSteamIdsNotification(List<String> list) {
        ChatStateListener chatStateListener;
        if (list == null || list.size() == 0 || (chatStateListener = this.chatStateListener) == null) {
            return;
        }
        chatStateListener.personaStateChanged(list);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendRelationshipChangeNotification(List<String> list) {
        ChatStateListener chatStateListener;
        if (list == null || list.size() == 0 || (chatStateListener = this.chatStateListener) == null) {
            return;
        }
        chatStateListener.relationshipStateChanged(list);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendNotificationCountsUpdate(UserNotificationCounts userNotificationCounts) {
        NotificationCountUpdateListener notificationCountUpdateListener = this.notificationCountUpdateListener;
        if (notificationCountUpdateListener != null) {
            notificationCountUpdateListener.notificationCountsChanged(userNotificationCounts);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void pollUmqForMessageContents() {
        if (this.umqId == null) {
            pollUmqStatus();
            return;
        }
        RequestBuilder uMQPollForMessageRequestBuilder = Endpoints.getUMQPollForMessageRequestBuilder(LoggedInUserAccountInfo.getLoginSteamID(), this.umqId, this.lastMessageNumber);
        uMQPollForMessageRequestBuilder.setResponseListener(new C01528());
        this.steamCommunityApplication.sendRequest(uMQPollForMessageRequestBuilder);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.valvesoftware.android.steam.community.UmqCommunicator$8 */
    /* loaded from: classes.dex */
    public class C01528 extends ResponseListener {
        C01528() {
        }

        @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
        public void onSuccess(JSONObject jSONObject) {
            UmqPollResult translate = UmqPollResultTranslator.translate(jSONObject);
            if (translate.statusCode == PollStatus.NOT_LOGGED_ON) {
                if (UmqCommunicator.this.stopPolling) {
                    return;
                }
                UmqCommunicator.this.loginToUmq();
            } else {
                UmqCommunicator.this.lastMessageNumber = translate.lastMessageNumber;
                if (translate.containsMessageText()) {
                    final List<UmqMessage> allMessagesWithText = translate.getAllMessagesWithText();
                    UmqCommunicator.this.steamCommunityApplication.runOnBackgroundThread(new Runnable() { // from class: com.valvesoftware.android.steam.community.UmqCommunicator.8.1
                        @Override // java.lang.Runnable
                        public void run() {
                            UmqCommunicator.this.umqdb.saveMessages(allMessagesWithText, LoggedInUserAccountInfo.getLoginSteamID(), true);
                            UmqCommunicator.this.uiThreadHandler.post(new Runnable() { // from class: com.valvesoftware.android.steam.community.UmqCommunicator.8.1.1
                                @Override // java.lang.Runnable
                                public void run() {
                                    UmqCommunicator.this.sendMessagesSavedNotification(allMessagesWithText);
                                    for (UmqMessage umqMessage : allMessagesWithText) {
                                        UmqCommunicator.this.sendNoticesAsNeeded(umqMessage.chatPartnerSteamId, umqMessage.text);
                                    }
                                }
                            });
                        }
                    });
                }
                UmqCommunicator.this.pollUmqStatus();
            }
        }

        @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
        public void onError(RequestErrorInfo requestErrorInfo) {
            UmqCommunicator.this.pollUmqStatus();
        }
    }

    public void loginToUmq() {
        loginToUmq(null);
    }

    public void loginToUmq(final ResponseListener responseListener) {
        RequestBuilder uMQLogonRequestBuilder = Endpoints.getUMQLogonRequestBuilder();
        uMQLogonRequestBuilder.setResponseListener(new ResponseListener() { // from class: com.valvesoftware.android.steam.community.UmqCommunicator.9
            @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
            public void onSuccess(JSONObject jSONObject) {
                UmqCommunicator.this.consecutiveLoginAttemptsFailed = 0;
                UmqCommunicator.this.umqId = jSONObject.optString("umqid");
                if (jSONObject.optInt("push", -1) == 0) {
                    UmqCommunicator.this.setServerPushStateBasedOnUserPreference();
                }
                UmqCommunicator.this.lastMessageNumber = jSONObject.optLong("message", -1L);
                UmqCommunicator.this.pollUmqStatus();
                ResponseListener responseListener2 = responseListener;
                if (responseListener2 != null) {
                    responseListener2.onSuccess(jSONObject);
                }
                if (UmqCommunicator.this.loggedInStatusChangedListener != null) {
                    UmqCommunicator.this.loggedInStatusChangedListener.loggedIn();
                }
            }

            @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
            public void onError(RequestErrorInfo requestErrorInfo) {
                if (UmqCommunicator.this.loggedInStatusChangedListener != null && UmqCommunicator.this.consecutiveLoginAttemptsFailed > 3) {
                    UmqCommunicator.this.loggedInStatusChangedListener.loggedOff();
                }
                UmqCommunicator.access$1808(UmqCommunicator.this);
                new Handler().postDelayed(new Runnable() { // from class: com.valvesoftware.android.steam.community.UmqCommunicator.9.1
                    @Override // java.lang.Runnable
                    public void run() {
                        UmqCommunicator.this.loginToUmq();
                    }
                }, 2000L);
            }
        });
        this.steamCommunityApplication.sendRequest(uMQLogonRequestBuilder);
    }

    public void setServerPushStateBasedOnUserPreference() {
        setPushInfoOnServer(this.steamCommunityApplication.GetSettingInfoDB().usePushInBackground(), null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setPushInfoOnServer(boolean z, final ResponseListener responseListener) {
        String storedRegistrationId = new GcmRegistrar().getStoredRegistrationId(this.steamCommunityApplication.getApplicationContext());
        if (storedRegistrationId == null || storedRegistrationId.length() == 0) {
            return;
        }
        RequestBuilder sendServerPushInfoRequestBuilder = Endpoints.getSendServerPushInfoRequestBuilder(storedRegistrationId, z, this.umqId);
        sendServerPushInfoRequestBuilder.setResponseListener(new ResponseListener() { // from class: com.valvesoftware.android.steam.community.UmqCommunicator.10
            @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
            public void onSuccess(JSONObject jSONObject) {
                ResponseListener responseListener2 = responseListener;
                if (responseListener2 != null) {
                    responseListener2.onSuccess(jSONObject);
                }
            }

            @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
            public void onError(RequestErrorInfo requestErrorInfo) {
                ResponseListener responseListener2 = responseListener;
                if (responseListener2 != null) {
                    responseListener2.onError(requestErrorInfo);
                }
            }
        });
        this.steamCommunityApplication.sendRequest(sendServerPushInfoRequestBuilder);
    }

    public void signOutOfAppCompletely() {
        this.stopPolling = true;
        setPushInfoOnServer(false, null);
    }

    public void logOffFromUmq(final ResponseListener responseListener) {
        stopImmediate();
        String str = this.umqId;
        if (str == null) {
            str = "0";
        }
        RequestBuilder uMQLogoffRequestBuilder = Endpoints.getUMQLogoffRequestBuilder(str);
        uMQLogoffRequestBuilder.setResponseListener(new ResponseListener() { // from class: com.valvesoftware.android.steam.community.UmqCommunicator.11
            @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
            public void onSuccess(JSONObject jSONObject) {
                ResponseListener responseListener2 = responseListener;
                if (responseListener2 != null) {
                    responseListener2.onSuccess(jSONObject);
                }
                UmqCommunicator.this.umqId = "0";
                if (UmqCommunicator.this.loggedInStatusChangedListener != null) {
                    UmqCommunicator.this.loggedInStatusChangedListener.loggedOff();
                }
            }

            @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
            public void onError(RequestErrorInfo requestErrorInfo) {
                ResponseListener responseListener2 = responseListener;
                if (responseListener2 != null) {
                    responseListener2.onError(requestErrorInfo);
                }
            }
        });
        this.steamCommunityApplication.sendRequest(uMQLogoffRequestBuilder);
    }

    public boolean isLoggedInToChat() {
        String str = this.umqId;
        return (str == null || str.equals("0")) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendMessagesSavedNotification(List<UmqMessage> list) {
        ChatStateListener chatStateListener = this.chatStateListener;
        if (chatStateListener == null) {
            return;
        }
        chatStateListener.newMessagesSaved(list);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendIsTypingNotification(List<UmqMessage> list) {
        ChatStateListener chatStateListener = this.chatStateListener;
        if (chatStateListener == null) {
            return;
        }
        chatStateListener.isTypingMessageReceived(list);
    }

    public void sendNoticesAsNeeded(String str, String str2) {
        ChatStateListener chatStateListener;
        boolean z = (SteamCommunityApplication.isInForeground && (chatStateListener = this.chatStateListener) != null && (chatStateListener.listenerWillHandleAllVisualChatNotifications() || this.chatStateListener.listenerWillHandleVisualChatNotificationForSteamId(str))) ? false : true;
        LocalDb localDb = this.umqdb;
        String personaNameForSteamId = localDb != null ? localDb.getPersonaNameForSteamId(str) : "";
        if (z) {
            NotificationSender.getInstance().sendChatNotification(str2, personaNameForSteamId, false);
        } else {
            NotificationSender.getInstance().ringOrVibrateAsNeededForChat(personaNameForSteamId);
        }
    }
}

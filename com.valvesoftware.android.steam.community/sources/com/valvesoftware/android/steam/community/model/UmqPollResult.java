package com.valvesoftware.android.steam.community.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class UmqPollResult {
    private boolean containsMessageText;
    public int nextSuggestedTimeoutDuration;
    public long pollId;
    public PollStatus statusCode;
    public long timeStamp;
    public long utcTimeStamp;
    public long lastMessageNumber = -1;
    public long messagebase = -1;
    private final ArrayList<UmqMessage> umqMessages = new ArrayList<>();
    private UmqMessageNotificationCounts umqNotificationCountMessage = null;

    public void addMessage(UmqMessageBase umqMessageBase) {
        if (umqMessageBase == null) {
            return;
        }
        if (umqMessageBase instanceof UmqMessageNotificationCounts) {
            this.umqNotificationCountMessage = (UmqMessageNotificationCounts) umqMessageBase;
        } else if (umqMessageBase instanceof UmqMessage) {
            this.umqMessages.add((UmqMessage) umqMessageBase);
            if (umqMessageBase.type == UmqMessageType.MESSAGE_TEXT) {
                this.containsMessageText = true;
            }
        }
    }

    public List<UmqMessage> getAllMessagesWithText() {
        ArrayList arrayList = new ArrayList();
        ArrayList<UmqMessage> arrayList2 = this.umqMessages;
        if (arrayList2 == null) {
            return Collections.EMPTY_LIST;
        }
        Iterator<UmqMessage> it = arrayList2.iterator();
        while (it.hasNext()) {
            UmqMessage next = it.next();
            if (next.type == UmqMessageType.MESSAGE_TEXT) {
                arrayList.add(next);
            }
        }
        return arrayList;
    }

    public boolean containsMessageText() {
        return this.containsMessageText;
    }

    public boolean containsIsTypingNotification() {
        return getTypingNotificationMessages().size() > 0;
    }

    public List<UmqMessage> getTypingNotificationMessages() {
        ArrayList arrayList = new ArrayList();
        ArrayList<UmqMessage> arrayList2 = this.umqMessages;
        if (arrayList2 == null) {
            return arrayList;
        }
        Iterator<UmqMessage> it = arrayList2.iterator();
        while (it.hasNext()) {
            UmqMessage next = it.next();
            if (next.type == UmqMessageType.TYPING) {
                arrayList.add(next);
            }
        }
        return arrayList;
    }

    public List<String> steamIdsWithPersonaStateChange() {
        ArrayList arrayList = new ArrayList();
        Iterator<UmqMessage> it = this.umqMessages.iterator();
        while (it.hasNext()) {
            UmqMessage next = it.next();
            if (next.type == UmqMessageType.PERSONA_STATE) {
                arrayList.add(next.chatPartnerSteamId);
            }
        }
        return arrayList;
    }

    public List<String> steamIdsWithRelationshipChanges() {
        ArrayList arrayList = new ArrayList();
        Iterator<UmqMessage> it = this.umqMessages.iterator();
        while (it.hasNext()) {
            UmqMessage next = it.next();
            if (next.type == UmqMessageType.PERSONA_RELATIONSHIP) {
                arrayList.add(next.chatPartnerSteamId);
            }
        }
        return arrayList;
    }

    public boolean containsNotificationCountUpdate() {
        return this.umqNotificationCountMessage != null;
    }

    public UmqMessageNotificationCounts getNotificationCountMessage() {
        return this.umqNotificationCountMessage;
    }
}

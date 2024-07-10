package com.valvesoftware.android.steam.community.jsontranslators;

import com.valvesoftware.android.steam.community.model.PersonaState;
import com.valvesoftware.android.steam.community.model.PollStatus;
import com.valvesoftware.android.steam.community.model.UmqMessage;
import com.valvesoftware.android.steam.community.model.UmqMessageBase;
import com.valvesoftware.android.steam.community.model.UmqMessageNotificationCounts;
import com.valvesoftware.android.steam.community.model.UmqMessageType;
import com.valvesoftware.android.steam.community.model.UmqPollResult;
import com.valvesoftware.android.steam.community.model.UserNotificationCounts;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class UmqPollResultTranslator {
    public static UmqPollResult translate(JSONObject jSONObject) {
        UmqMessageBase translateMessage;
        UmqPollResult umqPollResult = new UmqPollResult();
        umqPollResult.statusCode = PollStatus.getValueFromString(jSONObject.optString("error"));
        umqPollResult.lastMessageNumber = jSONObject.optLong("messagelast", -1L);
        umqPollResult.messagebase = jSONObject.optLong("messagebase");
        umqPollResult.nextSuggestedTimeoutDuration = jSONObject.optInt("sectimeout");
        umqPollResult.timeStamp = jSONObject.optLong("timestamp", -1L);
        umqPollResult.utcTimeStamp = jSONObject.optLong("utc_timestamp", -1L);
        umqPollResult.pollId = jSONObject.optLong("pollid", -1L);
        JSONArray optJSONArray = jSONObject.optJSONArray("messages");
        if (optJSONArray == null) {
            return umqPollResult;
        }
        for (int i = 0; i < optJSONArray.length(); i++) {
            JSONObject optJSONObject = optJSONArray.optJSONObject(i);
            if (optJSONObject != null && (translateMessage = translateMessage(optJSONObject)) != null) {
                umqPollResult.addMessage(translateMessage);
            }
        }
        return umqPollResult;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v2, types: [com.valvesoftware.android.steam.community.model.UmqMessage] */
    /* JADX WARN: Type inference failed for: r0v3, types: [com.valvesoftware.android.steam.community.model.UmqMessageBase] */
    /* JADX WARN: Type inference failed for: r0v4, types: [com.valvesoftware.android.steam.community.model.UmqMessageNotificationCounts] */
    private static UmqMessageBase translateMessage(JSONObject jSONObject) {
        UmqMessageType umqMessageTypeFromString;
        ?? umqMessage;
        if (jSONObject == null || (umqMessageTypeFromString = getUmqMessageTypeFromString(jSONObject.optString("type"))) == null) {
            return null;
        }
        if (umqMessageTypeFromString == UmqMessageType.NOTIFICATION_COUNTS) {
            umqMessage = new UmqMessageNotificationCounts();
            for (int i = 0; i < UserNotificationCounts.MAX_NOTIFICATION_TYPES; i++) {
                int optInt = jSONObject.optInt("n" + i, 0);
                if (optInt > 0) {
                    umqMessage.notificationCounts.SetNotificationCount(i, optInt);
                }
            }
        } else {
            umqMessage = new UmqMessage();
            umqMessage.chatPartnerSteamId = jSONObject.optString("steamid_from");
            umqMessage.text = jSONObject.optString("text");
            umqMessage.personaState = PersonaState.FromInteger(jSONObject.optInt("persona_state", -1));
        }
        umqMessage.type = umqMessageTypeFromString;
        umqMessage.utcTimeStamp = jSONObject.optLong("utc_timestamp", -1L);
        umqMessage.secureMessageId = jSONObject.optString("secure_message_id");
        umqMessage.isIncoming = true;
        return umqMessage;
    }

    private static UmqMessageType getUmqMessageTypeFromString(String str) {
        if (str == null) {
            return null;
        }
        if (str.equalsIgnoreCase("typing")) {
            return UmqMessageType.TYPING;
        }
        if (str.equalsIgnoreCase("emote")) {
            return UmqMessageType.EMOTE;
        }
        if (str.equalsIgnoreCase("saytext")) {
            return UmqMessageType.MESSAGE_TEXT;
        }
        if (str.equalsIgnoreCase("personastate")) {
            return UmqMessageType.PERSONA_STATE;
        }
        if (str.equalsIgnoreCase("personarelationship")) {
            return UmqMessageType.PERSONA_RELATIONSHIP;
        }
        if (str.equals("notificationcountupdate")) {
            return UmqMessageType.NOTIFICATION_COUNTS;
        }
        return null;
    }
}

package com.valvesoftware.android.steam.community.jsontranslators;

import com.valvesoftware.android.steam.community.model.MessageSession;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class ActiveMessageSessionsTranslator {
    public static List<MessageSession> translateList(JSONObject jSONObject) {
        MessageSession translateObject;
        ArrayList arrayList = new ArrayList();
        JSONArray optJSONArray = jSONObject.optJSONArray("message_sessions");
        if (optJSONArray == null) {
            return arrayList;
        }
        for (int i = 0; i < optJSONArray.length(); i++) {
            JSONObject optJSONObject = optJSONArray.optJSONObject(i);
            if (optJSONObject != null && (translateObject = translateObject(optJSONObject)) != null) {
                arrayList.add(translateObject);
            }
        }
        return arrayList;
    }

    private static MessageSession translateObject(JSONObject jSONObject) {
        MessageSession messageSession = new MessageSession();
        messageSession.steamId = TranslatorUtilities.steamIdFromAccountId(jSONObject.optString("accountid_friend"));
        messageSession.lastMessage = jSONObject.optString("last_message");
        messageSession.lastView = jSONObject.optString("last_view");
        messageSession.unreadMessageCount = jSONObject.optInt("unread_message_count");
        return messageSession;
    }
}

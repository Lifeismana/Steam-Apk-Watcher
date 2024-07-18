package com.valvesoftware.android.steam.community.jsontranslators;

import com.valvesoftware.android.steam.community.model.UmqMessage;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class UmqMessageHistoryTranslator {
    public static List<UmqMessage> translateList(JSONObject jSONObject, String str) {
        UmqMessage translate;
        ArrayList arrayList = new ArrayList();
        JSONArray optJSONArray = jSONObject.optJSONArray("messages");
        if (optJSONArray == null) {
            return arrayList;
        }
        for (int i = 0; i < optJSONArray.length(); i++) {
            JSONObject optJSONObject = optJSONArray.optJSONObject(i);
            if (optJSONObject != null && (translate = translate(optJSONObject, str)) != null) {
                arrayList.add(translate);
            }
        }
        return arrayList;
    }

    private static UmqMessage translate(JSONObject jSONObject, String str) {
        UmqMessage umqMessage = new UmqMessage();
        if (TranslatorUtilities.steamIdFromAccountId(jSONObject.optString("accountid")).equals(str)) {
            umqMessage.isIncoming = true;
        }
        umqMessage.chatPartnerSteamId = str;
        umqMessage.utcTimeStamp = jSONObject.optLong("timestamp");
        umqMessage.text = jSONObject.optString("message");
        return umqMessage;
    }
}

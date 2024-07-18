package com.valvesoftware.android.steam.community.jsontranslators;

import android.util.Log;
import com.valvesoftware.android.steam.community.model.Persona;
import com.valvesoftware.android.steam.community.model.PersonaRelationship;
import com.valvesoftware.android.steam.community.model.PersonaState;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class PersonaTranslator {
    public static List<Persona> translateList(JSONObject jSONObject) {
        ArrayList arrayList = new ArrayList();
        if (jSONObject == null) {
            Log.e("json_parsing", "Unexpected null JSONObject");
            return arrayList;
        }
        JSONArray names = jSONObject.names();
        JSONArray jSONArray = null;
        if (names.length() > 0) {
            Object opt = names.opt(0);
            if (opt instanceof String) {
                jSONArray = jSONObject.optJSONArray((String) opt);
            }
        }
        if (jSONArray == null) {
            return arrayList;
        }
        for (int i = 0; i < jSONArray.length(); i++) {
            JSONObject optJSONObject = jSONArray.optJSONObject(i);
            if (optJSONObject != null) {
                Persona translateObject = translateObject(optJSONObject);
                if (translateObject != null) {
                    arrayList.add(translateObject);
                }
            } else {
                Log.e(jSONObject.toString(), "expected friends array to contain object at index: " + i);
            }
        }
        return arrayList;
    }

    private static Persona translateObject(JSONObject jSONObject) {
        if (jSONObject == null) {
            Log.e("json_parsing", "attempted to parse null object");
            return null;
        }
        String optString = jSONObject.optString("steamid");
        if (optString == null) {
            Log.e("json_parsing", "no steamid while parsing: " + jSONObject.toString());
            return null;
        }
        Persona persona = new Persona();
        persona.steamId = optString;
        persona.personaName = jSONObject.optString("personaname");
        persona.realName = jSONObject.optString("realname");
        persona.smallAvatarUrl = jSONObject.optString("avatar");
        persona.mediumAvatarUrl = jSONObject.optString("avatarmedium");
        persona.fullAvatarUrl = jSONObject.optString("avatarfull");
        persona.personaState = PersonaState.FromInteger(jSONObject.optInt("personastate"));
        int optInt = jSONObject.optInt("personastateflags");
        persona.isOnWeb = (optInt & 256) == 256;
        persona.isOnMobile = (optInt & 512) == 512;
        persona.isOnTenFoot = (optInt & 1024) == 1024;
        persona.currentGameID = jSONObject.optInt("gameid");
        persona.currentGameString = jSONObject.optString("gameextrainfo");
        persona.lastOnlineTime = jSONObject.optInt("lastlogoff");
        String optString2 = jSONObject.optString("relationship");
        if (optString2 != null) {
            PersonaRelationship enumValue = PersonaRelationship.getEnumValue(optString2);
            if (enumValue == null) {
                enumValue = PersonaRelationship.none;
            }
            persona.relationship = enumValue;
        }
        return persona;
    }
}

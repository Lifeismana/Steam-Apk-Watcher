package com.valvesoftware.android.steam.community.jsontranslators;

import android.util.Log;
import com.valvesoftware.android.steam.community.model.Group;
import com.valvesoftware.android.steam.community.model.GroupRelationship;
import com.valvesoftware.android.steam.community.model.GroupType;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GroupTranslator {
    public static List<Group> translateList(JSONObject jSONObject) {
        ArrayList arrayList = new ArrayList();
        if (jSONObject == null) {
            Log.e("json_parsing", "Unexpected null JSONObject");
            return arrayList;
        }
        JSONArray names = jSONObject.names();
        JSONArray jSONArray = null;
        if (names != null && names.length() > 0) {
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
                Group translateObject = translateObject(optJSONObject);
                if (translateObject != null) {
                    arrayList.add(translateObject);
                }
            } else {
                Log.e(jSONObject.toString(), "expected friends array to contain object at index: " + i);
            }
        }
        return arrayList;
    }

    public static Group translateObject(JSONObject jSONObject) {
        if (jSONObject == null) {
            Log.e("json_parsing", "attempted to parse null object");
            return null;
        }
        String optString = jSONObject.optString("steamid");
        if (optString == null) {
            Log.e("json_parsing", "no steamid while parsing: " + jSONObject.toString());
            return null;
        }
        Group group = new Group();
        group.steamId = optString;
        group.name = jSONObject.optString("name");
        group.numUsersTotal = jSONObject.optInt("users");
        group.numUsersOnline = jSONObject.optInt("usersonline");
        group.type = GroupType.FromInteger(jSONObject.optInt("type"));
        group.smallAvatarUrl = jSONObject.optString("avatar");
        group.mediumAvatarUrl = jSONObject.optString("avatarmedium");
        group.fullAvatarUrl = jSONObject.optString("avatarfull");
        group.favoriteAppId = jSONObject.optInt("favoriteappid");
        group.profileUrl = jSONObject.optString("profileurl");
        try {
            group.relationship = GroupRelationship.valueOf(jSONObject.optString("relationship", "None"));
        } catch (Exception unused) {
            group.relationship = GroupRelationship.None;
        }
        if (group.hasProfileUrl()) {
            if (group.type == GroupType.OFFICIAL) {
                group.profileUrl = "/games/" + group.profileUrl;
            } else {
                group.profileUrl = "/groups/" + group.profileUrl;
            }
        }
        return group;
    }
}

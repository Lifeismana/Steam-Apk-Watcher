package com.valvesoftware.android.steam.community.model;

import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public enum PersonaRelationship {
    none,
    myself,
    friend,
    blocked,
    requestrecipient,
    requestinitiator,
    ignored,
    ignoredfriend,
    suggested;

    static Map<String, PersonaRelationship> stringFriendRelationshipMap = new HashMap();

    static {
        stringFriendRelationshipMap.put("none", none);
        stringFriendRelationshipMap.put("myself", myself);
        stringFriendRelationshipMap.put("friend", friend);
        stringFriendRelationshipMap.put("blocked", blocked);
        stringFriendRelationshipMap.put("requestrecipient", requestrecipient);
        stringFriendRelationshipMap.put("requestinitiator", requestinitiator);
        stringFriendRelationshipMap.put("ignored", ignored);
        stringFriendRelationshipMap.put("ignoredfriend", ignoredfriend);
        stringFriendRelationshipMap.put("suggested", suggested);
    }

    public static PersonaRelationship getEnumValue(String str) {
        return stringFriendRelationshipMap.get(str);
    }
}

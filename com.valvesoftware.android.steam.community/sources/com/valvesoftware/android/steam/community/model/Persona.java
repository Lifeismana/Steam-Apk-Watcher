package com.valvesoftware.android.steam.community.model;

import android.text.TextUtils;

/* loaded from: classes.dex */
public class Persona {
    public int currentGameID;
    public String fullAvatarUrl;
    public boolean isOnMobile;
    public boolean isOnTenFoot;
    public boolean isOnWeb;
    private long lastMessageTime;
    public String mediumAvatarUrl;
    private int numUnreadMessagesSent;
    public String personaName;
    public String realName;
    public String smallAvatarUrl;
    public String steamId;
    public PersonaState personaState = PersonaState.OFFLINE;
    public PersonaRelationship relationship = PersonaRelationship.none;
    public String currentGameString = "";
    public long lastOnlineTime = 0;
    private long twoDaysInSeconds = 172800;
    private PersonaStateCategoryInList categoryInList = PersonaStateCategoryInList.OFFLINE;

    public boolean hasSentUnreadMessage() {
        return this.numUnreadMessagesSent > 0;
    }

    public void overwriteOrMergeWith(Persona persona) {
        this.personaName = TextUtils.isEmpty(persona.personaName) ? this.personaName : persona.personaName;
        PersonaRelationship personaRelationship = persona.relationship;
        this.relationship = (personaRelationship == null || personaRelationship == PersonaRelationship.none) ? this.relationship : persona.relationship;
        PersonaStateCategoryInList personaStateCategoryInList = persona.categoryInList;
        if (personaStateCategoryInList == null) {
            personaStateCategoryInList = this.categoryInList;
        }
        this.categoryInList = personaStateCategoryInList;
        this.realName = TextUtils.isEmpty(persona.realName) ? persona.realName : this.realName;
        int i = persona.currentGameID;
        if (i == 0) {
            i = this.currentGameID;
        }
        this.currentGameID = i;
        this.currentGameString = persona.currentGameString;
        this.smallAvatarUrl = TextUtils.isEmpty(persona.smallAvatarUrl) ? this.smallAvatarUrl : persona.smallAvatarUrl;
        this.mediumAvatarUrl = TextUtils.isEmpty(persona.mediumAvatarUrl) ? this.mediumAvatarUrl : persona.mediumAvatarUrl;
        this.fullAvatarUrl = TextUtils.isEmpty(persona.fullAvatarUrl) ? this.fullAvatarUrl : persona.fullAvatarUrl;
        this.lastOnlineTime = Math.max(this.lastOnlineTime, persona.lastOnlineTime);
        PersonaState personaState = persona.personaState;
        if (personaState == null) {
            personaState = this.personaState;
        }
        this.personaState = personaState;
        this.numUnreadMessagesSent = Math.max(this.numUnreadMessagesSent, persona.numUnreadMessagesSent);
        this.isOnMobile = persona.isOnMobile;
        this.isOnTenFoot = persona.isOnTenFoot;
        this.isOnWeb = persona.isOnWeb;
        this.lastMessageTime = Math.max(persona.lastMessageTime, this.lastMessageTime);
        determineDisplayCategory();
    }

    public boolean isPlaying() {
        String str = this.currentGameString;
        return str != null && str.length() > 0;
    }

    public PersonaStateCategoryInList getDisplayCategory() {
        return this.categoryInList;
    }

    public boolean isOnline() {
        return this.personaState != PersonaState.OFFLINE;
    }

    public boolean isFriend() {
        return PersonaRelationship.friend == this.relationship;
    }

    public String toString() {
        String str = this.personaName;
        return str != null ? str : "";
    }

    public void setDisplayCategoryForSearch() {
        this.categoryInList = PersonaStateCategoryInList.SEARCH_ALL;
    }

    public long getLastMessageTime() {
        return this.lastMessageTime;
    }

    public void setLastMessageTime(long j) {
        this.lastMessageTime = j;
    }

    public boolean hasRecentlySentMessage() {
        return (System.currentTimeMillis() / 1000) - this.lastMessageTime <= this.twoDaysInSeconds;
    }

    public void incrementUnreadMessageCount() {
        this.numUnreadMessagesSent++;
    }

    public int getUnreadMessageCount() {
        return this.numUnreadMessagesSent;
    }

    public void clearUnreadMessageCount() {
        this.numUnreadMessagesSent = 0;
    }

    public void determineDisplayCategory() {
        if (this.relationship == PersonaRelationship.requestrecipient) {
            this.categoryInList = PersonaStateCategoryInList.REQUEST_INCOMING;
            return;
        }
        if (hasSentUnreadMessage() || hasRecentlySentMessage()) {
            this.categoryInList = PersonaStateCategoryInList.CHATS;
            return;
        }
        if (this.currentGameString.length() > 0) {
            this.categoryInList = PersonaStateCategoryInList.INGAME;
        } else if (this.personaState == PersonaState.OFFLINE) {
            this.categoryInList = PersonaStateCategoryInList.OFFLINE;
        } else {
            this.categoryInList = PersonaStateCategoryInList.ONLINE;
        }
    }
}

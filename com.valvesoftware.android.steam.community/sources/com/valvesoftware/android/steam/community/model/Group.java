package com.valvesoftware.android.steam.community.model;

import android.graphics.Bitmap;

/* loaded from: classes.dex */
public class Group {
    public Bitmap avatar;
    public String fullAvatarUrl;
    public String mediumAvatarUrl;
    public String name;
    public int numUsersOnline;
    public int numUsersTotal;
    public String profileUrl;
    public String smallAvatarUrl;
    public String steamId;
    public GroupType type = GroupType.PRIVATE;
    public GroupCategoryInList categoryInList = GroupCategoryInList.PUBLIC;
    public GroupRelationship relationship = GroupRelationship.None;
    public int favoriteAppId = 0;

    public boolean hasProfileUrl() {
        String str = this.profileUrl;
        return str != null && str.length() > 0;
    }

    public void merge(Group group) {
        this.numUsersOnline = group.numUsersOnline;
        this.numUsersTotal = group.numUsersTotal;
        this.profileUrl = group.profileUrl;
        this.avatar = group.avatar;
        this.name = group.name;
        this.categoryInList = group.categoryInList;
        this.smallAvatarUrl = group.smallAvatarUrl;
        this.mediumAvatarUrl = group.mediumAvatarUrl;
        this.relationship = group.relationship == GroupRelationship.None ? this.relationship : group.relationship;
        this.favoriteAppId = group.favoriteAppId;
        this.type = group.type;
        determineDisplayCategory();
    }

    private void determineDisplayCategory() {
        if (this.relationship == GroupRelationship.Invited) {
            this.categoryInList = GroupCategoryInList.REQUEST_INVITE;
            return;
        }
        if (this.type == GroupType.OFFICIAL) {
            this.categoryInList = GroupCategoryInList.OFFICIAL;
        } else if (this.type == GroupType.PRIVATE) {
            this.categoryInList = GroupCategoryInList.PRIVATE;
        } else {
            this.categoryInList = GroupCategoryInList.PUBLIC;
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Group group = (Group) obj;
        String str = this.steamId;
        if (str != null) {
            if (str.equals(group.steamId)) {
                return true;
            }
        } else if (group.steamId == null) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        String str = this.steamId;
        if (str != null) {
            return str.hashCode();
        }
        return 0;
    }

    public String toString() {
        String str = this.name;
        return str != null ? str : "";
    }
}

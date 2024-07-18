package com.valvesoftware.android.steam.community.model;

import java.util.ArrayList;
import java.util.Collection;

/* loaded from: classes.dex */
public class SearchResults {
    private final ArrayList<String> ids = new ArrayList<>();
    public int total;

    public void addSteamId(String str) {
        this.ids.add(str);
    }

    public Collection<String> getResultIds() {
        return (ArrayList) this.ids.clone();
    }

    public int getCurrentCount() {
        return this.ids.size();
    }
}

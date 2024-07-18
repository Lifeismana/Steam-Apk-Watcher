package com.valvesoftware.android.steam.community.jsontranslators;

import com.valvesoftware.android.steam.community.model.SearchResults;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class SearchResultsTranslator {
    public static SearchResults translate(JSONObject jSONObject) {
        String optString;
        SearchResults searchResults = new SearchResults();
        searchResults.total = jSONObject.optInt("total");
        JSONArray optJSONArray = jSONObject.optJSONArray("results");
        if (optJSONArray == null) {
            return searchResults;
        }
        for (int i = 0; i < optJSONArray.length(); i++) {
            JSONObject optJSONObject = optJSONArray.optJSONObject(i);
            if (optJSONObject != null && (optString = optJSONObject.optString("steamid", null)) != null) {
                searchResults.addSteamId(optString);
            }
        }
        return searchResults;
    }
}

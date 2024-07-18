package com.valvesoftware.android.steam.community.jsontranslators;

import com.valvesoftware.android.steam.community.model.UrlCategory;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class UrlCategoryTranslator {
    public static List<UrlCategory> translate(JSONObject jSONObject) {
        JSONArray optJSONArray;
        ArrayList arrayList = new ArrayList();
        if (jSONObject == null || (optJSONArray = jSONObject.optJSONArray("categories")) == null) {
            return arrayList;
        }
        for (int i = 0; i < optJSONArray.length(); i++) {
            JSONObject optJSONObject = optJSONArray.optJSONObject(i);
            if (optJSONObject != null) {
                UrlCategory urlCategory = new UrlCategory();
                urlCategory.title = optJSONObject.optString("label", "");
                urlCategory.url = optJSONObject.optString("url", "");
                arrayList.add(urlCategory);
            }
        }
        return arrayList;
    }
}

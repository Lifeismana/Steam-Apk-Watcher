package it.innove;

import android.os.Bundle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes3.dex */
public class BundleJSONConverter {
    private static final Map<Class<?>, Setter> SETTERS;

    public interface Setter {
        void setOnBundle(Bundle bundle, String str, Object obj) throws JSONException;

        void setOnJSON(JSONObject jSONObject, String str, Object obj) throws JSONException;
    }

    static {
        HashMap map = new HashMap();
        SETTERS = map;
        map.put(Boolean.class, new Setter() { // from class: it.innove.BundleJSONConverter.1
            @Override // it.innove.BundleJSONConverter.Setter
            public void setOnBundle(Bundle bundle, String str, Object obj) throws JSONException {
                bundle.putBoolean(str, ((Boolean) obj).booleanValue());
            }

            @Override // it.innove.BundleJSONConverter.Setter
            public void setOnJSON(JSONObject jSONObject, String str, Object obj) throws JSONException {
                jSONObject.put(str, obj);
            }
        });
        map.put(Integer.class, new Setter() { // from class: it.innove.BundleJSONConverter.2
            @Override // it.innove.BundleJSONConverter.Setter
            public void setOnBundle(Bundle bundle, String str, Object obj) throws JSONException {
                bundle.putInt(str, ((Integer) obj).intValue());
            }

            @Override // it.innove.BundleJSONConverter.Setter
            public void setOnJSON(JSONObject jSONObject, String str, Object obj) throws JSONException {
                jSONObject.put(str, obj);
            }
        });
        map.put(Long.class, new Setter() { // from class: it.innove.BundleJSONConverter.3
            @Override // it.innove.BundleJSONConverter.Setter
            public void setOnBundle(Bundle bundle, String str, Object obj) throws JSONException {
                bundle.putLong(str, ((Long) obj).longValue());
            }

            @Override // it.innove.BundleJSONConverter.Setter
            public void setOnJSON(JSONObject jSONObject, String str, Object obj) throws JSONException {
                jSONObject.put(str, obj);
            }
        });
        map.put(Double.class, new Setter() { // from class: it.innove.BundleJSONConverter.4
            @Override // it.innove.BundleJSONConverter.Setter
            public void setOnBundle(Bundle bundle, String str, Object obj) throws JSONException {
                bundle.putDouble(str, ((Double) obj).doubleValue());
            }

            @Override // it.innove.BundleJSONConverter.Setter
            public void setOnJSON(JSONObject jSONObject, String str, Object obj) throws JSONException {
                jSONObject.put(str, obj);
            }
        });
        map.put(String.class, new Setter() { // from class: it.innove.BundleJSONConverter.5
            @Override // it.innove.BundleJSONConverter.Setter
            public void setOnBundle(Bundle bundle, String str, Object obj) throws JSONException {
                bundle.putString(str, (String) obj);
            }

            @Override // it.innove.BundleJSONConverter.Setter
            public void setOnJSON(JSONObject jSONObject, String str, Object obj) throws JSONException {
                jSONObject.put(str, obj);
            }
        });
        map.put(String[].class, new Setter() { // from class: it.innove.BundleJSONConverter.6
            @Override // it.innove.BundleJSONConverter.Setter
            public void setOnBundle(Bundle bundle, String str, Object obj) throws JSONException {
                throw new IllegalArgumentException("Unexpected type from JSON");
            }

            @Override // it.innove.BundleJSONConverter.Setter
            public void setOnJSON(JSONObject jSONObject, String str, Object obj) throws JSONException {
                JSONArray jSONArray = new JSONArray();
                for (String str2 : (String[]) obj) {
                    jSONArray.put(str2);
                }
                jSONObject.put(str, jSONArray);
            }
        });
        map.put(JSONArray.class, new Setter() { // from class: it.innove.BundleJSONConverter.7
            @Override // it.innove.BundleJSONConverter.Setter
            public void setOnBundle(Bundle bundle, String str, Object obj) throws JSONException {
                JSONArray jSONArray = (JSONArray) obj;
                ArrayList<String> arrayList = new ArrayList<>();
                if (jSONArray.length() == 0) {
                    bundle.putStringArrayList(str, arrayList);
                    return;
                }
                for (int i = 0; i < jSONArray.length(); i++) {
                    Object obj2 = jSONArray.get(i);
                    if (obj2 instanceof String) {
                        arrayList.add((String) obj2);
                    } else {
                        throw new IllegalArgumentException("Unexpected type in an array: " + obj2.getClass());
                    }
                }
                bundle.putStringArrayList(str, arrayList);
            }

            @Override // it.innove.BundleJSONConverter.Setter
            public void setOnJSON(JSONObject jSONObject, String str, Object obj) throws JSONException {
                throw new IllegalArgumentException("JSONArray's are not supported in bundles.");
            }
        });
    }

    public static JSONObject convertToJSON(Bundle bundle) throws JSONException {
        JSONObject jSONObject = new JSONObject();
        for (String str : bundle.keySet()) {
            Object obj = bundle.get(str);
            if (obj != null) {
                if (obj instanceof List) {
                    JSONArray jSONArray = new JSONArray();
                    Iterator it2 = ((List) obj).iterator();
                    while (it2.hasNext()) {
                        jSONArray.put((String) it2.next());
                    }
                    jSONObject.put(str, jSONArray);
                } else if (obj instanceof Bundle) {
                    jSONObject.put(str, convertToJSON((Bundle) obj));
                } else {
                    Setter setter = SETTERS.get(obj.getClass());
                    if (setter == null) {
                        throw new IllegalArgumentException("Unsupported type: " + obj.getClass());
                    }
                    setter.setOnJSON(jSONObject, str, obj);
                }
            }
        }
        return jSONObject;
    }

    public static Bundle convertToBundle(JSONObject jSONObject) throws JSONException {
        Bundle bundle = new Bundle();
        Iterator<String> itKeys = jSONObject.keys();
        while (itKeys.hasNext()) {
            String next = itKeys.next();
            Object obj = jSONObject.get(next);
            if (obj != null && obj != JSONObject.NULL) {
                if (obj instanceof JSONObject) {
                    bundle.putBundle(next, convertToBundle((JSONObject) obj));
                } else {
                    Setter setter = SETTERS.get(obj.getClass());
                    if (setter == null) {
                        throw new IllegalArgumentException("Unsupported type: " + obj.getClass());
                    }
                    setter.setOnBundle(bundle, next, obj);
                }
            }
        }
        return bundle;
    }
}

package com.valvesoftware.android.steam.community;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.network.ForwardingCookieHandler;
import com.google.android.gms.common.GoogleApiAvailabilityLight;
import com.google.android.material.timepicker.TimeModel;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes3.dex */
public class ValveHelpersModule extends ReactContextBaseJavaModule {
    private static final String s_strSteamGuardStateFilenameBase = "Steamguard-";
    private ForwardingCookieHandler m_CookieHandler;

    ValveHelpersModule(ReactApplicationContext reactApplicationContext) {
        super(reactApplicationContext);
        this.m_CookieHandler = new ForwardingCookieHandler(reactApplicationContext);
    }

    @Override // com.facebook.react.bridge.NativeModule
    public String getName() {
        return "ValveHelpersModule";
    }

    @ReactMethod
    public void SetCookie(String str, String str2, String str3) throws IOException, URISyntaxException {
        URI uri = new URI(str2);
        HashMap hashMap = new HashMap();
        hashMap.put("Set-cookie", Collections.singletonList(str + "=" + str3 + "; path=/"));
        this.m_CookieHandler.put(uri, hashMap);
    }

    @ReactMethod
    public void ClearCookie(String str, String str2) throws IOException, URISyntaxException {
        SetCookie(str, str2, "deleted");
    }

    @ReactMethod
    public void ComputeHMAC(String str, String str2, Promise promise) {
        ComputeGenericHMAC(str, str2, promise, "HmacSHA1");
    }

    @ReactMethod
    public void ComputeSHA256HMAC(String str, String str2, Promise promise) {
        ComputeGenericHMAC(str, str2, promise, "HmacSHA256");
    }

    private void ComputeGenericHMAC(String str, String str2, Promise promise, String str3) {
        try {
            byte[] decode = Base64.decode(str.getBytes("UTF-8"), 2);
            SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.decode(str2.getBytes("UTF-8"), 2), str3);
            try {
                Mac mac = Mac.getInstance(str3);
                mac.init(secretKeySpec);
                promise.resolve(Base64.encodeToString(mac.doFinal(decode), 2));
            } catch (InvalidKeyException e) {
                promise.reject("HMAC Failed. Invalid Key.", e);
            } catch (NoSuchAlgorithmException e2) {
                promise.reject("HMAC Failed. No Such Algorithm.", e2);
            }
        } catch (UnsupportedEncodingException e3) {
            promise.reject("HMAC Failed. Unsupported input encoding.", e3);
        } catch (IllegalArgumentException e4) {
            promise.reject("HMAC Failed. Illegal decode argument.", e4);
        }
    }

    @ReactMethod
    public void ReadOldSessionInfo(Promise promise) {
        try {
            File file = new File(getReactApplicationContext().getDir("cache_i", 0), "login.json");
            JSONObject LoadJSONFromFile = LoadJSONFromFile(file.getAbsolutePath());
            if (LoadJSONFromFile == null) {
                promise.resolve(null);
                return;
            }
            JSONObject jSONObject = new JSONObject("{}");
            jSONObject.put("steamID", LoadJSONFromFile.optString("x_steamid"));
            jSONObject.put("token", LoadJSONFromFile.optString("wgtoken_secure"));
            promise.resolve(JSONObjectToMap(jSONObject));
            file.delete();
        } catch (JSONException e) {
            promise.reject("Failed to read session info.", e);
        }
    }

    @ReactMethod
    public void IsGooglePlayServicesAvailable(Promise promise) {
        try {
            if (GoogleApiAvailabilityLight.getInstance().isGooglePlayServicesAvailable(getCurrentActivity()) == 0) {
                promise.resolve(true);
            }
        } catch (NullPointerException unused) {
        }
        promise.resolve(false);
    }

    @ReactMethod
    public void OpenLinkInExternalBrowser(String str, Promise promise) {
        boolean z;
        try {
            ResolveInfo resolveActivity = getReactApplicationContext().getPackageManager().resolveActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://")), 65536);
            String str2 = (resolveActivity == null || resolveActivity.activityInfo == null || resolveActivity.activityInfo.packageName.isEmpty()) ? "com.android.chrome" : resolveActivity.activityInfo.packageName;
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(str));
            intent.setPackage(str2);
            intent.addFlags(268435456);
            getReactApplicationContext().startActivity(intent);
            z = true;
        } catch (ActivityNotFoundException unused) {
            z = false;
        }
        promise.resolve(Boolean.valueOf(z));
    }

    @ReactMethod
    public void ReadOldSteamGuardStates(Promise promise) {
        String optString;
        WritableArray createArray = Arguments.createArray();
        Iterator<String> it2 = GetOldSteamGuardStateFilenames().iterator();
        while (it2.hasNext()) {
            JSONObject LoadJSONFromFile = LoadJSONFromFile(it2.next());
            if (LoadJSONFromFile != null && (optString = LoadJSONFromFile.optString("steamid")) != null) {
                try {
                    if (!optString.isEmpty()) {
                        LoadJSONFromFile.put("steamid", optString);
                        createArray.pushMap(JSONObjectToMap(LoadJSONFromFile));
                    }
                } catch (JSONException unused) {
                }
            }
        }
        promise.resolve(createArray);
    }

    private ArrayList<String> GetOldSteamGuardStateFilenames() {
        ArrayList<String> arrayList = new ArrayList<>();
        for (File file : getReactApplicationContext().getFilesDir().listFiles()) {
            if (file.getName() != null && file.getName().startsWith(s_strSteamGuardStateFilenameBase)) {
                arrayList.add(file.getName());
            }
        }
        return arrayList;
    }

    private JSONObject LoadJSONFromFile(String str) {
        FileInputStream fileInputStream;
        FileInputStream fileInputStream2;
        ReactApplicationContext reactApplicationContext = getReactApplicationContext();
        try {
            File file = new File(str);
            if (file.isAbsolute()) {
                fileInputStream2 = new FileInputStream(file);
            } else {
                fileInputStream2 = reactApplicationContext.openFileInput(str);
            }
            try {
                byte[] bArr = new byte[fileInputStream2.available()];
                if (fileInputStream2 != null) {
                    try {
                        fileInputStream2.read(bArr);
                        fileInputStream2.close();
                    } catch (IOException unused) {
                    }
                }
                try {
                    return new JSONObject(new String(bArr));
                } catch (JSONException unused2) {
                    return null;
                }
            } catch (IOException unused3) {
                if (fileInputStream2 != null) {
                    try {
                        fileInputStream2.read(null);
                        fileInputStream2.close();
                    } catch (IOException unused4) {
                    }
                }
                return null;
            } catch (Throwable th) {
                fileInputStream = fileInputStream2;
                th = th;
                if (fileInputStream != null) {
                    try {
                        fileInputStream.read(null);
                        fileInputStream.close();
                    } catch (IOException unused5) {
                    }
                }
                throw th;
            }
        } catch (IOException unused6) {
            fileInputStream2 = null;
        } catch (Throwable th2) {
            th = th2;
            fileInputStream = null;
        }
    }

    @ReactMethod
    public void EraseOldSteamGuardStates(Promise promise) {
        ReactApplicationContext reactApplicationContext = getReactApplicationContext();
        Iterator<String> it2 = GetOldSteamGuardStateFilenames().iterator();
        boolean z = false;
        while (it2.hasNext()) {
            if (!reactApplicationContext.deleteFile(it2.next())) {
                z = true;
            }
        }
        if (z) {
            promise.reject("Failure", "Failed to delete at least one old state file");
        } else {
            promise.resolve(true);
        }
    }

    private static WritableMap JSONObjectToMap(JSONObject jSONObject) throws JSONException {
        WritableMap createMap = Arguments.createMap();
        Iterator<String> keys = jSONObject.keys();
        while (keys.hasNext()) {
            String next = keys.next();
            Object obj = jSONObject.get(next);
            if (obj instanceof JSONObject) {
                createMap.putMap(next, JSONObjectToMap((JSONObject) obj));
            } else if (obj instanceof JSONArray) {
                createMap.putArray(next, JSONArrayToArray((JSONArray) obj));
            } else if (obj instanceof String) {
                createMap.putString(next, (String) obj);
            } else if (obj instanceof Boolean) {
                createMap.putBoolean(next, ((Boolean) obj).booleanValue());
            } else if (obj instanceof Integer) {
                createMap.putInt(next, ((Integer) obj).intValue());
            } else if (obj instanceof Double) {
                createMap.putDouble(next, ((Double) obj).doubleValue());
            } else {
                createMap.putString(next, obj.toString());
            }
        }
        return createMap;
    }

    private static WritableArray JSONArrayToArray(JSONArray jSONArray) throws JSONException {
        WritableArray createArray = Arguments.createArray();
        for (int i = 0; i < jSONArray.length(); i++) {
            Object obj = jSONArray.get(i);
            if (obj instanceof JSONObject) {
                createArray.pushMap(JSONObjectToMap((JSONObject) obj));
            } else if (obj instanceof JSONArray) {
                createArray.pushArray(JSONArrayToArray((JSONArray) obj));
            } else if (obj instanceof String) {
                createArray.pushString((String) obj);
            } else if (obj instanceof Boolean) {
                createArray.pushBoolean(((Boolean) obj).booleanValue());
            } else if (obj instanceof Integer) {
                createArray.pushInt(((Integer) obj).intValue());
            } else if (obj instanceof Double) {
                createArray.pushDouble(((Double) obj).doubleValue());
            } else {
                createArray.pushString(obj.toString());
            }
        }
        return createArray;
    }

    @ReactMethod
    public void GetUniqueDeviceIdentifier(Promise promise) {
        String str;
        SharedPreferences sharedPreferences = getReactApplicationContext().getSharedPreferences("steam.uuid", 0);
        String string = sharedPreferences.getString("uuidKey", "");
        if (string.length() > 0) {
            promise.resolve(string);
            return;
        }
        try {
            str = UUID.randomUUID().toString();
        } catch (Exception e) {
            Log.e("RandomUUID", e.toString());
            str = null;
        }
        if (str == null) {
            str = String.format(Locale.US, TimeModel.NUMBER_FORMAT, Long.valueOf(new GregorianCalendar().getTimeInMillis()));
        }
        String format = String.format(Locale.US, "android:%s", str);
        sharedPreferences.edit().putString("uuidKey", format).commit();
        promise.resolve(format);
    }
}

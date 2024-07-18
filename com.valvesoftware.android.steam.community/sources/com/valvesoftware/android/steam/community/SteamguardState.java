package com.valvesoftware.android.steam.community;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;
import com.valvesoftware.android.steam.community.webrequests.Endpoints;
import com.valvesoftware.android.steam.community.webrequests.RequestBuilder;
import com.valvesoftware.android.steam.community.webrequests.RequestErrorInfo;
import com.valvesoftware.android.steam.community.webrequests.ResponseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class SteamguardState {
    private static Context sContext;
    private static ArrayList<SteamguardState> sSteamGuardStates = new ArrayList<>();
    private static boolean sbLoadedSteamguardStates = false;
    private boolean mCommitted;
    private JSONObject mInfo;
    private JSONObject mTwoFactorStatus;
    private TwoFactorToken mTwoFactorToken;

    /* loaded from: classes.dex */
    public static abstract class Completion {
        public abstract void failure(int i, String str);

        public abstract void success();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public enum ETwoFactorEmailType {
        k_ETwoFactorEmailType_None,
        k_ETwoFactorEmailType_Signup,
        k_ETwoFactorEmailType_Added,
        k_ETwoFactorEmailType_Removed
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public enum ETwoFactorTokenType {
        k_ETwoFactorTokenType_None,
        k_ETwoFactorTokenType_ValveMobileApp,
        k_ETwoFactorTokenType_ThirdParty
    }

    /* loaded from: classes.dex */
    public enum Scheme {
        None,
        Email,
        TwoFactor
    }

    public static void initializeSteamguardState(Context context) {
        sContext = context;
        getSteamGuardStates();
    }

    public static ArrayList<SteamguardState> getSteamGuardStates() {
        if (!sbLoadedSteamguardStates) {
            sbLoadedSteamguardStates = true;
            sSteamGuardStates = loadExistingSteamguardStates();
        }
        return sSteamGuardStates;
    }

    public static boolean hasLiveSteamguardStates() {
        Iterator<SteamguardState> it = getSteamGuardStates().iterator();
        while (it.hasNext()) {
            if (it.next().getTwoFactorToken() != null) {
                return true;
            }
        }
        return false;
    }

    public static ArrayList<SteamguardState> getTwoFactorSteamGuardStates() {
        ArrayList<SteamguardState> arrayList = new ArrayList<>();
        Iterator<SteamguardState> it = getSteamGuardStates().iterator();
        while (it.hasNext()) {
            SteamguardState next = it.next();
            if (next.getTwoFactorToken() != null) {
                arrayList.add(next);
            }
        }
        return arrayList;
    }

    public static ArrayList<SteamguardState> getSortedTwoFactorSteamGuardStates() {
        ArrayList<SteamguardState> twoFactorSteamGuardStates = getTwoFactorSteamGuardStates();
        Collections.sort(twoFactorSteamGuardStates, new Comparator<SteamguardState>() { // from class: com.valvesoftware.android.steam.community.SteamguardState.1
            @Override // java.util.Comparator
            public int compare(SteamguardState steamguardState, SteamguardState steamguardState2) {
                return steamguardState.getAccountName().compareToIgnoreCase(steamguardState2.getAccountName());
            }
        });
        return twoFactorSteamGuardStates;
    }

    public static SteamguardState steamguardStateForLoggedInUser() {
        return steamguardStateForSteamID(LoggedInUserAccountInfo.getLoginSteamID());
    }

    public static SteamguardState steamguardStateForSteamID(String str) {
        if (str == null) {
            return null;
        }
        Iterator<SteamguardState> it = sSteamGuardStates.iterator();
        while (it.hasNext()) {
            SteamguardState next = it.next();
            if (next.getSteamId().equals(str)) {
                return next;
            }
        }
        SteamguardState steamguardState = new SteamguardState(str);
        sSteamGuardStates.add(steamguardState);
        return steamguardState;
    }

    public static SteamguardState steamguardStateForGID(String str) {
        if (str == null) {
            return null;
        }
        Iterator<SteamguardState> it = sSteamGuardStates.iterator();
        while (it.hasNext()) {
            SteamguardState next = it.next();
            if (next.getTokenGID().equals(str)) {
                return next;
            }
        }
        return null;
    }

    public static Scheme stringToScheme(String str) {
        if (str == null) {
            return null;
        }
        for (Scheme scheme : Scheme.values()) {
            if (str.equalsIgnoreCase(scheme.toString())) {
                return scheme;
            }
        }
        return null;
    }

    static Scheme schemeNumberStringToScheme(String str) {
        if (str == null) {
            return null;
        }
        int parseInt = Integer.parseInt(str);
        for (Scheme scheme : Scheme.values()) {
            if (scheme.ordinal() == parseInt) {
                return scheme;
            }
        }
        return null;
    }

    public static int installSecret(String str) {
        String str2;
        JSONObject jSONObject;
        String optString;
        SteamguardState steamguardStateForSteamID;
        try {
            str2 = new String(Base64.decode(str.getBytes(), 0), "UTF-8");
        } catch (Exception unused) {
            str2 = null;
        }
        if (str2 != null) {
            try {
                jSONObject = new JSONObject(str2);
            } catch (Exception unused2) {
                jSONObject = null;
            }
            if (jSONObject != null && (optString = jSONObject.optString("steamid")) != null && (steamguardStateForSteamID = steamguardStateForSteamID(optString)) != null) {
                steamguardStateForSteamID.updateFromJSON(jSONObject);
                return 0;
            }
        }
        return -1;
    }

    public static void broadcastSteamguardStateAdded(String str) {
        Intent intent = new Intent();
        intent.setAction("TWOFACTORCODES_CHANGED");
        intent.putExtra("com.valve.community.added", str);
        sContext.sendBroadcast(intent);
    }

    public static void broadcastSteamguardStateRemoved(String str) {
        Intent intent = new Intent();
        intent.setAction("TWOFACTORCODES_CHANGED");
        intent.putExtra("com.valve.community.removed", str);
        sContext.sendBroadcast(intent);
    }

    public SteamguardState(String str) {
        this.mInfo = new JSONObject();
        try {
            this.mInfo.put("steamid", str);
        } catch (JSONException unused) {
        }
        this.mCommitted = true;
    }

    public SteamguardState(JSONObject jSONObject) {
        this.mInfo = jSONObject;
        this.mCommitted = true;
    }

    public String getSteamId() {
        return this.mInfo.optString("steamid");
    }

    public String getAccountName() {
        return this.mInfo.optString("account_name");
    }

    public TwoFactorToken getTwoFactorToken() {
        if (getScheme() != Scheme.TwoFactor) {
            return null;
        }
        if (this.mTwoFactorToken == null) {
            this.mTwoFactorToken = new TwoFactorToken(this.mInfo);
        }
        return this.mTwoFactorToken;
    }

    public String getTokenGID() {
        return this.mInfo.optString("token_gid");
    }

    public String getRevocationCode() {
        return this.mInfo.optString("revocation_code");
    }

    public Scheme getScheme() {
        return schemeNumberStringToScheme(this.mInfo.optString("steamguard_scheme", "0"));
    }

    public boolean saveToFile() {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = sContext.openFileOutput(stateFileForSteamId(getSteamId()), 0);
            fileOutputStream.write(this.mInfo.toString().getBytes());
            if (fileOutputStream == null) {
                return true;
            }
            try {
                fileOutputStream.close();
                return true;
            } catch (IOException unused) {
                return true;
            }
        } catch (FileNotFoundException unused2) {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException unused3) {
                }
            }
            return false;
        } catch (IOException unused4) {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException unused5) {
                }
            }
            return false;
        } catch (Throwable th) {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException unused6) {
                }
            }
            throw th;
        }
    }

    private static SteamguardState loadFromFile(Context context, String str) {
        FileInputStream fileInputStream;
        JSONObject jSONObject;
        try {
            fileInputStream = context.openFileInput(str);
        } catch (IOException unused) {
            fileInputStream = null;
        } catch (Throwable th) {
            th = th;
            fileInputStream = null;
        }
        try {
            byte[] bArr = new byte[fileInputStream.available()];
            fileInputStream.read(bArr, 0, fileInputStream.available());
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException unused2) {
                }
            }
            try {
                jSONObject = new JSONObject(new String(bArr));
            } catch (JSONException unused3) {
                jSONObject = null;
            }
            if (jSONObject != null) {
                return new SteamguardState(jSONObject);
            }
            return null;
        } catch (IOException unused4) {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException unused5) {
                }
            }
            return null;
        } catch (Throwable th2) {
            th = th2;
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException unused6) {
                }
            }
            throw th;
        }
    }

    private static ArrayList<SteamguardState> loadExistingSteamguardStates() {
        SteamguardState loadFromFile;
        ArrayList<SteamguardState> arrayList = new ArrayList<>();
        for (File file : sContext.getFilesDir().listFiles()) {
            if (file.getName() != null && file.getName().startsWith("Steamguard-") && (loadFromFile = loadFromFile(sContext, file.getName())) != null) {
                arrayList.add(loadFromFile);
            }
        }
        return arrayList;
    }

    public void updateFromJSON(JSONObject jSONObject) {
        this.mInfo = jSONObject;
        this.mCommitted = true;
        this.mTwoFactorToken = null;
        saveToFile();
        if (getTokenGID() != null) {
            broadcastSteamguardStateAdded(getTokenGID());
        }
    }

    private static String stateFileForSteamId(String str) {
        return String.format(Locale.US, "%s%s", "Steamguard-", str);
    }

    public void sanitize() {
        JSONObject jSONObject = new JSONObject();
        for (String str : new String[]{"steamguard_scheme", "steamid", "account_name"}) {
            if (this.mInfo.has(str)) {
                try {
                    jSONObject.put(str, this.mInfo.optString(str));
                } catch (JSONException unused) {
                }
            }
        }
        this.mInfo = jSONObject;
    }

    public void startGetTwoFactorStatus() {
        RequestBuilder twoFactorQueryStatusRequestBuilder = Endpoints.getTwoFactorQueryStatusRequestBuilder(LoggedInUserAccountInfo.getLoginSteamID());
        twoFactorQueryStatusRequestBuilder.setResponseListener(new ResponseListener() { // from class: com.valvesoftware.android.steam.community.SteamguardState.2
            @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
            public void onError(RequestErrorInfo requestErrorInfo) {
            }

            @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
            public void onSuccess(JSONObject jSONObject) {
                SteamguardState.this.handleTwoFactorStatus(jSONObject);
            }
        });
        sendRequest(twoFactorQueryStatusRequestBuilder);
    }

    public void startSetScheme(Scheme scheme, String str, Completion completion) {
        if (scheme == Scheme.TwoFactor && getScheme() == Scheme.TwoFactor) {
            String tokenGID = getTokenGID();
            internalSetScheme(Scheme.None);
            sanitize();
            saveToFile();
            broadcastSteamguardStateRemoved(tokenGID);
            startAddTwoFactor(str, completion);
            return;
        }
        if (scheme == Scheme.TwoFactor) {
            startAddTwoFactor(str, completion);
        } else {
            internalStartSetScheme(scheme, completion);
        }
    }

    private void sendRequest(RequestBuilder requestBuilder) {
        SteamCommunityApplication.GetInstance().sendRequest(requestBuilder);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class FinalizeTwoFactorState {
        public String activationCode;
        public Scheme oldScheme;
        public long timeOffset = -40;
        public int retriesRemaining = 10;
        public boolean bSentActivationCode = false;
        public int nSentAuthCodeCount = 0;

        public FinalizeTwoFactorState(String str) {
            this.activationCode = str;
            this.oldScheme = SteamguardState.this.getScheme();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finalizeHelperErrorCleanup(FinalizeTwoFactorState finalizeTwoFactorState) {
        if (finalizeTwoFactorState.nSentAuthCodeCount > 0) {
            internalSetScheme(finalizeTwoFactorState.oldScheme);
            sanitize();
            saveToFile();
            this.mCommitted = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finalizeAddTwoFactorHelper(final FinalizeTwoFactorState finalizeTwoFactorState, final Completion completion) {
        RequestBuilder twoFactorFinalizeAddAuthenticatorRequestBuilder;
        long currentTime = this.mTwoFactorToken.currentTime() + finalizeTwoFactorState.timeOffset;
        if (!finalizeTwoFactorState.bSentActivationCode) {
            twoFactorFinalizeAddAuthenticatorRequestBuilder = Endpoints.getTwoFactorFinalizeAddAuthenticatorRequestBuilder(LoggedInUserAccountInfo.getLoginSteamID(), finalizeTwoFactorState.activationCode, null, currentTime);
        } else {
            twoFactorFinalizeAddAuthenticatorRequestBuilder = Endpoints.getTwoFactorFinalizeAddAuthenticatorRequestBuilder(LoggedInUserAccountInfo.getLoginSteamID(), null, this.mTwoFactorToken.generateSteamGuardCodeForTime(currentTime), currentTime);
            if (finalizeTwoFactorState.nSentAuthCodeCount == 0) {
                this.mCommitted = true;
                internalSetScheme(Scheme.TwoFactor);
                saveToFile();
            }
            finalizeTwoFactorState.nSentAuthCodeCount++;
        }
        twoFactorFinalizeAddAuthenticatorRequestBuilder.setResponseListener(new ResponseListener() { // from class: com.valvesoftware.android.steam.community.SteamguardState.3
            @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
            public void onSuccess(JSONObject jSONObject) {
                if (!jSONObject.optBoolean("success")) {
                    SteamguardState.this.finalizeHelperErrorCleanup(finalizeTwoFactorState);
                    if (jSONObject.optInt("status") == 89) {
                        completion.failure(jSONObject.optInt("status"), SteamguardState.sContext.getResources().getString(R.string.SteamMobile_Steamguard_BadCode));
                        return;
                    } else {
                        completion.failure(jSONObject.optInt("status"), null);
                        return;
                    }
                }
                if (jSONObject.optBoolean("want_more")) {
                    if (finalizeTwoFactorState.retriesRemaining <= 0) {
                        SteamguardState.this.finalizeHelperErrorCleanup(finalizeTwoFactorState);
                        completion.failure(-1, null);
                        return;
                    }
                    if (!finalizeTwoFactorState.bSentActivationCode) {
                        finalizeTwoFactorState.bSentActivationCode = true;
                    } else {
                        finalizeTwoFactorState.timeOffset += 30;
                        finalizeTwoFactorState.retriesRemaining--;
                    }
                    SteamguardState.this.finalizeAddTwoFactorHelper(finalizeTwoFactorState, completion);
                    return;
                }
                SteamguardState.broadcastSteamguardStateAdded(SteamguardState.this.getTokenGID());
                completion.success();
            }

            @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
            public void onError(RequestErrorInfo requestErrorInfo) {
                if (finalizeTwoFactorState.retriesRemaining <= 0) {
                    if (finalizeTwoFactorState.nSentAuthCodeCount > 0) {
                        SteamguardState.broadcastSteamguardStateAdded(SteamguardState.this.getTokenGID());
                        completion.success();
                        return;
                    } else {
                        completion.failure(-1, null);
                        return;
                    }
                }
                finalizeTwoFactorState.timeOffset += 30;
                FinalizeTwoFactorState finalizeTwoFactorState2 = finalizeTwoFactorState;
                finalizeTwoFactorState2.retriesRemaining--;
                SteamguardState.this.finalizeAddTwoFactorHelper(finalizeTwoFactorState, completion);
            }
        });
        sendRequest(twoFactorFinalizeAddAuthenticatorRequestBuilder);
    }

    public void finalizeAddTwoFactor(String str, Completion completion) {
        finalizeAddTwoFactorHelper(new FinalizeTwoFactorState(str), completion);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void mergeJson(JSONObject jSONObject, JSONObject jSONObject2) {
        Iterator<String> keys = jSONObject2.keys();
        while (keys.hasNext()) {
            String next = keys.next();
            try {
                jSONObject.put(next, jSONObject2.get(next));
            } catch (JSONException unused) {
            }
        }
    }

    private void startAddTwoFactor(String str, final Completion completion) {
        RequestBuilder addAuthenticatorRequestBuilder = Endpoints.getAddAuthenticatorRequestBuilder(LoggedInUserAccountInfo.getLoginSteamID(), Integer.toString(ETwoFactorTokenType.k_ETwoFactorTokenType_ValveMobileApp.ordinal()), getUniqueIdForPhone());
        if (str != null) {
            addAuthenticatorRequestBuilder.appendKeyValue("sms_phone_id", str);
        }
        addAuthenticatorRequestBuilder.setResponseListener(new ResponseListener() { // from class: com.valvesoftware.android.steam.community.SteamguardState.4
            @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
            public void onSuccess(JSONObject jSONObject) {
                SteamguardState.mergeJson(SteamguardState.this.mInfo, jSONObject);
                SteamguardState steamguardState = SteamguardState.this;
                steamguardState.mTwoFactorToken = new TwoFactorToken(steamguardState.mInfo);
                if (jSONObject.has("shared_secret")) {
                    SteamguardState.this.sendActivationCodeEmail();
                    completion.success();
                } else {
                    completion.failure(-1, null);
                }
            }

            @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
            public void onError(RequestErrorInfo requestErrorInfo) {
                completion.failure(-1, null);
            }
        });
        sendRequest(addAuthenticatorRequestBuilder);
    }

    public static String getUniqueIdForPhone() {
        SharedPreferences sharedPreferences = sContext.getSharedPreferences("steam.uuid", 0);
        String string = sharedPreferences.getString("uuidKey", "");
        if (string.length() > 0) {
            return string;
        }
        String str = null;
        try {
            str = UUID.randomUUID().toString();
        } catch (Exception e) {
            Log.e("RandomUUID", e.toString());
        }
        if (str == null) {
            str = String.format(Locale.US, "%d", Long.valueOf(new GregorianCalendar().getTimeInMillis()));
        }
        String format = String.format(Locale.US, "android:%s", str);
        sharedPreferences.edit().putString("uuidKey", format).commit();
        return format;
    }

    public void sendActivationCodeEmail() {
        sendEmailOfType(ETwoFactorEmailType.k_ETwoFactorEmailType_Signup, "include_activation_code", "1");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendRemovalEmail() {
        sendEmailOfType(ETwoFactorEmailType.k_ETwoFactorEmailType_Removed, new String[0]);
    }

    private void sendEmailOfType(ETwoFactorEmailType eTwoFactorEmailType, String... strArr) {
        RequestBuilder twoFactorSendEmailRequestBuilder = Endpoints.getTwoFactorSendEmailRequestBuilder(LoggedInUserAccountInfo.getLoginSteamID(), eTwoFactorEmailType.ordinal());
        int i = 0;
        while (true) {
            int i2 = i + 1;
            if (i2 < strArr.length) {
                twoFactorSendEmailRequestBuilder.appendKeyValue(strArr[i], strArr[i2]);
                i += 2;
            } else {
                twoFactorSendEmailRequestBuilder.setResponseListener(new ResponseListener() { // from class: com.valvesoftware.android.steam.community.SteamguardState.5
                    @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
                    public void onSuccess(JSONObject jSONObject) {
                        jSONObject.toString();
                        jSONObject.toString();
                    }

                    @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
                    public void onError(RequestErrorInfo requestErrorInfo) {
                        requestErrorInfo.toString();
                        requestErrorInfo.toString();
                    }
                });
                sendRequest(twoFactorSendEmailRequestBuilder);
                return;
            }
        }
    }

    public void removeTwoFactorForScheme(Scheme scheme) {
        if (scheme == null || scheme == Scheme.TwoFactor) {
            return;
        }
        String tokenGID = getTokenGID();
        internalSetScheme(scheme);
        sanitize();
        saveToFile();
        broadcastSteamguardStateRemoved(tokenGID);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleTwoFactorStatus(JSONObject jSONObject) {
        this.mTwoFactorStatus = jSONObject;
        if (getScheme() == Scheme.TwoFactor) {
            Scheme schemeNumberStringToScheme = schemeNumberStringToScheme(jSONObject.optString("steamguard_scheme"));
            String optString = jSONObject.optString("token_gid");
            if (schemeNumberStringToScheme != null) {
                if (schemeNumberStringToScheme == Scheme.TwoFactor && optString == null) {
                    return;
                }
                if (getScheme() == Scheme.TwoFactor) {
                    removeTwoFactorForScheme(schemeNumberStringToScheme);
                } else {
                    if (getTokenGID() == null || optString.compareTo(getTokenGID()) == 0) {
                        return;
                    }
                    removeTwoFactorForScheme(Scheme.Email);
                }
            }
        }
    }

    private void internalStartSetScheme(final Scheme scheme, final Completion completion) {
        final String optString = this.mInfo.optString("steamguard_scheme");
        RequestBuilder removeAuthenticatorRequestBuilder = Endpoints.getRemoveAuthenticatorRequestBuilder(LoggedInUserAccountInfo.getLoginSteamID(), Integer.toString(scheme.ordinal()), this.mInfo.optString("revocation_code", null));
        removeAuthenticatorRequestBuilder.setResponseListener(new ResponseListener() { // from class: com.valvesoftware.android.steam.community.SteamguardState.6
            @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
            public void onSuccess(JSONObject jSONObject) {
                if (jSONObject.optBoolean("success")) {
                    if (optString.equals(Integer.toString(Scheme.TwoFactor.ordinal()))) {
                        SteamguardState.this.sendRemovalEmail();
                    }
                    String tokenGID = SteamguardState.this.getTokenGID();
                    SteamguardState.this.sanitize();
                    SteamguardState.this.internalSetScheme(scheme);
                    SteamguardState.this.saveToFile();
                    SteamguardState.broadcastSteamguardStateRemoved(tokenGID);
                    completion.success();
                    return;
                }
                completion.failure(-1, null);
            }

            @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
            public void onError(RequestErrorInfo requestErrorInfo) {
                requestErrorInfo.toString();
                completion.failure(-1, null);
            }
        });
        sendRequest(removeAuthenticatorRequestBuilder);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void internalSetScheme(Scheme scheme) {
        try {
            this.mInfo.put("steamguard_scheme", Integer.toString(scheme.ordinal()));
        } catch (JSONException unused) {
        }
    }

    static String percentEncodeUrlUnsafeChars(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char charAt = str.charAt(i);
            if (charAt == '+' || charAt == '/' || charAt == '=') {
                sb.append(String.format(Locale.US, "%%%02x", Integer.valueOf(charAt)));
            } else {
                sb.append(charAt);
            }
        }
        return sb.toString();
    }

    private String base64encryptedConfirmationHash(long j, String str) {
        int i;
        String optString = this.mInfo.optString("identity_secret");
        if (optString == null) {
            return "";
        }
        byte[] decode = Base64.decode(optString.getBytes(), 0);
        if (str != null) {
            i = str.length() > 32 ? 40 : str.length() + 8;
        } else {
            i = 8;
        }
        byte[] bArr = new byte[i];
        long j2 = j;
        int i2 = 8;
        while (true) {
            int i3 = i2 - 1;
            if (i2 <= 0) {
                break;
            }
            bArr[i3] = (byte) j2;
            j2 >>>= 8;
            i2 = i3;
        }
        if (str != null) {
            System.arraycopy(str.getBytes(), 0, bArr, 8, i - 8);
        }
        SecretKeySpec secretKeySpec = new SecretKeySpec(decode, "HmacSHA1");
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(secretKeySpec);
            return percentEncodeUrlUnsafeChars(Base64.encodeToString(mac.doFinal(bArr), 2));
        } catch (InvalidKeyException unused) {
            return null;
        } catch (NoSuchAlgorithmException unused2) {
            return null;
        }
    }

    public String getConfirmationUrl() {
        long currentTimeSeconds = TimeCorrector.getInstance().currentTimeSeconds();
        String base64encryptedConfirmationHash = base64encryptedConfirmationHash(currentTimeSeconds, "conf");
        return base64encryptedConfirmationHash == null ? "" : String.format(Locale.US, "%s?p=%s&a=%s&k=%s&t=%d&m=android&tag=%s", SteamAppUri.CONFIRMATION_WEB, getUniqueIdForPhone(), getSteamId(), base64encryptedConfirmationHash, Long.valueOf(currentTimeSeconds), "conf");
    }

    public String getTaggedConfirmationUrlParams(String str) {
        long currentTimeSeconds = TimeCorrector.getInstance().currentTimeSeconds();
        String base64encryptedConfirmationHash = base64encryptedConfirmationHash(currentTimeSeconds, str);
        return base64encryptedConfirmationHash == null ? "" : String.format(Locale.US, "p=%s&a=%s&k=%s&t=%d&m=android&tag=%s", getUniqueIdForPhone(), getSteamId(), base64encryptedConfirmationHash, Long.valueOf(currentTimeSeconds), str);
    }

    public static void handleTwoFactorRemovalNotification(String str, String str2) {
        SteamguardState steamguardStateForGID;
        Scheme stringToScheme;
        if (str == null || str2 == null || (steamguardStateForGID = steamguardStateForGID(str)) == null || (stringToScheme = stringToScheme(str2)) == null) {
            return;
        }
        steamguardStateForGID.removeTwoFactorForScheme(stringToScheme);
    }
}

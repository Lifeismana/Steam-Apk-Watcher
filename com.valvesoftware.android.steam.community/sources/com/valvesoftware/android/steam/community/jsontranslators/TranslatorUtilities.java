package com.valvesoftware.android.steam.community.jsontranslators;

import com.valvesoftware.android.steam.community.Config;

/* loaded from: classes.dex */
public class TranslatorUtilities {
    public static String steamIdFromAccountId(String str) {
        long j;
        long longValue = Long.valueOf(str).longValue();
        if (Config.STEAM_UNIVERSE_WEBAPI == Config.SteamUniverse.Dev) {
            j = 288230376151711744L;
        } else {
            j = Config.STEAM_UNIVERSE_WEBAPI == Config.SteamUniverse.Beta ? 144115188075855872L : 72057594037927936L;
        }
        return String.valueOf(longValue + j + 4294967296L + 4503599627370496L);
    }
}

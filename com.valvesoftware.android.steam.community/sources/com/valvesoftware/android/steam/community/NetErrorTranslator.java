package com.valvesoftware.android.steam.community;

import java.util.Arrays;

/* loaded from: classes.dex */
public class NetErrorTranslator {
    private static final String[] s_likelyNetworkErrors = {"ADDRESS_UNREACHABLE", "CONNECTION_ABORTED", "CONNECTION_CLOSED", "CONNECTION_FAILED", "CONNECTION_REFUSED", "CONNECTION_RESET", "CONNECTION_TIMED_OUT", "DNS_SERVER_FAILED", "DNS_TIMED_OUT", "INTERNET_DISCONNECTED", "MSG_TOO_BIG", "NAME_NOT_RESOLVED", "NAME_RESOLUTION_FAILED", "PROXY_AUTH_REQUESTED", "PROXY_AUTH_REQUESTED_WITH_NO_CONNECTION", "PROXY_AUTH_UNSUPPORTED", "PROXY_CERTIFICATE_INVALID", "PROXY_CONNECTION_FAILED", "TIMED_OUT"};

    /* JADX WARN: Removed duplicated region for block: B:10:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:7:0x0029  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static String translateError(String str) {
        String str2;
        if (str.startsWith("net::ERR_")) {
            if (Arrays.binarySearch(s_likelyNetworkErrors, str.substring(9)) >= 0) {
                str2 = SteamCommunityApplication.GetInstance().getResources().getString(R.string.network_error);
                return str2 != null ? SteamCommunityApplication.GetInstance().getResources().getString(R.string.unspecified_error) : str2;
            }
        }
        str2 = null;
        if (str2 != null) {
        }
    }
}

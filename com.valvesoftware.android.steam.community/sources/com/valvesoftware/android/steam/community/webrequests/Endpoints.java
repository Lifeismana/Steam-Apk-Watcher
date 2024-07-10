package com.valvesoftware.android.steam.community.webrequests;

import android.os.Build;
import com.android.volley.DefaultRetryPolicy;
import com.valvesoftware.android.steam.community.Config;
import com.valvesoftware.android.steam.community.R;
import com.valvesoftware.android.steam.community.SteamCommunityApplication;
import com.valvesoftware.android.steam.community.SteamguardState;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/* loaded from: classes.dex */
public class Endpoints {
    public static final String UMQ_LOGON_URL = Config.URL_WEBAPI_BASE + "/ISteamWebUserPresenceOAuth/Logon/v0001";
    public static final String UMQ_LOGOFF_URL = Config.URL_WEBAPI_BASE + "/ISteamWebUserPresenceOAuth/Logoff/v0001";
    public static final String UMQ_MESSAGE_REQ_URL = Config.URL_WEBAPI_BASE + "/ISteamWebUserPresenceOAuth/Message/v0001";
    public static final String UMQ_SERVER_INFO_URL = Config.URL_WEBAPI_BASE + "/ISteamWebAPIUtil/GetServerInfo/v0001";
    public static final String UMQ_DEVICE_INFO_URL = Config.URL_WEBAPI_BASE + "/ISteamWebUserPresenceOAuth/DeviceInfo/v0001";
    public static final String UMQ_POLL_STATUS_URL = Config.URL_WEBAPI_BASE + "/ISteamWebUserPresenceOAuth/PollStatus/v0001";
    public static final String UMQ_POLL_FOR_MESSAGES_URL = Config.URL_WEBAPI_BASE + "/ISteamWebUserPresenceOAuth/Poll/v0001";
    public static final String GET_USER_SUMMARIES_URL = Config.URL_WEBAPI_BASE + "/ISteamUserOAuth/GetUserSummaries/v0001";
    public static final String GET_GROUP_SUMMARIES_URL = Config.URL_WEBAPI_BASE + "/ISteamUserOAuth/GetGroupSummaries/v0001";
    public static final String GET_APP_SUMMARIES_URL = Config.URL_WEBAPI_BASE + "/ISteamGameOAuth/GetAppInfo/v0001";
    public static final String GET_USER_GROUP_LIST_URL = Config.URL_WEBAPI_BASE + "/ISteamUserOAuth/GetGroupList/v0001";
    public static final String TWO_FACTOR_BASE_URL = Config.URL_WEBAPI_BASE + "/ITwoFactorService/%s/v0001";
    public static final String MOBILEAUTH_BASE_URL = Config.URL_WEBAPI_BASE + "/IMobileAuthService/%s/v0001";
    public static final String MOBILENOTIFICATION_BASE_URL = Config.URL_WEBAPI_BASE + "/IMobileNotificationService/%s/v0001";
    public static final String GET_USER_FRIEND_LIST_URL = Config.URL_WEBAPI_BASE + "/ISteamUserOAuth/GetFriendList/v0001";
    public static final String GENERAL_SEARCH_URL = Config.URL_WEBAPI_BASE + "/ISteamUserOAuth/Search/v0001";
    public static final String MOBILE_STOREFRONT_CATEGORIES_URL = Config.URL_STORE_BASE + "/api/mobilestorefrontcategories/v0001";
    public static final String MOBILE_STOREFRONT_INDEX_CATEGORIES_URL = Config.URL_STORE_BASE + "/api/mobilestorefrontindexcategories/v0001";
    public static final String MOBILE_STOREFRONT_ACCOUNTDETAILS_CATEGORIES_URL = Config.URL_STORE_BASE + "/api/mobilestorefrontaccountdetailscategories/v0001";
    public static final String GET_ACTIVE_MESSAGE_SESSIONS_SINCE_URL = Config.URL_WEBAPI_BASE + "/IFriendMessagesService/GetActiveMessageSessions/v0001";
    public static final String GET_RECENT_MESSAGES_URL = Config.URL_WEBAPI_BASE + "/IFriendMessagesService/GetRecentMessages/v0001";
    public static final String MARK_OFFLINE_MESSAGES_READ_URL = Config.URL_WEBAPI_BASE + "/IFriendMessagesService/MarkOfflineMessagesRead/v0001";
    public static final String SWITCH_TO_PUSH_URL = Config.URL_WEBAPI_BASE + "/IMobileNotificationService/SwitchSessionToPush/v0001";

    public static ImageRequestBuilder getImageUrlRequestBuilder(String str) {
        return new ImageRequestBuilder(str);
    }

    public static RequestBuilder getFriendsSearchRequestBuilder(String str, int i, int i2) {
        JsonRequestBuilder buildRequestForGet = buildRequestForGet(GENERAL_SEARCH_URL);
        buildRequestForGet.appendKeyValue("keywords", str);
        buildRequestForGet.appendKeyValue("offset", String.valueOf(i));
        buildRequestForGet.appendKeyValue("count", String.valueOf(i2));
        buildRequestForGet.appendKeyValue("targets", "users");
        buildRequestForGet.appendKeyValue("fields", "all");
        return buildRequestForGet;
    }

    public static RequestBuilder getGroupsSearchRequestBuilder(String str, int i, int i2) {
        JsonRequestBuilder buildRequestForGet = buildRequestForGet(GENERAL_SEARCH_URL);
        buildRequestForGet.appendKeyValue("keywords", str);
        buildRequestForGet.appendKeyValue("offset", String.valueOf(i));
        buildRequestForGet.appendKeyValue("count", String.valueOf(i2));
        buildRequestForGet.appendKeyValue("targets", "groups");
        buildRequestForGet.appendKeyValue("fields", "all");
        return buildRequestForGet;
    }

    public static RequestBuilder getFriendListRequestBuilder(String str, String str2) {
        JsonRequestBuilder buildRequestForGet = buildRequestForGet(GET_USER_FRIEND_LIST_URL);
        buildRequestForGet.appendSteamId(str);
        buildRequestForGet.appendKeyValue("relationship", str2);
        return buildRequestForGet;
    }

    public static RequestBuilder getGroupListRequestBuilder(String str) {
        JsonRequestBuilder buildRequestForGet = buildRequestForGet(GET_USER_GROUP_LIST_URL);
        buildRequestForGet.appendSteamId(str);
        return buildRequestForGet;
    }

    public static RequestBuilder getUserSummaryRequestBuilder(String str) {
        JsonRequestBuilder buildRequestForGet = buildRequestForGet(GET_USER_SUMMARIES_URL);
        buildRequestForGet.appendKeyValue("steamids", str);
        return buildRequestForGet;
    }

    public static List<RequestBuilder> getGroupSummariesRequestBuilder(Collection<String> collection) {
        return getDetailRequestBuilders(collection, GET_GROUP_SUMMARIES_URL);
    }

    public static List<RequestBuilder> getUserSummariesRequestBuilder(Collection<String> collection) {
        return getDetailRequestBuilders(collection, GET_USER_SUMMARIES_URL);
    }

    public static RequestBuilder getActiveMessageSessions() {
        return buildRequestForGet(GET_ACTIVE_MESSAGE_SESSIONS_SINCE_URL);
    }

    public static RequestBuilder getRecentMessages(String str, String str2) {
        JsonRequestBuilder buildRequestForGet = buildRequestForGet(GET_RECENT_MESSAGES_URL);
        buildRequestForGet.appendKeyValue("steamid1", str);
        buildRequestForGet.appendKeyValue("steamid2", str2);
        buildRequestForGet.appendKeyValue("rtime32_start_time", "0");
        return buildRequestForGet;
    }

    private static List<RequestBuilder> getDetailRequestBuilders(Collection<String> collection, String str) {
        ArrayList arrayList = new ArrayList(collection);
        ArrayList arrayList2 = new ArrayList();
        while (arrayList.size() > 50) {
            List subList = arrayList.subList(arrayList.size() - 50, arrayList.size());
            arrayList2.add(getSingleDetailRequest(subList, str));
            subList.clear();
        }
        if (arrayList.size() > 0) {
            arrayList2.add(getSingleDetailRequest(arrayList, str));
        }
        return arrayList2;
    }

    private static RequestBuilder getSingleDetailRequest(Collection<String> collection, String str) {
        JsonRequestBuilder buildRequestForGet = buildRequestForGet(str);
        buildRequestForGet.appendArray("steamids", (String[]) collection.toArray(new String[collection.size()]));
        return buildRequestForGet;
    }

    public static RequestBuilder getUMQLogonRequestBuilder() {
        return buildRequestForPost(UMQ_LOGON_URL);
    }

    public static RequestBuilder getUMQLogoffRequestBuilder(String str) {
        JsonRequestBuilder buildRequestForPost = buildRequestForPost(UMQ_LOGOFF_URL);
        buildRequestForPost.appendKeyValue("umqid", str);
        return buildRequestForPost;
    }

    public static RequestBuilder getSendUMQMessageRequestBuilder(String str, String str2, String str3) {
        JsonRequestBuilder buildRequestForPost = buildRequestForPost(UMQ_MESSAGE_REQ_URL);
        buildRequestForPost.appendKeyValue("umqid", str3);
        buildRequestForPost.appendKeyValue("type", "saytext");
        buildRequestForPost.appendKeyValue("steamid_dst", str2);
        buildRequestForPost.appendKeyValue("text", str);
        return buildRequestForPost;
    }

    public static RequestBuilder getSendUMQTypingNotificationRequestBuilder(String str, String str2) {
        JsonRequestBuilder buildRequestForPost = buildRequestForPost(UMQ_MESSAGE_REQ_URL);
        buildRequestForPost.appendKeyValue("umqid", str2);
        buildRequestForPost.appendKeyValue("type", "typing");
        buildRequestForPost.appendKeyValue("steamid_dst", str);
        buildRequestForPost.appendKeyValue("text", "");
        return buildRequestForPost;
    }

    public static RequestBuilder getSendServerPushInfoRequestBuilder(String str, boolean z, String str2) {
        JsonRequestBuilder buildRequestForPost = buildRequestForPost(UMQ_DEVICE_INFO_URL);
        buildRequestForPost.appendKeyValue("deviceid", "GOOG::GCM:" + str);
        buildRequestForPost.appendKeyValue("lang", SteamCommunityApplication.GetInstance().getString(R.string.DO_NOT_LOCALIZE_COOKIE_Steam_Language));
        buildRequestForPost.appendKeyValue("version", Config.APP_VERSION);
        if (str2 != null) {
            buildRequestForPost.appendKeyValue("umqid", str2);
        }
        buildRequestForPost.appendKeyValue("im_enable", z ? "1" : "0");
        buildRequestForPost.appendKeyValue("tf_deviceid", SteamguardState.getUniqueIdForPhone());
        buildRequestForPost.appendKeyValue("device_model", Build.MODEL);
        buildRequestForPost.appendKeyValue("os_version", Build.VERSION.RELEASE);
        return buildRequestForPost;
    }

    public static RequestBuilder getSwitchToPushRequestBuilder(String str) {
        JsonRequestBuilder buildRequestForPost = buildRequestForPost(SWITCH_TO_PUSH_URL);
        buildRequestForPost.appendKeyValue("umqid", str);
        return buildRequestForPost;
    }

    public static RequestBuilder getUMQPollStatusRequestBuilder(String str, String str2, long j) {
        JsonRequestBuilder buildRequestForPost = buildRequestForPost(UMQ_POLL_STATUS_URL);
        buildRequestForPost.appendSteamId(str);
        buildRequestForPost.appendKeyValue("umqid", str2);
        buildRequestForPost.appendKeyValue("message", String.valueOf(j));
        buildRequestForPost.appendKeyValue("sectimeout", "25");
        buildRequestForPost.setRetryPolicy(new DefaultRetryPolicy(30000, 1, 1.0f));
        return buildRequestForPost;
    }

    public static RequestBuilder getUMQPollForMessageRequestBuilder(String str, String str2, long j) {
        JsonRequestBuilder buildRequestForPost = buildRequestForPost(UMQ_POLL_FOR_MESSAGES_URL);
        buildRequestForPost.appendSteamId(str);
        buildRequestForPost.appendKeyValue("umqid", str2);
        buildRequestForPost.appendKeyValue("message", String.valueOf(j));
        buildRequestForPost.appendKeyValue("sectimeout", "1");
        buildRequestForPost.setRetryPolicy(new DefaultRetryPolicy(30000, 1, 1.0f));
        return buildRequestForPost;
    }

    public static RequestBuilder getMarkMessagesReadRequestBuilder(String str) {
        StringRequestBuilder stringRequestBuilder = new StringRequestBuilder(MARK_OFFLINE_MESSAGES_READ_URL, true);
        stringRequestBuilder.appendKeyValue("steamid_friend", str);
        return stringRequestBuilder;
    }

    public static RequestBuilder getTwoFactorFinalizeAddAuthenticatorRequestBuilder(String str, String str2, String str3, long j) {
        JsonRequestBuilder buildRequestForPost = buildRequestForPost(String.format(TWO_FACTOR_BASE_URL, "FinalizeAddAuthenticator"));
        buildRequestForPost.appendSteamId(str);
        if (str2 != null) {
            buildRequestForPost.appendKeyValue("activation_code", str2);
        }
        if (str3 != null) {
            buildRequestForPost.appendKeyValue("authenticator_code", str3);
        }
        buildRequestForPost.appendKeyValue("authenticator_time", Long.toString(j));
        return buildRequestForPost;
    }

    public static RequestBuilder getAddAuthenticatorRequestBuilder(String str, String str2, String str3) {
        JsonRequestBuilder buildRequestForPost = buildRequestForPost(String.format(TWO_FACTOR_BASE_URL, "AddAuthenticator"));
        buildRequestForPost.appendSteamId(str);
        buildRequestForPost.appendKeyValue("authenticator_type", str2);
        buildRequestForPost.appendKeyValue("device_identifier", str3);
        return buildRequestForPost;
    }

    public static RequestBuilder getRemoveAuthenticatorRequestBuilder(String str, String str2, String str3) {
        JsonRequestBuilder buildRequestForPost = buildRequestForPost(String.format(TWO_FACTOR_BASE_URL, "RemoveAuthenticator"));
        buildRequestForPost.appendSteamId(str);
        buildRequestForPost.appendKeyValue("steamguard_scheme", str2);
        if (str3 != null) {
            buildRequestForPost.appendKeyValue("revocation_code", str3);
        }
        return buildRequestForPost;
    }

    public static RequestBuilder getTwoFactorSendEmailRequestBuilder(String str, int i) {
        JsonRequestBuilder buildRequestForPost = buildRequestForPost(String.format(TWO_FACTOR_BASE_URL, "SendEmail"));
        buildRequestForPost.appendSteamId(str);
        buildRequestForPost.appendKeyValue("email_type", Integer.toString(i));
        return buildRequestForPost;
    }

    public static RequestBuilder getTwoFactorQueryTimeRequestBuilder() {
        return buildRequestForPost(String.format(TWO_FACTOR_BASE_URL, "QueryTime"));
    }

    public static RequestBuilder getTwoFactorQueryStatusRequestBuilder(String str) {
        JsonRequestBuilder buildRequestForPost = buildRequestForPost(String.format(TWO_FACTOR_BASE_URL, "QueryStatus"));
        buildRequestForPost.appendSteamId(str);
        return buildRequestForPost;
    }

    public static JsonRequestBuilder getWGToken() {
        return buildRequestForPost(String.format(MOBILEAUTH_BASE_URL, "GetWGToken"));
    }

    public static JsonRequestBuilder getUserNotificationCounts() {
        return buildRequestForGet(String.format(MOBILENOTIFICATION_BASE_URL, "GetUserNotificationCounts"));
    }

    public static RequestBuilder getGenericJsonGetRequestBuilder(String str) {
        return buildRequestForGet(str);
    }

    private static JsonRequestBuilder buildRequestForGet(String str) {
        return new JsonRequestBuilder(str, false);
    }

    private static JsonRequestBuilder buildRequestForPost(String str) {
        return new JsonRequestBuilder(str, true);
    }
}

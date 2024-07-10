package com.valvesoftware.android.steam.community;

import android.net.Uri;

/* loaded from: classes.dex */
public class SteamAppUri {
    public static final String URL_CURRENT_USER_PROFILE_BASE_GENERIC = Config.URL_COMMUNITY_BASE + "/my";
    public static final String URL_CURRENT_USER_PROFILE_BASE_STEAMID = Config.URL_COMMUNITY_BASE + "/profiles";
    public static final String URL_CURRENT_USER_PROFILE_BASE_CUSTOMURL = Config.URL_COMMUNITY_BASE + "/id";
    public static final String CART = Config.URL_STORE_BASE + "/cart/";
    public static final String STEAM_NEWS = Config.URL_STORE_BASE + "/news/";
    public static final String STEAMGUARD_HELP = Config.URL_COMMUNITY_BASE + "/steamguard/help";
    public static final String STEAMGUARD_CHANGE = Config.URL_COMMUNITY_BASE + "/steamguard/change";
    public static final String STEAMGUARD_RCODE = Config.URL_COMMUNITY_BASE + "/steamguard/twofactor_recoverycode?countdown=0";
    public static final String STEAMGUARD_PRECHANGE = Config.URL_COMMUNITY_BASE + "/steamguard/prechange";
    public static final String CONFIRMATION_WEB = Config.URL_COMMUNITY_BASE + "/mobileconf/conf";
    public static final String STEAM_NOTIFICATIONS_SETTINGS = Config.URL_COMMUNITY_BASE + "/mobilesettings/GetManifest/v0001";

    public static Uri createChatUri(String str) {
        return Uri.parse("steammobile://chat?steamid=" + str);
    }

    public static Uri createCurrentUserProfileUri(String str) {
        if (LoggedInUserAccountInfo.isLoggedIn()) {
            return createSteamAppWebUri(URL_CURRENT_USER_PROFILE_BASE_STEAMID + "/" + LoggedInUserAccountInfo.getLoginSteamID() + str);
        }
        return createSteamAppWebUri(URL_CURRENT_USER_PROFILE_BASE_GENERIC + str);
    }

    public static Uri createFriendsSearchUri(String str) {
        return createSearchUri(str, "friends");
    }

    public static Uri createGroupsSearchUri(String str) {
        return createSearchUri(str, "groups");
    }

    public static Uri library() {
        return createCurrentUserProfileUri("/games/?tab=all");
    }

    public static Uri settings() {
        return createUri("steammobile://", "appsettings");
    }

    private static Uri createSearchUri(String str, String str2) {
        return Uri.parse("steammobile://" + str2 + "?search=" + str);
    }

    public static Uri groupWebPage(String str) {
        return createSteamAppWebUri(Config.URL_COMMUNITY_BASE + str);
    }

    public static Uri wishlist() {
        return createCurrentUserProfileUri("/wishlist/");
    }

    public static Uri shoppingCart() {
        return createSteamAppWebUri(CART);
    }

    public static Uri steamNews() {
        return createSteamAppWebUri(STEAM_NEWS);
    }

    public static Uri friendActivity() {
        return createCurrentUserProfileUri("/home/");
    }

    public static Uri createVisitProfileUri(String str) {
        return createSteamAppWebUri(Config.URL_COMMUNITY_BASE + "/profiles/" + str);
    }

    public static Uri createSteamAppWebUri(String str) {
        return Uri.parse("steammobile://openurl?url=" + str);
    }

    public static Uri searchSteam() {
        return createSteamAppWebUri(Config.URL_STORE_BASE + "/search/");
    }

    public static Uri catalog() {
        return createSteamAppWebUri(Config.URL_STORE_BASE);
    }

    public static Uri accountDetails() {
        return createSteamAppWebUri(Config.URL_STORE_BASE + "/account/");
    }

    public static Uri steamGuard() {
        return createUri("steammobile://", "steamguard");
    }

    public static Uri friendsList() {
        return createUri("steammobile://", "friends");
    }

    public static Uri groupsList() {
        return createUri("steammobile://", "groups");
    }

    public static Uri confirmationResource() {
        return createUri("steammobile://", "confirmation");
    }

    private static Uri createUri(String str, String str2) {
        return Uri.parse(str + str2);
    }

    public static Uri login() {
        return createUri("steammobile://", "login");
    }

    public static Uri deleteNotification() {
        return createUri("steammobile://", "deletenotification");
    }

    public static Uri notificationComments() {
        return createCurrentUserProfileUri("/commentnotifications");
    }

    public static Uri notificationItems() {
        return createCurrentUserProfileUri("/inventory");
    }

    public static Uri notificationInvites() {
        return createCurrentUserProfileUri("/home/invites");
    }

    public static Uri notificationGifts() {
        return createCurrentUserProfileUri("/inventory#pending_gifts");
    }

    public static Uri notificationTradeOffers() {
        return createCurrentUserProfileUri("/tradeoffers");
    }

    public static Uri notificationAsyncGame() {
        return createCurrentUserProfileUri("/gamenotifications");
    }

    public static Uri notificationModeratorMessage() {
        return createCurrentUserProfileUri("/moderatormessages");
    }

    public static String steamHelpUriPrefix() {
        return Config.URL_COMMUNITY_BASE + "/mobilelogin/help";
    }

    public static String steamSubscriberAgreementUriPrefix() {
        return Config.URL_STORE_BASE + "/mobilecheckout/ssapopup";
    }

    public static String steamPrivacyPolicyUriPrefix() {
        return Config.URL_STORE_BASE + "/mobilelogin/privacy_agreement";
    }

    public static String getURLISOCodeForLanguage(String str) {
        if ("english".equals(str)) {
            return "en";
        }
        if ("german".equals(str)) {
            return "de";
        }
        if ("french".equals(str)) {
            return "fr";
        }
        if ("italian".equals(str)) {
            return "it";
        }
        if ("korean".equals(str)) {
            return "ko";
        }
        if ("spanish".equals(str)) {
            return "es";
        }
        if ("schinese".equals(str)) {
            return "zh";
        }
        if ("schinese".equals(str)) {
            return "zh-cn";
        }
        if ("tchinese".equals(str)) {
            return "zh-tw";
        }
        if ("russian".equals(str)) {
            return "ru";
        }
        if ("thai".equals(str)) {
            return "th";
        }
        if ("japanese".equals(str)) {
            return "ja";
        }
        if ("brazilian".equals(str)) {
            return "pt-br";
        }
        if ("portuguese".equals(str)) {
            return "pt";
        }
        if ("polish".equals(str)) {
            return "pl";
        }
        if ("danish".equals(str)) {
            return "da";
        }
        if ("dutch".equals(str)) {
            return "nl";
        }
        if ("finnish".equals(str)) {
            return "fi";
        }
        if ("norwegian".equals(str)) {
            return "no";
        }
        if ("swedish".equals(str)) {
            return "sv";
        }
        if ("hungarian".equals(str)) {
            return "hu";
        }
        if ("czech".equals(str)) {
            return "cs";
        }
        if ("romanian".equals(str)) {
            return "ro";
        }
        if ("turkish".equals(str)) {
            return "tr";
        }
        if ("arabic".equals(str)) {
            return "ar";
        }
        if ("bulgarian".equals(str)) {
            return "bg";
        }
        if ("greek".equals(str)) {
            return "el";
        }
        if ("ukrainian".equals(str)) {
            return "uk";
        }
        return null;
    }

    public static String appendLanguageToUrl(String str) {
        String uRLISOCodeForLanguage = getURLISOCodeForLanguage(LoggedInUserAccountInfo.getLanguage());
        if (uRLISOCodeForLanguage == null) {
            return str;
        }
        return str + '/' + uRLISOCodeForLanguage;
    }
}

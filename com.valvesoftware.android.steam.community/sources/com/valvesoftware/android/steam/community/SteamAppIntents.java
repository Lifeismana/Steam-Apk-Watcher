package com.valvesoftware.android.steam.community;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.valvesoftware.android.steam.community.activity.MainActivity;

/* loaded from: classes.dex */
public class SteamAppIntents {
    public static String chatPartnerAvatarUrlKey = "chatPartnerAvatarUrl";
    public static String chatPartnerPersonaNameKey = "chatPartnerPersonaNameKey";
    public static String notificationId = "notificationId";

    public static Intent visitProfileIntent(Context context, String str) {
        return mainActivityIntent(context, SteamAppUri.createVisitProfileUri(str));
    }

    public static Intent searchFriendIntent(Context context, String str) {
        return mainActivityIntent(context, SteamAppUri.createFriendsSearchUri(str));
    }

    public static Intent chatIntent(Context context, String str) {
        return chatIntent(context, str, null, null);
    }

    public static Intent chatIntent(Context context, String str, String str2, String str3) {
        Intent mainActivityIntent = mainActivityIntent(context, SteamAppUri.createChatUri(str));
        if (str3 != null) {
            mainActivityIntent.putExtra(chatPartnerAvatarUrlKey, str3);
        }
        if (str2 != null) {
            mainActivityIntent.putExtra(chatPartnerPersonaNameKey, str2);
        }
        return mainActivityIntent;
    }

    public static Intent viewSteamGuard(Context context) {
        return mainActivityIntent(context, SteamAppUri.steamGuard());
    }

    public static Intent viewConfirmations(Context context) {
        return mainActivityIntent(context, SteamAppUri.confirmationResource());
    }

    public static Intent viewFriendsList(Context context) {
        return mainActivityIntent(context, SteamAppUri.friendsList());
    }

    public static Intent viewGroupsList(Context context) {
        return mainActivityIntent(context, SteamAppUri.groupsList());
    }

    public static Intent viewFriendActivity(Context context) {
        return mainActivityIntent(context, SteamAppUri.friendActivity());
    }

    public static Intent viewCatalog(Context context) {
        return mainActivityIntent(context, SteamAppUri.catalog());
    }

    public static Intent viewWishList(Context context) {
        return mainActivityIntent(context, SteamAppUri.wishlist());
    }

    public static Intent viewShoppingCart(Context context) {
        return mainActivityIntent(context, SteamAppUri.shoppingCart());
    }

    public static Intent searchSteam(Context context) {
        return mainActivityIntent(context, SteamAppUri.searchSteam());
    }

    public static Intent viewSteamNews(Context context) {
        return mainActivityIntent(context, SteamAppUri.steamNews());
    }

    public static Intent viewSettings(Context context) {
        return mainActivityIntent(context, SteamAppUri.settings());
    }

    public static Intent viewAccountDetails(Context context) {
        return mainActivityIntent(context, SteamAppUri.accountDetails());
    }

    public static Intent viewWebPage(Context context, String str) {
        return mainActivityIntent(context, SteamAppUri.createSteamAppWebUri(str));
    }

    public static Intent viewLibrary(Context context) {
        return mainActivityIntent(context, SteamAppUri.library());
    }

    public static Intent mainActivityIntent(Context context, Uri uri) {
        return new Intent(context, (Class<?>) MainActivity.class).setData(uri);
    }

    public static Intent communityURLIntent(Context context, String str) {
        return mainActivityIntent(context, SteamAppUri.createSteamAppWebUri(Config.URL_COMMUNITY_BASE + str));
    }

    public static Intent profileURLIntent(Context context, String str) {
        return mainActivityIntent(context, SteamAppUri.createCurrentUserProfileUri(str));
    }

    public static Intent helpURLIntent(Context context, String str) {
        return mainActivityIntent(context, SteamAppUri.createSteamAppWebUri(SteamAppUri.appendLanguageToUrl(Config.URL_HELP_BASE) + str));
    }

    public static Intent loginIntent(Context context) {
        return mainActivityIntent(context, SteamAppUri.login());
    }

    public static Intent notificationCommentsIntent(Context context) {
        return mainActivityIntent(context, SteamAppUri.notificationComments());
    }

    public static Intent notificationItemsIntent(Context context) {
        return mainActivityIntent(context, SteamAppUri.notificationItems());
    }

    public static Intent notificationInvitesIntent(Context context) {
        return mainActivityIntent(context, SteamAppUri.notificationInvites());
    }

    public static Intent notificationGiftsIntent(Context context) {
        return mainActivityIntent(context, SteamAppUri.notificationGifts());
    }

    public static Intent notificationAsyncGameIntent(Context context) {
        return mainActivityIntent(context, SteamAppUri.notificationAsyncGame());
    }

    public static Intent notificationModeratorMessageIntent(Context context) {
        return mainActivityIntent(context, SteamAppUri.notificationModeratorMessage());
    }

    public static Intent notificationTradeOffersIntent(Context context) {
        return mainActivityIntent(context, SteamAppUri.notificationTradeOffers());
    }

    public static Intent notificationHelpRequestReplyIntent(Context context) {
        return helpURLIntent(context, "/wizard/HelpRequests");
    }
}

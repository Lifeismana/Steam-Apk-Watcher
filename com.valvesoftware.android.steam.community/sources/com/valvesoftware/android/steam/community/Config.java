package com.valvesoftware.android.steam.community;

/* loaded from: classes.dex */
public class Config {
    public static String APP_VERSION = "2.3.9";
    public static int APP_VERSION_ID;
    public static final SteamUniverse STEAM_UNIVERSE_WEBAPI = SteamUniverse.Public;
    public static final SteamUniverse STEAM_UNIVERSE_WEBPHP = SteamUniverse.Public;
    public static final String URL_COMMUNITY_BASE;
    public static final String URL_HELP_BASE;
    public static final String URL_STORE_BASE;
    public static final String URL_WEBAPI_BASE;
    public static final String[] WG_AUTH_DOMAINS;

    /* loaded from: classes.dex */
    public enum SteamUniverse {
        Public,
        Beta,
        Dev
    }

    static {
        URL_WEBAPI_BASE = STEAM_UNIVERSE_WEBAPI == SteamUniverse.Public ? "https://api.steampowered.com:443" : STEAM_UNIVERSE_WEBAPI == SteamUniverse.Beta ? "https://api.beta.steampowered.com:443" : "https://landond3.valve.org:8283";
        StringBuilder sb = new StringBuilder();
        sb.append("https://");
        sb.append(STEAM_UNIVERSE_WEBPHP == SteamUniverse.Public ? "steamcommunity.com" : STEAM_UNIVERSE_WEBPHP == SteamUniverse.Beta ? "beta.steamcommunity.com" : "landond3.valve.org/community");
        URL_COMMUNITY_BASE = sb.toString();
        StringBuilder sb2 = new StringBuilder();
        sb2.append("https://");
        sb2.append(STEAM_UNIVERSE_WEBPHP == SteamUniverse.Public ? "store.steampowered.com" : STEAM_UNIVERSE_WEBPHP == SteamUniverse.Beta ? "store-beta.steampowered.com" : "landond3.valve.org/store");
        URL_STORE_BASE = sb2.toString();
        StringBuilder sb3 = new StringBuilder();
        sb3.append("https://");
        sb3.append(STEAM_UNIVERSE_WEBPHP == SteamUniverse.Public ? "help.steampowered.com" : STEAM_UNIVERSE_WEBPHP == SteamUniverse.Beta ? "help.beta.steampowered.com" : "landond3.valve.org/help");
        URL_HELP_BASE = sb3.toString();
        WG_AUTH_DOMAINS = new String[]{URL_COMMUNITY_BASE, URL_STORE_BASE, URL_HELP_BASE};
    }

    /* loaded from: classes.dex */
    public static class WebAPI {
        public static final String OAUTH_CLIENT_ID;

        static {
            String str;
            if (Config.STEAM_UNIVERSE_WEBAPI == SteamUniverse.Public) {
                str = "DE45CD61";
            } else {
                SteamUniverse steamUniverse = Config.STEAM_UNIVERSE_WEBAPI;
                SteamUniverse steamUniverse2 = SteamUniverse.Beta;
                str = "7DC60112";
            }
            OAUTH_CLIENT_ID = str;
        }
    }
}

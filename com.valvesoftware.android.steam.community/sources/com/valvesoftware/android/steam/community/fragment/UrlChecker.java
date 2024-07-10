package com.valvesoftware.android.steam.community.fragment;

import android.text.style.URLSpan;

/* loaded from: classes.dex */
public class UrlChecker {
    private static final String[] s_safeURIs = {"steampowered.com", "steamgames.com", "steamcommunity.com", "valvesoftware.com", "youtube.com", "live.com", "msn.com", "myspace.com", "facebook.com", "hi5.com", "wikipedia.org", "orkut.com", "rapidshare.com", "blogger.com", "megaupload.com", "friendster.com", "fotolog.net", "google.fr", "baidu.com", "microsoft.com", "ebay.com", "shacknews.com", "bbc.co.uk", "cnn.com", "foxsports.com", "pcmag.com", "nytimes.com", "flickr.com", "amazon.com", "veoh.com", "pcgamer.com", "metacritic.com", "fileplanet.com", "gamespot.com", "gametap.com", "ign.com", "kotaku.com", "xfire.com", "pcgames.gwn.com", "gamezone.com", "gamesradar.com", "digg.com", "engadget.com", "gizmodo.com", "gamesforwindows.com", "xbox.com", "cnet.com", "l4d.com", "teamfortress.com", "tf2.com", "half-life2.com", "aperturescience.com", "dayofdefeat.com", "dota2.com", "steamtranslation.ru", "playdota.com"};

    public static boolean isUrlUnsafe(URLSpan uRLSpan) {
        int i;
        String url = uRLSpan.getURL();
        if (url.startsWith("tel:") || url.startsWith("mailto:") || url.startsWith("geo:")) {
            return false;
        }
        if (url.startsWith("http://") || url.startsWith("rtsp://")) {
            i = 7;
        } else {
            if (!url.startsWith("https://")) {
                return true;
            }
            i = 8;
        }
        int length = url.length();
        for (char c : new char[]{':', '?', '/'}) {
            int indexOf = url.indexOf(c, i);
            if (indexOf >= 0 && indexOf < length) {
                length = indexOf;
            }
        }
        String substring = url.substring(i, length);
        for (String str : s_safeURIs) {
            if (substring.endsWith(str) && (substring.length() <= str.length() || substring.charAt((substring.length() - str.length()) - 1) == '.')) {
                return false;
            }
        }
        return true;
    }
}

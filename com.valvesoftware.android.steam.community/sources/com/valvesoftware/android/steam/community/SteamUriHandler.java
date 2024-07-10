package com.valvesoftware.android.steam.community;

import android.net.Uri;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Properties;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class SteamUriHandler {

    /* loaded from: classes.dex */
    public enum CommandProperty {
        url,
        call,
        title,
        steamid,
        oauth_token,
        wgtoken,
        wgtoken_secure,
        webcookie,
        gid,
        scheme,
        code,
        acct,
        refresh,
        ph,
        op,
        arg1,
        arg2
    }

    public static Result HandleSteamURI(Uri uri) {
        String uri2 = uri.toString();
        String encodedQuery = uri.getEncodedQuery();
        Result result = new Result();
        if (uri2.startsWith("steammobile://")) {
            try {
                String substring = uri2.substring(14);
                int indexOf = substring.indexOf("?");
                if (indexOf > 0) {
                    substring = substring.substring(0, indexOf);
                }
                result.command = Command.valueOf(substring);
                result.handled = true;
            } catch (RuntimeException unused) {
            }
        }
        if (result.handled) {
            try {
                result.props = new Properties();
                if (encodedQuery != null) {
                    if (result.command == Command.mobileloginsucceeded) {
                        JSONObject jSONObject = new JSONObject(Uri.decode(encodedQuery));
                        JSONArray names = jSONObject.names();
                        for (int i = 0; i < names.length(); i++) {
                            try {
                                String str = (String) names.opt(i);
                                result.props.put(str, jSONObject.get(str).toString());
                            } catch (Exception unused2) {
                            }
                        }
                    } else if (result.command.bHasArgs) {
                        for (String str2 : encodedQuery.split("&")) {
                            String[] split = str2.split("=", 2);
                            if (split.length > 1) {
                                result.props.put(URLDecoder.decode(split[0], "UTF-8"), URLDecoder.decode(split[1], "UTF-8"));
                            }
                        }
                    } else {
                        result.props.load(new ByteArrayInputStream(encodedQuery.getBytes()));
                    }
                }
            } catch (IOException | Exception unused3) {
            }
        }
        return result;
    }

    /* loaded from: classes.dex */
    public static class Result {
        public Command command;
        public boolean handled = false;
        public Properties props;

        public String getProperty(CommandProperty commandProperty) {
            return this.props.getProperty(commandProperty.toString());
        }

        public String getProperty(CommandProperty commandProperty, String str) {
            return this.props.getProperty(commandProperty.toString(), str);
        }
    }

    /* loaded from: classes.dex */
    public enum Command {
        openurl,
        settitle,
        login,
        closethis,
        notfound,
        opencategoryurl,
        errorrecovery,
        reloadpage,
        chat,
        openexternalurl,
        mobileloginsucceeded,
        application_internal,
        twofactorcode(true),
        steamguardset(true),
        steamguardvalidate(true),
        steamguardsendemail,
        getjsresult,
        steamguardgetgid,
        steamguardsuppresstwofactorgid(true),
        steamguardgetrevocation,
        steamguardconfrefresh(true),
        steamguardconfcount,
        currentuser,
        logout(true),
        livetokens,
        steamguard(true),
        lostauth;

        public boolean bHasArgs;

        Command() {
            this.bHasArgs = false;
        }

        Command(boolean z) {
            this.bHasArgs = false;
            this.bHasArgs = z;
        }
    }
}

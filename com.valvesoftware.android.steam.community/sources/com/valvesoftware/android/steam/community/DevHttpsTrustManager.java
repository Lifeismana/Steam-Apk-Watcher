package com.valvesoftware.android.steam.community;

import android.util.Log;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/* loaded from: classes.dex */
class DevHttpsTrustManager implements X509TrustManager {
    private static final X509Certificate[] _AcceptedIssuers = new X509Certificate[0];
    private static TrustManager[] trustManagers;

    @Override // javax.net.ssl.X509TrustManager
    public void checkClientTrusted(X509Certificate[] x509CertificateArr, String str) throws CertificateException {
    }

    @Override // javax.net.ssl.X509TrustManager
    public void checkServerTrusted(X509Certificate[] x509CertificateArr, String str) throws CertificateException {
    }

    DevHttpsTrustManager() {
    }

    public static void allowSslToValveDev() {
        SSLContext sSLContext;
        NoSuchAlgorithmException e;
        KeyManagementException e2;
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() { // from class: com.valvesoftware.android.steam.community.DevHttpsTrustManager.1
            @Override // javax.net.ssl.HostnameVerifier
            public boolean verify(String str, SSLSession sSLSession) {
                return str != null && (str.contains("valvesoftware.com") || str.contains("valve.org"));
            }
        });
        if (trustManagers == null) {
            trustManagers = new TrustManager[]{new DevHttpsTrustManager()};
        }
        try {
            sSLContext = SSLContext.getInstance("TLS");
        } catch (KeyManagementException e3) {
            sSLContext = null;
            e2 = e3;
        } catch (NoSuchAlgorithmException e4) {
            sSLContext = null;
            e = e4;
        }
        try {
            sSLContext.init(null, trustManagers, new SecureRandom());
        } catch (KeyManagementException e5) {
            e2 = e5;
            Log.e(DevHttpsTrustManager.class.getSimpleName(), e2.toString());
            HttpsURLConnection.setDefaultSSLSocketFactory(sSLContext.getSocketFactory());
        } catch (NoSuchAlgorithmException e6) {
            e = e6;
            Log.e(DevHttpsTrustManager.class.getSimpleName(), e.toString());
            HttpsURLConnection.setDefaultSSLSocketFactory(sSLContext.getSocketFactory());
        }
        HttpsURLConnection.setDefaultSSLSocketFactory(sSLContext.getSocketFactory());
    }

    @Override // javax.net.ssl.X509TrustManager
    public X509Certificate[] getAcceptedIssuers() {
        return _AcceptedIssuers;
    }
}

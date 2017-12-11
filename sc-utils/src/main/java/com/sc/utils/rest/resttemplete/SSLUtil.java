package com.sc.utils.rest.resttemplete;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.cert.CertificateException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public final class SSLUtil {
    private static final Logger logger = LoggerFactory.getLogger(SSLUtil.class);
    private static final TrustManager[] UNQUESTIONING_TRUST_MANAGER = new TrustManager[]{
            new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws CertificateException {

                }
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

            }
    };

    private SSLUtil() {
        throw new UnsupportedOperationException("Do not instantiate libraries.");
    }

    public static void turnOffSslChecking() {
        try {
            final SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, UNQUESTIONING_TRUST_MANAGER, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            logger.error("关闭ssl检查失败!", e);
        }
    }

    public static void turnOnSslChecking() {
        try {
            SSLContext.getInstance("SSL").init(null, null, null);
        } catch (Exception e) {
            logger.error("SSLcontext初始化失败!", e);
        }

    }
}
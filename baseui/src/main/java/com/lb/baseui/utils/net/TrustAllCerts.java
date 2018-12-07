package com.lb.baseui.utils.net;

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

/**
 * @author LiuBo
 * @date 2018-05-04
 */
public class TrustAllCerts implements X509TrustManager {
    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) {}

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) {}

    @Override
    public X509Certificate[] getAcceptedIssuers() {return new X509Certificate[0];}
}

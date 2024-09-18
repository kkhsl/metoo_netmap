package com.metoo.sqlite.utils.panabit.PanabitSendMsgUtils;

import javax.net.ssl.*;
import java.security.cert.X509Certificate;

public class SSLUtils {

    /**
     * 设置全局的 SSL 配置，跳过证书验证。
     */
    public static void disableCertificateValidation() {
        try {
            // 创建一个不进行证书验证的 TrustManager
            TrustManager[] trustAll = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            // 安装全局的信任管理器
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAll, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // 安装全局的主机名验证器
            HostnameVerifier allHostsValid = (hostname, session) -> true;
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to disable certificate validation", e);
        }
    }
}

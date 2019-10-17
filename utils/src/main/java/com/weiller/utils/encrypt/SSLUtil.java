package com.weiller.utils.encrypt;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * SSLUtil @version 1.0
 *
 */
public class SSLUtil {

    public static SSLSocketFactory getSSLSocketFactory() {
        try {
            SSLContext context = SSLContext.getInstance("TLS");
            TrustManager[] trustManagers = { new MyX509TrustManager() };
            context.init(null, trustManagers, new SecureRandom());
            return context.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static X509TrustManager getX509TrustManager() {
        return new MyX509TrustManager();
    }

}

class MyX509TrustManager implements X509TrustManager {

    public void checkClientTrusted(X509Certificate[] chain, String authType) {

    }

    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        if (chain == null) {
            throw new CertificateException("checkServerTrusted: X509Certificate array is null");
        }
        if (chain.length < 1) {
            throw new CertificateException("checkServerTrusted: X509Certificate is empty");
        }
        if (!(null != authType && authType.equals("ECDHE_RSA"))) {
            throw new CertificateException("checkServerTrusted: AuthType is not ECDHE_RSA");
        }

//        //检查所有证书
//        try {
//            TrustManagerFactory factory = TrustManagerFactory.getInstance("X509");
//            factory.init((KeyStore) null);
//            for (TrustManager trustManager : factory.getTrustManagers()) {
//                ((X509TrustManager) trustManager).checkServerTrusted(chain, authType);
//            }
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (KeyStoreException e) {
//            e.printStackTrace();
//        }

        //获取本地证书中的信息
        String clientEncoded = "";
        String clientSubject = "";
        String clientIssUser = "";
        CertificateFactory certificateFactory = CertificateFactory.getInstance("PKCS12");//("X.509");
        InputStream inputStream = SSLUtil.class.getResourceAsStream("clent.p12");
        X509Certificate clientCertificate = (X509Certificate) certificateFactory.generateCertificate(inputStream);
        clientEncoded = new BigInteger(1, clientCertificate.getPublicKey().getEncoded()).toString(16);
        clientSubject = clientCertificate.getSubjectDN().getName();
        clientIssUser = clientCertificate.getIssuerDN().getName();

        //获取网络中的证书信息
        X509Certificate certificate = chain[0];
        PublicKey publicKey = certificate.getPublicKey();
        String serverEncoded = new BigInteger(1, publicKey.getEncoded()).toString(16);

        if (!clientEncoded.equals(serverEncoded)) {
            throw new CertificateException("server's PublicKey is not equals to client's PublicKey");
        }
        String subject = certificate.getSubjectDN().getName();
        if (!clientSubject.equals(subject)) {
            throw new CertificateException("server's subject is not equals to client's subject");
        }
        String issuser = certificate.getIssuerDN().getName();
        if (!clientIssUser.equals(issuser)) {
            throw new CertificateException("server's issuser is not equals to client's issuser");
        }
    }

    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}

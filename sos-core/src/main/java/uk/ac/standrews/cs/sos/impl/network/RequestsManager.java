package uk.ac.standrews.cs.sos.impl.network;

import com.mashape.unirest.http.Unirest;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import uk.ac.standrews.cs.sos.interfaces.network.Response;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;

/**
 * Singleton Class
 *
 * If HTTPS requests do not work:
 * https://docs.oracle.com/cd/E19509-01/820-3503/6nf1il6g1/index.html (THIS DID NOT WORK)
 * https://stackoverflow.com/questions/25084104/https-certificate-validation-fails-when-using-a-truststore (Should use already existing cacerts)
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RequestsManager {

    private static RequestsManager lazyInstance;

    static {
        try {

            TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws java.security.cert.CertificateException {

                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws java.security.cert.CertificateException {

                }

                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

            } };


            SSLContext sslcontext = SSLContext.getInstance("SSL");
            sslcontext.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslcontext.getSocketFactory());
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext);
            CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
            Unirest.setHttpClient(httpclient);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Ensure that this class cannot be instantiated by other classes by making the constructor private
    private RequestsManager() {

//        try {
////            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
//            SSLContext sslcontext = SSLContexts.custom()
//                    .loadTrustMaterial(null, new TrustSelfSignedStrategy())
//                    .build();
//
//            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext);
//            CloseableHttpClient httpclient = HttpClients.custom()
//                    .setSSLSocketFactory(sslsf)
//                    .build();
//            Unirest.setHttpClient(httpclient);
//
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (KeyStoreException e) {
//            e.printStackTrace();
//        } catch (KeyManagementException e) {
//            e.printStackTrace();
//        }
    }

    public static RequestsManager getInstance(){
        if(lazyInstance == null){
            lazyInstance = new RequestsManager();
        }
        return lazyInstance;
    }

    public void playAsyncRequest(AsyncRequest request) throws IOException {
        request.play();
    }

    public Response playSyncRequest(SyncRequest request) throws IOException {
        return request.play();
    }

}

package uk.ac.standrews.cs.sos.impl.network;

import uk.ac.standrews.cs.sos.interfaces.network.Response;

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
        System.setProperty("javax.net.ssl.trustStore","/Library/Internet Plug-Ins/JavaAppletPlugin.plugin/Contents/Home/lib/security/cacerts");
    }

    // Ensure that this class cannot be instantiated by other classes by making the constructor private
    private RequestsManager() {}

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

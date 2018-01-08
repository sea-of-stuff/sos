package uk.ac.standrews.cs.sos.network;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.options.Options;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.interfaces.network.Response;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

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

    // Ensure that this class cannot be instantiated by other classes by making the constructor private
    private RequestsManager() {}

    public static RequestsManager getInstance() {
        if(lazyInstance == null){
            Options.refresh(); // Make sure that Unirest instance is restarted
            lazyInstance = new RequestsManager();
        }
        return lazyInstance;
    }

    public Response playSyncRequest(SyncRequest request) throws IOException {
        return request.play();
    }

    public void shutdown() {

        try {
            lazyInstance = null;
            Unirest.shutdown();
        } catch (IOException e) {
            SOS_LOG.log(LEVEL.ERROR, "Unable to shutdown Requests Manager");
        }
    }

}

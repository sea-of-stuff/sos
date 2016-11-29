package uk.ac.standrews.cs.sos.network;

import okhttp3.OkHttpClient;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Singleton Class
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RequestsManager {

    private static RequestsManager lazyInstance;
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    // Ensure that this class cannot be instantiated by other classes
    private RequestsManager() {
    }

    public static RequestsManager getInstance(){
        if(lazyInstance == null){
            lazyInstance = new RequestsManager();
        }
        return lazyInstance;
    }

    public void playAsyncRequest(AsyncRequest request) throws IOException {
        request.play(client);
    }

    public Response playSyncRequest(SyncRequest request) throws IOException {
        return request.play(client);
    }

    public void shutdown() {
        SOS_LOG.log(LEVEL.INFO, "Attempt to shutdown RequestsManager");
        client.dispatcher().executorService().shutdown();
        SOS_LOG.log(LEVEL.INFO, "RequestsManager: shutdown finished");
    }

}

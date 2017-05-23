package uk.ac.standrews.cs.sos.impl.network;

import okhttp3.OkHttpClient;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.sos.interfaces.network.Response;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton Class
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RequestsManager {

    private static RequestsManager lazyInstance;

    // Useful resource about OkHttp ciphers
    // https://docs.google.com/spreadsheets/d/1C3FdZSlCBq_-qrVwG1KDIzNIB3Hyg_rKAcgmSzOsHyQ/edit#gid=0
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    // Ensure that this class cannot be instantiated by other classes by making the constructor private
    private RequestsManager() {
        SOS_LOG.log(LEVEL.INFO, "Init OkHttpClient logging");
        Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);
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

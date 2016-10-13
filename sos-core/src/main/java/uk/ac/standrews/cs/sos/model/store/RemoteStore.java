package uk.ac.standrews.cs.sos.model.store;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.network.*;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RemoteStore implements Store {

    private RequestsManager requestsManager;
    private Node node;
    private InputStream inputStream;

    public RemoteStore(RequestsManager requestsManager, Node node, InputStream inputStream) {
        this.requestsManager = requestsManager;
        this.node = node;
        this.inputStream = inputStream;
    }

    @Override
    public IGUID store() throws StorageException {

        URL url = null;
        try {
            url = SOSEP.STORAGE_POST_DATA(node);
            SyncRequest request = new SyncRequest(Method.POST, url);
            request.setBody(inputStream);

            Response response = requestsManager.playSyncRequest(request);

            System.out.println("REPLICATION--> " + response.getCode());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public LocationBundle getLocationBundle() {
        return null;
    }
}

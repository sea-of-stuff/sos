package uk.ac.standrews.cs.sos.model.store;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.network.Method;
import uk.ac.standrews.cs.sos.network.SOSEP;
import uk.ac.standrews.cs.sos.network.SyncRequest;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RemoteStore implements Store {

    private Node node;
    private InputStream inputStream;

    public RemoteStore(Node node, InputStream inputStream) {
        this.node = node;
        this.inputStream = inputStream;
    }

    @Override
    public IGUID store() throws StorageException {

        URL url = null;
        try {
            url = SOSEP.STORAGE_POST_DATA(node);
            SyncRequest request = new SyncRequest(Method.POST, url);

            System.out.println("REPLICATION--> " + request.getRespondeCode());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public LocationBundle getLocationBundle() {
        return null;
    }
}

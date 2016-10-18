package uk.ac.standrews.cs.sos.model.store;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.model.locations.SOSLocation;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.locations.bundles.PersistLocationBundle;
import uk.ac.standrews.cs.sos.network.*;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RemoteStore implements Store {

    private Node node;
    private InputStream inputStream;
    private IGUID dataGUID;

    public RemoteStore(Node node, InputStream inputStream) {
        this.node = node;
        this.inputStream = inputStream;
    }

    @Override
    public IGUID store() throws StorageException {

        URL url;
        try {
            url = SOSEP.STORAGE_POST_DATA(node);
            SyncRequest request = new SyncRequest(Method.POST, url);
            request.setBody(inputStream);

            Response response = RequestsManager.getInstance().playSyncRequest(request);

            dataGUID = null; // TODO - get this from response
            System.out.println("REPLICATION--> " + response.getCode());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public LocationBundle getLocationBundle() {
        try {
            return new PersistLocationBundle(new SOSLocation(node.getNodeGUID(), dataGUID));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }
}

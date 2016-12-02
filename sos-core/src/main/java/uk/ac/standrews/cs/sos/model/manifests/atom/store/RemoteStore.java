package uk.ac.standrews.cs.sos.model.manifests.atom.store;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.actors.protocol.DataReplication;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.model.locations.SOSLocation;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.locations.bundles.PersistLocationBundle;
import uk.ac.standrews.cs.sos.utils.Tuple;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Set;

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

        try {
            Tuple<IGUID, Tuple<Set<LocationBundle>, Set<Node>>> response = DataReplication.TransferDataRequest(inputStream, node);

            // TODO - get locations back!
            dataGUID = response.x;

        } catch (SOSProtocolException e) {
            e.printStackTrace();
        }

        return dataGUID;
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

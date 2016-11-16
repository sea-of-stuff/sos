package uk.ac.standrews.cs.sos.model.manifests.atom.store;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.locations.bundles.PersistLocationBundle;
import uk.ac.standrews.cs.sos.storage.LocalStorage;

import java.io.InputStream;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class StreamPersist extends StreamStore {

    public StreamPersist(IGUID nodeGUID, LocalStorage storage, InputStream inputStream) {
        super(nodeGUID, storage, inputStream);
    }

    @Override
    protected LocationBundle getBundle(Location location) {
        return new PersistLocationBundle(location);
    }

}

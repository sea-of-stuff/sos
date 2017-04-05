package uk.ac.standrews.cs.sos.impl.manifests.atom.store;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.impl.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.impl.locations.bundles.PersistLocationBundle;
import uk.ac.standrews.cs.sos.impl.storage.LocalStorage;
import uk.ac.standrews.cs.sos.model.Location;

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

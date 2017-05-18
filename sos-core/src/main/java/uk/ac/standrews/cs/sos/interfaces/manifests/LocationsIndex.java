package uk.ac.standrews.cs.sos.interfaces.manifests;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.sos.impl.locations.bundles.LocationBundle;

import java.io.IOException;
import java.util.Iterator;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface LocationsIndex {

    void addLocation(IGUID guid, LocationBundle locationBundle);

    Iterator<LocationBundle> findLocations(IGUID guid);

    void persist(IFile file) throws IOException;

}

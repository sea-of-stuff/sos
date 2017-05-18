package uk.ac.standrews.cs.sos.interfaces.manifests;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.sos.impl.locations.bundles.LocationBundle;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;

/**
 * Maps entities and locations
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface LocationsIndex extends Serializable {

    void addLocation(IGUID guid, LocationBundle locationBundle);

    Iterator<LocationBundle> findLocations(IGUID guid);

    void persist(IFile file) throws IOException;

}

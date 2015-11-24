package model.interfaces.components.manifests;

import model.implementations.utils.GUID;
import model.implementations.utils.Location;

import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface UnionManifest extends Manifest {

    /**
     *
     * @return
     */
    GUID getGUIDContent();

    /**
     *
     * @return
     */
    Collection<Location> getLocations();
}

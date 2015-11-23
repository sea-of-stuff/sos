package interfaces.components.manifests;

import interfaces.components.Location;

import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface AtomManifest extends Manifest {

    Collection<Location> getLocations();
}

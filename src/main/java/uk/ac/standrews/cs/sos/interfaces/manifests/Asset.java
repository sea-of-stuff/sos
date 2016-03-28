package uk.ac.standrews.cs.sos.interfaces.manifests;

import uk.ac.standrews.cs.utils.GUID;

import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Asset extends Manifest {

    GUID getVersionGUID();

    GUID getInvariantGUID();

    Collection<GUID> getPreviousManifests();

    Collection<GUID> getMetadata();
}

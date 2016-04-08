package uk.ac.standrews.cs.sos.interfaces.manifests;

import uk.ac.standrews.cs.IGUID;

import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Asset extends Manifest {

    IGUID getVersionGUID();

    IGUID getInvariantGUID();

    Collection<IGUID> getPreviousManifests();

    Collection<IGUID> getMetadata();
}

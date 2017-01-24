package uk.ac.standrews.cs.sos.interfaces.manifests;

import uk.ac.standrews.cs.IGUID;

import java.util.Set;

/**
 * An Asset Manifest represents a particular snapshot for some given data.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Asset extends Manifest {

    /**
     * This is the unique ID of this version.
     *
     * @return version GUID
     */
    IGUID getVersionGUID();

    /**
     * This is the unique ID for this asset.
     * Use the invariant GUID to link together multiple versions.
     *
     * @return invariant GUID
     */
    IGUID getInvariantGUID();

    /**
     * Return a list of GUIDs for the manifests under the previous relationship.
     *
     * @return a list of GUIDs for the previous versions
     */
    Set<IGUID> getPreviousVersions();

    /**
     * Returns a reference to this asset metadata
     *
     * @return GUID of the metadata
     */
    IGUID getMetadata();
}

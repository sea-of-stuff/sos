package uk.ac.standrews.cs.sos.model;

import uk.ac.standrews.cs.guid.IGUID;

import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Versionable extends Manifest {

    /**
     * This is the unique ID for this asset.
     * Use the invariant GUID to link together multiple versions.
     *
     * @return invariant GUID
     */
    IGUID invariant();

    /**
     * Return a list of GUIDs for the manifests under the previous relationship.
     *
     * @return a list of GUIDs for the previous versions
     */
    Set<IGUID> previous();
}

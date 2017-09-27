package uk.ac.standrews.cs.sos.model;

import uk.ac.standrews.cs.guid.IGUID;

import java.util.Set;

/**
 * A Version Manifest represents a particular snapshot for some given data.
 *
 * Example:
 *
 * {
 *  "Type" : "Version",
 *  "GUID" : "0e62db288f2b2bc4e1f6077f2e1732d9b4ad5547",
 *  "Invariant" : "7efcf9267222d8911cd7c7ef05a1b8a1904be5a8",
 *  "ContentGUID" : "32096c2e0eff33d844ee6d675407ace18289357d",
 *  "Previous" : [ "69b302db3c23765c3478c69433c108916760e5df" ],
 *  "Metadata" : "3f845edc76b7e892ddca1f6e290750fe805e7f00",
 *  "Signature" : "MC0CFFr94rYWRJRoUkWr5KHrh2d6+MOdAhUAgnN35l27nFKeKytaeezb8caP/VE="
 * }
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Version extends Manifest {

    /**
     * This is the unique ID of this version.
     * This method returns the same value as the method guid()
     *
     * @return version GUID
     */
    IGUID getVersionGUID();

    /**
     * This is the GUID of the content referred by this version
     *
     * @return content GUID
     */
    IGUID getContentGUID();

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
     * Returns a reference to this asset metadata.
     *
     * TODO - Set<IGUID>
     *
     * @return GUID of the metadata
     */
    IGUID getMetadata();
}

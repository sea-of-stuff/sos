package uk.ac.standrews.cs.sos.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.impl.json.VersionManifestDeserializer;
import uk.ac.standrews.cs.sos.impl.json.VersionManifestSerializer;

/**
 * A Version Manifest represents a particular snapshot for some given data.
 *
 * Example:
 *
 * {
 *  "Type" : "Version",
 *  "guid" : "0e62db288f2b2bc4e1f6077f2e1732d9b4ad5547",
 *  "invariant" : "7efcf9267222d8911cd7c7ef05a1b8a1904be5a8",
 *  "content" : "32096c2e0eff33d844ee6d675407ace18289357d",
 *  "previous" : [ "69b302db3c23765c3478c69433c108916760e5df" ],
 *  "metadata" : "3f845edc76b7e892ddca1f6e290750fe805e7f00",
 *  "signature" : "MC0CFFr94rYWRJRoUkWr5KHrh2d6+MOdAhUAgnN35l27nFKeKytaeezb8caP/VE="
 * }
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@JsonSerialize(using = VersionManifestSerializer.class)
@JsonDeserialize(using = VersionManifestDeserializer.class)
public interface Version extends Versionable, SignedManifest {

    /**
     * This is the unique ID of this version.
     * This method returns the same value as the method guid()
     *
     * @return version GUID
     */
    IGUID version();

    /**
     * This is the GUID of the content referred by this version
     *
     * @return content GUID
     */
    IGUID content();

    /**
     * Returns a reference to this asset metadata.
     *
     * TODO - Set<IGUID>
     *
     * @return GUID of the metadata
     */
    IGUID getMetadata();
}

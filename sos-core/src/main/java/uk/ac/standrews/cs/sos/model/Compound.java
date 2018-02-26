package uk.ac.standrews.cs.sos.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.impl.json.CompoundManifestDeserializer;
import uk.ac.standrews.cs.sos.impl.json.CompoundManifestSerializer;

import java.util.Set;

/**
 * A compound serves as an aggregator of atoms, compounds and versions.
 *
 * Example:
 *
 * {
 *  "Type" : "Compound",
 *  "compound_type" : "COLLECTION",
 *  "guid" : "cba74f828335fa96298f5efb3b2bf669ddc91031",
 *  "contents" : [
 *      {
 *          "label" : "folder",
 *          "guid" : "606c92e9707fd89d288c198b28f6cf3079be63bd"
 *      },
 *      {
 *          "guid" : "3ee75808c94ab7f53188e91a71cfa2bdfbcd1ebc"
 *      }
 *  ],
 *  "signature" : "MCwCFHE36niavy6cRQjEk6dd8oBlGkXXAhQQus9CIRZWCEoGDKydiuA6N/51Eg=="
 * }
 *
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@JsonSerialize(using = CompoundManifestSerializer.class)
@JsonDeserialize(using = CompoundManifestDeserializer.class)
public interface Compound extends SignedManifest {

    /**
     * Get the contents of this compound.
     *
     * @return the contents of this compound
     */
    Set<Content> getContents();

    /**
     * Get the content of the compound matching this label
     *
     * @param label of the content to get
     * @return content. Null if not found
     */
    Content getContent(String label);

    /**
     * Get the content of the compound matching this guid
     *
     * @param guid of the content to get
     * @return content. Null if not found
     */
    Content getContent(IGUID guid);

    /**
     * Get the type of compound.
     *
     * @return the compound type
     */
    CompoundType getCompoundType();
}

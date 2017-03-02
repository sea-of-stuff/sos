package uk.ac.standrews.cs.sos.interfaces.model;

import uk.ac.standrews.cs.sos.model.manifests.CompoundType;
import uk.ac.standrews.cs.sos.model.manifests.Content;

import java.util.Set;

/**
 * A compound serves as an aggregator of atoms, compounds and versions.
 *
 * Example:
 *
 * {
 *  "Type" : "Compound",
 *  "Compound_Type" : "COLLECTION",
 *  "GUID" : "cba74f828335fa96298f5efb3b2bf669ddc91031",
 *  "Content" : [
 *      {
 *          "Label" : "folder",
 *          "GUID" : "606c92e9707fd89d288c198b28f6cf3079be63bd"
 *      },
 *      {
 *          "Label" : "file.txt",
 *          "GUID" : "3ee75808c94ab7f53188e91a71cfa2bdfbcd1ebc"
 *      }
 *  ],
 *  "Signature" : "MCwCFHE36niavy6cRQjEk6dd8oBlGkXXAhQQus9CIRZWCEoGDKydiuA6N/51Eg=="
 * }
 *
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Compound extends Manifest {

    /**
     * Get the contents of this compound.
     *
     * @return the contents of this compound
     */
    Set<Content> getContents();

    /**
     * Get the type of compound.
     *
     * @return the compound type
     */
    CompoundType getCompoundType();
}

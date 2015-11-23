package model.interfaces.components.manifests;

import model.interfaces.components.identity.Signature;
import model.interfaces.components.utils.GUID;

/**
 * A manifest is an entity that describes assets, compounds and atoms by
 * recording metadata about them. A manifest is not updatable.
 * Manifests are publishable within the sea of stuff and allow discoverability
 * of assets, compounds and atoms. Manifests are represented as a
 * set of labels and values.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Manifest {

    /**
     * @return the GUID of this manifest
     */
    GUID getGUID();

    /**
     * @return the signature used for this manifest.
     *         Null if the manifest was not signed.
     */
    Signature getSignature();

    /**
     * @return the timestamp of creating for this manifest.
     */
    long getTimestamp();

    /**
     * @return a string representing the type of this manifest.
     */
    String getManifestType();
}

package model.interfaces.components.entities;

import model.implementations.utils.GUID;
import model.interfaces.SeaOfStuff;
import model.interfaces.components.identity.Signature;

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

     void setGuid(GUID guid);

     void setSignature(Signature signature);

     void setTimestamp(long timestamp);

     GUID getGUID();

     Signature getSignature();

     long getTimestamp();

     String getManifestType();

    /**
     * Verify this manifest's GUID against its content.
     *
     * {@link SeaOfStuff#verifyManifest(Manifest)}
     *
     * @return
     */
    boolean verify();

    /**
     *
     * Note that any java object inherits from Object and thus implements
     * the method {@link Object#toString()}. However, it is good design that
     * classes implementing Manifest DO implement this method.
     *
     * @return string representation of this manifest.
     */
    String toString();
}
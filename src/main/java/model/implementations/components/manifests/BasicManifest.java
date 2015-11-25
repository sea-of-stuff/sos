package model.implementations.components.manifests;

import model.implementations.utils.GUID;
import model.interfaces.SeaOfStuff;
import model.interfaces.components.entities.Manifest;
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
public abstract class BasicManifest implements Manifest {

    private GUID guid;
    private Signature signature;
    private long timestamp;
    private final String manifestType;

    protected BasicManifest(String manifestType) {
        this.manifestType = manifestType;
    }

    public void setGuid(GUID guid) {
        this.guid = guid;
    }

    public void setSignature(Signature signature) {
        this.signature = signature;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public GUID getGUID() {
        return this.guid;
    }

    public Signature getSignature() {
        return this.signature;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public String getManifestType() {
        return this.manifestType;
    }

    /**
     * Verify this manifest's GUID against its content.
     *
     * {@link SeaOfStuff#verifyManifest(Manifest)}
     *
     * @return
     */
    public abstract boolean verify();

    /**
     *
     * Note that any java object inherits from Object and thus implements
     * the method {@link Object#toString()}. However, it is good design that
     * classes implementing Manifest DO implement this method.
     *
     * @return string representation of this manifest.
     */
    public abstract String toString();
}

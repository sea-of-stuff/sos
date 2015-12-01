package model.implementations.components.manifests;

import model.implementations.utils.GUID;
import model.interfaces.SeaOfStuff;
import model.interfaces.components.entities.Manifest;
import model.interfaces.components.identity.Identity;
import model.interfaces.components.identity.Signature;

import java.time.Clock;

/**
 * The BasicManifest defines the base implementation for all other manifests.
 * This class implements some of the methods that can be generalised across all
 * other types of manifests. Manifests extending the BasicManifest MUST provide
 * implementations for the abstract methods defined in this class.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class BasicManifest implements Manifest {

    private GUID guid;
    private Signature signature;
    private long timestamp;
    private final String manifestType;

    /**
     * Constructor for a BasicManifest.
     * Initialise the type of manifest and the timestamp for this manifest.
     *
     * @param manifestType
     */
    protected BasicManifest(String manifestType) {
        this.manifestType = manifestType;
        this.timestamp = generateTimestamp();
    }

    /**
     * Generate the GUID of this manifest.
     *
     * @return the GUID of this manifest.
     */
    protected abstract GUID generateGUID();

    /**
     * Generate the signature for this manifest.
     *
     * @return the signature for this manifest.
     */
    protected abstract Signature generateSignature(Identity identity);

    /**
     * Verify this manifest's GUID against its content.
     *
     * {@link SeaOfStuff#verifyManifest(Manifest)}
     *
     * @return true if the GUID of the manifest matches the content.
     */
    @Override
    public abstract boolean verify();

    /**
     * Checks whether this manifest contains valid key-value entries.
     *
     * @return true if the manifest is valid.
     */
    @Override
    public abstract boolean isValid();

    /**
     * Transform the content of this manifest to a JSON representation.
     *
     * @return JSON representation of this manifest.
     */
    @Override
    public abstract String toJSON();

    @Override
    public GUID getGUID() {
        return this.guid;
    }

    /**
     * Get the signature for this manifest.
     *
     * @return the signature of this manifest.
     */
    @Override
    public Signature getSignature() {
        return this.signature;
    }

    private long generateTimestamp() {
        Clock clock = Clock.systemDefaultZone();
        return clock.millis();
    }

    @Override
    public long getTimestamp() {
        return this.timestamp;
    }

    @Override
    public String getManifestType() {
        return this.manifestType;
    }

}

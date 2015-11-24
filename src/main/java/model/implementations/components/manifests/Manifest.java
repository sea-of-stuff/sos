package model.implementations.components.manifests;

import model.implementations.utils.GUID;
import model.interfaces.components.identity.Signature;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class Manifest implements model.interfaces.components.manifests.Manifest {

    private GUID guid;
    private Signature signature;
    private long timestamp;
    private String manifestType;

    public void setGuid(GUID guid) {
        this.guid = guid;
    }

    public void setSignature(Signature signature) {
        this.signature = signature;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setManifestType(String manifestType) {
        this.manifestType = manifestType;
    }

    @Override
    public GUID getGUID() {
        return this.guid;
    }

    @Override
    public Signature getSignature() {
        return this.signature;
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

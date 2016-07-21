package uk.ac.standrews.cs.sos.model.manifests.builders;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.metadata.Metadata;

import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class VersionBuilder {

    private IGUID content;
    private Metadata metadata;
    private Collection<IGUID> metadataCollection;
    private IGUID invariant;
    private Collection<IGUID> previousCollection;

    private boolean invariantIsSet = false;
    private boolean metadataIsSet = false;
    private boolean prevIsSet = false;

    private boolean isMetadata = false;
    private boolean isMetadataCollection = false;

    public VersionBuilder(IGUID content) {
        this.content = content;
    }

    public VersionBuilder setInvariant(IGUID invariant) {
        if (!invariantIsSet) {
            this.invariant = invariant;
            invariantIsSet = true;
        }

        return this;
    }

    public VersionBuilder setMetadata(Metadata metadata) {
        if (!metadataIsSet) {
            this.metadata = metadata;
            isMetadata = true;
            metadataIsSet = true;
        }

        return this;
    }

    public VersionBuilder setMetadataCollection(Collection<IGUID> metadataCollection) {
        if (!metadataIsSet) {
            this.metadataCollection = metadataCollection;
            isMetadataCollection = true;
            metadataIsSet = true;
        }

        return this;
    }

    public VersionBuilder setPrevious(Collection<IGUID> previousCollection) {
        if (!prevIsSet) {
            this.previousCollection = previousCollection;
            prevIsSet = true;
        }

        return this;
    }

    public boolean hasInvariant() {
        return invariantIsSet;
    }

    public boolean hasMetadata() {
        return metadataIsSet;
    }

    public boolean hasPrevious() {
        return prevIsSet;
    }

    /**
     * Return true if the builder encapsulates a metadata object
     * @return
     */
    public boolean isMetadata() {
        return isMetadata;
    }

    /**
     * Return true if the builder encapsulates a metadata collection
     * @return
     */
    public boolean isMetadataCollection() {
        return isMetadataCollection;
    }

    public IGUID getContent() {
        return content;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public Collection<IGUID> getMetadataCollection() {
        return metadataCollection;
    }

    public IGUID getInvariant() {
        return invariant;
    }

    public Collection<IGUID> getPreviousCollection() {
        return previousCollection;
    }

}

package uk.ac.standrews.cs.sos.model.manifests.builders;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.interfaces.metadata.SOSMetadata;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class VersionBuilder {

    private IGUID content;
    private Set<SOSMetadata> metadata;
    private IGUID invariant;
    private Set<IGUID> previousCollection;

    private boolean invariantIsSet = false;
    private boolean metadataIsSet = false;
    private boolean prevIsSet = false;

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

    public VersionBuilder setMetadata(Set<SOSMetadata> metadata) {
        if (!metadataIsSet) {
            this.metadata = metadata;
            metadataIsSet = true;
        }

        return this;
    }

    public VersionBuilder setMetadata(SOSMetadata metadata) {
        if (!metadataIsSet) {
            this.metadata = new LinkedHashSet<>();
            this.metadata.add(metadata);
            metadataIsSet = true;
        }

        return this;
    }

    public VersionBuilder setPrevious(Set<IGUID> previousCollection) {
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

    public IGUID getContent() {
        return content;
    }

    public Set<IGUID> getMetadataCollection() {
        if (metadata == null) {
            return null;
        }

        Set<IGUID> retval = new LinkedHashSet<>();
        for(SOSMetadata meta:metadata) {
            try {
                retval.add(meta.guid());
            } catch (GUIDGenerationException e) {
                e.printStackTrace();
            }
        }
        return retval;
    }

    public IGUID getInvariant() {
        return invariant;
    }

    public Set<IGUID> getPreviousCollection() {
        return previousCollection;
    }

}

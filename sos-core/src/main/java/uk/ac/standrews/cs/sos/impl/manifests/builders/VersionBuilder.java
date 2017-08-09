package uk.ac.standrews.cs.sos.impl.manifests.builders;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.model.Metadata;
import uk.ac.standrews.cs.sos.model.Role;

import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class VersionBuilder extends ManifestBuilder {

    private IGUID content;
    private Metadata metadata;
    private IGUID invariant;
    private Set<IGUID> previousCollection;
    private AtomBuilder atomBuilder;
    private CompoundBuilder compoundBuilder;

    private boolean invariantIsSet = false;
    private boolean metadataIsSet = false;
    private boolean prevIsSet = false;

    public VersionBuilder() {}

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

    public VersionBuilder setAtomBuilder(AtomBuilder atomBuilder) {
        this.atomBuilder = atomBuilder;

        return this;
    }

    public VersionBuilder setCompoundBuilder(CompoundBuilder compoundBuilder) {
        this.compoundBuilder = compoundBuilder;

        return this;
    }

    public VersionBuilder setContent(IGUID content) {
        this.content = content;

        return this;
    }

    public IGUID getContent() {
        return content;
    }

    public IGUID getMetadataCollection() {
        if (metadata == null) {
            return null;
        }

        return metadata.guid();
    }

    public IGUID getInvariant() {
        return invariant;
    }

    public Set<IGUID> getPreviousCollection() {
        return previousCollection;
    }

    public AtomBuilder getAtomBuilder() {
        return atomBuilder;
    }

    public CompoundBuilder getCompoundBuilder() {
        return compoundBuilder;
    }

    @Override
    public VersionBuilder setRole(Role role) {
        this.role = role;

        return this;
    }
}

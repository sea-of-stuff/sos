/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module core.
 *
 * core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.sos.impl.datamodel.builders;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.impl.metadata.MetadataBuilder;
import uk.ac.standrews.cs.sos.model.Metadata;

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
    private MetadataBuilder metadataBuilder;

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

    public VersionBuilder setMetadataBuilder(MetadataBuilder metadataBuilder) {
        this.metadataBuilder = metadataBuilder;

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

    public MetadataBuilder getMetadataBuilder() {

        return metadataBuilder;
    }

    public boolean hasMetadataBuilder() {
        return metadataBuilder != null;
    }

}

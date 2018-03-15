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

import uk.ac.standrews.cs.sos.model.CompoundType;
import uk.ac.standrews.cs.sos.model.Content;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CompoundBuilder extends ManifestBuilder {

    private CompoundType type;
    private Set<Content> contents;
    private AtomBuilder atomBuilder;
    // TODO - chunk size param

    public CompoundBuilder() {
        type = CompoundType.COLLECTION;
        contents = new LinkedHashSet<>();
    }

    public CompoundType getType() {
        return type;
    }

    public CompoundBuilder setType(CompoundType type) {
        this.type = type;

        return this;
    }

    public Set<Content> getContents() {
        return contents;
    }

    public CompoundBuilder setContents(Set<Content> contents) {
        this.contents = contents;

        return this;
    }

    public CompoundBuilder setAtomBuilder(AtomBuilder atomBuilder) {
        if (type == CompoundType.DATA) {
            this.atomBuilder = atomBuilder;
        }

        return this;
    }

    public AtomBuilder getAtomBuilder() {
        return atomBuilder;
    }

}

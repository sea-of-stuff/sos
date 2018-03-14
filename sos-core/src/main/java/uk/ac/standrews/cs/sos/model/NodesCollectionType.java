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
package uk.ac.standrews.cs.sos.model;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public enum NodesCollectionType {

    LOCAL("LOCAL"),             // This local node
    SPECIFIED("SPECIFIED"),     // The collection is limited to the specified nodes
    ANY("ANY");                 // The collection is unlimited

    private final String text;

    NodesCollectionType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    /**
     * Parse a string to the corresponding ManifestType enum
     * @param value
     * @return
     */
    public static NodesCollectionType get(String value) {
        for(NodesCollectionType v : values())
            if(v.toString().equalsIgnoreCase(value)) return v;
        throw new IllegalArgumentException();
    }
}

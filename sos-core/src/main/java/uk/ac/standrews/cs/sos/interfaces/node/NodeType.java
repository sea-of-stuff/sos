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
package uk.ac.standrews.cs.sos.interfaces.node;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public enum NodeType {

    AGENT("agent"),
    CMS("cms"), // TODO - find another acronym because it conflicts with content management system
    MDS("mds"),
    MMS("mms"),
    NDS("nds"),
    RMS("rms"),
    STORAGE("storage"),
    UNKNOWN("unknown");

    private final String text;

    /**
     * Construct a new NodeType
     * This method is not visible outside of this class
     */
    NodeType(final String text) {
        this.text = text;
    }

    /**
     * Get the string representation of the enum NodeType
     * @return string for the NodeType
     */
    @Override
    public String toString() {
        return text;
    }

    /**
     * Parse a string to the corresponding NodeType enum
     * @param value string version of the enum
     * @return the enum NodeType
     */
    public static NodeType get(String value) {
        for(NodeType v : values())
            if(v.toString().equalsIgnoreCase(value)) return v;
        throw new IllegalArgumentException();
    }
}

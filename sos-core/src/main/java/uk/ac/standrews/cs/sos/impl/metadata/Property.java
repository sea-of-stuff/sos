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
package uk.ac.standrews.cs.sos.impl.metadata;

import uk.ac.standrews.cs.guid.IGUID;

/**
 * Triplet (type, key, value) for holding metadata information
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Property {

    private MetaType type;
    private String key;
    private String value_s;
    private long value_l;
    private double value_d;
    private boolean value_b;
    private IGUID value_g;
    private boolean encrypted = false;

    // Should be used only for encrypted meta properties!
    public Property(MetaType type, String key, String value) {
        this.type = type;
        this.key = key;
        this.value_s = value;
        this.encrypted = true;
    }

    public Property(String key, String value) {
        this.type = MetaType.STRING;
        this.key = key;
        this.value_s = value;
    }

    public Property(String key, long value) {
        this.type = MetaType.LONG;
        this.key = key;
        this.value_l = value;
    }

    public Property(String key, double value) {
        this.type = MetaType.DOUBLE;
        this.key = key;
        this.value_d = value;
    }

    public Property(String key, boolean value) {
        this.type = MetaType.BOOLEAN;
        this.key = key;
        this.value_b = value;
    }

    public Property(String key, IGUID value) {
        this.type = MetaType.GUID;
        this.key = key;
        this.value_g = value;
    }

    public MetaType getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public String getValue_s() {
        return value_s;
    }

    public long getValue_l() {
        return value_l;
    }

    public double getValue_d() {
        return value_d;
    }

    public boolean getValue_b() {
        return value_b;
    }

    public IGUID getValue_g() {
        return value_g;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    @Override
    public String toString() {

        String retval = type.toString();
        retval += "_";
        retval += key;
        retval += "_";

        if (encrypted) {
            retval += value_s;

        } else {

            switch (type) {
                case LONG:
                    retval += value_l;
                    break;
                case DOUBLE:
                    retval += value_d;
                    break;
                case BOOLEAN:
                    retval += value_b;
                    break;
                case STRING:
                    retval += value_s;
                    break;
                case GUID:
                    retval += value_g.toMultiHash();
                    break;
            }
        }

        return retval;
    }
}

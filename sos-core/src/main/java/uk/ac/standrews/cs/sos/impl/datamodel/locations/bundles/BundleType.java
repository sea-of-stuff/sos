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
package uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles;

import java.util.Objects;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class BundleType {

    private final String type;

    public BundleType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BundleType that = (BundleType) o;
        return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }
}

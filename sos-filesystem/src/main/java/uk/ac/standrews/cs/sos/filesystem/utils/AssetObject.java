/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module filesystem.
 *
 * filesystem is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * filesystem is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with filesystem. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.sos.filesystem.utils;

import uk.ac.standrews.cs.guid.IGUID;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AssetObject {

    private IGUID invariant;
    private IGUID version;

    public AssetObject(IGUID invariant, IGUID version) {
        this.invariant = invariant;
        this.version = version;
    }

    public IGUID getVersion() {
        return version;
    }

    public IGUID getInvariant() {
        return invariant;
    }
}

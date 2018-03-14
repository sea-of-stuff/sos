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
package uk.ac.standrews.cs.sos.impl.datamodel;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.SecureAtom;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SecureAtomManifest extends AtomManifest implements SecureAtom {

    private HashMap<IGUID, String> rolesToKeys;

    /**
     * Creates a valid secure atom manifest
     *
     * @param guid of protected atom
     * @param locations where protected atom is stored
     * @param rolesToKeys for protected atom
     */
    public SecureAtomManifest(IGUID guid, Set<LocationBundle> locations, HashMap<IGUID, String> rolesToKeys) {
        super(guid, locations);
        this.manifestType = ManifestType.ATOM_PROTECTED;
        this.rolesToKeys = rolesToKeys;
    }

    public SecureAtomManifest(IGUID guid, Set<LocationBundle> locations) {
        super(guid, locations);
        this.manifestType = ManifestType.ATOM_PROTECTED;
        this.rolesToKeys = new LinkedHashMap<>();
    }

    @Override
    public HashMap<IGUID, String> keysRoles() {

        return rolesToKeys;
    }

    @Override
    public void setKeysRoles(HashMap<IGUID, String> keysRoles) {
        this.rolesToKeys = keysRoles;
    }

    @Override
    public void addKeyRole(IGUID role, String encryptedKey) {
        this.rolesToKeys.put(role, encryptedKey);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SecureAtomManifest that = (SecureAtomManifest) o;
        return Objects.equals(rolesToKeys, that.rolesToKeys);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), rolesToKeys);
    }
}

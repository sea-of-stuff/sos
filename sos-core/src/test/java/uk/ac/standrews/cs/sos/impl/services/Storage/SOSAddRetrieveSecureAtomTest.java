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
package uk.ac.standrews.cs.sos.impl.services.Storage;

import org.apache.commons.io.IOUtils;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.usro.RoleImpl;
import uk.ac.standrews.cs.sos.impl.usro.UserImpl;
import uk.ac.standrews.cs.sos.model.*;
import uk.ac.standrews.cs.sos.utils.HelperTest;

import static org.testng.AssertJUnit.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSAddRetrieveSecureAtomTest extends StorageServiceTest {

    @Test
    public void basicAddSecureAtomTest() throws Exception {

        User user = new UserImpl("TEST_ADD_SECURE_ATOM");
        Role role = new RoleImpl(user, "ROLE_TEST_ADD_SECURE_ATOM");

        Location location = HelperTest.createDummyDataFile(localStorage);

        AtomBuilder clearDataBuilder = new AtomBuilder()
                .setLocation(location);
        Atom atomManifest = storageService.addAtom(clearDataBuilder);
        assertNotNull(atomManifest.getData());

        AtomBuilder builder = (AtomBuilder) new AtomBuilder()
                .setLocation(location)
                .setRole(role)
                .setProtectFlag(true);
        SecureAtom secureAtomManifest = (SecureAtom) storageService.addAtom(builder);

        assertNotNull(secureAtomManifest.getData());
        assertFalse(IOUtils.contentEquals(secureAtomManifest.getData().getInputStream(), location.getSource()));

        assertNotSame(secureAtomManifest.guid(), atomManifest.guid());
        assertNotSame(secureAtomManifest.getData(), atomManifest.getData());
    }

    @Test
    public void readSecureAtomDataTest() throws Exception {

        User user = new UserImpl("TEST_ADD_SECURE_ATOM");
        Role role = new RoleImpl(user, "ROLE_TEST_ADD_SECURE_ATOM");

        Location location = HelperTest.createDummyDataFile(localStorage);

        AtomBuilder builder = (AtomBuilder) new AtomBuilder()
                .setLocation(location)
                .setRole(role)
                .setProtectFlag(true);
        SecureAtom secureAtomManifest = (SecureAtom) storageService.addAtom(builder);

        // TODO - storage.getData(role)
    }

}

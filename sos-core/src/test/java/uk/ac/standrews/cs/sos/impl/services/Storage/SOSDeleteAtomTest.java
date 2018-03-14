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

import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.sos.exceptions.manifest.AtomNotFoundException;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles.BundleTypes;
import uk.ac.standrews.cs.sos.model.Atom;
import uk.ac.standrews.cs.sos.model.Location;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.utils.HelperTest;

import static org.testng.AssertJUnit.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSDeleteAtomTest extends StorageServiceTest {

    @Test (expectedExceptions = AtomNotFoundException.class)
    public void deleteAtomDataTest() throws Exception {
        Location location = HelperTest.createDummyDataFile(localStorage);

        AtomBuilder builder = new AtomBuilder()
                .setLocation(location)
                .setBundleType(BundleTypes.PERSISTENT)
                .setSetLocationAndProvenance(false);
        Atom manifest = storageService.addAtom(builder);
        assertEquals(manifest.getType(), ManifestType.ATOM);

        storageService.deleteAtom(manifest.guid());
        storageService.getAtomContent(manifest);
    }

    @Test (expectedExceptions = AtomNotFoundException.class)
    public void deleteAtomAlreadyDeletedTest() throws Exception {
        Location location = HelperTest.createDummyDataFile(localStorage);

        AtomBuilder builder = new AtomBuilder()
                .setLocation(location)
                .setBundleType(BundleTypes.PERSISTENT)
                .setSetLocationAndProvenance(false);
        Atom manifest = storageService.addAtom(builder);
        assertEquals(manifest.getType(), ManifestType.ATOM);

        storageService.deleteAtom(manifest.guid());
        storageService.deleteAtom(manifest.guid());
    }

    @Test (expectedExceptions = AtomNotFoundException.class)
    public void deleteAtomNotAddedTest() throws Exception {

        storageService.deleteAtom(GUIDFactory.generateRandomGUID());
    }

}

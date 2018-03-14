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
package uk.ac.standrews.cs.sos.impl.services.ManifestData;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.HEADNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.impl.manifest.ManifestFactory;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Version;
import uk.ac.standrews.cs.sos.utils.ManifestUtils;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.testng.AssertJUnit.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSDeleteManifestTest extends ManifestDataServiceTest {

    @Test
    public void deleteAddedManifestTest() throws ManifestPersistException, ManifestNotFoundException {

        IGUID guid = GUIDFactory.generateRandomGUID();
        Manifest manifest = ManifestFactory.createAtomManifest(guid, new LinkedHashSet<>());

        manifestsDataService.addManifest(manifest);
        manifestsDataService.delete(guid);
        // No exception is thrown

        Set<IGUID> atoms = manifestsDataService.getManifests(ManifestType.ATOM);
        assertEquals(atoms.size(), 0);
    }

    @Test (expectedExceptions = ManifestNotFoundException.class)
    public void deleteAddedManifestWithRetrieveTest() throws ManifestPersistException, ManifestNotFoundException {

        IGUID guid = GUIDFactory.generateRandomGUID();
        Manifest manifest = ManifestFactory.createAtomManifest(guid, new LinkedHashSet<>());

        manifestsDataService.addManifest(manifest);
        manifestsDataService.delete(guid);
        manifestsDataService.getManifest(guid);
    }

    @Test (expectedExceptions = ManifestNotFoundException.class)
    public void deleteDeletedManifestTest() throws ManifestPersistException, ManifestNotFoundException {

        IGUID guid = GUIDFactory.generateRandomGUID();
        Manifest manifest = ManifestFactory.createAtomManifest(guid, new LinkedHashSet<>());

        manifestsDataService.addManifest(manifest);
        manifestsDataService.delete(guid);
        manifestsDataService.delete(guid);
    }

    @Test (expectedExceptions = ManifestNotFoundException.class)
    public void deleteManifestNotAddedTest() throws ManifestNotFoundException {

        manifestsDataService.delete(GUIDFactory.generateRandomGUID());
    }

    @Test
    public void deleteLocationOfDeletedManifestTest() throws ManifestPersistException, ManifestNotFoundException {

        IGUID guid = GUIDFactory.generateRandomGUID();
        Manifest manifest = ManifestFactory.createAtomManifest(guid, new LinkedHashSet<>());

        manifestsDataService.addManifest(manifest);
        manifestsDataService.delete(guid);
        manifestsDataService.deleteLocalLocation(guid); // Does not throws any exception
    }

    @Test
    public void deleteAddedVersionManifestTest() throws Exception {

        Manifest manifest = ManifestUtils.createDummyVersion();

        manifestsDataService.addManifest(manifest);
        manifestsDataService.delete(manifest.guid());
        // No exception is thrown

        Set<IGUID> assetInvariants = manifestsDataService.getManifests(ManifestType.VERSION);
        assertEquals(assetInvariants.size(), 0);
    }

    @Test (expectedExceptions = ManifestNotFoundException.class)
    public void deleteAddedVersionManifestWithRetrieveTest() throws Exception {

        Manifest manifest = ManifestUtils.createDummyVersion();

        manifestsDataService.addManifest(manifest);
        manifestsDataService.delete(manifest.guid());
        manifestsDataService.getManifest(manifest.guid());
    }

    @Test (expectedExceptions = HEADNotFoundException.class)
    public void checkHeadIsDeletedTest() throws Exception {

        Version manifest = ManifestUtils.createDummyVersion();

        manifestsDataService.addManifest(manifest);
        manifestsDataService.getHead(manifest.invariant());

        manifestsDataService.delete(manifest.guid());
        manifestsDataService.getHead(manifest.invariant());
    }

}

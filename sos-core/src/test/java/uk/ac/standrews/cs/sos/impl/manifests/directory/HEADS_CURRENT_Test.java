package uk.ac.standrews.cs.sos.impl.manifests.directory;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.CommonTest;
import uk.ac.standrews.cs.sos.exceptions.manifest.CURRENTNotFoundException;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsDirectory;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.Version;
import uk.ac.standrews.cs.sos.utils.ManifestUtils;
import uk.ac.standrews.cs.sos.utils.UserRoleUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.testng.Assert.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class HEADS_CURRENT_Test extends CommonTest {

    void basicHeadTest(ManifestsDirectory directory) throws Exception {

        Version versionManifest = ManifestUtils.createDummyVersion();
        directory.advanceHead(versionManifest);

        Set<IGUID> heads = directory.getHeads(versionManifest.getInvariantGUID());
        assertNotNull(heads);
        assertEquals(heads.size(), 1);

        assertTrue(heads.contains(versionManifest.getVersionGUID()));
    }

    void advanceHeadTest(ManifestsDirectory directory) throws Exception {

        Version versionManifest = ManifestUtils.createDummyVersion();
        directory.advanceHead(versionManifest);

        // Create new version for same asset and advance the head
        Version newVersionManifest = ManifestUtils.createDummyVersion(GUIDFactory.recreateGUID("456"), Collections.singleton(versionManifest.guid()), versionManifest.getInvariantGUID());

        directory.advanceHead(newVersionManifest);

        Set<IGUID> heads = directory.getHeads(versionManifest.getInvariantGUID());
        assertNotNull(heads);
        assertEquals(heads.size(), 1);

        assertTrue(heads.contains(newVersionManifest.guid()));
    }

    void multipleHeadsTest(ManifestsDirectory directory) throws Exception {

        Version versionManifest = ManifestUtils.createDummyVersion();
        Version siblingVersionManifest = ManifestUtils.createDummyVersion(GUIDFactory.generateRandomGUID(), versionManifest.getInvariantGUID());

        directory.advanceHead(versionManifest);
        directory.advanceHead(siblingVersionManifest);

        Set<IGUID> heads = directory.getHeads(versionManifest.getInvariantGUID());
        assertNotNull(heads);
        assertEquals(heads.size(), 2);

        assertTrue(heads.contains(versionManifest.guid()));
        assertTrue(heads.contains(siblingVersionManifest.guid()));
    }

    void advanceMultipleHeadsTest(ManifestsDirectory directory) throws Exception {

        Version versionManifest = ManifestUtils.createDummyVersion();
        Version siblingVersionManifest = ManifestUtils.createDummyVersion(GUIDFactory.generateRandomGUID(), versionManifest.getInvariantGUID());

        directory.advanceHead(versionManifest);
        directory.advanceHead(siblingVersionManifest);

        // Create new version for same asset and advance the head
        Version newVersionManifest = ManifestUtils.createDummyVersion(GUIDFactory.recreateGUID("789"),
                new HashSet<>(Arrays.asList(versionManifest.guid(), siblingVersionManifest.guid())),
                versionManifest.getInvariantGUID());

        directory.advanceHead(newVersionManifest);

        Set<IGUID> heads = directory.getHeads(versionManifest.getInvariantGUID());
        assertNotNull(heads);
        assertEquals(heads.size(), 1);

        assertTrue(heads.contains(newVersionManifest.guid()));
    }

    void basicCurrentTest(ManifestsDirectory directory) throws Exception, CURRENTNotFoundException {

        Role role = UserRoleUtils.BareRoleMock();
        Version versionManifest = ManifestUtils.createDummyVersion();

        directory.setCurrent(role, versionManifest);

        IGUID current = directory.getCurrent(role, versionManifest.getInvariantGUID());
        assertEquals(current, versionManifest.guid());
    }

    void basicMultiCurrentSameVersionTest(ManifestsDirectory directory) throws Exception, CURRENTNotFoundException {

        Role role = UserRoleUtils.BareRoleMock();
        Role otherRole = UserRoleUtils.BareRoleMock();
        Version versionManifest = ManifestUtils.createDummyVersion();

        directory.setCurrent(role, versionManifest);
        directory.setCurrent(otherRole, versionManifest);

        IGUID current = directory.getCurrent(role, versionManifest.getInvariantGUID());
        assertEquals(current, versionManifest.guid());

        IGUID otherCurrent = directory.getCurrent(otherRole, versionManifest.getInvariantGUID());
        assertEquals(otherCurrent, versionManifest.guid());
    }

    void basicMultiCurrentDifferentVersionTest(ManifestsDirectory directory) throws Exception, CURRENTNotFoundException {

        Role role = UserRoleUtils.BareRoleMock();
        Role otherRole = UserRoleUtils.BareRoleMock();
        Version versionManifest = ManifestUtils.createDummyVersion();
        Version otherVersionManifest = ManifestUtils.createDummyVersion();

        directory.setCurrent(role, versionManifest);
        directory.setCurrent(otherRole, otherVersionManifest);

        IGUID current = directory.getCurrent(role, versionManifest.getInvariantGUID());
        assertEquals(current, versionManifest.guid());

        IGUID otherCurrent = directory.getCurrent(otherRole, otherVersionManifest.getInvariantGUID());
        assertEquals(otherCurrent, otherVersionManifest.guid());
    }

}

package uk.ac.standrews.cs.sos.impl.manifests.directory;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.CommonTest;
import uk.ac.standrews.cs.sos.exceptions.manifest.HEADNotFoundException;
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
public class TIPS_HEAD_Test extends CommonTest {

    void basicTipTest(ManifestsDirectory directory) throws Exception {

        Version versionManifest = ManifestUtils.createDummyVersion();
        directory.advanceTip(versionManifest);

        Set<IGUID> tips = directory.getTips(versionManifest.getInvariantGUID());
        assertNotNull(tips);
        assertEquals(tips.size(), 1);

        assertTrue(tips.contains(versionManifest.getVersionGUID()));
    }

    void advanceTipTest(ManifestsDirectory directory) throws Exception {

        Version versionManifest = ManifestUtils.createDummyVersion();
        directory.advanceTip(versionManifest);

        // Create new version for same asset and advance the tip
        Version newVersionManifest = ManifestUtils.createDummyVersion(GUIDFactory.recreateGUID("456"), Collections.singleton(versionManifest.guid()), versionManifest.getInvariantGUID());

        directory.advanceTip(newVersionManifest);

        Set<IGUID> tips = directory.getTips(versionManifest.getInvariantGUID());
        assertNotNull(tips);
        assertEquals(tips.size(), 1);

        assertTrue(tips.contains(newVersionManifest.guid()));
    }

    void multipleTipsTest(ManifestsDirectory directory) throws Exception {

        Version versionManifest = ManifestUtils.createDummyVersion();
        Version siblingVersionManifest = ManifestUtils.createDummyVersion(GUIDFactory.generateRandomGUID(), versionManifest.getInvariantGUID());

        directory.advanceTip(versionManifest);
        directory.advanceTip(siblingVersionManifest);

        Set<IGUID> tips = directory.getTips(versionManifest.getInvariantGUID());
        assertNotNull(tips);
        assertEquals(tips.size(), 2);

        assertTrue(tips.contains(versionManifest.guid()));
        assertTrue(tips.contains(siblingVersionManifest.guid()));
    }

    void advanceMultipleTipsTest(ManifestsDirectory directory) throws Exception {

        Version versionManifest = ManifestUtils.createDummyVersion();
        Version siblingVersionManifest = ManifestUtils.createDummyVersion(GUIDFactory.generateRandomGUID(), versionManifest.getInvariantGUID());

        directory.advanceTip(versionManifest);
        directory.advanceTip(siblingVersionManifest);

        // Create new version for same asset and advance the tip
        Version newVersionManifest = ManifestUtils.createDummyVersion(GUIDFactory.recreateGUID("789"),
                new HashSet<>(Arrays.asList(versionManifest.guid(), siblingVersionManifest.guid())),
                versionManifest.getInvariantGUID());

        directory.advanceTip(newVersionManifest);

        Set<IGUID> tips = directory.getTips(versionManifest.getInvariantGUID());
        assertNotNull(tips);
        assertEquals(tips.size(), 1);

        assertTrue(tips.contains(newVersionManifest.guid()));
    }

    void basicHeadTest(ManifestsDirectory directory) throws Exception, HEADNotFoundException {

        Role role = UserRoleUtils.BareRoleMock();
        Version versionManifest = ManifestUtils.createDummyVersion();

        directory.setHead(role, versionManifest);

        IGUID head = directory.getHead(role, versionManifest.getInvariantGUID());
        assertEquals(head, versionManifest.guid());
    }

    void basicMultiHeadSameVersionTest(ManifestsDirectory directory) throws Exception, HEADNotFoundException {

        Role role = UserRoleUtils.BareRoleMock();
        Role otherRole = UserRoleUtils.BareRoleMock();
        Version versionManifest = ManifestUtils.createDummyVersion();

        directory.setHead(role, versionManifest);
        directory.setHead(otherRole, versionManifest);

        IGUID head = directory.getHead(role, versionManifest.getInvariantGUID());
        assertEquals(head, versionManifest.guid());

        IGUID otherHead = directory.getHead(otherRole, versionManifest.getInvariantGUID());
        assertEquals(otherHead, versionManifest.guid());
    }

    void basicMultiHeadDifferentVersionTest(ManifestsDirectory directory) throws Exception, HEADNotFoundException {

        Role role = UserRoleUtils.BareRoleMock();
        Role otherRole = UserRoleUtils.BareRoleMock();
        Version versionManifest = ManifestUtils.createDummyVersion();
        Version otherVersionManifest = ManifestUtils.createDummyVersion();

        directory.setHead(role, versionManifest);
        directory.setHead(otherRole, otherVersionManifest);

        IGUID head = directory.getHead(role, versionManifest.getInvariantGUID());
        assertEquals(head, versionManifest.guid());

        IGUID otherHead = directory.getHead(otherRole, otherVersionManifest.getInvariantGUID());
        assertEquals(otherHead, otherVersionManifest.guid());
    }

    void noDuplicatesInTip(ManifestsDirectory directory) throws Exception {

        Version versionManifest = ManifestUtils.createDummyVersion();
        directory.advanceTip(versionManifest);
        directory.advanceTip(versionManifest);
        directory.advanceTip(versionManifest);
        directory.advanceTip(versionManifest);

        Set<IGUID> tips = directory.getTips(versionManifest.getInvariantGUID());
        assertNotNull(tips);
        assertEquals(tips.size(), 1);

        assertTrue(tips.contains(versionManifest.getVersionGUID()));
    }

}

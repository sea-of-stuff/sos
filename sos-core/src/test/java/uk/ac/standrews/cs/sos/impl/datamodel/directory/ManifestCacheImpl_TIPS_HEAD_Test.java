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
package uk.ac.standrews.cs.sos.impl.datamodel.directory;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.HEADNotFoundException;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsIndex;
import uk.ac.standrews.cs.sos.model.Version;
import uk.ac.standrews.cs.sos.utils.ManifestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.testng.Assert.*;
import static uk.ac.standrews.cs.sos.constants.Internals.GUID_ALGORITHM;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestCacheImpl_TIPS_HEAD_Test {

    @Test
    public void basicTipTest() throws Exception {
        ManifestsIndex index = new ManifestsIndexImpl();

        Version versionManifest = ManifestUtils.createDummyVersion();
        index.advanceTip(versionManifest);

        Set<IGUID> tips = index.getTips(versionManifest.invariant());
        assertNotNull(tips);
        assertEquals(tips.size(), 1);

        assertTrue(tips.contains(versionManifest.version()));
    }

    @Test
    public void advanceTipTest() throws Exception {
        ManifestsIndex index = new ManifestsIndexImpl();

        Version versionManifest = ManifestUtils.createDummyVersion();
        index.advanceTip(versionManifest);

        // Create new version for same asset and advance the tip
        Version newVersionManifest = ManifestUtils.createDummyVersion(GUIDFactory.generateRandomGUID(GUID_ALGORITHM), Collections.singleton(versionManifest.guid()), versionManifest.invariant());

        index.advanceTip(newVersionManifest);

        Set<IGUID> tips = index.getTips(versionManifest.invariant());
        assertNotNull(tips);
        assertEquals(tips.size(), 1);

        assertTrue(tips.contains(newVersionManifest.guid()));
    }

    @Test
    public void multipleTipsTest() throws Exception {
        ManifestsIndex index = new ManifestsIndexImpl();

        Version versionManifest = ManifestUtils.createDummyVersion();
        Version siblingVersionManifest = ManifestUtils.createDummyVersion(GUIDFactory.generateRandomGUID(GUID_ALGORITHM), versionManifest.invariant());

        index.advanceTip(versionManifest);
        index.advanceTip(siblingVersionManifest);

        Set<IGUID> tips = index.getTips(versionManifest.invariant());
        assertNotNull(tips);
        assertEquals(tips.size(), 2);

        assertTrue(tips.contains(versionManifest.guid()));
        assertTrue(tips.contains(siblingVersionManifest.guid()));
    }

    @Test
    public void advanceMultipleTipsTest() throws Exception {
        ManifestsIndex index = new ManifestsIndexImpl();

        Version versionManifest = ManifestUtils.createDummyVersion();
        Version siblingVersionManifest = ManifestUtils.createDummyVersion(GUIDFactory.generateRandomGUID(GUID_ALGORITHM), versionManifest.invariant());

        index.advanceTip(versionManifest);
        index.advanceTip(siblingVersionManifest);

        // Create new version for same asset and advance the tip
        Version newVersionManifest = ManifestUtils.createDummyVersion(GUIDFactory.generateRandomGUID(GUID_ALGORITHM),
                new HashSet<>(Arrays.asList(versionManifest.guid(), siblingVersionManifest.guid())),
                versionManifest.invariant());

        index.advanceTip(newVersionManifest);

        Set<IGUID> tips = index.getTips(versionManifest.invariant());
        assertNotNull(tips);
        assertEquals(tips.size(), 1);

        assertTrue(tips.contains(newVersionManifest.guid()));
    }

    @Test
    public void basicHeadTest() throws Exception {
        ManifestsIndex index = new ManifestsIndexImpl();

        Version versionManifest = ManifestUtils.createDummyVersion();
        index.setHead(versionManifest);

        IGUID head = index.getHead(versionManifest.invariant());
        assertEquals(head, versionManifest.guid());
    }

    @Test
    public void basicOnlyOneHeadSameVersionTest() throws Exception {
        ManifestsIndex index = new ManifestsIndexImpl();

        Version versionManifest = ManifestUtils.createDummyVersion();

        index.setHead(versionManifest);
        index.setHead(versionManifest);

        IGUID head = index.getHead(versionManifest.invariant());
        assertEquals(head, versionManifest.guid());
    }

    @Test
    public void basicMultiHeadDifferentVersionTest() throws Exception {
        ManifestsIndex index = new ManifestsIndexImpl();

        Version versionManifest = ManifestUtils.createDummyVersion();
        Version otherVersionManifest = ManifestUtils.createDummyVersion();

        index.setHead(versionManifest);
        index.setHead(otherVersionManifest);

        IGUID head = index.getHead(versionManifest.invariant());
        assertEquals(head, versionManifest.guid());

        IGUID otherHead = index.getHead(otherVersionManifest.invariant());
        assertEquals(otherHead, otherVersionManifest.guid());
    }

    @Test
    public void noDuplicatesInTip() throws Exception {
        ManifestsIndex index = new ManifestsIndexImpl();

        Version versionManifest = ManifestUtils.createDummyVersion();
        index.advanceTip(versionManifest);
        index.advanceTip(versionManifest);
        index.advanceTip(versionManifest);
        index.advanceTip(versionManifest);

        Set<IGUID> tips = index.getTips(versionManifest.invariant());
        assertNotNull(tips);
        assertEquals(tips.size(), 1);

        assertTrue(tips.contains(versionManifest.version()));
    }

    @Test
    public void basicTipDeletedTest() throws Exception {
        ManifestsIndex index = new ManifestsIndexImpl();

        Version versionManifest = ManifestUtils.createDummyVersion();
        index.advanceTip(versionManifest);

        index.delete(versionManifest);

        Set<IGUID> tips = index.getTips(versionManifest.invariant());
        assertNotNull(tips);
        assertEquals(tips.size(), 0);
    }

    @Test (expectedExceptions = HEADNotFoundException.class)
    public void basicHeadDeletedTest() throws Exception {
        ManifestsIndex index = new ManifestsIndexImpl();

        Version versionManifest = ManifestUtils.createDummyVersion();
        index.setHead(versionManifest);

        index.delete(versionManifest);
        index.getHead(versionManifest.invariant());
    }

}

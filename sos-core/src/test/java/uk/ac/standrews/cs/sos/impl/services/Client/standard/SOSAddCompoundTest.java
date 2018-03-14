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
package uk.ac.standrews.cs.sos.impl.services.Client.standard;

import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.datamodel.CompoundManifest;
import uk.ac.standrews.cs.sos.impl.datamodel.ContentImpl;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.CompoundBuilder;
import uk.ac.standrews.cs.sos.model.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static uk.ac.standrews.cs.sos.constants.Internals.GUID_ALGORITHM;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSAddCompoundTest extends AgentTest {

    @Override
    @BeforeMethod
    public void setUp(Method testMethod) throws Exception {
        super.setUp(testMethod);
    }

    @Override
    @AfterMethod
    public void tearDown() throws InterruptedException, DataStorageException, IOException {
        super.tearDown();
    }

    @Test
    public void testAddCompound() throws Exception {
        Content cat = new ContentImpl("cat", GUIDFactory.generateRandomGUID(GUID_ALGORITHM));
        Set<Content> contents = new LinkedHashSet<>();
        contents.add(cat);

        CompoundBuilder compoundBuilder = new CompoundBuilder()
                .setType(CompoundType.DATA)
                .setContents(contents);
        Compound manifest = agent.addCompound(compoundBuilder);
        Assert.assertEquals(manifest.getType(), ManifestType.COMPOUND);

        Manifest retrievedManifest = agent.getManifest(manifest.guid());
        assertEquals(retrievedManifest.getType(), ManifestType.COMPOUND);

        Set<Content> retrievedContents = ((CompoundManifest) retrievedManifest).getContents();
        Iterator<Content> iterator = retrievedContents.iterator();
        assertEquals(cat, iterator.next());

        JSONAssert.assertEquals(manifest.toString(), retrievedManifest.toString(), true);
    }

    /**
     * Unable to verify the compound as the role has no private keys to generate the signature in the first place
     * @throws Exception
     */
    @Test
    public void testAddCompoundAndVerifyFails() throws Exception {
        Content cat = new ContentImpl("cat", GUIDFactory.generateRandomGUID(GUID_ALGORITHM));
        Set<Content> contents = new LinkedHashSet<>();
        contents.add(cat);

        CompoundBuilder compoundBuilder = new CompoundBuilder()
                .setType(CompoundType.DATA)
                .setContents(contents);
        Compound manifest = agent.addCompound(compoundBuilder);
        Manifest retrievedManifest = agent.getManifest(manifest.guid());

        Role role = localSOSNode.getUSRO().activeRole();
        boolean isVerified = agent.verifyManifestSignature(role, retrievedManifest);
        assertFalse(isVerified);
    }

}

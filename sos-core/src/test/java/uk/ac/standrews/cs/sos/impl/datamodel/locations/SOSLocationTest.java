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
package uk.ac.standrews.cs.sos.impl.datamodel.locations;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.utils.HelperTest;

import java.io.InputStream;
import java.net.MalformedURLException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static uk.ac.standrews.cs.sos.constants.Internals.GUID_ALGORITHM;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSLocationTest extends SetUpTest {

    private static IGUID NODE_GUID = GUIDFactory.generateRandomGUID(GUID_ALGORITHM);
    private static IGUID DATA_GUID = GUIDFactory.generateRandomGUID(GUID_ALGORITHM);

    @Test
    public void testGetURI() throws Exception {
        SOSLocation location = new SOSLocation(NODE_GUID, DATA_GUID);
        assertEquals(location.getURI().toString(), "sos://" +
                NODE_GUID.toMultiHash() + "/" +
                DATA_GUID.toMultiHash());
    }

    @Test
    public void testMakeURIFromString() throws Exception {
        SOSLocation location = new SOSLocation(NODE_GUID, DATA_GUID);
        SOSLocation stringLocation = new SOSLocation("sos://" +
                NODE_GUID.toMultiHash() + "/" +
                DATA_GUID.toMultiHash());
        assertEquals(stringLocation, location);
    }

    @Test
    public void testGetSource() throws Exception {
        HelperTest.createDummyDataFile(localStorage, DATA_GUID.toMultiHash());

        SOSLocation location = new SOSLocation(localSOSNode.guid(), DATA_GUID);
        InputStream inputStream = location.getSource();
        String retrieved = HelperTest.InputStreamToString(inputStream);

        assertTrue(retrieved.contains("The first line"));
        assertTrue(retrieved.contains("The second line"));
    }

    @Test (expectedExceptions = MalformedURLException.class)
    public void wrongURINoNodeGUIDTest() throws Exception {
        new SOSLocation("sos://" +
                "/" +
                DATA_GUID.toMultiHash());
    }

    @Test (expectedExceptions = MalformedURLException.class)
    public void wrongURINoDataGUIDTest() throws Exception {
        new SOSLocation("sos://" +
                NODE_GUID.toMultiHash() + "/");
    }

    @Test (expectedExceptions = MalformedURLException.class)
    public void wrongURINoSlashTest() throws Exception {
        new SOSLocation("sos://" +
                NODE_GUID.toMultiHash() +
                DATA_GUID.toMultiHash());
    }

}

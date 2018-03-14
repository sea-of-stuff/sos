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
package uk.ac.standrews.cs.sos.impl.protocol;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AtomReplicationSequentialTest extends AtomReplicationBaseTest {

    @Test
    public void basicMockServerTest() throws GUIDGenerationException, SOSProtocolException, NodeNotFoundException {
        super.basicMockServerTest(true);
    }

    @Test
    public void replicateToNoStorageNodeTest() throws GUIDGenerationException, SOSProtocolException, NodeNotFoundException {
        super.replicateToNoStorageNodeTest(true);
    }

    @Test
    public void replicateOnlyOnceTest() throws GUIDGenerationException, SOSProtocolException, NodeNotFoundException {
        super.replicateOnlyOnceTest(true);
    }

    @Test
    public void replicateOnlyOnceSecondTest() throws GUIDGenerationException, SOSProtocolException, NodeNotFoundException {
        super.replicateOnlyOnceSecondTest(true);
    }

    @Test
    public void replicateToSameNodeTwiceTest() throws GUIDGenerationException, SOSProtocolException, NodeNotFoundException {
        super.replicateToSameNodeTwiceTest(true);
    }

    @Test
    public void replicateSameDataTwiceTest() throws GUIDGenerationException, SOSProtocolException, NodeNotFoundException {
        super.replicateSameDataTwiceTest(true);
    }

    @Test
    public void basicTimeoutMockServerTest() throws GUIDGenerationException, SOSProtocolException, NodeNotFoundException {
        super.basicTimeoutMockServerTest(true);
    }
}
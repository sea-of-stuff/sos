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
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.model.Location;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class URILocationTest extends SetUpTest {

    @Test
    public void testConstructorLocalURL() throws URISyntaxException, IOException {
        Location location = new URILocation("/sos/data/test.txt");
        assertEquals(location.getURI().toString(), "file://localhost/sos/data/test.txt");
    }

    @Test
    public void testConstructorLocalFileURL() throws URISyntaxException, IOException {
        Location location = new URILocation("file:///sos/data/test.txt");
        assertEquals(location.getURI().toString(), "file://localhost/sos/data/test.txt");
    }

    @Test
    public void testConstructorURL() throws URISyntaxException, IOException {
        Location location = new URILocation("http://fakehost.co.uk/sos/data/test.txt");
        assertEquals(location.getURI().toString(), "http://fakehost.co.uk/sos/data/test.txt");
    }
}
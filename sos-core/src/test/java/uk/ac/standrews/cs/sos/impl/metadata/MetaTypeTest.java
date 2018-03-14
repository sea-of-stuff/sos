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
package uk.ac.standrews.cs.sos.impl.metadata;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.CommonTest;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class MetaTypeTest extends CommonTest {

    @Test
    public void metatypeToStringTest() {

        assertEquals(MetaType.LONG.toString(), "long");
        assertEquals(MetaType.DOUBLE.toString(), "double");
        assertEquals(MetaType.BOOLEAN.toString(), "boolean");
        assertEquals(MetaType.STRING.toString(), "string");
        assertEquals(MetaType.GUID.toString(), "guid");
        assertEquals(MetaType.ANY.toString(), "any");
    }
}

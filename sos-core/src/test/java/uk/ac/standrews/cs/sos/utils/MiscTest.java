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
package uk.ac.standrews.cs.sos.utils;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.CommonTest;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class MiscTest extends CommonTest {

    @Test
    public void isIntegerTest_1() {

        String a = "10";
        boolean isInteger = Misc.isIntegerNumber(a);
        assertTrue(isInteger);
    }

    @Test
    public void isIntegerTest_2() {

        String a = "0";
        boolean isInteger = Misc.isIntegerNumber(a);
        assertTrue(isInteger);
    }

    @Test
    public void isIntegerTest_3() {

        String a = "-10";
        boolean isInteger = Misc.isIntegerNumber(a);
        assertTrue(isInteger);
    }

    @Test
    public void isIntegerTest_4() {

        String a = "1234567890";
        boolean isInteger = Misc.isIntegerNumber(a);
        assertTrue(isInteger);
    }

    @Test
    public void isIntegerTest_5() {

        String a = "01";
        boolean isInteger = Misc.isIntegerNumber(a);
        assertTrue(isInteger);
    }

    @Test
    public void isIntegerTest_6() {

        String a = "1.2";
        boolean isInteger = Misc.isIntegerNumber(a);
        assertFalse(isInteger);
    }

    @Test
    public void isRealTest_1() {

        String a = "1.2";
        boolean isReal = Misc.isRealNumber(a);
        assertTrue(isReal);
    }

    @Test
    public void isRealTest_2() {

        String a = "-1.2";
        boolean isReal = Misc.isRealNumber(a);
        assertTrue(isReal);
    }

    @Test
    public void isRealTest_3() {

        String a = "0.0";
        boolean isReal = Misc.isRealNumber(a);
        assertTrue(isReal);
    }

    @Test
    public void isRealTest_4() {

        String a = "1";
        boolean isReal = Misc.isRealNumber(a);
        assertTrue(isReal);
    }

    @Test
    public void isBooleanTest_1() {

        String a = "true";
        boolean isBoolean = Misc.isBoolean(a);
        assertTrue(isBoolean);
    }

    @Test
    public void isBooleanTest_2() {

        String a = "false";
        boolean isBoolean = Misc.isBoolean(a);
        assertTrue(isBoolean);
    }

    @Test
    public void isBooleanTest_3() {

        String a = "TRUE";
        boolean isBoolean = Misc.isBoolean(a);
        assertTrue(isBoolean);
    }

    @Test
    public void isBooleanTest_4() {

        String a = "FALSE";
        boolean isBoolean = Misc.isBoolean(a);
        assertTrue(isBoolean);
    }
}

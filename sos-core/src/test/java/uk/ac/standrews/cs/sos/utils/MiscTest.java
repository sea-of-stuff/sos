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

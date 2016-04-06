/*
 * Created on Dec 12, 2004 at 5:24:27 PM.
 */
package uk.ac.standrews.cs.sos.model.utils;


import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.exceptions.utils.GUIDGenerationException;
import uk.ac.standrews.cs.utils.keys.KeyImpl;

import java.math.BigInteger;

import static org.testng.AssertJUnit.assertEquals;

/**
 * Test class for KeyImpl.
 * 
 * @author graham
 */
public class KeyImplTest {
    
    private static KeyImpl k1;
    private static KeyImpl k2;
    private static KeyImpl k3;
    private static KeyImpl k4;
    private static KeyImpl k5;
    private static KeyImpl k6;
    private static KeyImpl k7;
    private static KeyImpl k8;
    private static KeyImpl k9;


    static {
        try {
            k1 = new KeyImpl(new BigInteger("-1"));
            k2 = new KeyImpl(BigInteger.ZERO);
            k3 = new KeyImpl(BigInteger.ONE);
            k4 = new KeyImpl(new BigInteger("3247823487234"));
            k5 = new KeyImpl(KeyImpl.KEYSPACE_SIZE.subtract(BigInteger.ONE));
            k6 = new KeyImpl(KeyImpl.KEYSPACE_SIZE);
            k7 = new KeyImpl(KeyImpl.KEYSPACE_SIZE.add(BigInteger.ONE));
            k8 = new KeyImpl(KeyImpl.KEYSPACE_SIZE.add(KeyImpl.KEYSPACE_SIZE.add(BigInteger.ONE)));
            k9 = new KeyImpl(new BigInteger("-1").multiply(KeyImpl.KEYSPACE_SIZE.add(KeyImpl.KEYSPACE_SIZE)).add(BigInteger.ONE));

        } catch (GUIDGenerationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests whether the key integers are as expected. All should lie in range zero to keyspace_size - 1.
     */
    @Test
    public void testBigIntegerRepresentation() {
        
        // -1 should wrap to keyspace_size - 1.
        assertEquals(k1.bigIntegerRepresentation(), KeyImpl.KEYSPACE_SIZE.subtract(BigInteger.ONE));
        
        // Original integers were within range, so should remain the same.
        assertEquals(k2.bigIntegerRepresentation(), BigInteger.ZERO);
        assertEquals(k3.bigIntegerRepresentation(), BigInteger.ONE);
        assertEquals(k4.bigIntegerRepresentation(), new BigInteger("3247823487234"));
        assertEquals(k5.bigIntegerRepresentation(), KeyImpl.KEYSPACE_SIZE.subtract(BigInteger.ONE));
        
        // keyspace_size should wrap to 0.
        assertEquals(k6.bigIntegerRepresentation(), BigInteger.ZERO);
        
        // keyspace_size + 1 should wrap to 1.
        assertEquals(k7.bigIntegerRepresentation(), BigInteger.ONE);
        
        // 2 * keyspace_size + 1 should wrap to 1.
        assertEquals(k8.bigIntegerRepresentation(), BigInteger.ONE);
        
        // -2 * keyspace_size + 1 should wrap to 1.
        assertEquals(k9.bigIntegerRepresentation(), BigInteger.ONE);
    }
    
    /**
     * Tests whether the string representations are as expected. Should contain hex integers in range zero to keyspace size - 1.
     */
    @Test
    public void testToString() {
        
        assertEquals(k1.toString(), "ffffffffffffffffffffffffffffffffffffffff");
        assertEquals(k2.toString(), "0000000000000000000000000000000000000000");
        assertEquals(k3.toString(), "0000000000000000000000000000000000000001");
        assertEquals(k4.toString(), "000000000000000000000000000002f4315d8102");
        assertEquals(k5.toString(), "ffffffffffffffffffffffffffffffffffffffff");
        assertEquals(k6.toString(), "0000000000000000000000000000000000000000");
        assertEquals(k7.toString(), "0000000000000000000000000000000000000001");
        assertEquals(k8.toString(), "0000000000000000000000000000000000000001");
        assertEquals(k9.toString(), "0000000000000000000000000000000000000001");
    }

    /**
     * Tests whether key comparison works as expected.
     */
    @Test
    public void testCompareTo() {
        
        // k1 is the largest possible key, keyspace_size - 1.
        assertEquals(k1.compareTo(k2), 1);
        assertEquals(k1.compareTo(k3), 1);
        assertEquals(k1.compareTo(k4), 1);
        
        // k1 = k5.
        assertEquals(k1.compareTo(k5), 0);
        
        // k2 is the smallest possible key, zero.
        assertEquals(k2.compareTo(k1), -1);
        assertEquals(k2.compareTo(k3), -1);
        assertEquals(k2.compareTo(k4), -1);
        
        // k2 = k6.
        assertEquals(k2.compareTo(k6), 0);
        
        // Miscellaneous pairs.
        assertEquals(k4.compareTo(k1), -1);
        assertEquals(k4.compareTo(k2), 1);
        assertEquals(k4.compareTo(k3), 1);
        assertEquals(k4.compareTo(k4), 0);
    }

}

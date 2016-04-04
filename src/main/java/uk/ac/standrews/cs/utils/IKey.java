package uk.ac.standrews.cs.utils;

import java.math.BigInteger;

/**
 * Interface defining keys.
 *
 * @author sja7, al, stuart, graham
 */
public interface IKey extends Comparable {
    
    /**
     * @return a BigInteger representation of this key
     */
    BigInteger bigIntegerRepresentation();
    
    /**
     * @return a string representation of this key
     */
    String toString();
}

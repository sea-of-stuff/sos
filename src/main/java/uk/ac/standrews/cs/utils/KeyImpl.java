package uk.ac.standrews.cs.utils;

import uk.ac.standrews.cs.sos.exceptions.utils.GUIDGenerationException;

import java.math.BigInteger;

/**
 * Implementation of key.
 * 
 * @author stuart, al, graham, sja7
 * @author sic2 - removed p2p dependencies
 */
public class KeyImpl implements IGUID {

    //********************** Constants ***********************
    
    public static final int KEYLENGTH = 160;

    private static final BigInteger TWO = BigInteger.ONE.add(BigInteger.ONE);

    public static final BigInteger KEYSPACE_SIZE = TWO.pow(KEYLENGTH);
    
    /**
     * The radix used in converting the key's value to a string.
     */
    public static final int DEFAULT_TO_STRING_RADIX = 16;
    
    /**
     * The length of the key's value in digits.
     */
    public static final int DEFAULT_TO_STRING_LENGTH = 40;

    //************************ Fields ************************

    public BigInteger key_value;

    //********************* Constructors *********************
    
    /**
     * Default constructor for use in deserialization.
     */
    public KeyImpl () {
    	/* This constructor deliberately blank... */
    }
    
    /**
     * Creates a new key using the given value modulo the key space size.
     * 
     * @param key_value the value of the key
     */
    public KeyImpl(BigInteger key_value) throws GUIDGenerationException {
        try {
            this.key_value = key_value.remainder(KEYSPACE_SIZE);
            
            // Allow for negative key value.
            if (this.key_value.compareTo(BigInteger.ZERO) < 0)
                this.key_value = this.key_value.add(KEYSPACE_SIZE);
            
        } catch (Exception e) {
            throw new GUIDGenerationException();
        }
    }

    /**
     * Creates a new key using a string representation of a BigInteger to base DEFAULT_TO_STRING_RADIX.
     *
     * @param s the string value of the key
     * @see #DEFAULT_TO_STRING_RADIX
     */
    public KeyImpl(String s) throws GUIDGenerationException {
        this(new BigInteger(s, DEFAULT_TO_STRING_RADIX));
    }

    //*********************** Key Methods ************************

    /**
     * Returns the representation of this key.
     *
     * @return the representation of this key
     */
    public BigInteger bigIntegerRepresentation() {
        return key_value;
    }

    /**
     * Returns a string representation of the key value.
     *
     * @return a string representation of the key value using the default radix and length
     */
    public String toString() {
        return toString(DEFAULT_TO_STRING_RADIX, DEFAULT_TO_STRING_LENGTH);
    }

    /**
     * Returns a string representation of the key value.
     *
     * @param radix the radix
     * @return a string representation of the key value using the given radix
     */
    public String toString(int radix) {
        int bits_per_digit = RadixMethods.bitsNeededTORepresent(radix);
        int toStringLength = KEYLENGTH / bits_per_digit;

        return toString(radix, toStringLength);
    }

    /**
     * Returns a string representation of the key value.
     *
     * @param radix the radix
     * @param stringLength the length to which the key representation should be padded
     * @return a string representation of the key value using the given radix
     */
    public String toString(int radix, int stringLength) {
        StringBuffer result = new StringBuffer(key_value.toString(radix));
        while (result.length() < stringLength) result.insert(0, '0');
        return result.toString();
    }

    /**
     * Compares this key with another.
     *
     * @param o the key to compare
     * @return -1, 0, or 1 if the argument key is greater, equal to, or less
     *         than this node, respectively
     */
    public int compareTo(Object o) {
        try {
            IKey k = (IKey) o;
            return key_value.compareTo(k.bigIntegerRepresentation());
        } catch (ClassCastException e) {
            return 0;
        }
    }

    /**
     * Compares this key with another.
     *
     * @param o the key to compare
     * @return true if the argument key's representation is equal to that of this node
     */
    public boolean equals(Object o) {
        try {
            IKey k = (IKey) o;
            return key_value.equals(k.bigIntegerRepresentation());
        } catch (ClassCastException e) {
            return false;
        }
    }

    public int hashCode(){
        return toString().hashCode();
    }
}
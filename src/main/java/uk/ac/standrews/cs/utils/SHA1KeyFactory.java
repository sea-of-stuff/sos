/*
 * Created on 26-Oct-2004
 */
package uk.ac.standrews.cs.utils;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import uk.ac.standrews.cs.sos.exceptions.utils.GUIDGenerationException;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Provides various ways to generate keys.
 * 
 * @author stuart, graham
 */
public class SHA1KeyFactory {
	
	private static final int HEX_BASE = 16;
	
    /**
     * Prints out the digest in a form that can be easily compared to the test vectors. 
     */
    /*
    public static String toHex(byte[] bytes ) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length ; i++) { 
            char c1, c2;
            c1 = (char)((bytes[i] >>> 4) & 0xf);
            c2 = (char)(bytes[i] & 0xf);
            c1 = (char)((c1 > 9) ? 'a' + (c1 - 10) : '0' + c1);
            c2 = (char)((c2 > 9) ? 'a' + (c2 - 10) : '0' + c2);
            sb.append(c1);
            sb.append(c2);
        }
        return sb.toString();
    }
    */
    
    public static byte[] hash( byte[] bytes ) throws GUIDGenerationException {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA");
			digest.update(bytes, 0, bytes.length);
			return digest.digest();
		} catch (NoSuchAlgorithmException e) {
			throw new GUIDGenerationException();
		}

		/*
			sun.security.provider.SHA sha = new sun.security.provider.SHA();
			sha.engineUpdate
			return sha.engineDigest();
        */
    }

    public static byte[] hash(InputStream source) throws GUIDGenerationException {
        try {
            return DigestUtils.sha1(source);
        } catch (IOException e) {
			throw new GUIDGenerationException();
        }

		/*
			sun.security.provider.SHA sha = new sun.security.provider.SHA();
			sha.engineUpdate
			return sha.engineDigest();
        */
    }
    /**
	 * Creates a key with an arbitrary value. Subsequent calls return keys with the same value.
	 * 
	 * @return a key with an arbitrary value
	 */
	public static IKey generateKey() throws GUIDGenerationException {
		return generateKey("null");
	}

	/**
	 * Creates a key with a value generated from the given string.
	 * 
	 * @param string the string from which to generate the key's value
	 * @return a key with a value generated from s
	 */
	public static IKey generateKey(String string) throws GUIDGenerationException {
        if (string == null) {
            throw new GUIDGenerationException();
        }

        return generateKey(string.getBytes());
	}
	
	/**
	 * Creates a new key using the String representation of a BigInteger
	 * This method has been added for use in for de-serialisation - al
	 * 
	 * @param s - the String representation of a serialised Key
	 * @return a new Key using the parameter s as a long value
	 */
	public static IKey recreateKey(String s) throws GUIDGenerationException {
	    return new KeyImpl(s);
	}
	
	/**
	 * Creates a key with a value generated from the given byte array.
	 * 
	 * @param bytes the array from which to generate the key's value
	 * @return a key with a value generated from bytes
	 */
	public static IKey generateKey(byte[] bytes) throws GUIDGenerationException {
        if (bytes == null || bytes.length == 0) {
            throw new GUIDGenerationException();
        }

        byte[] hashed = hash(bytes);
		String hex = Hex.encodeHexString(hashed);

        BigInteger bi = new BigInteger(hex, HEX_BASE);  // Convert to decimal.
        return new KeyImpl(bi);
	}

    public static IKey generateKey(InputStream source) throws GUIDGenerationException {
		if (source == null) {
            throw new GUIDGenerationException();
        }

        byte[] hashed = hash(source);
        String hex = Hex.encodeHexString(hashed);

        BigInteger bi = new BigInteger(hex, HEX_BASE);  // Convert to decimal.
        return new KeyImpl(bi);
    }
	
	/**
	 * Creates a key with a pseudo-random value.
	 * 
	 * @return a key with a pseudo-random value
	 */
	public static IKey generateRandomKey() throws GUIDGenerationException {
		String seed = String.valueOf(System.currentTimeMillis()) +
                String.valueOf(Runtime.getRuntime().freeMemory());
		return generateKey(seed);
	}

}

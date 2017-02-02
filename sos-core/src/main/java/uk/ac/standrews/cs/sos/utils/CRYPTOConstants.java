package uk.ac.standrews.cs.sos.utils;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CRYPTOConstants {

    // Suppresses default constructor, ensuring non-instantiability.
    private CRYPTOConstants() {}

    public static final String RSA_ALGORITHM = "RSA";
    public static final String KEYS_ALGORITHM = "DSA";
    public static final int KEY_SIZE = 512; // in bits - size cannot exceed 2048 according to SUN specs
    public static final String PROVIDER = "SUN";
    public static final String SIGNATURE_ALGORITHM = "SHA1withDSA";

    public static final String SECURE_RANDOM_ALGORITHM = "SHA1PRNG";
    public static final String SECURE_RANDOM_PROVIDER = "SUN";

}

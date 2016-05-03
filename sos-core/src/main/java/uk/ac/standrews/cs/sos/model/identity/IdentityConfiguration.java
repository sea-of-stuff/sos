package uk.ac.standrews.cs.sos.model.identity;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class IdentityConfiguration {

    // Suppresses default constructor, ensuring non-instantiability.
    private IdentityConfiguration() {}

    public static final String KEYS_ALGORITHM = "RSA";
    public static final int KEY_SIZE = 512; // in bits - size cannot exceed 2048 according to SUN specs
    public static final String PROVIDER = "BC";
    public static final String SIGNATURE_ALGORITHM = "SHA1withRSA";

}

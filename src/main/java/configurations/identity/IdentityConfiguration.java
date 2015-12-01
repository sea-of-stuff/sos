package configurations.identity;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class IdentityConfiguration {

    // Suppresses default constructor, ensuring non-instantiability.
    private IdentityConfiguration() {}

    public static final String ALGORITHM = "RSA";
    public static final int KEY_SIZE = 1024; // in bytes

    // XXX This is OS dependent
    public static final String PRIVATE_KEY_FILE = "keys/private.key";
    public static final String PUBLIC_KEY_FILE = "keys/public.key";
}

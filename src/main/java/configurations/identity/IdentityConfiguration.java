package configurations.identity;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class IdentityConfiguration {

    public static final String ALGORITHM = "RSA";
    public static final int KEY_SIZE = 1024; // in bytes

    // TODO - os dependent
    public static final String PRIVATE_KEY_FILE = "C:/keys/private.key";
    public static final String PUBLIC_KEY_FILE = "C:/keys/public.key";
}

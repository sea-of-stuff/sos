package uk.ac.standrews.cs.sos.constants;

/**
 * This class contains some useful uk.ac.standrews.cs.sos.constants for constructing manifests.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class JSONConstants {

    // Suppresses default constructor, ensuring non-instantiability.
    private JSONConstants() {}

    // Manifest keys
    public static final String KEY_TYPE = "Type";
    public static final String KEY_GUID = "GUID";
    public static final String KEY_SIGNER = "Signer";
    public static final String KEY_SIGNATURE = "Signature";
    public static final String KEY_LOCATIONS = "Locations";
    public static final String KEY_COMPOUND_TYPE = "Compound_Type";
    public static final String KEY_CONTENT_GUID = "ContentGUID";
    public static final String KEY_CONTENTS = "Content";
    public static final String KEY_INVARIANT = "Invariant";
    public static final String KEY_PREVIOUS_GUID = "Previous";
    public static final String KEY_METADATA_GUID = "Metadata";

    // Secure Manifest keys
    public static final String KEYS_PROTECTION = "Keys";
    public static final String KEYS_PROTECTION_KEY = "Key";
    public static final String KEYS_PROTECTION_ROLE = "Role";

    // Content keys
    public static final String CONTENT_KEY_GUID = "GUID";
    public static final String CONTENT_KEY_LABEL = "Label";

    // Location bundle keys
    public static final String BUNDLE_TYPE = "Type";
    public static final String BUNDLE_LOCATION = "Location";

    // Metadata
    public static final String KEY_META_PROPERTIES = "Properties";
    public static final String KEY_META_KEY = "Key";
    public static final String KEY_META_TYPE = "Type";
    public static final String KEY_META_VALUE = "Value";

    // Role/User
    public static final String KEY_USER = "User";
    public static final String KEY_NAME = "Name";
    public static final String KEY_SIGNATURE_CERTIFICATE = "Certificate";
    public static final String KEY_PUBLIC_KEY = "PublicKey";
}

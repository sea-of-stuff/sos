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
    public static final String BUNDLE_TYPE = "type";
    public static final String BUNDLE_LOCATION = "location";

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

    // Node
    public static final String KEY_NODE_GUID = "guid";
    public static final String KEY_NODE_SIGNATURE_CERTIFICATE = "signCert";
    public static final String KEY_NODE_HOSTNAME = "hostname";
    public static final String KEY_NODE_PORT = "port";
    public static final String KEY_NODE_SERVICES = "services";
    public static final String KEY_NODE_SERVICES_STORAGE = "storage";
    public static final String KEY_NODE_SERVICES_CMS = "cms";
    public static final String KEY_NODE_SERVICES_DDS = "dds";
    public static final String KEY_NODE_SERVICES_NDS = "nds";
    public static final String KEY_NODE_SERVICES_RMS = "rms";
    public static final String KEY_NODE_SERVICES_MMS = "mms";

    // Predicate
    public static final String KEY_PREDICATE = "Predicate";
    public static final String KEY_PREDICATE_DEPENDENCIES = "Dependencies";
    public static final String KEY_PREDICATE_MAX_AGE = "Max-Age"; // TODO - in context

    // Policy
    public static final String KEY_POLICY = "Policy";
    public static final String KEY_POLICY_APPLY = "Apply";
    public static final String KEY_POLICY_SATISFIED = "Satisfied";
    public static final String KEY_POLICY_FIELDS = "Fields";
    public static final String KEY_POLICY_FIELD_TYPE = "Type";
    public static final String KEY_POLICY_FIELD_NAME = "Name";
    public static final String KEY_POLICY_FIELD_VAL = "Value";

    public static final String KEY_NODE_SERVICE_IS_EXPOSED = "exposed";
}

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
    public static final String KEY_TYPE = "type";
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

    // User/Role
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
    public static final String KEY_NODE_SERVICE_IS_EXPOSED = "exposed";

    // Context
    public static final String KEY_CONTEXT_NAME = "name";
    public static final String KEY_CONTEXT_INVARIANT = "invariant";
    public static final String KEY_CONTEXT_PREVIOUS = "previous";
    public static final String KEY_CONTEXT_CONTENT = "content";
    public static final String KEY_CONTEXT_DOMAIN = "domain";
    public static final String KEY_CONTEXT_CODOMAIN = "codomain";
    public static final String KEY_CONTEXT_PREDICATE = "predicate";
    public static final String KEY_CONTEXT_POLICIES = "policies";


    // Computational units
    public static final String KEY_COMPUTATIONAL_DEPENDENCIES = "dependencies";

    // Predicate
    public static final String KEY_PREDICATE = "predicate";
    public static final String KEY_PREDICATE_MAX_AGE = "max-Age"; // TODO - have it in the context

    // Policy
    public static final String KEY_POLICY_APPLY = "apply";
    public static final String KEY_POLICY_SATISFIED = "satisfied";
    public static final String KEY_POLICY_FIELDS = "fields";
    public static final String KEY_POLICY_FIELD_TYPE = "type";
    public static final String KEY_POLICY_FIELD_NAME = "name";
    public static final String KEY_POLICY_FIELD_VAL = "value";

}

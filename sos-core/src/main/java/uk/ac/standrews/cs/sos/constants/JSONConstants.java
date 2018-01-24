package uk.ac.standrews.cs.sos.constants;

/**
 * This class contains some useful uk.ac.standrews.cs.sos.constants for constructing manifests.
 *
 * TODO - use all lowercase for keys
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class JSONConstants {

    // Suppresses default constructor, ensuring non-instantiability.
    private JSONConstants() {}

    // Manifest General keys
    public static final String KEY_TYPE = "type";
    public static final String KEY_GUID = "guid";
    public static final String KEY_SIGNER = "signer";
    public static final String KEY_SIGNATURE = "signature";
    public static final String KEY_SIGNATURE_CERTIFICATE = "certificate";
    public static final String KEY_LOCATIONS = "locations";
    public static final String KEY_COMPOUND_TYPE = "compound_type";
    public static final String KEY_CONTENT = "content";
    public static final String KEY_CONTENTS = "contents";
    public static final String KEY_INVARIANT = "invariant";
    public static final String KEY_PREVIOUS_GUID = "previous";
    public static final String KEY_METADATA_GUID = "metadata";

    // Secure Manifest keys
    public static final String KEYS_PROTECTION = "keys";
    public static final String KEYS_PROTECTION_KEY = "key";
    public static final String KEYS_PROTECTION_ROLE = "role";

    // Content keys
    public static final String CONTENT_KEY_GUID = "guid";
    public static final String CONTENT_KEY_LABEL = "label";

    // Location bundle keys
    public static final String BUNDLE_TYPE = "type";
    public static final String BUNDLE_LOCATION = "location";

    // Metadata
    public static final String KEY_META_PROPERTIES = "properties";
    public static final String KEY_META_KEY = "key";
    public static final String KEY_META_TYPE = "type";
    public static final String KEY_META_VALUE = "value";

    // User/Role
    public static final String KEY_USER = "user";
    public static final String KEY_NAME = "name";
    public static final String KEY_PUBLIC_KEY = "public_key";

    // Node
    public static final String KEY_NODE_HOSTNAME = "hostname";
    public static final String KEY_NODE_PORT = "port";
    public static final String KEY_NODE_SERVICES = "services";
    public static final String KEY_NODE_SERVICES_STORAGE = "storage";
    public static final String KEY_NODE_SERVICES_CMS = "cms";
    public static final String KEY_NODE_SERVICES_MDS = "mds";
    public static final String KEY_NODE_SERVICES_NDS = "nds";
    public static final String KEY_NODE_SERVICES_RMS = "rms";
    public static final String KEY_NODE_SERVICES_MMS = "mms";
    public static final String KEY_NODE_SERVICES_EXPERIMENT = "experiment";
    public static final String KEY_NODE_SERVICE_IS_EXPOSED = "exposed";

    // NodesCollection
    public static final String KEY_NODES_COLLECTION_TYPE = "type";
    public static final String KEY_NODES_COLLECTION_REFS = "nodes";

    // Context
    public static final String KEY_CONTEXT_TIMESTAMP = "timestamp";
    public static final String KEY_CONTEXT_NAME = "name";
    public static final String KEY_CONTEXT_INVARIANT = "invariant";
    public static final String KEY_CONTEXT_PREVIOUS = "previous";
    public static final String KEY_CONTEXT_CONTENT = "content";
    public static final String KEY_CONTEXT_DOMAIN = "domain";
    public static final String KEY_CONTEXT_CODOMAIN = "codomain";
    public static final String KEY_CONTEXT_PREDICATE = "predicate";
    public static final String KEY_CONTEXT_MAX_AGE = "max_age";
    public static final String KEY_CONTEXT_POLICIES = "policies";

    // Predicate
    public static final String KEY_PREDICATE = "predicate";

    // Policy
    public static final String KEY_POLICY_APPLY = "apply";
    public static final String KEY_POLICY_SATISFIED = "satisfied";
    public static final String KEY_POLICY_FIELDS = "fields";
    public static final String KEY_POLICY_FIELD_TYPE = "type";
    public static final String KEY_POLICY_FIELD_NAME = "name";
    public static final String KEY_POLICY_FIELD_VAL = "value";

}

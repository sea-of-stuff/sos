package uk.ac.standrews.cs.sos.model.manifests;

/**
 * This class contains some useful uk.ac.standrews.cs.sos.constants for constructing manifests.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestConstants {

    // Suppresses default constructor, ensuring non-instantiability.
    private ManifestConstants() {}

    // Manifest keys
    public static final String KEY_TYPE = "Type";
    public static final String KEY_GUID = "GUID";
    public static final String KEY_TIMESTAMP = "Timestamp";
    public static final String KEY_SIGNATURE ="Signature";
    public static final String KEY_LOCATIONS = "Locations";
    public static final String KEY_COMPOUND_TYPE = "Compound_Type";
    public static final String KEY_CONTENT_GUID = "ContentGUID";
    public static final String KEY_CONTENTS = "Content";
    public static final String KEY_INVARIANT = "Invariant";
    public static final String KEY_PREVIOUS_GUID = "Previous";
    public static final String KEY_METADATA_GUID = "Metadata";

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

    // Protection
    public static final String KEY_KEYS = "Keys";

}

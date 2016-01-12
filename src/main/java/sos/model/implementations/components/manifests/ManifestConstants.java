package sos.model.implementations.components.manifests;

/**
 * This class contains some useful constants for constructing manifests.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestConstants {

    // Suppresses default constructor, ensuring non-instantiability.
    private ManifestConstants() {}

    // Manifest types
    public static final String ATOM = "Atom";
    public static final String COMPOUND = "Compound";
    public static final String ASSET = "Asset";

    // Manifest keys
    public static final String KEY_TYPE = "Type";
    public static final String KEY_SIGNATURE ="Signature";
    public static final String KEY_LOCATIONS = "Locations";
    public static final String KEY_CONTENT_GUID = "ContentGUID";
    public static final String KEY_CONTENTS = "Contents";
    public static final String KEY_PREVIOUS_GUID = "PreviousGUID";
    public static final String KEY_METADATA_GUID = "MetadataGUID";

    // Content keys
    public static final String CONTENT_KEY_GUID = "GUID";
    public static final String CONTENT_KEY_LABEL = "Label";

}

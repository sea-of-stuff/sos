package model.implementations.utils;

import model.implementations.components.manifests.ManifestConstants;
import org.json.JSONObject;

/**
 * Envelope class used to represent some content with metadata associated with it.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Content {

    private GUID guid;
    private String value;
    private String type;

    /**
     * Constructs a content envelope using a GUID.
     *
     * @param guid
     */
    public Content(GUID guid) {
        this.guid = guid;
    }

    /**
     * Constructs a content envelope using a GUID and some metadata information
     * regarding the content: type and value
     * (e.g. type - "label", value - "holidays").
     *
     * @param type
     * @param value
     * @param guid
     */
    public Content(String type, String value, GUID guid) {
        this(guid);
        this.type = type;
        this.value = value;
    }

    /**
     * Gets the GUID of the content.
     *
     * @return GUID of the content.
     */
    public GUID getGUID() {
        return guid;
    }

    /**
     * Gets a JSON representation of the content.
     *
     * @return JSON object representing this content.
     */
    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();

        addTypeAndValue(obj);
        addGUID(obj);

        return obj;
    }

    @Override
    public String toString() {
        return toJSON().toString();
    }

    private void addTypeAndValue(JSONObject obj) {
        if (typeAndValueExist()) {
            obj.put(ManifestConstants.CONTENT_KEY_TYPE, this.type);
            obj.put(ManifestConstants.CONTENT_KEY_VALUE, this.value);
        }
    }

    private boolean typeAndValueExist() {
        return type != null && value != null &&
                !type.isEmpty() && !value.isEmpty();

    }

    private void addGUID(JSONObject obj) {
        obj.put(ManifestConstants.CONTENT_KEY_GUID, this.guid);
    }
}

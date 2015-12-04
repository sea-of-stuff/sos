package model.implementations.utils;

import model.implementations.components.manifests.ManifestConstants;
import org.json.JSONObject;

/**
 * Sub-unit used to represent some content with metadata associated with it.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Content {

    private GUID guid;
    private String value;
    private String type;

    public Content(GUID guid) {
        this.guid = guid;
    }

    public Content(String type, String value, GUID guid) {
        this(guid);
        this.type = type;
        this.value = value;
    }

    public GUID getGUID() {
        return guid;
    }

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();

        addTypeAndValue(obj);
        addGUID(obj);

        return obj;
    }

    private void addTypeAndValue(JSONObject obj) {
        if (typeAndValueExist()) {
            obj.put(ManifestConstants.KEY_TYPE, this.type);
            obj.put(ManifestConstants.KEY_VALUE, this.value);
        }
    }

    private boolean typeAndValueExist() {
        return type != null && value != null &&
                !type.isEmpty() && !value.isEmpty();

    }

    private void addGUID(JSONObject obj) {
        obj.put(ManifestConstants.KEY_GUID, this.guid);
    }

    public String toString() {
        return toJSON().toString();
    }
}

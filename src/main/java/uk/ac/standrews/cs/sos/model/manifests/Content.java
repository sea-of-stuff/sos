package uk.ac.standrews.cs.sos.model.manifests;

import com.google.gson.JsonObject;
import uk.ac.standrews.cs.utils.GUID;

import java.util.Objects;

/**
 * Envelope class used to represent some content with metadata associated with it.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Content {

    final private GUID guid;
    private String label;

    /**
     * Constructs a content envelope using a GUID.
     *
     * @param guid
     */
    public Content(GUID guid) {
        this.guid = guid;
    }

    /**
     * Constructs a content envelope using a GUID and a label.
     * (e.g. label - "holidays").
     *
     * @param label
     * @param guid
     */
    public Content(String label, GUID guid) {
        this(guid);
        this.label = label;
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
     * Gets the label of this content.
     *
     * @return label of the content.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Gets a JSON representation of the content.
     *
     * @return JSON object representing this content.
     */
    public JsonObject toJSON() {
        JsonObject obj = new JsonObject();

        if (label != null && !label.isEmpty()) {
            obj.addProperty(ManifestConstants.CONTENT_KEY_LABEL, this.label);
        }

        obj.addProperty(ManifestConstants.CONTENT_KEY_GUID, this.guid.toString());
        return obj;
    }

    @Override
    public String toString() {
        return toJSON().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Content content = (Content) o;
        return Objects.equals(guid, content.guid) &&
                Objects.equals(label, content.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(guid, label);
    }
}

package uk.ac.standrews.cs.sos.model.manifests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.json.ContentDeserializer;
import uk.ac.standrews.cs.sos.json.ContentSerializer;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.util.Objects;

/**
 * Envelope class used to represent some content with metadata associated with it.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@JsonSerialize(using = ContentSerializer.class)
@JsonDeserialize(using = ContentDeserializer.class)
public class Content {

    final private IGUID guid;
    private String label;

    /**
     * Constructs a content envelope using a GUID.
     *
     * @param guid
     */
    public Content(IGUID guid) {
        this.guid = guid;
    }

    /**
     * Constructs a content envelope using a GUID and a label.
     * (e.g. label - "holidays").
     *
     * @param label
     * @param guid
     */
    public Content(String label, IGUID guid) {
        this(guid);
        this.label = label;
    }

    /**
     * Gets the GUID of the content.
     *
     * @return GUID of the content.
     */
    public IGUID getGUID() {
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

    @Override
    public String toString() {
        try {
            return JSONHelper.JsonObjMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
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

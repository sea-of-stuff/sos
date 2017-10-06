package uk.ac.standrews.cs.sos.impl.datamodel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.impl.json.ContentDeserializer;
import uk.ac.standrews.cs.sos.impl.json.ContentSerializer;
import uk.ac.standrews.cs.sos.model.Content;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.util.Objects;

/**
 * Envelope class used to represent some content with metadata associated with it.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@JsonSerialize(using = ContentSerializer.class)
@JsonDeserialize(using = ContentDeserializer.class)
public class ContentImpl implements Content {

    final private IGUID guid;
    private String label;

    /**
     * Constructs a content envelope using a GUID.
     *
     * @param guid
     */
    public ContentImpl(IGUID guid) {
        this.guid = guid;
    }

    /**
     * Constructs a content envelope using a GUID and a label.
     * (e.g. label - "holidays").
     *
     * @param label
     * @param guid
     */
    public ContentImpl(String label, IGUID guid) {
        this(guid);
        this.label = label;
    }

    @Override
    public IGUID getGUID() {
        return guid;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        try {
            return JSONHelper.JsonObjMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            SOS_LOG.log(LEVEL.ERROR, "Unable to generate JSON for content object " + this);
            return "";
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContentImpl content = (ContentImpl) o;
        return Objects.equals(guid, content.guid) &&
                Objects.equals(label, content.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(guid, label);
    }
}

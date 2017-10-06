package uk.ac.standrews.cs.sos.impl.metadata.basic;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.impl.json.MetadataDeserializer;
import uk.ac.standrews.cs.sos.impl.json.MetadataSerializer;
import uk.ac.standrews.cs.sos.impl.metadata.AbstractMetadata;
import uk.ac.standrews.cs.sos.model.Metadata;

import java.util.HashMap;
import java.util.Set;

/**
 * note: this is created only in deserialization
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@JsonDeserialize(using = MetadataDeserializer.class)
@JsonSerialize(using = MetadataSerializer.class)
public class BasicMetadata extends AbstractMetadata implements Metadata {

    private HashMap<String, Object> metadata;

    public BasicMetadata() {
        super(new String[]{});

        metadata = new HashMap<>();
    }

    public void setGUID(IGUID guid) {
        this.guid = guid;
    }

    public void addProperty(String property, Object value) {
        metadata.put(property, value);
    }

    @Override
    public Object getProperty(String propertyName) {

        return metadata.get(propertyName);
    }

    @Override
    public boolean hasProperty(String propertyName) {
        return metadata.containsKey(propertyName);
    }

    @Override
    public String[] getAllPropertyNames() {
        Set<String> keySet = metadata.keySet();

        return keySet.toArray(new String[keySet.size()]);
    }

}

package uk.ac.standrews.cs.sos.model.metadata.basic;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import uk.ac.standrews.cs.sos.interfaces.metadata.SOSMetadata;
import uk.ac.standrews.cs.sos.json.MetadataDeserializer;
import uk.ac.standrews.cs.sos.json.MetadataSerializer;
import uk.ac.standrews.cs.sos.model.metadata.AbstractMetadata;

import java.util.HashMap;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@JsonDeserialize(using = MetadataDeserializer.class)
@JsonSerialize(using = MetadataSerializer.class)
public class BasicMetadata extends AbstractMetadata implements SOSMetadata {

    private HashMap<String, String> metadata;

    public BasicMetadata() {
        super(new String[]{});

        metadata = new HashMap<>();
    }

    public void addProperty(String property, String value) {
        metadata.put(property, value);
    }

    @Override
    public String getProperty(String propertyName) {
        return metadata.get(propertyName);
    }

    @Override
    public String[] getAllPropertyNames() {
        Set<String> keySet = metadata.keySet();

        return keySet.toArray(new String[keySet.size()]);
    }

}

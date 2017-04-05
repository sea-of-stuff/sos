package uk.ac.standrews.cs.sos.impl.metadata.tika;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.impl.metadata.AbstractMetadata;
import uk.ac.standrews.cs.sos.json.MetadataSerializer;
import uk.ac.standrews.cs.sos.model.Metadata;

import java.util.Arrays;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@JsonSerialize(using = MetadataSerializer.class)
public class TikaMetadata extends AbstractMetadata implements Metadata {

    private org.apache.tika.metadata.Metadata tikaMetadata;

    public TikaMetadata(org.apache.tika.metadata.Metadata metadata, String[] ignoreMetadata) throws GUIDGenerationException {
        super(ignoreMetadata);
        this.tikaMetadata = metadata;
        this.guid = generateGUID();
    }

    public Object getProperty(String propertyName) {
        boolean ignore = Arrays.asList(ignoreMetadata).contains(propertyName);


        if (ignore) {
            return null;
        } else {
            String p = tikaMetadata.get(propertyName);
            if (isInteger(p)) {
                return Integer.parseInt(p);
            }

            return p;
        }
    }

    @Override
    public boolean hasProperty(String propertyName) {
        return getProperty(propertyName) != null;
    }

    public void addProperty(String property, String value) {
        tikaMetadata.add(property, value);
    }

    @Override
    public String[] getAllPropertyNames() {
        return tikaMetadata.names();
    }

}

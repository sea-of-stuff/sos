package uk.ac.standrews.cs.sos.model.metadata.tika;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.tika.metadata.Metadata;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.interfaces.model.SOSMetadata;
import uk.ac.standrews.cs.sos.json.MetadataSerializer;
import uk.ac.standrews.cs.sos.model.metadata.AbstractMetadata;

import java.util.Arrays;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@JsonSerialize(using = MetadataSerializer.class)
public class TikaMetadata extends AbstractMetadata implements SOSMetadata {

    private Metadata tikaMetadata;

    public TikaMetadata(Metadata metadata, String[] ignoreMetadata) throws GUIDGenerationException {
        super(ignoreMetadata);
        this.tikaMetadata = metadata;
        this.guid = generateGUID();
    }

    public String getProperty(String propertyName) {

        boolean ignore = Arrays.asList(ignoreMetadata).contains(propertyName);
        return ignore ? null : tikaMetadata.get(propertyName);
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

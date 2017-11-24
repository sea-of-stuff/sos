package uk.ac.standrews.cs.sos.impl.metadata.tika;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.impl.json.SecureMetadataSerializer;
import uk.ac.standrews.cs.sos.impl.metadata.AbstractMetadata;
import uk.ac.standrews.cs.sos.model.Metadata;

import java.util.Arrays;

/**
 * TODO - work in progress
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@JsonSerialize(using = SecureMetadataSerializer.class)
public class SecureTikaMetadata extends AbstractMetadata implements Metadata {

    private org.apache.tika.metadata.Metadata tikaMetadata;

    public SecureTikaMetadata(org.apache.tika.metadata.Metadata metadata, String[] ignoreMetadata) throws GUIDGenerationException {
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
            if (isNumber(p)) {
                return Long.parseLong(p);
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

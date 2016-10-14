package uk.ac.standrews.cs.sos.metadata;

import org.apache.tika.metadata.Metadata;
import uk.ac.standrews.cs.sos.interfaces.metadata.SOSMetadata;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class TikaMetadata implements SOSMetadata {

    private Metadata tikaMetadata;

    public TikaMetadata(Metadata metadata) {
        this.tikaMetadata = metadata;
    }

    public String getProperty(String propertyName) {
        return tikaMetadata.get(propertyName);
    }

    public String[] getAllPropertyNames() {
        return tikaMetadata.names();
    }
}

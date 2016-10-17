package uk.ac.standrews.cs.sos.metadata.tika;

import org.apache.tika.metadata.Metadata;
import uk.ac.standrews.cs.sos.interfaces.metadata.SOSMetadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class TikaMetadata implements SOSMetadata {

    private Metadata tikaMetadata;
    private String[] ignoreMetadata;

    public TikaMetadata(Metadata metadata, String[] ignoreMetadata) {
        this.tikaMetadata = metadata;
        this.ignoreMetadata = ignoreMetadata;
    }

    public String getProperty(String propertyName) {

        boolean ignore = Arrays.asList(ignoreMetadata).contains(propertyName);

        return ignore ? null : tikaMetadata.get(propertyName);
    }

    public String[] getAllPropertyNames() {

        List<String> filteredNames = new ArrayList<>();
        for(String meta:tikaMetadata.names()) {
            boolean ignore = Arrays.asList(ignoreMetadata).contains(meta);
            if (!ignore) {
                filteredNames.add(meta);
            }
        }

        return filteredNames.toArray(new String[filteredNames.size()]);
    }
}

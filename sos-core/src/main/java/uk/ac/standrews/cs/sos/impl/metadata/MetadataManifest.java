package uk.ac.standrews.cs.sos.impl.metadata;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.impl.json.MetadataSerializer;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Metadata;

import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@JsonSerialize(using = MetadataSerializer.class)
public class MetadataManifest extends AbstractMetadata implements Metadata {

    private HashMap<String, MetaProperty> metadata;

    protected MetadataManifest(ManifestType manifestType) {
        super(manifestType);

        metadata = new HashMap<>();
    }

    public MetadataManifest() {
        this(ManifestType.METADATA);
    }

    public MetadataManifest(IGUID guid, HashMap<String, MetaProperty> metadata) {
        this(ManifestType.METADATA);

        this.guid = guid;
        this.metadata = metadata;
    }

    public void addProperty(MetaProperty property) {

        // If guid has been set, then do not add any properties any more
        if (guid == null) {
            metadata.put(property.getKey(), property);
        }
    }

    @Override
    public MetaProperty getProperty(String propertyName) {

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

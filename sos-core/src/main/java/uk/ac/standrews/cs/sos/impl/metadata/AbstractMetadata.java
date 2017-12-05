package uk.ac.standrews.cs.sos.impl.metadata;

import uk.ac.standrews.cs.sos.impl.manifest.AbstractSignedManifest;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Metadata;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.utils.IO;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class AbstractMetadata extends AbstractSignedManifest implements Metadata {

    protected HashMap<String, MetaProperty> metadata;

    AbstractMetadata(ManifestType manifestType, Role signer) {
        super(manifestType, signer);
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

    @Override
    public InputStream contentToHash() {

        String toHash = getType().toString();

        for(String key:metadata.keySet()) {
            MetaProperty metaProperty = metadata.get(key);
            toHash += "MP" + metaProperty.toString();
        }

        return IO.StringToInputStream(toHash);
    }

}

package uk.ac.standrews.cs.sos.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import uk.ac.standrews.cs.sos.impl.manifests.SecureCompoundManifest;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SecureCompoundManifestDeserializer extends JsonDeserializer<SecureCompoundManifest> {

    @Override
    public SecureCompoundManifest deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {

        // TODO
        return null;
    }
}

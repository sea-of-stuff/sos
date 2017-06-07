package uk.ac.standrews.cs.sos.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.exceptions.crypto.ProtectionException;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.impl.roles.RoleImpl;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.utilities.crypto.AsymmetricEncryption;
import uk.ac.standrews.cs.utilities.crypto.CryptoException;
import uk.ac.standrews.cs.utilities.crypto.DigitalSignature;

import java.io.IOException;
import java.security.PublicKey;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RoleDeserializer extends JsonDeserializer<Role> {

    @Override
    public Role deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        try {
            IGUID roleGUID = CommonJson.GetGUID(node, JSONConstants.KEY_GUID);
            IGUID userGUID = CommonJson.GetGUID(node, JSONConstants.KEY_USER);

            String name = node.get(JSONConstants.KEY_NAME).asText();
            String signature = node.get(JSONConstants.KEY_SIGNATURE).textValue();

            String certificateString = node.get(JSONConstants.KEY_SIGNATURE_CERTIFICATE).asText();
            PublicKey certificate = DigitalSignature.getCertificate(certificateString);

            String publicKeyString = node.get(JSONConstants.KEY_PUBLIC_KEY).asText();
            PublicKey publicKey = AsymmetricEncryption.getPublicKeyFromString(publicKeyString);

            return new RoleImpl(userGUID, roleGUID, name, signature, certificate, publicKey);
        } catch (GUIDGenerationException | SignatureException | ProtectionException | CryptoException e) {
            throw new IOException(e);
        }

    }
}

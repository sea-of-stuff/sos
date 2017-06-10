package uk.ac.standrews.cs.sos.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.utilities.crypto.AsymmetricEncryption;
import uk.ac.standrews.cs.utilities.crypto.CryptoException;
import uk.ac.standrews.cs.utilities.crypto.DigitalSignature;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RoleSerializer  extends JsonSerializer<Role> {

    @Override
    public void serialize(Role role, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {

        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField(JSONConstants.KEY_GUID, role.guid().toString());
        jsonGenerator.writeStringField(JSONConstants.KEY_USER, role.getUser().toString());
        jsonGenerator.writeStringField(JSONConstants.KEY_NAME, role.getName());
        jsonGenerator.writeStringField(JSONConstants.KEY_SIGNATURE, role.getSignature());

        try {
            jsonGenerator.writeStringField(JSONConstants.KEY_SIGNATURE_CERTIFICATE, DigitalSignature.getCertificateString(role.getSignatureCertificate()));
            jsonGenerator.writeStringField(JSONConstants.KEY_PUBLIC_KEY, AsymmetricEncryption.keyToBase64(role.getPubKey()));
        } catch (CryptoException e) {
            throw new IOException("Unable to write signature certificate for user");
        }

        jsonGenerator.writeEndObject();
    }
}

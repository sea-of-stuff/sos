/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module core.
 *
 * core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.sos.impl.json;

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

        jsonGenerator.writeStringField(JSONConstants.KEY_TYPE, role.getType().toString());
        jsonGenerator.writeStringField(JSONConstants.KEY_GUID, role.guid().toMultiHash());
        jsonGenerator.writeStringField(JSONConstants.KEY_USER, role.getUser().toMultiHash());
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

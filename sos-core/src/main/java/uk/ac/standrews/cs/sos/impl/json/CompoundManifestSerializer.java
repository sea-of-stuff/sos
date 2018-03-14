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
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.model.Compound;
import uk.ac.standrews.cs.sos.model.Content;

import java.io.IOException;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CompoundManifestSerializer extends JsonSerializer<Compound> {

    @Override
    public void serialize(Compound compoundManifest, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField(JSONConstants.KEY_TYPE, compoundManifest.getType().toString());
        jsonGenerator.writeStringField(JSONConstants.KEY_COMPOUND_TYPE, compoundManifest.getCompoundType().toString());
        jsonGenerator.writeStringField(JSONConstants.KEY_GUID, compoundManifest.guid().toMultiHash());

        jsonGenerator.writeFieldName(JSONConstants.KEY_CONTENTS);
        jsonGenerator.writeStartArray();
        serializeContents(compoundManifest, jsonGenerator);
        jsonGenerator.writeEndArray();

        String signature = compoundManifest.getSignature();
        IGUID signer = compoundManifest.getSigner();
        if (signature != null && !signature.isEmpty() && signer != null && !signer.isInvalid()) {
            jsonGenerator.writeStringField(JSONConstants.KEY_SIGNER, signer.toMultiHash());
            jsonGenerator.writeStringField(JSONConstants.KEY_SIGNATURE, signature);
        }

        jsonGenerator.writeEndObject();
    }

    private void serializeContents(Compound compoundManifest, JsonGenerator jsonGenerator) throws IOException {
        Set<Content> contents = compoundManifest.getContents();
        for(Content content:contents) {
            jsonGenerator.writeObject(content);
        }
    }
}

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
import uk.ac.standrews.cs.sos.model.Policy;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class PolicySerializer extends JsonSerializer<Policy> {

    @Override
    public void serialize(Policy policy, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {

        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField(JSONConstants.KEY_TYPE, policy.getType().toString());
        jsonGenerator.writeStringField(JSONConstants.KEY_GUID, policy.guid().toMultiHash());

        jsonGenerator.writeStringField(JSONConstants.KEY_POLICY_APPLY, policy.apply().asText());
        jsonGenerator.writeStringField(JSONConstants.KEY_POLICY_SATISFIED, policy.satisfied().asText());

        if (policy.fields().size() > 0) {
            jsonGenerator.writeFieldName(JSONConstants.KEY_POLICY_FIELDS);
            jsonGenerator.writeTree(policy.fields());
        }

        jsonGenerator.writeEndObject();
    }
}

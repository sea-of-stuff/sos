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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.sos.exceptions.reflection.ClassLoaderException;
import uk.ac.standrews.cs.sos.impl.context.reflection.SOSReflection;
import uk.ac.standrews.cs.sos.model.Predicate;

import java.io.IOException;

/**
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class PredicateDeserializer extends JsonDeserializer<Predicate> {

    @Override
    public Predicate deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {

        try {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            SOSReflection.instance().load(node);
            return SOSReflection.instance().predicateInstance(node);

        } catch (ClassLoaderException e) {
            throw new IOException(e);
        }

    }

}

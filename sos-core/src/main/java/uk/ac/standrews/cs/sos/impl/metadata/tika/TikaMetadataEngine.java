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
package uk.ac.standrews.cs.sos.impl.metadata.tika;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;
import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataException;
import uk.ac.standrews.cs.sos.impl.metadata.MetadataBuilder;
import uk.ac.standrews.cs.sos.impl.metadata.MetadataManifest;
import uk.ac.standrews.cs.sos.impl.metadata.Property;
import uk.ac.standrews.cs.sos.impl.metadata.SecureMetadataManifest;
import uk.ac.standrews.cs.sos.interfaces.metadata.MetadataEngine;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.utils.Misc;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.HashMap;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class TikaMetadataEngine implements MetadataEngine {

    @Override
    public uk.ac.standrews.cs.sos.model.Metadata processData(MetadataBuilder metadataBuilder) throws MetadataException {

        AutoDetectParser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler(-1); // No limits on how much data to process
        Metadata metadata = new Metadata();
        ParseContext context = new ParseContext();
        context.set(Parser.class, new AutoDetectParser());

        try (InputStream stream = metadataBuilder.getData().getInputStream()) {
            parser.parse(stream, handler, metadata, context);

            HashMap<String, Property> metamap = new HashMap<>();

            processTikaProperties(metamap, metadata);
            sizeProperty(metamap, metadataBuilder.getData());
            timestampProperty(metamap);

            return makeMetadataManifest(metamap, metadataBuilder.getRole(), metadataBuilder.isProtect());

        } catch (IOException | TikaException | SAXException e) {
            SOS_LOG.log(LEVEL.ERROR, "TikaMetadataEngine - bad error. Metadata could not be generated properly");
        } catch (ManifestNotMadeException e) {
            SOS_LOG.log(LEVEL.ERROR, "TikaMetadataEngine - unable to generate metadata manifest");
        } catch (Error e) {
            SOS_LOG.log(LEVEL.ERROR, "TikaMetadataEngine - very bad error. Metadata could not be generated properly");
        }

        try {
            HashMap<String, Property> metamap = new HashMap<>();
            sizeProperty(metamap, metadataBuilder.getData());
            timestampProperty(metamap);
            return makeMetadataManifest(metamap, metadataBuilder.getRole(), metadataBuilder.isProtect());
        } catch (ManifestNotMadeException e) {
            throw new MetadataException("TikaMetadataEngine - bad error. Metadata could not be generated properly", e);
        }
    }

    private void processTikaProperties(HashMap<String, Property> metamap, Metadata metadata) {
        for(String key:metadata.names()) {
            Property property;

            String value = metadata.get(key);
            if (value == null) {
                property = new Property(key);
            } else if (Misc.isIntegerNumber(value)) { // check integer numbers always before real numbers
                property = new Property(key, Long.parseLong(value));
            } else if (Misc.isRealNumber(value)) {
                property = new Property(key, Double.parseDouble(value));
            } else if (Misc.isBoolean(value)) {
                property = new Property(key, Boolean.parseBoolean(value));
            } else {
                property = new Property(key, value);
            }

            metamap.put(key, property);
        }
    }

    private void sizeProperty(HashMap<String, Property> metamap, Data data) {
        Property sizeProperty = new Property("Size", data.getSize());
        metamap.put(sizeProperty.getKey(), sizeProperty);
    }

    private void timestampProperty(HashMap<String, Property> metamap) {
        Instant instant = Instant.now();
        Property timestampProperty = new Property("Timestamp", instant.toEpochMilli());
        metamap.put(timestampProperty.getKey(), timestampProperty);
    }

    private uk.ac.standrews.cs.sos.model.Metadata makeMetadataManifest(HashMap<String, Property> metamap, Role role, boolean encrypt) throws ManifestNotMadeException {
        if (encrypt && role != null) {
            return new SecureMetadataManifest(metamap, role);
        } else {
            return new MetadataManifest(metamap, role);
        }
    }

}

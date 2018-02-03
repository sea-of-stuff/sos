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
import uk.ac.standrews.cs.sos.impl.metadata.MetaProperty;
import uk.ac.standrews.cs.sos.impl.metadata.MetadataBuilder;
import uk.ac.standrews.cs.sos.impl.metadata.MetadataManifest;
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

            HashMap<String, MetaProperty> metamap = new HashMap<>();

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
            HashMap<String, MetaProperty> metamap = new HashMap<>();
            sizeProperty(metamap, metadataBuilder.getData());
            timestampProperty(metamap);
            return makeMetadataManifest(metamap, metadataBuilder.getRole(), metadataBuilder.isProtect());
        } catch (ManifestNotMadeException e) {
            throw new MetadataException("TikaMetadataEngine - bad error. Metadata could not be generated properly", e);
        }
    }

    private void processTikaProperties(HashMap<String, MetaProperty> metamap, Metadata metadata) {
        for(String key:metadata.names()) {
            MetaProperty metaProperty;

            String value = metadata.get(key);
            if (value == null) {
                metaProperty = new MetaProperty(key);
            } else if (Misc.isIntegerNumber(value)) { // check integer numbers always before real numbers
                metaProperty = new MetaProperty(key, Long.parseLong(value));
            } else if (Misc.isRealNumber(value)) {
                metaProperty = new MetaProperty(key, Double.parseDouble(value));
            } else if (Misc.isBoolean(value)) {
                metaProperty = new MetaProperty(key, Boolean.parseBoolean(value));
            } else {
                metaProperty = new MetaProperty(key, value);
            }

            metamap.put(key, metaProperty);
        }
    }

    private void sizeProperty(HashMap<String, MetaProperty> metamap, Data data) {
        MetaProperty sizeMetaProperty = new MetaProperty("Size", data.getSize());
        metamap.put(sizeMetaProperty.getKey(), sizeMetaProperty);
    }

    private void timestampProperty(HashMap<String, MetaProperty> metamap) {
        Instant instant = Instant.now();
        MetaProperty timestampMetaProperty = new MetaProperty("Timestamp", instant.toEpochMilli());
        metamap.put(timestampMetaProperty.getKey(), timestampMetaProperty);
    }

    private uk.ac.standrews.cs.sos.model.Metadata makeMetadataManifest(HashMap<String, MetaProperty> metamap, Role role, boolean encrypt) throws ManifestNotMadeException {
        if (encrypt && role != null) {
            return new SecureMetadataManifest(metamap, role);
        } else {
            return new MetadataManifest(metamap, role);
        }
    }

}

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
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataException;
import uk.ac.standrews.cs.sos.impl.metadata.AbstractMetadataEngine;
import uk.ac.standrews.cs.sos.impl.metadata.MetaProperty;
import uk.ac.standrews.cs.sos.impl.metadata.MetadataManifest;
import uk.ac.standrews.cs.sos.impl.metadata.SecureMetadataManifest;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.utils.Misc;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class TikaMetadataEngine extends AbstractMetadataEngine {

    @Override
    public uk.ac.standrews.cs.sos.model.Metadata processData(Data data, Role role) throws MetadataException {

        AutoDetectParser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler(-1); // NO Limit on how much to process
        Metadata metadata = new Metadata();
        ParseContext context = new ParseContext();
        context.set(Parser.class, new AutoDetectParser());

        try (InputStream stream = data.getInputStream()) {
            parser.parse(stream, handler, metadata, context);

            uk.ac.standrews.cs.sos.model.Metadata meta;
            if (role == null) {
                meta = new MetadataManifest();
            } else {
                meta = new SecureMetadataManifest(null); // pass the role?
            }

            for(String key:metadata.names()) {

                String p = metadata.get(key);
                if (Misc.isNumber(p)) {

                    MetaProperty metaProperty = new MetaProperty(key, Long.parseLong(p));
                    meta.addProperty(metaProperty);
                    continue;
                }

                // attempt to put GUID?

                MetaProperty metaProperty = new MetaProperty(key, p);
                meta.addProperty(metaProperty);
            }

            // iterate over processed meta properties

            MetaProperty sizeMetaProperty = new MetaProperty("Size", data.getSize());
            meta.addProperty(sizeMetaProperty);

            Instant instant = Instant.now();
            MetaProperty timestampMetaProperty = new MetaProperty("Timestamp", instant.toEpochMilli());
            meta.addProperty(timestampMetaProperty);

            meta.generateAndSetGUID();
            return meta;

        } catch (IOException | TikaException | SAXException e) {
            SOS_LOG.log(LEVEL.ERROR, "TikaMetadataEngine - bad error. Metadata could not be generated properly");
            throw new MetadataException("TikaMetadataEngine - bad error. Metadata could not be generated properly", e);
        } catch (Error e) {
            SOS_LOG.log(LEVEL.ERROR, "TikaMetadataEngine - very bad error. Metadata could not be generated properly");
            throw new MetadataException("TikaMetadataEngine - very bad error. Metadata could not be generated properly", e);
        }

    }

}

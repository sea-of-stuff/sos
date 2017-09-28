package uk.ac.standrews.cs.sos.impl.metadata.tika;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;
import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataException;
import uk.ac.standrews.cs.sos.impl.metadata.AbstractMetadataEngine;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.io.InputStream;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class TikaMetadataEngine extends AbstractMetadataEngine {

    @Override
    public TikaMetadata processData(Data data) throws MetadataException {

        AutoDetectParser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler(-1); // NO Limit on how much to process
        Metadata metadata = new Metadata();
        ParseContext context = new ParseContext();
        context.set(Parser.class, new AutoDetectParser());

        try (InputStream stream = data.getInputStream()) {
            parser.parse(stream, handler, metadata, context);

            TikaMetadata meta = new TikaMetadata(metadata, TikaIgnoreMetadata.IGNORE_METADATA);
            meta.addProperty("Size", Long.toString(data.getSize()));

            ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
            meta.addProperty("Timestamp", utc.toString());

            return meta;

        } catch (IOException | TikaException | SAXException | GUIDGenerationException e) {
            SOS_LOG.log(LEVEL.ERROR, "TikaMetadataEngine - bad error. Metadata could not be generated properly");
            throw new MetadataException("TikaMetadataEngine - bad error. Metadata could not be generated properly", e);
        } catch (Error e) {
            SOS_LOG.log(LEVEL.ERROR, "TikaMetadataEngine - very bad error. Metadata could not be generated properly");
            throw new MetadataException("TikaMetadataEngine - very bad error. Metadata could not be generated properly", e);
        }

    }

}

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
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataException;
import uk.ac.standrews.cs.sos.impl.metadata.AbstractMetadataEngine;

import java.io.IOException;
import java.io.InputStream;

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

            long unixTime = System.currentTimeMillis() / 1000L;
            meta.addProperty("Timestamp", Long.toString(unixTime));

            return meta;

        } catch (IOException | TikaException | SAXException | GUIDGenerationException e) {
            throw new MetadataException("Unable to generate metadata from given data", e);
        }

    }

}

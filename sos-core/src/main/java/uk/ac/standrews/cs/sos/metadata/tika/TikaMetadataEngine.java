package uk.ac.standrews.cs.sos.metadata.tika;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;
import uk.ac.standrews.cs.sos.exceptions.metadata.SOSMetadataException;
import uk.ac.standrews.cs.sos.metadata.AbstractMetadataEngine;
import uk.ac.standrews.cs.storage.data.Data;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class TikaMetadataEngine extends AbstractMetadataEngine {

    @Override
    public TikaMetadata processData(Data data) throws SOSMetadataException {

        AutoDetectParser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();

        try (InputStream stream = data.getInputStream()) {
            parser.parse(stream, handler, metadata);

            return new TikaMetadata(metadata, TikaIgnoreMetadata.IGNORE_METADATA);
        } catch (IOException | TikaException | SAXException e) {
            throw new SOSMetadataException("Unable to parse metadata from given data", e);
        }

    }

}

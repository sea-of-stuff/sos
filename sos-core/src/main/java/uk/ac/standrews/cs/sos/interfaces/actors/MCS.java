package uk.ac.standrews.cs.sos.interfaces.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.metadata.SOSMetadataException;
import uk.ac.standrews.cs.sos.interfaces.metadata.SOSMetadata;

import java.io.InputStream;

/**
 * Metadata Computation Service
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface MCS extends SeaOfStuff {

    SOSMetadata addMetadata(InputStream inputStream) throws SOSMetadataException;

    SOSMetadata getMetadata(IGUID guid);
}

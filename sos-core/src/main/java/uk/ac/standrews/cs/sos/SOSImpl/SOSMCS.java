package uk.ac.standrews.cs.sos.SOSImpl;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.metadata.SOSMetadata;
import uk.ac.standrews.cs.sos.interfaces.sos.MCS;

import java.io.InputStream;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSMCS implements MCS {

    @Override
    public SOSMetadata addMetadata(InputStream inputStream) {
        return null;
    }

    @Override
    public SOSMetadata getMetadata(IGUID guid) {
        return null;
    }
}

package uk.ac.standrews.cs.sos.network.roles;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;

import java.io.InputStream;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Consumer implements Role {

    public final static byte CONSUMER_MASK = 0b0100;

    @Override
    public byte getRoleMask() {
        return CONSUMER_MASK;
    }

    @Override
    public Manifest getManifest(IGUID guid) {
        return null;
    }

    @Override
    public InputStream getAtom(IGUID guid) {
        return null;
    }
}

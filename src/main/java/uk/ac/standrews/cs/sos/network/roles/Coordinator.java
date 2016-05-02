package uk.ac.standrews.cs.sos.network.roles;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;

import java.io.InputStream;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Coordinator implements Role {

    public final static byte COORDINATOR_MASK = 0b0001;

    @Override
    public byte getRoleMask() {
        return COORDINATOR_MASK;
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

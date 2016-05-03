package uk.ac.standrews.cs.sos.network.roles;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;

import java.io.InputStream;

import static uk.ac.standrews.cs.sos.network.roles.RoleMasks.PRODUCER_MASK;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Producer implements Role {

    @Override
    public byte getRoleMask() {
        return PRODUCER_MASK;
    }

    public Manifest getManifest(IGUID guid) {
        return null;
    }

    @Override
    public InputStream getAtom(IGUID guid) {
        return null;
    }
}

package uk.ac.standrews.cs.sos.network.roles;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;

import java.io.InputStream;

import static uk.ac.standrews.cs.sos.network.roles.RoleMasks.CONSUMER_MASK;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Consumer implements Role {

    @Override
    public byte getRoleMask() {
        return CONSUMER_MASK;
    }

    @Override
    public Manifest getManifest(IGUID guid) {

        // REST CALLS
        // 1. get manifest given guid

        return null;
    }

    @Override
    public InputStream getAtom(IGUID guid) {

        // 1. get data is location is known
        // 2. otherwise look for manifest, then do step 1 once location is known

        return null;
    }
}

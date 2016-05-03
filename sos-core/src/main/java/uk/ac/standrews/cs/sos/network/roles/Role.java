package uk.ac.standrews.cs.sos.network.roles;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;

import java.io.InputStream;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Role {

    byte getRoleMask();

    Manifest getManifest(IGUID guid);
    InputStream getAtom(IGUID guid);
}

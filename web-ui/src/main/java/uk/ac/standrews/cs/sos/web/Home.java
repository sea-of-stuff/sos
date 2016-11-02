package uk.ac.standrews.cs.sos.web;

import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

import java.net.URISyntaxException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Home {

    public static String Render() throws URISyntaxException, ManifestPersistException, StorageException, ManifestNotFoundException {
        return VelocityUtils.RenderTemplate("velocity/index.vm");
    }

}

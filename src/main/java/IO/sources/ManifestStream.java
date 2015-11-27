package IO.sources;

import model.interfaces.components.entities.Manifest;

import java.io.IOException;
import java.io.InputStream;

/**
 * Returns a stream of manifests from the Sea of Stuff.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestStream extends InputStream {

    @Override
    public int read() throws IOException {
        return 0;
    }

    /**
     * Get a manifest from the ManifestStream. The manifest is returned from
     * the current and available view of the sea of stuff.
     *
     * If no manifest is available, then a null value is returned.
     *
     * @return the next manifest in the stream.
     * @throws IOException if the input stream has been closed, or
     * if some other I/O error occurs.
     */
    public Manifest readManifest() throws IOException {
        return null;
    }
}

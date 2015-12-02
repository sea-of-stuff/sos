package IO;

import model.interfaces.components.entities.Manifest;

import java.io.IOException;
import java.util.stream.Stream;

/**
 * Returns a stream of manifests from the Sea of Stuff.
 *
 * We use Java 8 streams because they allow infinite data to be streamed and
 * let us focus more on data manipulation, rather than data storage.
 *
 * Also considered using InputStreams, but they are too low-level and not the
 * granularity we are looking for.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestStream {

    // TODO - not sure if JAVA8 Streams are a good choice. Need to explore more.
    private Stream<Manifest> manifestStream;

    /**
     * Get a stream of manifests from the ManifestStream.
     * The stream is returned from the current and available view of the sea of stuff.
     *
     *
     * @return the stream of manifests.
     * @throws IOException if the input stream has been closed, or
     * if some other I/O error occurs.
     */
    public Stream<Manifest> getManifestStream() throws IOException {
        return manifestStream;
    }

    private void addToStream(Manifest manifest) {
        manifestStream = Stream.concat(manifestStream, Stream.of(manifest));
    }
}

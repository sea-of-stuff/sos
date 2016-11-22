package uk.ac.standrews.cs.sos.interfaces.sos;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.model.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

import java.io.InputStream;

/**
 * The Storage roles defines an entry point in the SOS to store data.
 *
 * Data stored via a storage node is available to other nodes in the SOS.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Storage extends SeaOfStuff {

    Atom addAtom(AtomBuilder atomBuilder, boolean persist) throws StorageException, ManifestPersistException;

    /**
     * Get an atom's data given an AtomManifest.
     *
     * @param atom describing the atom to retrieve.
     * @return InputStream
     */
    InputStream getAtomContent(Atom atom);

    /**
     * Get the data for the atom with the specified GUID
     *
     * @param guid
     * @return
     */
    InputStream getAtomContent(IGUID guid);

    void flush();

}

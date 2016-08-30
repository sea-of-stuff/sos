package uk.ac.standrews.cs.sos.interfaces.sos;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;

import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface DDS extends SeaOfStuff {

    /**
     * Add a manifest to the sea of stuff.
     * If {@code recursive} is true, then manifests referenced from the specified one will also be added,
     * assuming that such manifests are available and reachable.
     * This operation will be performed recursively.
     *
     * @param manifest to add to the NodeManager
     * @param recursive if true adds the references manifests and data recursively.
     * @return Manifest - the returned manifests might differ from the one passed to the sea of stuff {@code manifest}
     * @throws ManifestPersistException
     */
    void addManifest(Manifest manifest, boolean recursive) throws ManifestPersistException;

    /**
     * Get the manifest that matches a given GUID.
     *
     * @param guid                  of the manifest.
     * @return Manifest             the manifest associated with the GUID.
     * @throws ManifestNotFoundException if the GUID is not known within the currently
     *                              explorable Sea of Stuff.
     *
     */
    Manifest getManifest(IGUID guid) throws ManifestNotFoundException;

    /**
     *
     * @param type
     * @return
     * @throws ManifestNotFoundException
     */
    Collection<IGUID> findManifestByType(String type) throws ManifestNotFoundException;

    /**
     *
     * @param label
     * @return
     * @throws ManifestNotFoundException
     */
    Collection<IGUID> findManifestByLabel(String label) throws ManifestNotFoundException;

    /**
     *
     * @param invariant
     * @return
     * @throws ManifestNotFoundException
     */
    Collection<IGUID> findVersions(IGUID invariant) throws ManifestNotFoundException;

}

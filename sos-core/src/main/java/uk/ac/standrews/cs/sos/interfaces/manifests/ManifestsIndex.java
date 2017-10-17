package uk.ac.standrews.cs.sos.interfaces.manifests;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.HEADNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.TIPNotFoundException;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Versionable;

import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface ManifestsIndex {

    /**
     *
     * @param manifest
     */
    void track(Manifest manifest);

    /**
     *
     * @param type
     * @return
     */
    Set<IGUID> getManifests(ManifestType type);

    /**
     * References to Versionable manifests (i.e. Version, Context)
     *
     * @param invariant
     * @return
     */
    Set<IGUID> getVersions(IGUID invariant);

    /**
     * Tips of versionable manifest
     *
     * @param invariant
     * @return
     * @throws TIPNotFoundException
     */
    Set<IGUID> getTips(IGUID invariant) throws TIPNotFoundException;

    /**
     * Head of versionable manifest
     *
     * @param invariant
     * @return
     * @throws HEADNotFoundException
     */
    IGUID getHead(IGUID invariant) throws HEADNotFoundException;


    /**
     * Set this version to be the head
     *
     * @param versionable
     */
    void setHead(Versionable versionable);

    /**
     * Advance the tip for this version
     *
     * @param versionable
     */
    void advanceTip(Versionable versionable);

    /**
     * Persist index to disk
     */
    void flush();

    /**
     * Rebuild index from disk
     */
    void rebuild();
}

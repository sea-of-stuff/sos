package uk.ac.standrews.cs.sos.interfaces.index;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.index.IndexException;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;

import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Index {

    void killInstance() throws IndexException;

    void flushDB() throws IndexException;

    void addManifest(Manifest manifest) throws IndexException;

    Collection<IGUID> getVersions(IGUID guid, int results, int skip) throws IndexException;

    Collection<IGUID> getMetaLabelMatches(String label, int results, int skip) throws IndexException;

    Collection<IGUID> getManifestsOfType(String type, int results, int skip) throws IndexException;

}

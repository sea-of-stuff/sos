package uk.ac.standrews.cs.sos.interfaces;

import uk.ac.standrews.cs.sos.exceptions.IndexException;
import uk.ac.standrews.cs.sos.model.SeaConfiguration;
import uk.ac.standrews.cs.utils.GUID;

import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Index {

    void killInstance() throws IndexException;

    void flushDB() throws IndexException;

    void addManifest(Manifest manifest) throws IndexException;

    Collection<GUID> getVersions(GUID guid, int results, int skip) throws IndexException;

    Collection<GUID> getMetaLabelMatches(String label, int results, int skip) throws IndexException;

    Collection<GUID> getManifestsOfType(String type, int results, int skip) throws IndexException;

    SeaConfiguration getConfiguration();

}

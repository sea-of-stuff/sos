package uk.ac.standrews.cs.sos.interfaces;

import uk.ac.standrews.cs.sos.exceptions.manifest.UnknownManifestTypeException;
import uk.ac.standrews.cs.sos.model.SeaConfiguration;
import uk.ac.standrews.cs.sos.model.manifests.Content;
import uk.ac.standrews.cs.utils.GUID;

import java.io.IOException;
import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Index {
    // TODO - remove methods that are to be managed by the manifests manager, no need to be in the index/index

    public abstract void killInstance() throws IOException;

    public abstract void flushDB() throws IOException;

    public abstract void addManifest(Manifest manifest) throws UnknownManifestTypeException;

    public abstract Collection<GUID> getVersions(GUID guid, int results, int skip) throws IOException;

    public abstract Collection<Content> getContents(GUID contentGUID);

    public abstract Collection<GUID> getMetaLabelMatches(String label, int results, int skip) throws IOException;

    public abstract Collection<GUID> getManifestsOfType(String type, int results, int skip) throws IOException;

    public abstract SeaConfiguration getConfiguration();

}

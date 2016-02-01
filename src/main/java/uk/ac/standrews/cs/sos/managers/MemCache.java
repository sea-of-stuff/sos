package uk.ac.standrews.cs.sos.managers;

import org.apache.lucene.queryparser.classic.ParseException;
import uk.ac.standrews.cs.sos.configurations.SeaConfiguration;
import uk.ac.standrews.cs.sos.exceptions.manifest.UnknownManifestTypeException;
import uk.ac.standrews.cs.sos.model.implementations.utils.Content;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUID;
import uk.ac.standrews.cs.sos.model.interfaces.components.Manifest;

import java.io.IOException;
import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class MemCache {
    // TODO - remove methods that are to be managed by the manifests manager, no need to be in the cache/index

    public abstract void killInstance() throws IOException;

    public abstract void flushDB() throws IOException;

    public abstract void addManifest(Manifest manifest) throws UnknownManifestTypeException;

    public abstract Collection<GUID> getVersions(GUID guid, int results, int skip) throws IOException;

    public abstract Collection<Content> getContents(GUID contentGUID);

    public abstract Collection<GUID> getMetaLabelMatches(String label) throws IOException;

    public abstract Collection<GUID> getManifestsOfType(String type) throws IOException, ParseException;

    public abstract SeaConfiguration getConfiguration();

}

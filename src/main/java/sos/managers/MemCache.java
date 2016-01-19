package sos.managers;

import sos.exceptions.UnknownManifestTypeException;
import sos.model.implementations.utils.Content;
import sos.model.implementations.utils.GUID;
import sos.model.implementations.utils.Location;
import sos.model.interfaces.components.Manifest;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class MemCache {

    public abstract void killInstance();

    public abstract void flushDB();

    public abstract void addManifest(Manifest manifest) throws UnknownManifestTypeException;

    public abstract String getManifestType(GUID manifestGUID);

    public abstract Collection<Location> getLocations(GUID manifestGUID) throws MalformedURLException;

    public abstract String getSignature(GUID manifestGUID);

    public abstract Collection<Manifest> getManifests(GUID guid);

    public abstract Collection<Content> getContents(GUID contentGUID);

    public abstract GUID getInvariant(GUID manifestGUID);

    public abstract Collection<GUID> getPrevs(GUID manifestGUID);

    public abstract Collection<GUID> getMetadata(GUID manifestGUID);

    // TODO - write description
    public abstract Set<String> getMetaLabelMatches(String value);

}

package sos.managers;

import sos.exceptions.UnknownManifestTypeException;
import sos.model.implementations.utils.GUID;
import sos.model.interfaces.components.Manifest;

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

    public abstract Collection<String> getLocations(GUID manifestGUID);

    public abstract String getSignature(GUID manifestGUID);

    public abstract Set<String> getManifests(GUID guid);

    public abstract Set<String> getContents(GUID contentGUID);

    public abstract String getIncarnation(GUID manifestGUID);

    public abstract Set<String> getPrevs(GUID manifestGUID);

    public abstract Set<String> getMetadata(GUID manifestGUID);

    // FIXME - set of GUIDs!!!
    public abstract Set<String> getMetaLabelMatches(String value);

}

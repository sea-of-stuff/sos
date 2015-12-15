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

    public abstract void addManifest(Manifest manifest) throws UnknownManifestTypeException;

    public abstract String getManifestType(GUID manifestGUID);

    public abstract Collection<String> getLocations(GUID manifestGUID);

    // FIXME - set of GUIDs!!!
    public abstract Set<String> getMetaValueMatches(String value);

    public abstract Set<String> getMetaTypeMatches(String type);

}

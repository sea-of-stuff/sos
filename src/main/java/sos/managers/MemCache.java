package sos.managers;

import sos.exceptions.UnknownManifestTypeException;
import sos.model.interfaces.components.Manifest;

import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class MemCache {

    public abstract void addManifest(Manifest manifest) throws UnknownManifestTypeException;

    // public abstract GUID getGUIDReference(String match);

    // FIXME - set of GUIDs!!!
    public abstract Set<String> getMetaValueMatches(String value);

    public abstract Set<String> getMetaTypeMatches(String type);

}

package sos.managers;

import sos.exceptions.UnknownManifestTypeException;
import sos.model.implementations.utils.GUID;
import sos.model.interfaces.components.Manifest;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class MemCache {

    public abstract void addManifest(Manifest manifest) throws UnknownManifestTypeException;

    public abstract GUID getGUIDReference(String match);

}

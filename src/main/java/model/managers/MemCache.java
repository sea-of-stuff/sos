package model.managers;

import model.implementations.utils.GUID;
import model.interfaces.components.Manifest;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class MemCache {

    public abstract void addManifest(Manifest manifest);

    public abstract GUID getGUIDReference(String match);

}

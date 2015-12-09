package model.managers;

import model.implementations.utils.GUID;
import model.interfaces.components.Manifest;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RedisCache extends MemCache {


    @Override
    public void addManifest(Manifest manifest) {

    }

    @Override
    public GUID getGUIDReference(String match) {
        return null;
    }
}

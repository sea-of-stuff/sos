/*
 * Created on 19-Aug-2005
 */
package uk.ac.standrews.cs.utils;

import uk.ac.standrews.cs.sos.exceptions.utils.GUIDGenerationException;

import java.io.InputStream;

public class GUIDFactory {
    
    public static IGUID generateRandomGUID() throws GUIDGenerationException {
        return (KeyImpl)SHA1KeyFactory.generateRandomKey();
    }
    
    public static IGUID recreateGUID(String string) throws GUIDGenerationException {
        return (KeyImpl)SHA1KeyFactory.recreateKey(string);
    }

    public static IGUID generateGUID(String string) throws GUIDGenerationException {
        return (KeyImpl) SHA1KeyFactory.generateKey(string);
    }

    public static IGUID generateGUID(InputStream inputStream) throws GUIDGenerationException {
        return (KeyImpl) SHA1KeyFactory.generateKey(inputStream);
    }
    
}

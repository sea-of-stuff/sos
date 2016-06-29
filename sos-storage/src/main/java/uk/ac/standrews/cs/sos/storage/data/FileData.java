/*
 * Created on May 23, 2005 at 10:51:17 AM.
 */
package uk.ac.standrews.cs.sos.storage.data;

import java.io.*;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * IData implementation using a conventional file.
 *
 * @author al
 */
public class FileData implements Data {

    private static final Logger log = Logger.getLogger( FileData.class.getName() );

    private File theFile;
    
    /**
     * Creates an instance using a given file.
     * 
     * @param theFile a file containing the underlying data
     */
    public FileData(File theFile) {
        this.theFile = theFile;
    }
    
    /**
     * Gets the data.
     * 
     * @return the underlying data
     */
    public byte[] getState() {
        byte[] bytes = new byte[(int) getSize()];
        try {
            new FileInputStream(theFile).read(bytes);
        } catch (FileNotFoundException e) {
            log.log(Level.SEVERE, "Cannot find file: " + theFile.getName(), e);
        } catch (IOException e) {
            log.log(Level.SEVERE, "IO Exception on file: " + theFile.getName(), e);
        }
        return bytes;
    }
    
    /**
     * Gets the size of the data in bytes.
     * 
     * @return the size of the data
     */
    public long getSize() {
        return theFile.length();
    }

    /**
     * Creates an input stream reading from the file.
     * 
     * @return an input stream reading from the file
     */
    public InputStream getInputStream() throws FileNotFoundException {
        return new FileInputStream(theFile);
    }

    /**
     * Tests equality with another instance.
     * 
     * @return true if the file's contents are equivalent to those of the given file
     * @see Object#equals(Object)
     */
    public boolean equals( Object o ) {
        return o instanceof Data && Arrays.equals( getState(), ((Data)(o)).getState() );
    }
}

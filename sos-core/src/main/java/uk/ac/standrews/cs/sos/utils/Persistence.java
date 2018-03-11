package uk.ac.standrews.cs.sos.utils;

import uk.ac.standrews.cs.castore.exceptions.DataException;
import uk.ac.standrews.cs.castore.exceptions.PersistenceException;
import uk.ac.standrews.cs.castore.interfaces.IFile;

import java.io.*;

/**
 * Utility methods to facilitate the persistence of objects.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Persistence {

    public static void persist(Object object, IFile file) throws IOException {
        if (!file.exists()) {
            try {
                file.persist();
            } catch (PersistenceException e) {
                throw new IOException(e);
            }
        }

        try (OutputStream ostream = IO.toOutputStream(file.getData().getInputStream());
             ObjectOutputStream p = new ObjectOutputStream(ostream)) {

            p.writeObject(object);
            p.flush();
        } catch (DataException e) {
            throw new IOException(e);
        }
    }

    public static Object load(IFile file) throws IOException, ClassNotFoundException {

        // Check that file is not empty
        if (!file.exists() || file.getSize() == 0) {
            throw new IOException("File is empty");
        }

        try (InputStream istream = file.getData().getInputStream();
             ObjectInputStream q = new ObjectInputStream(istream)) {

            return q.readObject();
        } catch (DataException e) {
            throw new IOException(e);
        }

    }
}

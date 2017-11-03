package uk.ac.standrews.cs.sos.utils;

import uk.ac.standrews.cs.castore.exceptions.PersistenceException;
import uk.ac.standrews.cs.castore.interfaces.IFile;

import java.io.*;

/**
 * Utility methods to facilitate the persistence of objects.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Persistence {

    public static void Persist(Object object, IFile file) throws IOException {
        if (!file.exists()) {
            try {
                file.persist();
            } catch (PersistenceException e) {
                throw new IOException(e);
            }
        }

        try (FileOutputStream ostream = new FileOutputStream(file.toFile());
             ObjectOutputStream p = new ObjectOutputStream(ostream)) {

            p.writeObject(object);
            p.flush();
        }
    }

    public static Object Load(IFile file) throws IOException, ClassNotFoundException {

        // Check that file is not empty
        try (BufferedReader br = new BufferedReader(new FileReader(file.getPathname()))) {
            if (br.readLine() == null) {
                return null;
            }

            try (FileInputStream istream = new FileInputStream(file.toFile());
                 ObjectInputStream q = new ObjectInputStream(istream)) {

                return q.readObject();
            }
        }
    }
}

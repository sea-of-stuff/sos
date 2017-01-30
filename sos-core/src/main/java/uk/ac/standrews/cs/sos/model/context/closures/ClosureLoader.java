package uk.ac.standrews.cs.sos.model.context.closures;

import uk.ac.standrews.cs.sos.interfaces.context.Closure;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ClosureLoader {

    private static final String CLASS_PACKAGE = "uk.ac.standrews.cs.sos.model.context.closures.";

    public Closure load(String path, String className) {

        File file = new File(path + className + ".java");

        try {
            // Convert File to a URL
            URL url = file.toURL();
            URL[] urls = new URL[]{url};

            // Create a new class loader with the directory
            ClassLoader cl = new URLClassLoader(urls);
            Class cls = cl.loadClass(CLASS_PACKAGE + className);

            return (Closure) cls.newInstance();

        } catch (MalformedURLException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }

        return null;
    }
}

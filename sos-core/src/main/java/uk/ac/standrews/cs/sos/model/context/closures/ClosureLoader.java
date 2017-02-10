package uk.ac.standrews.cs.sos.model.context.closures;

import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.context.ClosureLoaderException;
import uk.ac.standrews.cs.sos.interfaces.context.Closure;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ClosureLoader {

    private static final String CLASS_PACKAGE = "uk.ac.standrews.cs.sos.model.context.closures.";

    public static void Load(String path, String className) throws ClosureLoaderException {

        File file = new File(path + className + ".java");

        try {
            // Convert File to a URL
            URL url = file.toURL();
            URL[] urls = new URL[]{url};

            // Create a new class loader with the directory
            ClassLoader cl = new URLClassLoader(urls);
            Class cls = cl.loadClass(CLASS_PACKAGE + className);
            SOS_LOG.log(LEVEL.INFO, "Loaded closure: " + cls.getName());
        } catch (MalformedURLException | ClassNotFoundException e) {
            throw new ClosureLoaderException("Unable to load closure for class: " + className);
        }

    }

    public static Closure Instance(String className) throws ClosureLoaderException {

        try {
            Class<?> clazz = Class.forName(CLASS_PACKAGE + className);
            return (Closure) clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new ClosureLoaderException("Unable to create instance for class " + className);
        }
    }

    public static void Load(String path) throws ClosureLoaderException {

        ArrayList<String> notLoaded = new ArrayList<>();

        File folder = new File(path);
        File[] files = folder.listFiles();
        for(File f:files) {
            if (f.isFile()) {
                String name = f.getName();
                if (name.length() <= 5) {
                    notLoaded.add(name);
                    continue;
                }

                name = name.substring(0, name.length() - 5); // Ignore ".java" extension in filename
                try {
                    Load(path, name);
                } catch (ClosureLoaderException e) {
                    notLoaded.add(f.getName());
                }
            }
        }

        if (!notLoaded.isEmpty()) {
            throw new ClosureLoaderException("Unable to load the following classes: " + Arrays.toString(notLoaded.toArray()));
        }

    }
}

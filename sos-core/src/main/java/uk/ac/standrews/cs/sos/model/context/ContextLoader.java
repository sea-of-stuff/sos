package uk.ac.standrews.cs.sos.model.context;

import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.sos.actors.SOSAgent;
import uk.ac.standrews.cs.sos.exceptions.context.ContextLoaderException;
import uk.ac.standrews.cs.sos.interfaces.actors.Agent;
import uk.ac.standrews.cs.sos.interfaces.model.Context;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextLoader {

    private static final String CLASS_PACKAGE = "uk.ac.standrews.cs.sos.model.context.defaults.";

    /**
     * Load context class from path
     * @param path
     * @param className
     * @throws ContextLoaderException
     */
    public static void Load(String path, String className) throws ContextLoaderException {

        File file = new File(path + className + ".java");

        if (!file.exists()) {
            throw new ContextLoaderException("File for class " + className + " was not found");
        }

        try {
            // Convert File to a URL
            URL url = file.toURL();
            URL[] urls = new URL[]{url};

            // Create a new class loader with the directory
            ClassLoader cl = new URLClassLoader(urls);
            Class cls = cl.loadClass(CLASS_PACKAGE + className);
            SOS_LOG.log(LEVEL.INFO, "Loaded context: " + cls.getName());
        } catch (MalformedURLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new ContextLoaderException("Unable to load context for class: " + className);
        }

    }

    public static Context Instance(Agent agent, String className) throws ContextLoaderException {

        try {
            Class<?> clazz = Class.forName(CLASS_PACKAGE + className);

            Class[] cArg = new Class[2];
            cArg[0] = SOSAgent.class;
            cArg[1] = String.class;

            return (Context) clazz.getDeclaredConstructor(cArg).newInstance(agent, className);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException |
                NoSuchMethodException | InvocationTargetException e) {
            throw new ContextLoaderException("Unable to create instance for class " + className);
        }
    }

    /**
     * Load multiple contexts at path
     * @param path
     * @throws ContextLoaderException
     */
    public static void Load(String path) throws ContextLoaderException {

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
                } catch (ContextLoaderException e) {
                    notLoaded.add(f.getName());
                }
            }
        }

        if (!notLoaded.isEmpty()) {
            throw new ContextLoaderException("Unable to load the following classes: " + Arrays.toString(notLoaded.toArray()));
        }

    }
}

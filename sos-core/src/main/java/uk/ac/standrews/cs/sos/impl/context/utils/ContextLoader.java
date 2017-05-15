package uk.ac.standrews.cs.sos.impl.context.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.io.Files;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.sos.actors.Agent;
import uk.ac.standrews.cs.sos.exceptions.context.ContextLoaderException;
import uk.ac.standrews.cs.sos.impl.actors.SOSAgent;
import uk.ac.standrews.cs.sos.model.Context;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * TODO - Load any maven dependencies using Maven Artifact Resolver
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextLoader {

    private static final String TEST_RESOURCES_PATH = "src/test/resources/contexts/";
    private static final String TEST_TARGET_PATH = "target/classes/";

    /**
     * Load multiple contexts at path
     *
     * @param path
     * @throws ContextLoaderException
     */
    public static void Load(String path) throws ContextLoaderException {

        ArrayList<String> notLoaded = new ArrayList<>();

        File folder = new File(path);
        File[] files = folder.listFiles();
        for(File f: files != null ? files : new File[0]) {
            if (f.isFile()) {

                try {
                    Load(path);
                } catch (ContextLoaderException e) {
                    notLoaded.add(f.getName());
                }

            }
        }

        if (!notLoaded.isEmpty()) {
            throw new ContextLoaderException("Unable to load the following classes: " + Arrays.toString(notLoaded.toArray()));
        }

    }

    /**
     * Load the context at the given path
     *
     * @param path
     * @throws ContextLoaderException
     */
    public static void LoadContext(String path) throws ContextLoaderException {

        try {
            File file = new File(path);
            JsonNode node = JSONHelper.JsonObjMapper().readTree(file);

            LoadContext(node);

        } catch (IOException | ContextLoaderException e) {
            throw new ContextLoaderException(e);
        }
    }

    /**
     * Load a context given a context JSON structure
     *
     * @param node
     * @throws ContextLoaderException
     */
    public static void LoadContext(JsonNode node) throws ContextLoaderException {

        try {
            String clazzString = ContextClassBuilder.ConstructClass(node);

            String clazzName = node.get("name").asText();
            File sourceClazzFile = new File(Files.createTempDir() + "/" + clazzName + ".java");
            sourceClazzFile.deleteOnExit();
            try (PrintWriter out = new PrintWriter(sourceClazzFile)){
                out.println(clazzString);
            }

            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
            fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Collections.singletonList(new File(TEST_TARGET_PATH)));

            // Compile the file
            JavaCompiler.CompilationTask task = compiler.getTask(null,
                    fileManager,
                    diagnostics,
                    null,
                    null,
                    fileManager.getJavaFileObjectsFromFiles(Collections.singletonList(sourceClazzFile)));

            if (task.call()) {

                String path = TEST_TARGET_PATH + ContextClassBuilder.PACKAGE.replace(".", "/") + "/" + clazzName + ".class";
                File dir = new File(path).getParentFile();
                Load(dir, clazzName);

            } else {

                for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                    SOS_LOG.log(LEVEL.ERROR, "Error on line " + diagnostic.getLineNumber() + " in " + diagnostic.getSource().toUri());
                }
            }

            fileManager.close();

        } catch (IOException | ContextLoaderException e) {
            throw new ContextLoaderException(e);
        }
    }

    /**
     * Load context class from path
     * @param dir
     * @param className
     * @throws ContextLoaderException
     */
    private static void Load(File dir, String className) throws ContextLoaderException {

        if (!dir.exists()) {
            throw new ContextLoaderException("File for class " + className + " was not found");
        }

        try {
            // Convert File to a URL
            URL url = dir.toURL();
            URL[] urls = new URL[]{url};

            // Create a new class loader with the directory
            ClassLoader cl = new URLClassLoader(urls);
            Class cls = cl.loadClass(ContextClassBuilder.PACKAGE + "." + className);
            SOS_LOG.log(LEVEL.INFO, "Loaded context: " + cls.getName());

        } catch (MalformedURLException | ClassNotFoundException e) {
            throw new ContextLoaderException("Unable to load context for class: " + className);
        }

    }

    /**
     * Creates context instance
     *
     * @param className
     * @return
     * @throws ContextLoaderException
     */
    public static Context Instance(String className) throws ContextLoaderException {

        try {
            Class<?> clazz = Class.forName(ContextClassBuilder.PACKAGE + "." + className);

            Context context = (Context) clazz.newInstance(); // FIXME - pass any parameters
            return context;
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new ContextLoaderException("Unable to create instance for class " + className);
        }
    }

    /**
     * Creates Context instance with constructor params
     *
     * @param agent
     * @param className
     * @return
     * @throws ContextLoaderException
     */
    public static Context Instance(String className, Agent agent) throws ContextLoaderException {

        try {
            Class<?> clazz = Class.forName(ContextClassBuilder.PACKAGE + "." + className);

            Class[] cArg = new Class[1];
            cArg[0] = SOSAgent.class;

            return (Context) clazz.getDeclaredConstructor(cArg).newInstance(agent);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException |
                NoSuchMethodException | InvocationTargetException e) {
            throw new ContextLoaderException("Unable to create instance for class " + className);
        }
    }

}

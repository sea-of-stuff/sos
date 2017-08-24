package uk.ac.standrews.cs.sos.impl.context.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.io.Files;
import org.apache.commons.lang3.text.WordUtils;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.context.ContextLoaderException;
import uk.ac.standrews.cs.sos.impl.context.PolicyActions;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.model.Context;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.utils.FileUtils;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * TODO - Load any maven dependencies using Maven Artifact Resolver?
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextLoader {

    /**
     * Load multiple contexts at path
     *
     * @param path
     * @throws ContextLoaderException
     */
    public static void LoadMultipleContexts(String path) throws ContextLoaderException {

        ArrayList<String> notLoaded = new ArrayList<>();

        File folder = new File(path);
        File[] files = folder.listFiles();
        for(File f: files != null ? files : new File[0]) {
            if (f.isFile()) {

                try {
                    LoadContextFromPath(path);
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
    public static void LoadContextFromPath(String path) throws ContextLoaderException {

        try {
            File file = new File(path);
            JsonNode node = JSONHelper.JsonObjMapper().readTree(file);

            LoadContext(node);

        } catch (IOException | ContextLoaderException e) {
            throw new ContextLoaderException(e);
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static void LoadContext(String node) throws ContextLoaderException {
        try {
            JsonNode jsonNode = JSONHelper.JsonObjMapper().readTree(node);
            LoadContext(jsonNode);
        } catch (IOException e) {
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

        String targetClassPath = SOSLocalNode.settings.getServices().getCms().getLoadedPath();

        if (!new File(targetClassPath).exists()) {
            FileUtils.MakePath(targetClassPath);
        }

        try {
            String clazzString = ContextClassBuilder.ConstructClass(node);
            // System.out.println(clazzString);

            // Print class to file
            String clazzName = WordUtils.capitalize(node.get("name").asText());
            File sourceClazzFile = new File(Files.createTempDir() + "/" + clazzName + ".java");
            if (sourceClazzFile.exists()) sourceClazzFile.delete();
            sourceClazzFile.deleteOnExit();
            try (PrintWriter out = new PrintWriter(sourceClazzFile)){
                out.println(clazzString);
            }

            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            // System.out.println("COMPILER: " + compiler);
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
            fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Collections.singletonList(new File(targetClassPath)));

            // Compile the file
            JavaCompiler.CompilationTask task = compiler.getTask(
                    null,
                    fileManager,
                    diagnostics,
                    null,
                    null,
                    fileManager.getJavaFileObjects(sourceClazzFile));

            if (task.call()) {

                Load(clazzName);

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
     * @param className
     * @throws ContextLoaderException
     */
    private static void Load(String className) throws ContextLoaderException {

        try {
            ClassLoader cl = ClassLoaderForContexts();
            Class<?> cls = cl.loadClass(ContextClassBuilder.PACKAGE + "." + className);
            SOS_LOG.log(LEVEL.INFO, "Loaded context: " + cls.getName());

        } catch (ClassNotFoundException e) {
            throw new ContextLoaderException("Unable to load context for class: " + className);
        }

    }

    /**
     * Creates context instance
     *
     * @param className
     * @param policyActions
     * @param contextName
     * @param domain
     * @param codomain
     * @return
     * @throws ContextLoaderException
     */
    public static Context Instance(String className, PolicyActions policyActions, String contextName, NodesCollection domain, NodesCollection codomain) throws ContextLoaderException {

        try {
            ClassLoader classLoader = ClassLoaderForContexts();
            Class<?> clazz = Class.forName(ContextClassBuilder.PACKAGE + "." + className, true, classLoader);
            Constructor<?> constructor = clazz.getConstructor(PolicyActions.class, String.class, NodesCollection.class, NodesCollection.class);
            Context context = (Context) constructor.newInstance(policyActions, contextName, domain, codomain);
            return context;
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
            throw new ContextLoaderException("Unable to create instance for class " + className);
        }
    }

    /**
     * Creates context instance
     *
     * @param className
     * @param policyActions
     * @param guid
     * @param contextName
     * @param domain
     * @param codomain
     * @return
     * @throws ContextLoaderException
     */
    public static Context Instance(String className, PolicyActions policyActions, IGUID guid, String contextName, NodesCollection domain, NodesCollection codomain) throws ContextLoaderException {

        try {
            ClassLoader classLoader = ClassLoaderForContexts();
            Class<?> clazz = Class.forName(ContextClassBuilder.PACKAGE + "." + className, true, classLoader);
            Constructor<?> constructor = clazz.getConstructor(PolicyActions.class, IGUID.class, String.class, NodesCollection.class, NodesCollection.class);
            Context context = (Context) constructor.newInstance(policyActions, guid, contextName, domain, codomain);
            return context;
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
            throw new ContextLoaderException("Unable to create instance for class " + className);
        }
    }


    private static ClassLoader ClassLoaderForContexts() throws ContextLoaderException {

        String targetClassPath = SOSLocalNode.settings.getServices().getCms().getLoadedPath();
        File contextClassDirectory = new File(targetClassPath);

        if (!contextClassDirectory.exists()) {
            throw new ContextLoaderException("Cannot find path for context classes");
        }

        try {
            URL url = contextClassDirectory.toURL();
            URL[] urls = new URL[]{url};

            // Create a new class loader with the directory
            ClassLoader cl = new URLClassLoader(urls);
            return cl;
        } catch (MalformedURLException e) {
            throw new ContextLoaderException("Cannot create class loader for contexts");
        }
    }
}

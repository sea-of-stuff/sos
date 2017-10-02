package uk.ac.standrews.cs.sos.impl.context.reflection;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.io.Files;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.exceptions.reflection.ClassBuilderException;
import uk.ac.standrews.cs.sos.exceptions.reflection.ClassLoaderException;
import uk.ac.standrews.cs.sos.impl.context.PolicyActions;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.model.*;
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
public class ClassLoader {

    /**
     * Load multiple classes at path
     *
     * @param path
     * @throws ClassLoaderException
     */
    public static void LoadMultipleClasses(String path) throws ClassLoaderException {

        ArrayList<String> notLoaded = new ArrayList<>();

        File folder = new File(path);
        File[] files = folder.listFiles();
        for(File f: files != null ? files : new File[0]) {
            if (f.isFile()) {

                try {
                    LoadClassFromPath(path);
                } catch (ClassLoaderException e) {
                    notLoaded.add(f.getName());
                }

            }
        }

        if (!notLoaded.isEmpty()) {
            throw new ClassLoaderException("Unable to load the following classes: " + Arrays.toString(notLoaded.toArray()));
        }

    }

    /**
     * Load the class at the given path
     *
     * @param path
     * @throws ClassLoaderException
     */
    public static void LoadClassFromPath(String path) throws ClassLoaderException {

        try {
            File file = new File(path);
            JsonNode node = JSONHelper.JsonObjMapper().readTree(file);

            Load(node);

        } catch (IOException | ClassLoaderException e) {
            throw new ClassLoaderException(e);
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static void Load(String node) throws ClassLoaderException {

        try {
            JsonNode jsonNode = JSONHelper.JsonObjMapper().readTree(node);
            Load(jsonNode);
        } catch (IOException e) {
            throw new ClassLoaderException(e);
        }
    }

    /**
     * Load a class given a context JSON structure
     *
     * @param node
     * @throws ClassLoaderException
     */
    public static void Load(JsonNode node) throws ClassLoaderException {

        String targetClassPath = SOSLocalNode.settings.getServices().getCms().getLoadedPath();

        if (!new File(targetClassPath).exists()) {
            FileUtils.MakePath(targetClassPath);
        }

        try {
            ManifestType type = ManifestType.get(node.get(JSONConstants.KEY_TYPE).textValue());
            ClassBuilder classBuilder = ClassBuilderFactory.getClassBuilder(type.toString());
            String clazzString = classBuilder.constructClass(node);
            // System.out.println(clazzString); // THIS LINE IS HERE FOR DEBUG PURPOSES

            // Print class to file
            String clazzName = classBuilder.className(node);
            File sourceClazzFile = new File(Files.createTempDir() + "/" + clazzName + ".java");
            if (sourceClazzFile.exists()) { sourceClazzFile.delete(); }
            sourceClazzFile.deleteOnExit();
            try (PrintWriter out = new PrintWriter(sourceClazzFile)){
                out.println(clazzString);
            }

            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

            try(StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null)) {
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

                    LoadClassName(clazzName);

                } else {

                    for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                        SOS_LOG.log(LEVEL.ERROR, "Error on line " + diagnostic.getLineNumber() + " in " + diagnostic.getSource().toUri());
                    }
                }

            }

        } catch (IOException | ClassLoaderException | ClassBuilderException e) {
            throw new ClassLoaderException(e);
        }
    }

    /**
     * Load context class from path
     * @param className
     * @throws ClassLoaderException
     */
    private static void LoadClassName(String className) throws ClassLoaderException {

        try {
            java.lang.ClassLoader cl = SOSClassLoader();
            Class<?> cls = cl.loadClass(ContextClassBuilder.PACKAGE + "." + className);
            SOS_LOG.log(LEVEL.INFO, "Loaded class: " + cls.getName());

        } catch (ClassNotFoundException e) {
            throw new ClassLoaderException("Unable to load class: " + className);
        }

    }

    public static Predicate PredicateInstance(JsonNode node) throws ClassLoaderException {

        try {
            ManifestType type = ManifestType.get(node.get(JSONConstants.KEY_TYPE).textValue());
            ClassBuilder classBuilder = ClassBuilderFactory.getClassBuilder(type.toString());
            String className = classBuilder.className(node);
            String predicateCode = node.get(JSONConstants.KEY_PREDICATE).asText();
            long maxage = node.has(JSONConstants.KEY_PREDICATE_MAX_AGE) ? node.get(JSONConstants.KEY_PREDICATE_MAX_AGE).asLong() : 0; // TODO - have this in context

            java.lang.ClassLoader classLoader = SOSClassLoader();
            Class<?> clazz = Class.forName(ContextClassBuilder.PACKAGE + "." + className, true, classLoader);
            Constructor<?> constructor = clazz.getConstructor(String.class, long.class);
            return (Predicate) constructor.newInstance(predicateCode, maxage);

        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                ClassBuilderException | IOException e) {

            throw new ClassLoaderException("Unable to create instance for Predicate from jsonnode " + node.toString());
        }

    }

    public static Policy PolicyInstance() {

        // TODO - create policy instance similarly to the predicate one
        return null;
    }

    /**
     * REMOVEME - not a computational unit
     * Creates context instance
     *
     * @param className // TODO this is the guid of the context
     * @param jsonNode
     * @param policyActions
     * @param contextName
     * @param domain
     * @param codomain
     * @return
     * @throws ClassLoaderException
     */
    public static Context Instance(String className, JsonNode jsonNode, PolicyActions policyActions, String contextName, NodesCollection domain, NodesCollection codomain) throws ClassLoaderException {

        try {
            java.lang.ClassLoader classLoader = SOSClassLoader();
            Class<?> clazz = Class.forName(ContextClassBuilder.PACKAGE + "." + className, true, classLoader);
            Constructor<?> constructor = clazz.getConstructor(JsonNode.class, PolicyActions.class, String.class, NodesCollection.class, NodesCollection.class);
            Context context = (Context) constructor.newInstance(jsonNode, policyActions, contextName, domain, codomain);
            return context;
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
            throw new ClassLoaderException("Unable to create instance for class " + className);
        }
    }


    private static java.lang.ClassLoader SOSClassLoader() throws ClassLoaderException {

        String targetClassPath = SOSLocalNode.settings.getServices().getCms().getLoadedPath();
        File classesDirectory = new File(targetClassPath);

        if (!classesDirectory.exists()) {
            throw new ClassLoaderException("Cannot find path for java classes");
        }

        try {
            URL url = classesDirectory.toURL();
            URL[] urls = new URL[]{url};

            // Create a new class loader with the directory
            java.lang.ClassLoader cl = new URLClassLoader(urls);
            return cl;
        } catch (MalformedURLException e) {
            throw new ClassLoaderException("Cannot create class loader");
        }
    }
}

package uk.ac.standrews.cs.sos.impl.context;

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
import java.util.Collections;

/**
 * TODO - Load any maven dependencies using Maven Artifact Resolver
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextLoader {

    private static final String TEST_RESOURCES_PATH = "src/test/resources/contexts/";
    private static final String TEST_TARGET_PATH = "target/classes/";

    public static void LoadContext(String path) throws ContextLoaderException {

        try {
            File file = new File(path);
            JsonNode node = JSONHelper.JsonObjMapper().readTree(file);

            LoadContext(node);

        } catch (IOException | ContextLoaderException e) {
            throw new ContextLoaderException(e);
        }
    }

    public static void LoadContext(JsonNode node) throws ContextLoaderException {

        try {
            String clazzString = ContextBuilder.ConstructClass(node);

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

                String path = TEST_TARGET_PATH + ContextBuilder.PACKAGE.replace(".", "/") + "/" + clazzName + ".class";
                File dir = new File(path).getParentFile();
                Load(dir, clazzName);

            } else {

                for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                    System.out.format("Error on line %d in %s%n",
                            diagnostic.getLineNumber(),
                            diagnostic.getSource().toUri());
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
            Class cls = cl.loadClass(ContextBuilder.PACKAGE + "." + className);
            SOS_LOG.log(LEVEL.INFO, "Loaded context: " + cls.getName());

        } catch (MalformedURLException | ClassNotFoundException e) {
            throw new ContextLoaderException("Unable to load context for class: " + className);
        }

    }

    public static Context Instance(String className) throws ContextLoaderException {

        try {
            Class<?> clazz = Class.forName(ContextBuilder.PACKAGE + "." + className);

            Context context = (Context) clazz.newInstance();
            return context.build();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new ContextLoaderException("Unable to create instance for class " + className);
        }
    }

    /**
     * @deprecated
     *
     * @param agent
     * @param className
     * @return
     * @throws ContextLoaderException
     */
    public static Context Instance(Agent agent, String className) throws ContextLoaderException {

        try {
            Class<?> clazz = Class.forName(ContextBuilder.PACKAGE + "." + className);

            Class[] cArg = new Class[2];
            cArg[0] = SOSAgent.class;
            cArg[1] = String.class;

            return (Context) clazz.getDeclaredConstructor(cArg).newInstance(agent, className);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException |
                NoSuchMethodException | InvocationTargetException e) {
            throw new ContextLoaderException("Unable to create instance for class " + className);
        }
    }

//    /**
//     *
//     * @deprecated
//     *
//     * Load multiple contexts at path
//     * @param path
//     * @throws ContextLoaderException
//     */
//    public static void Load(String path) throws ContextLoaderException {
//
//        ArrayList<String> notLoaded = new ArrayList<>();
//
//        File folder = new File(path);
//        File[] files = folder.listFiles();
//        for(File f:files) {
//            if (f.isFile()) {
//                String name = f.getName();
//                if (name.length() <= 5) {
//                    notLoaded.add(name);
//                    continue;
//                }
//
//                name = name.substring(0, name.length() - 5); // Ignore ".java" extension in filename
//                try {
//                    Load(path, name);
//                } catch (ContextLoaderException e) {
//                    notLoaded.add(f.getName());
//                }
//            }
//        }
//
//        if (!notLoaded.isEmpty()) {
//            throw new ContextLoaderException("Unable to load the following classes: " + Arrays.toString(notLoaded.toArray()));
//        }
//
//    }
}

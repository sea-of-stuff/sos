/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module core.
 *
 * core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.sos.impl.context.reflection;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.io.Files;
import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.exceptions.reflection.ClassBuilderException;
import uk.ac.standrews.cs.sos.exceptions.reflection.ClassLoaderException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Policy;
import uk.ac.standrews.cs.sos.model.Predicate;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSReflection {

    private LocalStorage localStorage;
    private Set<String> loadedClasses;

    private static SOSReflection instance;

    private SOSReflection(LocalStorage localStorage) {
        this.localStorage = localStorage;
        loadedClasses = new LinkedHashSet<>();
    }

    public static void init(LocalStorage localStorage) {

        instance = new SOSReflection(localStorage);
    }

    public static SOSReflection instance() {

        return instance;
    }

    /**
     * Load a class given a context JSON structure
     *
     * @param node JSON representation for computational unit to load
     * @throws ClassLoaderException if computational unit could not be loaded properly
     */
    public void load(JsonNode node) throws ClassLoaderException {

        try {
            IDirectory targetClassPath = localStorage.getJavaDirectory();

            ManifestType type = ManifestType.get(node.get(JSONConstants.KEY_TYPE).textValue());
            SOS_LOG.log(LEVEL.INFO, "Preparing to load class for computation unit of type: " + type.toString());
            ClassBuilder classBuilder = ClassBuilderFactory.getClassBuilder(type.toString());
            String clazzString = classBuilder.constructClass(node);
            // System.out.println(clazzString); // THIS LINE IS HERE FOR DEBUG PURPOSES

            // Print class to file
            String clazzName = classBuilder.className(node);
            // Assume that we have access to the local file system and can create a temporary directory there
            File sourceClazzFile = new File(Files.createTempDir() + "/" + clazzName + ".java");
            if (sourceClazzFile.exists()) {
                boolean deleted = sourceClazzFile.delete();
                if (!deleted) {
                    throw new IOException("Unable to delete existing source class file: " + sourceClazzFile.getAbsolutePath());
                }
            }
            sourceClazzFile.deleteOnExit();
            try (PrintWriter out = new PrintWriter(sourceClazzFile)){
                out.println(clazzString);
            }

            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

            try(StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null)) {
                fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Collections.singletonList(targetClassPath.toFile()));

                // Compile the file
                JavaCompiler.CompilationTask task = compiler.getTask(
                        null,
                        fileManager,
                        diagnostics,
                        null,
                        null,
                        fileManager.getJavaFileObjects(sourceClazzFile));

                if (task.call()) {
                    loadClassName(clazzName);
                } else {
                    for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                        SOS_LOG.log(LEVEL.ERROR, "Error on line " + diagnostic.getLineNumber() + " in " + diagnostic.getSource().toUri());
                    }
                }

            }

        } catch (IOException | DataStorageException | ClassLoaderException | ClassBuilderException e) {
            throw new ClassLoaderException(e);
        }
    }

    /**
     * Load context class from path
     * @param className to be loaded
     * @throws ClassLoaderException if class could not be loaded
     */
    private void loadClassName(String className) throws ClassLoaderException {

        if (loadedClasses.contains(ClassBuilderFactory.PACKAGE + "." + className)) return;

        try {
            java.lang.ClassLoader cl = SOSClassLoader();
            Class<?> cls = cl.loadClass(ClassBuilderFactory.PACKAGE + "." + className);
            loadedClasses.add(ClassBuilderFactory.PACKAGE + "." + className);
            SOS_LOG.log(LEVEL.INFO, "Loaded class: " + cls.getName());

        } catch (ClassNotFoundException e) {
            throw new ClassLoaderException("Unable to load class: " + className);
        }

    }

    public Predicate predicateInstance(JsonNode node) throws ClassLoaderException {

        try {
            ClassBuilder classBuilder = ClassBuilderFactory.getClassBuilder("PREDICATE");
            String className = classBuilder.className(node);

            java.lang.ClassLoader classLoader = SOSClassLoader();
            Class<?> clazz = Class.forName(ClassBuilderFactory.PACKAGE + "." + className, true, classLoader);
            Constructor<?> constructor = clazz.getConstructor(JsonNode.class);
            return (Predicate) constructor.newInstance(node);

        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                ClassBuilderException | IOException e) {

            throw new ClassLoaderException("Unable to create instance for Predicate from jsonnode " + node.toString());
        } catch (Exception e) {

            throw new ClassLoaderException("General exception while creating predicate instance. jsonnode: " + node.toString());
        }

    }

    public Policy policyInstance(JsonNode node) throws ClassLoaderException {

        try {
            ClassBuilder classBuilder = ClassBuilderFactory.getClassBuilder("POLICY");
            String className = classBuilder.className(node);

            java.lang.ClassLoader classLoader = SOSClassLoader();
            Class<?> clazz = Class.forName(ClassBuilderFactory.PACKAGE + "." + className, true, classLoader);
            Constructor<?> constructor = clazz.getConstructor(JsonNode.class);
            return (Policy) constructor.newInstance(node);

        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                ClassBuilderException | IOException e) {

            throw new ClassLoaderException("Unable to create instance for Policy from jsonnode " + node.toString());
        } catch (Exception e) {

            throw new ClassLoaderException("General exception while creating policy instance. jsonnode: " + node.toString());
        }

    }

    private java.lang.ClassLoader SOSClassLoader() throws ClassLoaderException {

        try {
            IDirectory targetClassPath = localStorage.getJavaDirectory();

            URL url = targetClassPath.getPath().toUri().toURL();
            URL[] urls = new URL[]{url};

            // Create a new class loader with the directory
            return new URLClassLoader(urls);
        } catch (IOException | DataStorageException e) {
            throw new ClassLoaderException("Cannot create class loader");
        }
    }
}

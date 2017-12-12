package uk.ac.standrews.cs.sos.experiments;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.io.FileUtils;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.exceptions.ServiceException;
import uk.ac.standrews.cs.sos.exceptions.context.ContextException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.exceptions.userrole.UserRolePersistException;
import uk.ac.standrews.cs.sos.experiments.distribution.NetworkException;
import uk.ac.standrews.cs.sos.experiments.distribution.NetworkOperations;
import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.VersionBuilder;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.URILocation;
import uk.ac.standrews.cs.sos.impl.metadata.MetadataBuilder;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.instrument.InstrumentFactory;
import uk.ac.standrews.cs.sos.instrument.StatsTYPE;
import uk.ac.standrews.cs.sos.model.*;
import uk.ac.standrews.cs.sos.services.ContextService;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface ExperimentUnit {

    /**
     * The setupIteration for the experiment unit
     *
     * @throws ExperimentException if the experiment unit could not be setup
     */
    void setup() throws ExperimentException;

    /**
     * The code that runs the actual experiment for this unit
     *
     * @throws ExperimentException if the experiment unit could not be run
     */
    void run() throws ExperimentException;

    /**
     * Add the data inside the folder to the local node
     *
     * @param node where to add the content
     * @param folder where the content for the experiment is
     * @throws IOException if content could not be added properly
     */
    default List<IGUID> addFolderContentToNode(SOSLocalNode node, File folder) throws IOException {

        PlainFileVisitor<Path> fv = new PlainFileVisitor<>(node);

        long start = System.nanoTime();
        System.out.println("Files added: ");
        Files.walkFileTree(folder.toPath(), fv);
        System.out.println("\nTime to add all contents: " + (System.nanoTime() - start) / 1000000000.0 + " seconds");

        return fv.getVersions();
    }

    class PlainFileVisitor<T extends Path> extends SimpleFileVisitor<T> {

        SOSLocalNode node;
        int counter;
        List<IGUID> versions = new LinkedList<>();

        public PlainFileVisitor(SOSLocalNode node) {
            this.node = node;

            counter = 0;
        }

        public List<IGUID> getVersions() {
            return versions;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            // System.out.println("File " + file.toUri().toString());
            counter++;
            if (counter % 100 == 0) {
                System.out.print("  " + counter);
            }

            try {
                AtomBuilder atomBuilder = new AtomBuilder()
                        .setLocation(new URILocation(file.toUri().toString()));

                MetadataBuilder metadataBuilder = new MetadataBuilder()
                        .setData(atomBuilder.getData());

                VersionBuilder versionBuilder = new VersionBuilder()
                        .setAtomBuilder(atomBuilder)
                        .setMetadataBuilder(metadataBuilder);

                Version version = node.getAgent().addData(versionBuilder);
                versions.add(version.guid());
                InstrumentFactory.instance().measure(StatsTYPE.experiment, StatsTYPE.none, "Added version " + version.guid().toShortString() + " from URI " + file.toString());
            } catch (URISyntaxException  | ServiceException e) {
                e.printStackTrace();
            }

            return FileVisitResult.CONTINUE;
        }

    }

    default List<IGUID> addFolderContentToNode(SOSLocalNode node, File folder, Role role) throws IOException {

        ProtectedFileVisitor<Path> fv = new ProtectedFileVisitor<>(node, role);

        long start = System.nanoTime();
        System.out.println("Files added: ");
        Files.walkFileTree(folder.toPath(), fv);
        System.out.println("\nTime to add all contents: " + (System.nanoTime() - start) / 1000000000.0 + " seconds");

        return fv.getVersions();
    }

    class ProtectedFileVisitor<T extends Path> extends PlainFileVisitor<T> {

        Role role;

        public ProtectedFileVisitor(SOSLocalNode node, Role role) {
            super(node);
            this.role = role;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            // System.out.println("File " + file.toUri().toString());
            counter++;
            if (counter % 100 == 0) {
                System.out.print("  " + counter);
            }

            try {
                AtomBuilder atomBuilder = (AtomBuilder) new AtomBuilder()
                        .setLocation(new URILocation(file.toUri().toString()))
                        .setRole(role)
                        .setProtectFlag(true);
                VersionBuilder versionBuilder = new VersionBuilder()
                        .setAtomBuilder(atomBuilder);

                Version version = node.getAgent().addData(versionBuilder);
                versions.add(version.guid());
                InstrumentFactory.instance().measure(StatsTYPE.experiment, StatsTYPE.none, "Added version " + version.guid().toShortString() + " from URI " + file.toString());
            } catch (URISyntaxException  | ServiceException e) {
                e.printStackTrace();
            }

            return FileVisitResult.CONTINUE;
        }

    }

    /**
     * Add the users/roles for this experiment to the node
     *
     * @param node where to add the users and roles
     * @param experiment settings
     */
    default void addFolderUSROToNode(SOSLocalNode node, ExperimentConfiguration.Experiment experiment) {

        File folder = new File(experiment.getExperimentNode().getUsroPath());

        File keysToBeAdded = new File(folder, "keys");
        File keysFolder = new File(SOSLocalNode.settings.getKeys().getLocation());

        try {
            File[] keys = keysToBeAdded.listFiles();
            assert keys != null;
            for (File srcFile: keys) {
                if (srcFile.isFile()) {
                    FileUtils.copyFileToDirectory(srcFile, keysFolder);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        File[] listOfFiles = folder.listFiles();
        assert listOfFiles != null;
        for (File file : listOfFiles) {

            if (file.isFile() && file.getName().endsWith(".json")) {

                try {
                    JsonNode jsonNode = JSONHelper.JsonObjMapper().readTree(file);
                    ManifestType type = ManifestType.get(jsonNode.get(JSONConstants.KEY_TYPE).textValue());
                    switch(type) {

                        case ROLE:
                            Role role = JSONHelper.JsonObjMapper().readValue(file, Role.class);
                            node.getUSRO().addRole(role);
                            break;
                        case USER:
                            User user = JSONHelper.JsonObjMapper().readValue(file, User.class);
                            node.getUSRO().addUser(user);
                            break;
                    }


                } catch (IOException | UserRolePersistException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    default List<IGUID> addFolderContentToNodeAsAtoms(SOSLocalNode node, File folder) throws IOException {

        FileVisitor<Path> fv = new FileVisitor<>(node);

        long start = System.nanoTime();
        System.out.println("Files added: ");
        Files.walkFileTree(folder.toPath(), fv);
        System.out.println("\nTime to add all contents: " + (System.nanoTime() - start) / 1000000000.0 + " seconds");

        return fv.getVersions();
    }

    class FileVisitor<T extends Path> extends PlainFileVisitor<T> {

        public FileVisitor(SOSLocalNode node) {
            super(node);
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            // System.out.println("File " + file.toUri().toString());
            counter++;
            if (counter % 100 == 0) {
                System.out.print("  " + counter);
            }

            try {
                AtomBuilder atomBuilder = new AtomBuilder()
                        .setLocation(new URILocation(file.toUri().toString()));

                Atom atom = node.getStorageService().addAtom(atomBuilder);
                versions.add(atom.guid());

                InstrumentFactory.instance().measure(StatsTYPE.experiment, StatsTYPE.none, "Added atom " + atom.guid().toShortString() + " from URI " + file.toString());
            } catch (URISyntaxException | DataStorageException | ManifestPersistException e) {
                e.printStackTrace();
            }

            return FileVisitResult.CONTINUE;
        }

    }

    default void addContext(ContextService cms, ExperimentConfiguration.Experiment experiment, String context_name) throws ContextException {
        String contextPath = experiment.getExperimentNode().getContextsPath() + experiment.getName() + "/" + context_name + ".json";
        IGUID context = cms.addContext(new File(contextPath));
        InstrumentFactory.instance().measure(StatsTYPE.experiment, StatsTYPE.none, "Added context " + context_name + " " + context.toShortString());
    }

    default void sendFiles(ExperimentConfiguration.Experiment.Node node, String lDirectoryPath, String rDirectoryPath) throws ExperimentException {

        System.out.println("Sending files via SCP to node: " + node.toString());
        try (NetworkOperations scp = new NetworkOperations()){
            scp.setSsh(node.getSsh());
            scp.connect();

            String path = node.getPath() + node.getSsh().getUser() + "/";
            File[] listOfFiles = new File(lDirectoryPath).listFiles();
            assert listOfFiles != null;
            for (File file : listOfFiles) {

                long start = System.nanoTime();
                scp.sendFile(file.getAbsolutePath(), path + rDirectoryPath + file.getName(), false);
                long duration = System.nanoTime() - start;
                InstrumentFactory.instance().measure(StatsTYPE.policies, StatsTYPE.scp, Long.toString(file.length()), duration);

            }

        } catch (IOException | NetworkException e) {
            throw new ExperimentException();
        }
    }

    default void sendFilesViaRuntime(ExperimentConfiguration.Experiment.Node node, String lDirectoryPath, String rDirectoryPath) throws ExperimentException {

        System.out.println("Sending files via SCP to node: " + node.toString());

        try {
            String path = node.getPath() + node.getSsh().getUser() + "/";

            // Create folder at remote node
            Runtime rt = Runtime.getRuntime();
            System.out.println("Creating remote path for SCP");
            Process process = rt.exec("ssh " + node.getSsh().getHost() + " 'mkdir -p " + path + rDirectoryPath + "'");
            int exitVal = process.waitFor();
            System.out.println("Remote path for SCP created. Error code: " + exitVal);

            File[] listOfFiles = new File(lDirectoryPath).listFiles();
            assert listOfFiles != null;
            long start = System.nanoTime();
            for (File file : listOfFiles) {

                process = rt.exec("scp " + file.getAbsolutePath() + " " + node.getSsh().getHost() + ":" + path + rDirectoryPath + file.getName());
                exitVal = process.waitFor();
                if (exitVal != 0) {
                    throw new ExperimentException("Exception for SCP operation");
                }
            }
            long duration = System.nanoTime() - start;
            InstrumentFactory.instance().measure(StatsTYPE.policies, StatsTYPE.policy_apply_dataset, StatsTYPE.scp.name(), duration);

        } catch (IOException | InterruptedException e) {
            throw new ExperimentException();
        }

    }

    default void rest_a_bit() throws ExperimentException {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new ExperimentException();
        }
    }

}

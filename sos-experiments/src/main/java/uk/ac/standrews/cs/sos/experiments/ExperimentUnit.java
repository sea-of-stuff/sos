package uk.ac.standrews.cs.sos.experiments;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.io.FileUtils;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.exceptions.ServiceException;
import uk.ac.standrews.cs.sos.exceptions.context.ContextException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.exceptions.userrole.UserRolePersistException;
import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;
import uk.ac.standrews.cs.sos.impl.datamodel.VersionManifest;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.VersionBuilder;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.URILocation;
import uk.ac.standrews.cs.sos.impl.manifest.ManifestFactory;
import uk.ac.standrews.cs.sos.impl.metadata.MetadataBuilder;
import uk.ac.standrews.cs.sos.impl.node.NodesCollectionImpl;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.impl.protocol.TasksQueue;
import uk.ac.standrews.cs.sos.impl.protocol.tasks.ManifestDeletion;
import uk.ac.standrews.cs.sos.instrument.InstrumentFactory;
import uk.ac.standrews.cs.sos.instrument.StatsTYPE;
import uk.ac.standrews.cs.sos.model.*;
import uk.ac.standrews.cs.sos.services.ContextService;
import uk.ac.standrews.cs.sos.services.NodeDiscoveryService;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.utils.Misc;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

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
     *
     * @throws ExperimentException
     */
    default void finish() throws ExperimentException {
        // do nothing
    }

    /**
     * Add the data inside the folder to the local node
     *
     * @param node where to add the content
     * @param folder where the content for the experiment is
     * @param datasetSize limits the dataset for this experiment by this param. If -1, no limit will be enforced
     * @throws IOException if content could not be added properly
     */
    default List<IGUID> addFolderContentToNode(SOSLocalNode node, File folder, int datasetSize) throws IOException {

        PlainFileVisitor<Path> fv = new PlainFileVisitor<>(node, datasetSize);

        long start = System.nanoTime();
        System.out.println("Files added: ");
        Files.walkFileTree(folder.toPath(), fv);
        System.out.println("\nTime to add all contents: " + (System.nanoTime() - start) / 1000000000.0 + " seconds");

        return fv.getVersions();
    }

    class PlainFileVisitor<T extends Path> extends SimpleFileVisitor<T> {

        SOSLocalNode node;
        int datasetSize;
        int counter;
        List<IGUID> versions = new LinkedList<>();

        public PlainFileVisitor(SOSLocalNode node, int datasetSize) {
            this.node = node;
            this.datasetSize = datasetSize;

            counter = 0;
        }

        public List<IGUID> getVersions() {
            return versions;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            // System.out.println("File " + file.toUri().toString());
            if (datasetSize != -1 && datasetSize <= counter) {
                return FileVisitResult.CONTINUE;
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

            counter++;
            if (counter % 100 == 0) {
                System.out.print("  " + counter);
            }

            return FileVisitResult.CONTINUE;
        }

    }

    default List<IGUID> addFolderContentToNode(SOSLocalNode node, File folder, Role role) throws IOException {

        ProtectedFileVisitor<Path> fv = new ProtectedFileVisitor<>(node, role);

        long start = System.nanoTime();
        System.out.println("[Atom+Version] Files added: ");
        Files.walkFileTree(folder.toPath(), fv);
        System.out.println("\n[Atom+Version] Time to add all contents: " + (System.nanoTime() - start) / 1000000000.0 + " seconds");

        return fv.getVersions();
    }

    class ProtectedFileVisitor<T extends Path> extends PlainFileVisitor<T> {

        Role role;

        public ProtectedFileVisitor(SOSLocalNode node, Role role) {
            super(node, -1);
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
                    JsonNode jsonNode = JSONHelper.jsonObjMapper().readTree(file);
                    ManifestType type = ManifestType.get(jsonNode.get(JSONConstants.KEY_TYPE).textValue());
                    switch(type) {

                        case ROLE:
                            Role role = JSONHelper.jsonObjMapper().readValue(file, Role.class);
                            node.getUSRO().addRole(role);
                            break;
                        case USER:
                            User user = JSONHelper.jsonObjMapper().readValue(file, User.class);
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
        System.out.println("[Atom] Files added: ");
        Files.walkFileTree(folder.toPath(), fv);
        System.out.println("\n[Atom] Time to add all contents: " + (System.nanoTime() - start) / 1000000000.0 + " seconds");

        return fv.getVersions();
    }

    class FileVisitor<T extends Path> extends PlainFileVisitor<T> {

        public FileVisitor(SOSLocalNode node) {
            super(node, -1);
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

    default IGUID addContext(ContextService cms, ExperimentConfiguration.Experiment experiment, String context_name) throws ContextException {
        String contextPath = experiment.getExperimentNode().getContextsPath() + experiment.getName() + "/" + context_name + ".json";
        IGUID context = cms.addContext(new File(contextPath));
        InstrumentFactory.instance().measure(StatsTYPE.experiment, StatsTYPE.none, "Added context " + context_name + " " + context.toShortString());

        return context;
    }

    default void rest_a_bit() throws ExperimentException {
        rest_a_bit("", 1000);
    }

    default void rest_a_bit(long milliseconds) throws ExperimentException {
        rest_a_bit("", milliseconds);
    }

    default void rest_a_bit(String message, long milliseconds) throws ExperimentException {
        try {
            System.out.println(message + "--- Going to sleep for " + (milliseconds / 1000) + " seconds ---");
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            throw new ExperimentException();
        }
    }

    /**
     *
     * @param experiment configuration
     * @param node experiment node
     * @param context with domain where to distributed content
     * @param datasetSize limits the dataset for this experiment by this param. If -1, no limit will be enforced
     * @return set of versions ref for versions added
     * @throws IOException if data could not be distributed properly
     */
    default List<IGUID> distributeData(ExperimentConfiguration.Experiment experiment, SOSLocalNode node, Context context, int datasetSize) throws IOException {

        int domainSize = context.domain(false).size();
        System.out.println("Domain size: " + domainSize + " (local node included)");

        ExperimentConfiguration.Experiment.ExperimentNode experimentNode = experiment.getExperimentNode();
        String datasetPath = experimentNode.getDatasetPath();
        File folderDataset = new File(datasetPath);

        List<IGUID> addedContents = new LinkedList<>();
        if (domainSize == 1) {
            addedContents = addFolderContentToNode(node, folderDataset, datasetSize);

        } else {
            assert(context.domain(false).type() == NodesCollectionType.SPECIFIED);

            // Retrieve list of files to distribute
            File[] listOfFiles = folderDataset.listFiles();
            assert(listOfFiles != null);
            // Truncate dataset if necessary
            if (datasetSize != -1) {
                if (datasetSize > listOfFiles.length) throw new IOException("Original dataset is too small to be truncated with a size of " + datasetSize);
                listOfFiles = Arrays.copyOf(listOfFiles, datasetSize);
            }
            Misc.shuffleArray(listOfFiles);
            System.out.println("Total number of files: " + listOfFiles.length);

            // The split is done considering this local node too.
            // The split is approximated with an upper bound
            int filesPerSublist = (listOfFiles.length + (domainSize) - 1) / (domainSize);
            System.out.println("Files per node: " + filesPerSublist);

            // Perform list splitting into sublists
            // Last elements of last sublist might contain null values
            File[][] sublists = new File[domainSize][filesPerSublist];
            if (experimentNode.isEqual_distribution_dataset()) {

                for(int i = 0; i < domainSize; i++) {
                    for(int j = 0; j < filesPerSublist && (i * filesPerSublist + j) < listOfFiles.length; j++) {
                        sublists[i][j] = listOfFiles[i * filesPerSublist + j];
                    }
                }

            } else {

                // TODO - consider ranges as specified in configuration
                int[][] distributionSets = experimentNode.getDistribution_sets();
            }

            // Add first sublist to local node (which is always part of the domain)
            List<IGUID> addedContentsInLocalNode = addContentToLocalNode(node, sublists[0]);
            addedContents.addAll(addedContentsInLocalNode);

            // Distribute data indexed by sublists to remote nodes
            int i = 1;
            for(IGUID nodeInDomain:context.domain(true).nodesRefs()) {
                List<IGUID> addedContentsInNode = distributeDataToNode(node, sublists[i], nodeInDomain);
                addedContents.addAll(addedContentsInNode);
                i++;
            }

        }

        return addedContents;
    }

    default List<IGUID> addContentToLocalNode(SOSLocalNode node, File[] sublist) throws IOException {

        List<IGUID> addedContents = new LinkedList<>();

        for(File file:sublist) {

            if (file == null) continue;

            try {
                Location dataLocation = new URILocation(file.getAbsolutePath());

                AtomBuilder atomBuilder = new AtomBuilder()
                        .setLocation(dataLocation);

                Atom atom = node.getStorageService().addAtom(atomBuilder); // using this method the data is added properly to the other node

                IGUID invariant = GUIDFactory.generateGUID(atom.guid().toMultiHash());
                Version version = ManifestFactory.createVersionManifest(atom.guid(), invariant, null, null, null);
                node.getMDS().addManifest(version);

                addedContents.add(version.guid());

            } catch (GUIDGenerationException | URISyntaxException | DataStorageException | ManifestPersistException | ManifestNotMadeException e) {
                throw new IOException("Unable to add data and/or version to local experiment node properly");
            }
        }

        System.out.println("Added " + addedContents.size() + " files to local node with GUID " + node.guid().toMultiHash());

        return addedContents;
    }

    default List<IGUID> distributeDataToNode(SOSLocalNode node, File[] sublist, IGUID nodeRef) throws IOException {

        List<IGUID> addedContents = new LinkedList<>();
        for(File file:sublist) {

            if (file == null) continue;

            IGUID addedContent = distributeDatumToNode(node, file, nodeRef);
            addedContents.add(addedContent);
        }

        System.out.println("Distributed " + addedContents.size() + " files to node with GUID " + nodeRef.toMultiHash());

        return addedContents;
    }

    default IGUID distributeDatumToNode(SOSLocalNode node, File file, IGUID nodeRef) throws IOException {

        try {
            Set<IGUID> nodes = new LinkedHashSet<>();
            nodes.add(nodeRef);
            NodesCollection remoteNode = new NodesCollectionImpl(nodes);

            Location dataLocation = new URILocation(file.getAbsolutePath());

            AtomBuilder atomBuilder = new AtomBuilder()
                    .setDoNotStoreDataLocally(true)
                    .setDoNotStoreManifestLocally(true)
                    .setReplicationFactor(1)
                    .setReplicationNodes(remoteNode)
                    .setLocation(dataLocation);

            Atom atom  = node.getStorageService().addAtom(atomBuilder); // using this method the data is added properly to the other node

            IGUID invariant = GUIDFactory.generateGUID(atom.guid().toMultiHash());
            Version version = ManifestFactory.createVersionManifest(atom.guid(), invariant, null, null, null);
            node.getMDS().addManifest(version, false, remoteNode, 1, false);

            return version.guid();

        } catch (DataStorageException | ManifestPersistException | URISyntaxException | ManifestNotMadeException | GUIDGenerationException e) {
            throw new IOException("Unable to distribute data and/or version to remote experiment node properly");
        }
    }

    default void deleteData(SOSLocalNode node, Context context, List<IGUID> versionsToDelete) {

        System.out.println("Delete data in local node.");
        for(IGUID guid:versionsToDelete) {

            try {
                node.getMDS().delete(guid);
            } catch (ManifestNotFoundException e) { /* IGNOREME */ }

        }

        System.out.println("Delete data in remote nodes.");
        NodeDiscoveryService nodeDiscoveryService = node.getNDS();

        // Delete versions only. It does not matter if atoms are deleted or not since they are not processed directly by contexts
        for(IGUID guid:versionsToDelete) {
            Version versionToDelete = new VersionManifest(GUIDFactory.generateRandomGUID(), guid, GUIDFactory.generateRandomGUID(), null,
                    GUIDFactory.generateRandomGUID(), GUIDFactory.generateRandomGUID(), "");

            ManifestDeletion manifestDeletion = new ManifestDeletion(nodeDiscoveryService, context.domain(true), versionToDelete);
            TasksQueue.instance().performSyncTask(manifestDeletion);
        }
    }

    default void deleteContext(SOSLocalNode node, Context context) {

        NodeDiscoveryService nodeDiscoveryService = node.getNDS();
        NodesCollection domain = context.domain(true);
        ManifestDeletion manifestDeletion = new ManifestDeletion(nodeDiscoveryService, domain, context);
        TasksQueue.instance().performSyncTask(manifestDeletion);
    }
}

package uk.ac.standrews.cs.sos.experiments.experiments;

import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.experiments.Experiment;
import uk.ac.standrews.cs.sos.experiments.ExperimentConfiguration;
import uk.ac.standrews.cs.sos.experiments.ExperimentUnit;
import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.URILocation;
import uk.ac.standrews.cs.sos.impl.node.NodesCollectionImpl;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.model.Atom;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.services.StorageService;

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
 * Test data replication - sequential vs parallel
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Experiment_REPL_1 extends BaseExperiment implements Experiment {

    private Iterator<ExperimentUnit> experimentUnitIterator;

    // Must be static to be initialized before constructor
    private static Integer[] replicationFactors = new Integer[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    private static Boolean[] replicationMethods = new Boolean[] { true, false };

    public Experiment_REPL_1(ExperimentConfiguration experimentConfiguration, String outputFilename) throws ExperimentException {
        super(experimentConfiguration, outputFilename);

        List<ExperimentUnit> units = new LinkedList<>();
        for(int i = 0; i < experiment.getSetup().getIterations(); i++) {
            for (int replicationFactor : replicationFactors) {
                for(boolean replicationMethod : replicationMethods) {
                    units.add(new ExperimentUnit_REPL_1(replicationFactor, replicationMethod));
                }
            }
        }
        Collections.shuffle(units);

        experimentUnitIterator = units.iterator();
    }

    @Override
    public ExperimentUnit getExperimentUnit() {

        return experimentUnitIterator.next();
    }

    @Override
    public int numberOfTotalIterations() {

        return experiment.getSetup().getIterations() * replicationFactors.length * replicationMethods.length;
    }

    private class ExperimentUnit_REPL_1 implements ExperimentUnit {

        private StorageService storageService;
        private NodesCollection codomain;

        private int replicationFactor;
        private boolean parallel;

        public ExperimentUnit_REPL_1(int replicationFactor, boolean parallel) {
            this.replicationFactor = replicationFactor;
            this.parallel = parallel;
        }

        @Override
        public void setup() throws ExperimentException {
            System.out.println("Node GUID is " + node.guid().toMultiHash());
            System.out.println("Parallel: " + parallel + " ReplicationFactor: " + replicationFactor);

            storageService = node.getStorageService();

            try {
                Set<IGUID> nodeRefs = new LinkedHashSet<>();
                nodeRefs.add(GUIDFactory.recreateGUID("SHA256_16_aed7bbf1e6ef5c8d22162c096ab069b8d2056696be262551951660aac6d836ef")); // <sif/hogun>-2
                nodeRefs.add(GUIDFactory.recreateGUID("SHA256_16_14cdbb3b1154681751681ecf7f0a627cdfb858cb928a6d045befede3099fc2b4")); // <sif/hogun>-3
                nodeRefs.add(GUIDFactory.recreateGUID("SHA256_16_867ab9daa29ed55ec7761ba4218076cfeaa1f308d6b13cee3e5323b273b24b1f")); // <sif/hogun>-4
                nodeRefs.add(GUIDFactory.recreateGUID("SHA256_16_39cf1bcbe1ad206e2e862d9abe5158e05338df0e348661b5a0a8c952337921c0")); // <sif/hogun>-5
                nodeRefs.add(GUIDFactory.recreateGUID("SHA256_16_5c1e5af5c2c816978946387a3b6ba2bfc5a182226ad1b50780243ba392c830f0")); // <sif/hogun>-6
                nodeRefs.add(GUIDFactory.recreateGUID("SHA256_16_5057aaafd6defeab2a0739ea69095f271d94af2fbb20812b06b57434fda1a790")); // <sif/hogun>-7
                nodeRefs.add(GUIDFactory.recreateGUID("SHA256_16_e56ed40c6857d2aba85d9e06a2044d845c851b5456772d60402a6d8049f80086")); // <sif/hogun>-8
                nodeRefs.add(GUIDFactory.recreateGUID("SHA256_16_6462c57359b1f4a4c96b6e4ef00469ee9c566763a212a7144d2ff76ad5b1c439")); // <sif/hogun>-9
                nodeRefs.add(GUIDFactory.recreateGUID("SHA256_16_d23876cfd080fc08074b478bf05e3fb22e7abf0164a8396e47884c9b7010540f")); // <sif/hogun>-10
                nodeRefs.add(GUIDFactory.recreateGUID("SHA256_16_b00f88a8eaf152286b097d057a0e4bd8e74465f2562e4a5bccbd14a660702e59")); // <sif/hogun>-11

                codomain = new NodesCollectionImpl(nodeRefs);
            } catch (GUIDGenerationException e) {
                throw new ExperimentException(e);
            }
        }

        @Override
        public void run() throws ExperimentException {

            try {
                if (parallel) {
                    SOSLocalNode.settings.getServices().getStorage().setSequentialReplication(false);
                } else {
                    SOSLocalNode.settings.getServices().getStorage().setSequentialReplication(true);
                }

                String datasetPath = experiment.getExperimentNode().getDatasetPath();
                addFolderContentToNode(storageService, new File(datasetPath), codomain, replicationFactor);
            } catch (IOException e) {
                throw new ExperimentException(e);
            }
        }

        List<IGUID> addFolderContentToNode(StorageService storageService, File folder, NodesCollection codomain, int replicationFactor) throws IOException {

            PlainFileVisitor<Path> fv = new PlainFileVisitor<>(storageService, codomain, replicationFactor);

            long start = System.nanoTime();
            System.out.println("Files added: ");
            Files.walkFileTree(folder.toPath(), fv);
            System.out.println("\nTime to add all contents: " + (System.nanoTime() - start) / 1000000000.0 + " seconds");

            return fv.getVersions();
        }

        class PlainFileVisitor<T extends Path> extends SimpleFileVisitor<T> {

            StorageService storageService;
            NodesCollection codomain;
            int replicationFactor;
            int counter;
            List<IGUID> versions = new LinkedList<>();

            public PlainFileVisitor(StorageService storageService, NodesCollection codomain, int replicationFactor) {
                this.storageService = storageService;
                this.codomain = codomain;
                this.replicationFactor = replicationFactor;

                counter = 0;
            }

            public List<IGUID> getVersions() {
                return versions;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                // System.out.println("File " + file.toUri().toString());

                try {
                    AtomBuilder atomBuilder = new AtomBuilder()
                            .setLocation(new URILocation(file.toUri().toString()))
                            .setReplicationNodes(codomain)
                            .setReplicationFactor(replicationFactor)
                            .setDoNotStoreDataLocally(true)
                            .setDoNotStoreManifestLocally(true);

                    Atom atom = storageService.addAtom(atomBuilder);
                } catch (URISyntaxException | DataStorageException | ManifestPersistException e) {
                    e.printStackTrace();
                }

                counter++;
                if (counter % 100 == 0) {
                    System.out.print("  " + counter);
                }

                return FileVisitResult.CONTINUE;
            }

        }

    }

}

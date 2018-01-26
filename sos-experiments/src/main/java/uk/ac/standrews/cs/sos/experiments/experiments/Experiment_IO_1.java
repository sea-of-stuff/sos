package uk.ac.standrews.cs.sos.experiments.experiments;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.ServiceException;
import uk.ac.standrews.cs.sos.experiments.Experiment;
import uk.ac.standrews.cs.sos.experiments.ExperimentConfiguration;
import uk.ac.standrews.cs.sos.experiments.ExperimentUnit;
import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;
import uk.ac.standrews.cs.sos.instrument.InstrumentFactory;
import uk.ac.standrews.cs.sos.instrument.StatsTYPE;
import uk.ac.standrews.cs.sos.services.Agent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

/**
 *
 * Testing IO on data and manifests
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Experiment_IO_1 extends BaseExperiment implements Experiment {

    public Experiment_IO_1(ExperimentConfiguration experimentConfiguration, String outputFilename) throws ExperimentException {
        super(experimentConfiguration, outputFilename);
    }

    @Override
    public ExperimentUnit getExperimentUnit() {
        return new ExperimentUnit_IO_1();
    }

    private class ExperimentUnit_IO_1 implements ExperimentUnit {

        @Override
        public void setup() {}

        @Override
        public void run() throws ExperimentException {
            List<IGUID> atoms;

            String datasetPath = experiment.getExperimentNode().getDatasetPath();
            try {
                atoms = addFolderContentToNodeAsAtoms(node, new File(datasetPath));

            } catch (IOException e) {
                e.printStackTrace();
                throw new ExperimentException();
            }

            rest_a_bit();

            // TODO - comment this line out to invalidate caches
            // node.getMDS().shutdown();
            Agent agent = node.getAgent();

            for(IGUID atom:atoms) {
                try {
                    agent.getData(atom);
                } catch (ServiceException e) {
                    throw new ExperimentException("Unable to get data for atom with GUID " + atom.toMultiHash());
                }
            }

            rest_a_bit();

            // READ/WRITE files via FS and take measurements
            try {
                addFilesViaFS(new File(datasetPath));
            } catch (IOException e) {
                throw new ExperimentException("Unable to read/write files via FS");
            }
        }
    }


    private void addFilesViaFS(File folder) throws IOException {

        SimpleFileVisitor<Path> fv = new FileVisitor<>();

        long start = System.nanoTime();
        System.out.println("Files added: ");
        Files.walkFileTree(folder.toPath(), fv);
        System.out.println("\nTime to add all contents: " + (System.nanoTime() - start) / 1000000000.0 + " seconds");
    }

    class FileVisitor<T extends Path> extends SimpleFileVisitor<T> {

        int counter = 0;

        @Override
        public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {

            counter++;
            if (counter % 100 == 0) {
                System.out.print("  " + counter);
            }

            // READ FILE
            long start = System.nanoTime();
            File file = path.toFile();
            int size = (int) file.length();
            byte[] bytes = new byte[size];
            try (FileInputStream fileInputStream = new FileInputStream(file)){
                fileInputStream.read(bytes);
            }
            long duration = System.nanoTime() - start;
            InstrumentFactory.instance().measure(StatsTYPE.io, StatsTYPE.fs_read_file, Integer.toString(size), duration);

            // WRITE FILE
            start = System.nanoTime();
            try(FileOutputStream out = new FileOutputStream("test_file")) {
                out.write(bytes);
            }
            duration = System.nanoTime() - start;
            InstrumentFactory.instance().measure(StatsTYPE.io, StatsTYPE.fs_write_file, Integer.toString(size), duration);

            return FileVisitResult.CONTINUE;
        }

    }


    public static void main(String[] args) throws ConfigurationException, ExperimentException {

        File experimentConfigurationFile = new File(CONFIGURATION_FOLDER.replace("{experiment}", "io_1") + "configuration.json");
        ExperimentConfiguration experimentConfiguration = new ExperimentConfiguration(experimentConfigurationFile);

        Experiment_IO_1 experiment_io_1 = new Experiment_IO_1(experimentConfiguration, "io_1_on_text1mb_10its_1");
        experiment_io_1.process();
    }
}

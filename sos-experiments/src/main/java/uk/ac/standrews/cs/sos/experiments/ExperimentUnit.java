package uk.ac.standrews.cs.sos.experiments;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.io.FileUtils;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.exceptions.ServiceException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataException;
import uk.ac.standrews.cs.sos.exceptions.userrole.UserRolePersistException;
import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.VersionBuilder;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.URILocation;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.instrument.InstrumentFactory;
import uk.ac.standrews.cs.sos.instrument.StatsTYPE;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.User;
import uk.ac.standrews.cs.sos.model.Version;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface ExperimentUnit {

    /**
     * The setupIteration for the experiment unit
     *
     * @throws ExperimentException
     */
    void setup() throws ExperimentException;

    /**
     * The code that runs the actual experiment for this unit
     *
     * @throws ExperimentException
     */
    void run() throws ExperimentException;


    /**
     * Add the data inside the folder to the local node
     *
     * @param node where to add the content
     * @param folder where the content for the experiment is
     * @throws URISyntaxException
     * @throws MetadataException
     * @throws IOException
     */
    default void addFolderContentToNode(SOSLocalNode node, File folder) throws URISyntaxException, MetadataException, IOException {

        SimpleFileVisitor<Path> fv = new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                // System.out.println("File " + file.toUri().toString());

                try {
                    AtomBuilder atomBuilder = new AtomBuilder().setLocation(new URILocation(file.toUri().toString()));
                    VersionBuilder versionBuilder = new VersionBuilder()
                            .setAtomBuilder(atomBuilder);

                    Version version = node.getAgent().addData(versionBuilder);
                    InstrumentFactory.instance().measure(StatsTYPE.experiment, "Added version " + version.guid().toShortString() + " from URI " + file.toString());
                } catch (URISyntaxException  | ServiceException e) {
                    e.printStackTrace();
                }

                return FileVisitResult.CONTINUE;
            }
        };

        long start = System.nanoTime();
        Files.walkFileTree(folder.toPath(), fv);
        System.out.println("Time to add all contents: " + (System.nanoTime() - start) / 1000000000.0 + " seconds");
    }

    // TODO - add content as protected

    /**
     * Add the users/roles for this experiment to the node
     *
     * @param node
     * @param folder
     */
    default void addFolderUSROToNode(SOSLocalNode node, File folder) {

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
                            node.getRMS().addRole(role);
                            break;
                        case USER:
                            User user = JSONHelper.JsonObjMapper().readValue(file, User.class);
                            node.getRMS().addUser(user);
                            break;
                    }


                } catch (IOException | UserRolePersistException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}

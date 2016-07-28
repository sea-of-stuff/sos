package uk.ac.standrews.cs.sos;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Make {

    private static final String CURRENT_DIRECTORY = Paths.get(".").toAbsolutePath().normalize().toString();

    private static final String CONFIG_TEMPLATE = "# Fill the following properties \n" +
            "# You can use the tilde ~ for the home directory\n" +
            "db.path=~/sos/db/dump.db\n" +
            "node.port=8080\n" +
            "storage.access.key=\n" +
            "db.password=\n" +
            "db.username=\n" +
            "node.hostname=\n" +
            "node.guid=6b67f67f31908dd0e574699f163eda2cc117f7f4\n" +
            "keys.folder=~/sos/keys/\n" +
            "storage.secret.key=\n" +
            "storage.type=local\n" +
            "storage.location=/sos/\n" +
            "storage.password=\n" +
            "db.hostname=\n" +
            "node.is.client=true\n" +
            "db.type=sqlite\n" +
            "storage.username=\n" +
            "node.is.storage=false\n" +
            "storage.hostname=\n" +
            "node.is.coordinator=false\n";

    public static void main(String[] args) throws IOException {
        if (args.length >= 1 && args.length <=2 && args[0].equals("default")) {
            makeDefault(args);
            return;
        }

        System.out.println("Wrong parameters");





    }

    private static void makeDefault(String[] args) throws IOException {
        String outputPath = (args.length == 2 ? args[1] : CURRENT_DIRECTORY) + "/";

        File dir = new File(outputPath);
        System.out.println("Writing configuration to the following path: " + dir.getAbsolutePath());

        File file = new File(outputPath + "config.properties");
        Files.write(file.toPath(), CONFIG_TEMPLATE.getBytes());
        System.out.println("Default configuration written: " + file.getAbsolutePath());
    }
}

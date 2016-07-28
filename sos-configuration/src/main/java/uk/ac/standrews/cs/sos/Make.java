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

    // TODO - add comments for each property
    private static final String CONFIG_TEMPLATE = "# Fill the following properties \n" +
            "# You can use the tilde ~ for the home directory\n" +
            "node.guid=\n" +
            "node.port=8080\n" +
            "node.hostname=\n" +
            "node.is.client=\n" +
            "node.is.storage=\n" +
            "node.is.discovery.data=\n" +
            "node.is.dns=\n" +
            "\n" +
            "db.path=~/sos/db/dump.db\n" +
            "db.password=\n" +
            "db.username=\n" +
            "db.hostname=\n" +
            "db.type=sqlite\n" +
            "\n" +
            "storage.access.key=\n" +
            "storage.secret.key=\n" +
            "storage.type=local\n" +
            "storage.location=~/sos/\n" +
            "storage.password=\n" +
            "storage.username=\n" +
            "storage.hostname=\n" +
            "\n" +
            "keys.folder=~/sos/keys/\n" +
            "\n";

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

package uk.ac.standrews.cs.sos.configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Make {

    private static final String CURRENT_DIRECTORY = Paths.get(".").toAbsolutePath().normalize().toString();

    private static final String CONFIG_TEMPLATE =
            "{\n" +
                    "    \"node\" : {\n" +
                    "        \"guid\" : \"6b67f67f31908dd0e574699f163eda2cc117f7f4\"\n" +
                    "        \"port\" : 8080\n" +
                    "        \"hostname\" : \"\"\n" +
                    "        \"is\" : {\n" +
                    "            \"agent\" : true\n" +
                    "            \"storage\" : false\n" +
                    "            \"dds\" : false\n" +
                    "            \"nds\" : false\n" +
                    "            \"mms\" : false\n" +
                    "            \"cms\" : false\n" +
                    "            \"rms\" : false\n" +
                    "        }\n" +
                    "    }\n" +
                    "\n" +
                    "    \"db\" : {\n" +
                    "        \"filename\" : \"dump.db\"\n" +
                    "    }\n" +
                    "\n" +
                    "    \"storage\" : {\n" +
                    "        \"type\" : \"local\"\n" +
                    "        \"location\" : \"/sos/\"\n" +
                    "        \"access.key\" : \"\"\n" +
                    "        \"secret.key\" : \"\"\n" +
                    "        \"hostname\" : \"\"\n" +
                    "        \"username\" : \"\"\n" +
                    "        \"password\" : \"\"\n" +
                    "    }\n" +
                    "\n" +
                    "    \"keys\" : {\n" +
                    "        \"folder\" : \"~/sos/keys/\"\n" +
                    "    }\n" +
                    "\n" +
                    "    \"policy\" : {\n" +
                    "        \"replication\" : {\n" +
                    "            \"factor\" : 0\n" +
                    "        }\n" +
                    "        \"manifest\" : {\n" +
                    "            \"local\" : true\n" +
                    "            \"remote\" : false\n" +
                    "            \"replication\" : 0\n" +
                    "        }\n" +
                    "    }\n" +
                    "\n" +
                    "    \"bootstrap\" : []\n" +
                    "\n" +
                    "}";


    private static final String OLD_TEMPLATE = "# Fill the following properties \n" +
            "# You can use the tilde ~ for the home directory\n" +
            "node.guid= # SHA-1 (160bits) - optional\n" +
            "node.port=8080\n" +
            "node.hostname= \t # true/false\n" +
            "node.is.agent= \t # true/false\n" +
            "node.is.storage= \t # true/false\n" +
            "node.is.dds= \t # true/false\n" +
            "node.is.mms= \t # true/false\n" +
            "node.is.nds= \t # true/false\n" +
            "\n" +
            "db.filename=dump.db\n" +
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
            "\n" +
            "policy.replication.factor=0\n" +
            "policy.manifest.locally=true\n" +
            "policy.manifest.remotely=false\n" +
            "policy.manifest.replication=0\n" +
            "\n";

    /**
     *
     * @param args first argument must be 'default', second one should be the path for the configuration file
     * @throws IOException if the configuration file could not be generated
     */
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

package uk.ac.standrews.cs.sos.app;

import org.apache.commons.cli.*;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.fs.exceptions.FileSystemCreationException;
import uk.ac.standrews.cs.fs.interfaces.IFileSystem;
import uk.ac.standrews.cs.sos.filesystem.SOSFileSystemFactory;
import uk.ac.standrews.cs.sos.interfaces.sos.Client;
import uk.ac.standrews.cs.sos.jetty.JettyApp;
import uk.ac.standrews.cs.sos.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.web.WebApp;
import uk.ac.standrews.cs.webdav.entrypoints.WebDAVLauncher;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class MAISOS {

    private static final String CONFIG_OPT = "c";
    private static final String REST_OPT = "j";
    private static final String WEB_APP_OPT = "w";
    private static final String WEBDAV_OPT = "wd";
    private static final String ROOT_OPT = "root";

    public static void main(String[] args) throws Exception {
        CommandLineParser parser = new DefaultParser();
        Options options = CreateOptions();

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("posix", options );

        CommandLine line;
        try {
            line = parser.parse(options, args);
        } catch (ParseException e) {
            throw new MAISOSException(e);
        }

        String configFilePath = line.getOptionValue(CONFIG_OPT);
        SOSLocalNode sos = ServerState.init(configFilePath);

        IGUID root = getRootGUID(line);

        if (line.hasOption(WEBDAV_OPT)) {
            // TODO - pass config info to webdav
            Launch(root, 8082, sos.getClient());
        }

        if (line.hasOption(WEB_APP_OPT)) {
            // TODO get port from config
            // TODO - pass fs from webdav
            WebApp.RUN(sos, root, 9999);
        }

        if (line.hasOption(REST_OPT)) {
            JettyApp.RUN(sos);
        }

        HandleExit();
    }

    private static Options CreateOptions() {
        Options options = new Options();

        options.addOption(Option.builder(CONFIG_OPT)
                .required()
                .hasArg()
                .desc("Config file used for this SOS instance")
                .build());

        options.addOption(Option.builder(REST_OPT)
                .required(false)
                .desc("Run a RESTful service")
                .build());

        options.addOption(Option.builder(WEBDAV_OPT)
                .required(false)
                .desc("Make a webdav server")
                .build());

        options.addOption(Option.builder(WEB_APP_OPT)
                .required(false)
                .desc("Run a web interface")
                .build());

        options.addOption(Option.builder(ROOT_OPT)
                .required(false)
                .hasArg()
                .desc("Define the root GUID for this fs")
                .build());

        return options;
    }

    private static void HandleExit() throws IOException {
        ShutdownHook shutdownHook = new ShutdownHook();
        shutdownHook.attachShutDownHook();

        System.out.println("Press Enter to stop");
        System.in.read();
    }

    private static class ShutdownHook {

        void attachShutDownHook() {
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    ServerState.kill();

                    System.out.println("exit");
                }
            });
        }
    }

    private static void Launch(IGUID root, int port, Client client) {

        try {
            IFileSystem file_system = new SOSFileSystemFactory(root)
                            .makeFileSystem(client);

            System.out.println("Starting WEBDAV server on port: " + port);
            WebDAVLauncher.StartWebDAVServer(file_system, port);
        } catch (FileSystemCreationException e) {
            System.out.println("couldn't create file system: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Socket error: " + e.getMessage());
        }
    }

    private static IGUID getRootGUID(CommandLine line) throws GUIDGenerationException {
        IGUID root;
        if (line.hasOption(ROOT_OPT)) {
            String rootGUID = line.getOptionValue(ROOT_OPT);
            root = GUIDFactory.recreateGUID(rootGUID);
        } else {
            root = GUIDFactory.generateRandomGUID();
        }

        return root;
    }

}

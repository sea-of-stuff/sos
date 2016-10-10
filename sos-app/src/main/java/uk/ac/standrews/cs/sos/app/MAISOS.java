package uk.ac.standrews.cs.sos.app;

import org.apache.commons.cli.*;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
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

    public static void main(String[] args) throws Exception {
        CommandLineParser parser = new DefaultParser();
        Options options = createOptions();

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

        if (line.hasOption(REST_OPT)) {
            JettyApp.RUN(sos);
        }

        if (line.hasOption(WEB_APP_OPT)) {
            WebApp.RUN(sos, 9999);
        }

        if (line.hasOption(WEBDAV_OPT)) {
            // TODO - pass config info to webdav
            IGUID root = GUIDFactory.recreateGUID("73e057624a5b5005ab0e35ca45f6fb48ddfa8d5e");
            Launch(root, 8082, sos.getClient());
        }


        handleExit();
    }

    private static Options createOptions() {
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
        return options;
    }

    private static void handleExit() throws IOException {
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

    public static void Launch(IGUID root, int port, Client client) {

        try {
            IFileSystem file_system =
                    new SOSFileSystemFactory(root)
                            .makeFileSystem(client);

            WebDAVLauncher.StartWebDAVServer(file_system, port);
        } catch (FileSystemCreationException e) {
            System.out.println("couldn't create file system: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Socket error: " + e.getMessage());
        }
    }

}

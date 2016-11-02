package uk.ac.standrews.cs.sos.app;

import org.apache.commons.cli.*;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.fs.interfaces.IFileSystem;
import uk.ac.standrews.cs.sos.filesystem.SOSFileSystemFactory;
import uk.ac.standrews.cs.sos.jetty.JettyApp;
import uk.ac.standrews.cs.sos.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.web.WebApp;
import uk.ac.standrews.cs.webdav.entrypoints.WebDAVLauncher;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class MAISOS {

    private static final String CONFIG_OPT = "c";
    private static final String REST_OPT = "j";
    private static final String FS_OPT = "fs";
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

        ExecutorService executor = Executors.newSingleThreadExecutor();

        IGUID root = getRootGUID(line);

        if (line.hasOption(FS_OPT)) {
            // TODO - pass config info to webdav and webapp

            IFileSystem fileSystem = new SOSFileSystemFactory(root)
                    .makeFileSystem(sos.getClient());

            executor.submit(() -> {
                try {
                    Launch(fileSystem, 8082);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });


            WebApp.RUN(sos, fileSystem, 9999);
        }

        if (line.hasOption(REST_OPT)) {

            executor.submit(() -> {
                try {
                    JettyApp.RUN(sos);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        // FIXME - not working. It might be because there are threads being launghed
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

        options.addOption(Option.builder(FS_OPT)
                .required(false)
                .desc("Make a webdav server and a web interface")
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
        System.out.println("Exiting...");
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

    private static void Launch(IFileSystem fileSystem, int port) {

        try {
            System.out.println("Starting WEBDAV server on port: " + port);
            WebDAVLauncher.StartWebDAVServer(fileSystem, port);
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

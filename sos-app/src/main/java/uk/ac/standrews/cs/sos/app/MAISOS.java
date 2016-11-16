package uk.ac.standrews.cs.sos.app;

import org.apache.commons.cli.*;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.fs.exceptions.FileSystemCreationException;
import uk.ac.standrews.cs.fs.interfaces.IFileSystem;
import uk.ac.standrews.cs.sos.configuration.SOSConfiguration;
import uk.ac.standrews.cs.sos.filesystem.SOSFileSystemFactory;
import uk.ac.standrews.cs.sos.interfaces.sos.Agent;
import uk.ac.standrews.cs.sos.jetty.JettyApp;
import uk.ac.standrews.cs.sos.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.web.WebApp;
import uk.ac.standrews.cs.webdav.entrypoints.WebDAVLauncher;

import java.io.File;
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
        CommandLine line = InitCLI(args);

        String configFilePath = line.getOptionValue(CONFIG_OPT);
        File configFile = new File(configFilePath);
        SOSConfiguration configuration = new SOSConfiguration(configFile);

        SOSLocalNode sos = ServerState.init(configuration);
        assert sos != null;

        IGUID root = getRootGUID(line);

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        if (line.hasOption(FS_OPT)) {
            HandleFSApp(executorService, sos, root, configuration);
        }

        if (line.hasOption(REST_OPT)) {
            HandleJettyApp(executorService, sos);
        }

        HandleExit(executorService);
    }

    private static CommandLine InitCLI(String[] args) throws MAISOSException {
        CommandLineParser parser = new DefaultParser();
        Options options = CreateOptions();

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("posix", options);

        CommandLine line;
        try {
            line = parser.parse(options, args);
        } catch (ParseException e) {
            throw new MAISOSException(e);
        }

        return line;
    }

    private static Options CreateOptions() {
        Options options = new Options();

        options.addOption(Option.builder(CONFIG_OPT)
                .required()
                .hasArg()
                .desc("Config file used for this SOS instance. " +
                      "You can use the sos-configuration utility to start making your configuration.")
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

    private static void HandleExit(ExecutorService executorService) throws IOException {
        ShutdownHook shutdownHook = new ShutdownHook(executorService);
        shutdownHook.attachShutDownHook();

        System.out.println("Press Enter to stop");
        System.in.read();
        System.out.println("Exiting...");
    }

    private static class ShutdownHook {

        ExecutorService executorService;

        ShutdownHook(ExecutorService executorService) {
            this.executorService = executorService;
        }

        void attachShutDownHook() {
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    executorService.shutdown();

                    ServerState.kill();
                    System.out.println("SOS instance terminated");
                }
            });
        }
    }

    private static void HandleFSApp(ExecutorService executorService, SOSLocalNode sos, IGUID root,
                                    SOSConfiguration configuration) throws FileSystemCreationException {

        Agent agent = sos.getAgent();
        if (agent == null) {
            return;
        }

        IFileSystem fileSystem = new SOSFileSystemFactory(root)
                .makeFileSystem(agent);

        executorService.submit(() -> {
            try {
                LaunchWebDAV(fileSystem, configuration.getWebDAVPort());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


        WebApp.RUN(sos, fileSystem, configuration.getWebAppPort());
    }

    private static void HandleJettyApp(ExecutorService executorService, SOSLocalNode sos) {
        executorService.submit(() -> {
            try {
                JettyApp.RUN(sos);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void LaunchWebDAV(IFileSystem fileSystem, int port) {

        try {
            System.out.println("Starting WEBDAV server on port: " + port);
            WebDAVLauncher.StartWebDAVServer(fileSystem, port);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
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

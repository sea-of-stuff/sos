package uk.ac.standrews.cs.sos.app;

import org.apache.commons.cli.*;
import uk.ac.standrews.cs.fs.exceptions.FileSystemCreationException;
import uk.ac.standrews.cs.fs.interfaces.IFileSystem;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.filesystem.SOSFileSystemFactory;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.jetty.JettyApp;
import uk.ac.standrews.cs.sos.services.Agent;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;
import uk.ac.standrews.cs.sos.web.WebApp;
import uk.ac.standrews.cs.webdav.entrypoints.WebDAVLauncher;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    /**
     *
     * Example: java -jar sos.jar -c /Users/sic2/config.json -j -fs -root 73a7f67f31908dd0e574699f163eda2cc117f7f4
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        CommandLine cli = InitCLI(args);

        String configFilePath = cli.getOptionValue(CONFIG_OPT);
        File configFile = new File(configFilePath);
        SettingsConfiguration configuration = new SettingsConfiguration(configFile);

        SOSLocalNode sos = ServerState.init(configuration.getSettingsObj());
        assert sos != null;

        IGUID root = getRootGUID(cli);

        ExecutorService executorService = Executors.newFixedThreadPool(3);

        if (cli.hasOption(REST_OPT)) {
            HandleJettyApp(executorService, sos);
        }

        if (cli.hasOption(FS_OPT)) {
            HandleFSApp(executorService, sos, root, configuration.getSettingsObj());
        }

        HandleExit(executorService);
    }

    private static CommandLine InitCLI(String[] args) throws MAISOSException {
        CommandLineParser parser = new DefaultParser();
        Options options = CreateOptions();

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("posix", options);
        System.out.println("\n===================================================\n\n");

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

        SOS_LOG.log(LEVEL.INFO, "SOS Shutdown handler has been registered. Send SIGTERM on process");
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

                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    SOS_LOG.log(LEVEL.INFO, "SOS Instance Terminated at " + dateFormat.format(new Date()));
                }
            });
        }
    }

    private static void HandleFSApp(ExecutorService executorService, SOSLocalNode sos, IGUID root,
                                    SettingsConfiguration.Settings settings) throws FileSystemCreationException {

        Agent agent = sos.getAgent();
        if (agent == null) {
            return;
        }

        IFileSystem fileSystem = new SOSFileSystemFactory(root)
                .makeFileSystem(agent);

        executorService.submit(() -> {
            try {
                SOS_LOG.log(LEVEL.INFO, "Launching the WebApp on port: " + settings.getWebAPP().getPort());
                WebApp.RUN(sos, fileSystem, settings.getWebAPP().getPort());

                SOS_LOG.log(LEVEL.INFO, "Launching the WebDAV on port: " + settings.getWebDAV().getPort());
                LaunchWebDAV(fileSystem, settings.getWebDAV().getPort());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void HandleJettyApp(ExecutorService executorService, SOSLocalNode sos) {
        executorService.submit(() -> {
            try {
                SOS_LOG.log(LEVEL.INFO, "Launching the REST App on port: " + sos.getHostAddress().getPort());
                JettyApp.RUN(sos);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void LaunchWebDAV(IFileSystem fileSystem, int port) {

        try {
            WebDAVLauncher.StartWebDAVServer(fileSystem, port);
        } catch (IOException e) {
            SOS_LOG.log(LEVEL.ERROR, "Error while starting WebDAV server on port: " + port + " with error: " + e.getMessage());
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

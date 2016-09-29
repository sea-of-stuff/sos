package uk.ac.standrews.cs.sos.app;

import org.apache.commons.cli.*;
import uk.ac.standrews.cs.sos.jetty.JettyApp;
import uk.ac.standrews.cs.sos.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.web.WebApp;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class MAISOS {

    private static final String CONFIG_OPT = "c";
    private static final String JETTY_OPT = "j";
    private static final String WEB_OPT = "w";
    private static final String WEBDAV_OPT = "wd";

    public static void main(String[] args) throws Exception {
        CommandLineParser parser = new DefaultParser();
        Options options = createOptions();
        CommandLine line = parser.parse(options, args);

        String configFilePath = line.getOptionValue(CONFIG_OPT);
        SOSLocalNode sos = ServerState.init(configFilePath);

        if (line.hasOption(JETTY_OPT)) {
            JettyApp.RUN(sos);
        }

        if (line.hasOption(WEBDAV_OPT)) {
            // TODO: Start Webdav
            System.out.println("Starting webdav - N/A");
        }

        if (line.hasOption(WEB_OPT)) {
            WebApp.RUN(sos);
        }

        handleExit();
    }

    private static Options createOptions() {
        Options options = new Options();

        options.addOption(Option.builder(CONFIG_OPT)
                .required()
                .hasArg()
                .desc("Config files used for this SOS instance")
                .build());

        options.addOption(Option.builder(JETTY_OPT)
                .required(false)
                .desc("Make a jetty server")
                .build());

        options.addOption(Option.builder(WEBDAV_OPT)
                .required(false)
                .desc("Make a webdav server")
                .build());

        options.addOption(Option.builder(WEB_OPT)
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

}

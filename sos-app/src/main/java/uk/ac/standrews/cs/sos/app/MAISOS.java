package uk.ac.standrews.cs.sos.app;

import uk.ac.standrews.cs.sos.jetty.JettyApp;
import uk.ac.standrews.cs.sos.node.SOSLocalNode;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class MAISOS {

    public static void main(String[] args) throws Exception {

        // READ CONFIG PATH
        String configFilePath = args[1];

        // Start Server State
        SOSLocalNode sos = ServerState.init(configFilePath);

        // Start rest-jetty
        JettyApp.RUN(sos);

        // Start Webdav

        // Start WEB


        handleExit();
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


package uk.ac.standrews.cs.sos;

import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.node.LocalSOSNode;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ServerState {

    public static LocalSOSNode sos;

    public static void init() {
        System.out.println("Starting SOS");
        ServerState.startSOS();
        System.out.println("SOS started");
    }

    public static void kill() {
        // TODO
    }

    private static void startSOS() {
        try {
            try {
                LocalSOSNode.create();
            } catch (SOSProtocolException e) {
                e.printStackTrace();
            }

            ServerState.sos = LocalSOSNode.getInstance();
        } catch (SOSException e) {
            e.printStackTrace();
        }
    }
}

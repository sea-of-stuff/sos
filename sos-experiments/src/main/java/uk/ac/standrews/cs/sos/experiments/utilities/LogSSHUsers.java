package uk.ac.standrews.cs.sos.experiments.utilities;

import java.util.Scanner;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LogSSHUsers {

    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);

        System.out.println("Option: log, collect");
        String option = in.nextLine().toLowerCase();

        switch (option) {
            case "log":
                log(); break;
            case "collect":
                collect(); break;
            default:
                System.err.println("Unknown option");
        }
    }

    private static void log() {


        // Stop logging
        // Remove log files
        // Distributed log script to all nodes in cluster
        // Start log script (use cron job?)
    }

    private static void collect() {

        // Retrieve logs to designated folder
        // Inspect logs and print summaries of all logged users for each node
    }
}

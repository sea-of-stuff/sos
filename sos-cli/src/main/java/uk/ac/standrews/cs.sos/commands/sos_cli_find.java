package uk.ac.standrews.cs.commands;

import org.apache.commons.cli.*;
import uk.ac.standrews.cs.sos.exceptions.UnknownGUIDException;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUID;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUIDsha1;
import uk.ac.standrews.cs.sos.model.interfaces.components.Manifest;
import uk.ac.standrews.cs.sos_cli;

import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class sos_cli_find {

    private final static String TYPE = "t";
    private final static String LABEL = "l";
    private final static String GUID = "g";

    // NOTE - limited to find a manifest by its GUID!
    public static void GetManifest(String[] args) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        Options options = createOptions();
        manageInput(parser, options, args);
    }

    private static Options createOptions() {
        Options options = new Options();
        options.addOption(getTypeOption());
        options.addOption(getLabelOption());
        options.addOption(getGuidOption());
        return options;
    }

    private static Option getTypeOption() {
        return Option.builder(TYPE)
                .required(false)
                .hasArg()
                .desc("Type of manifest")
                .build();
    }

    private static Option getLabelOption() {
        return Option.builder(LABEL)
                .required(false)
                .hasArg()
                .desc("Label in manifest")
                .build();
    }

    private static Option getGuidOption() {
        return Option.builder(GUID)
                .required(false)
                .hasArg()
                .desc("GUID of manifest")
                .build();
    }

    private static void manageInput(CommandLineParser parser, Options options, String[] args) {

        try {
            CommandLine line = parser.parse(options, args);

            if(line.hasOption(GUID)) {
                String guidS = line.getOptionValue(GUID);
                GUID guid = new GUIDsha1(guidS);
                Manifest manifest = sos_cli.seaOfStuff.getManifest(guid);
                System.out.print(manifest.toJSON());
            } else if (line.hasOption(TYPE)) {
                String type = line.getOptionValue(TYPE);
                Collection<GUID> guids = sos_cli.seaOfStuff.findManifestByType(type);
                printGUIDs(guids);
            } else if (line.hasOption(LABEL)) {
                String label = line.getOptionValue(LABEL);
                Collection<GUID> guids = sos_cli.seaOfStuff.findManifestByLabel(label);
                printGUIDs(guids);
            }
        } catch (ParseException exp) {
            System.out.println( "Unexpected exception:" + exp.getMessage() );
        } catch (UnknownGUIDException e) {
            System.out.println("Manifest not found");
        }
    }

    private static void printGUIDs(Collection<GUID> guids) {
        for(GUID guid:guids) {
            System.out.println("GUID: " + guid.toString());
        }
    }

}

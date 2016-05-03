package uk.ac.standrews.cs.commands;

import org.apache.commons.cli.*;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.exceptions.storage.ManifestSaveException;
import uk.ac.standrews.cs.sos.model.implementations.utils.Location;
import uk.ac.standrews.cs.sos.model.interfaces.components.Manifest;
import uk.ac.standrews.cs.sos_cli;

import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class sos_cli_atom {

    private final static String LOCATIONS = "l";

    public static void AddAtom(String[] args) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        Options options = createOptions();
        manageInput(parser, options, args);
    }

    private static Options createOptions() {
        Options options = new Options();
        options.addOption(getAtomOption());
        return options;
    }

    private static Option getAtomOption() {
       return Option.builder(LOCATIONS)
                .required(true)
                .hasArgs()
                .desc("Locations of the atom")
                .build();
    }

    private static void manageInput(CommandLineParser parser, Options options, String[] args) {
        try {
            CommandLine line = parser.parse(options, args);

            if(line.hasOption(LOCATIONS)) {
                String[] paths = line.getOptionValues(LOCATIONS);
                ArrayList<Location> locations = new ArrayList<Location>();
                for(String path:paths) {
                    locations.add(new Location(path));
                }
                Manifest manifest = sos_cli.seaOfStuff.addAtom(locations);
                System.out.println("GUID: " + manifest.getContentGUID());
            }
        } catch (ParseException exp) {
            System.out.println( "Unexpected exception:" + exp.getMessage() );
        } catch (ManifestSaveException e) {
            System.out.println("Manifest could not be saved");
        } catch (ManifestNotMadeException e) {
            System.out.println("Manifest could not be generated");
        } catch (URISyntaxException e) {
            System.out.println("Location's syntax is not valid");
        } catch (DataStorageException e) {
            System.out.println("Data could not be stored");
        }
    }

}

package uk.ac.standrews.cs.commands;

import org.apache.commons.cli.*;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.storage.ManifestSaveException;
import uk.ac.standrews.cs.sos.model.implementations.utils.Content;
import uk.ac.standrews.cs.sos.model.interfaces.components.Manifest;
import uk.ac.standrews.cs.sos_cli;
import uk.ac.standrews.cs.utility.Helper;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class sos_cli_compound {

    private final static String CONTENTS = "c";

    public static void AddCompound(String[] args) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        Options options = createOptions();
        manageInput(parser, options, args);
    }

    private static Options createOptions() {
        Options options = new Options();
        options.addOption(getCompoundContentOption());
        return options;
    }

    private static Option getCompoundContentOption() {
        return Option.builder(CONTENTS)
                .required(true)
                .hasArgs()
                .desc("Contents of the compound")
                .build();
    }

    private static void manageInput(CommandLineParser parser, Options options, String[] args) {
        try {
            CommandLine line = parser.parse(options, args);

            if(line.hasOption(CONTENTS)) {
                String[] contentStrings = line.getOptionValues(CONTENTS);
                Collection<Content> contents = new ArrayList<Content>();

                for(int i = 0; i < contentStrings.length; i++) {
                    Content content = Helper.getContent(contentStrings[i]);
                    contents.add(content);
                }
                Manifest manifest = sos_cli.seaOfStuff.addCompound(contents);
                System.out.println("GUID: " + manifest.getContentGUID());
            }
        } catch (ParseException exp) {
            System.out.println( "Unexpected exception:" + exp.getMessage() );
        } catch (ManifestSaveException e) {
            System.out.println("Manifest could not be saved");
        } catch (ManifestNotMadeException e) {
            System.out.println("Manifest could not be generated");
        }
    }

}

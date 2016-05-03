package uk.ac.standrews.cs;

import uk.ac.standrews.cs.sos.configurations.DefaultConfiguration;
import uk.ac.standrews.cs.sos.configurations.SeaConfiguration;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyGenerationException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyLoadedException;
import uk.ac.standrews.cs.sos.managers.LuceneCache;
import uk.ac.standrews.cs.sos.managers.MemCache;
import uk.ac.standrews.cs.sos.model.implementations.SeaOfStuffImpl;
import uk.ac.standrews.cs.sos.model.interfaces.SeaOfStuff;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class sos_cli {

    public static SeaOfStuff seaOfStuff;
    public static SeaConfiguration configuration;
    public static MemCache cache;

    public static void main(String[] args) throws KeyGenerationException, KeyLoadedException, IOException {
        try {
            configuration = new DefaultConfiguration();
            cache = LuceneCache.getInstance(configuration);
            seaOfStuff = new SeaOfStuffImpl(configuration, cache);
            new sos_cli_manager(args);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cache != null)
                cache.killInstance();
        }

        System.exit(0); // FIXME - this is needed to make the jar executable return to the terminal
    }

}

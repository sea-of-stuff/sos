package uk.ac.standrews.cs;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.model.implementations.utils.Location;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class sos_cliTest {

    @Test
    public void testMainAddAtom() throws Exception {
        //createDummyDataFile();
        //sos_cli.main(new String[] {"-atom", "-l", "/Users/sic2/Desktop/simone.JPG"});
    }

    private Location createDummyDataFile() throws FileNotFoundException, UnsupportedEncodingException, URISyntaxException {
        // FIXME - do not use hardcoded path
        String location = "/Users/sic2/Desktop/test.txt";

        File file = new File(location);
        File parent = file.getParentFile();
        if(!parent.exists() && !parent.mkdirs()){
            throw new IllegalStateException("Couldn't create dir: " + parent);
        }

        PrintWriter writer = new PrintWriter(file);
        writer.println("The first line");
        writer.println("The second line");
        writer.close();

        return new Location("file://"+location);
    }
}
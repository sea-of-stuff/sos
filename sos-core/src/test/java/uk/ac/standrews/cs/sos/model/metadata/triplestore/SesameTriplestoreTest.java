package uk.ac.standrews.cs.sos.model.metadata.triplestore;

import org.testng.annotations.Test;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SesameTriplestoreTest {

    @Test
    public void dummy() {
        SesameTriplestore test = new SesameTriplestore();
        test.addTriple("simone", "is", "person");
        test.addTriple("simone", "likes", "fish");
        test.addTriple("simone", "likes", "bird");
        test.addTriple("simone", "has", "atom");
        test.getTriples("likes");
    }
}
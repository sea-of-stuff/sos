package uk.ac.standrews.cs.sos.instrument;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Measure {

    String COMMA = ",";

    String csvHeader();
    String csv();
}

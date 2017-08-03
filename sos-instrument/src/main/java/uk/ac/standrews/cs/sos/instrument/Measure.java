package uk.ac.standrews.cs.sos.instrument;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Measure {

    String COMMA = ",";
    String TAB = "\t";

    String csvHeader();
    String csv();

    String tsvHeader();
    String tsv();
}

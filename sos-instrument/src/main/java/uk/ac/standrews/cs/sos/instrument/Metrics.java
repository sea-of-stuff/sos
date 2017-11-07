package uk.ac.standrews.cs.sos.instrument;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Metrics {

    String TAB = "\t";

    String tsvHeader();
    String tsv();

    void setStatsType(StatsTYPE statsType);
    void setSubType(StatsTYPE subtype);
}

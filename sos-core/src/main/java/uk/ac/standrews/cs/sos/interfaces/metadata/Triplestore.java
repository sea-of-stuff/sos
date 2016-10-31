package uk.ac.standrews.cs.sos.interfaces.metadata;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Triplestore {

    void addTriple(String subject, String predicate, String object);

    void getTriples(String predicate);

    void getTriples(String object, String predicate);

    void persist();
}

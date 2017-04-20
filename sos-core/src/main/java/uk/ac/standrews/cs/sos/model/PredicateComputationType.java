package uk.ac.standrews.cs.sos.model;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public enum PredicateComputationType {

    BEFORE_STORING, // Predicate is run before the data is stored
    AFTER_STORING, // Predicate is run just after the data is stored
    PERIODICALLY, // Predicate is run on background
    AFTER_READING // Predicate is run just before reading the data
}

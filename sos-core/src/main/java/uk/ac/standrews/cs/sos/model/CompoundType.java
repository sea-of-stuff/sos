package uk.ac.standrews.cs.sos.model;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public enum CompoundType {

    COLLECTION("COLLECTION"),
    DATA("DATA");

    private final String text;

    CompoundType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}

package uk.ac.standrews.cs.sos.model;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public enum NodesCollectionType {

    LOCAL("LOCAL"),             // This local node
    SPECIFIED("SPECIFIED"),     // The collection is limited to the specified nodes
    ANY("ANY");                 // The collection is unlimited

    private final String text;

    NodesCollectionType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    /**
     * Parse a string to the corresponding ManifestType enum
     * @param value
     * @return
     */
    public static NodesCollectionType get(String value) {
        for(NodesCollectionType v : values())
            if(v.toString().equalsIgnoreCase(value)) return v;
        throw new IllegalArgumentException();
    }
}

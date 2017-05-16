package uk.ac.standrews.cs.sos.interfaces.node;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public enum NodeType {

    AGENT("agent"),
    CMS("cms"),
    DDS("dds"),
    MMS("mms"),
    NDS("nds"),
    RMS("rms"),
    STORAGE("storage");


    private final String text;

    /**
     * Construct a new NodeType
     * This method is not visible outside of this class
     */
    NodeType(final String text) {
        this.text = text;
    }

    /**
     * Get the string representation of the enum NodeType
     * @return
     */
    @Override
    public String toString() {
        return text;
    }

    /**
     * Parse a string to the corresponding NodeType enum
     * @param value
     * @return
     */
    public static NodeType get(String value) {
        for(NodeType v : values())
            if(v.toString().equalsIgnoreCase(value)) return v;
        throw new IllegalArgumentException();
    }
}

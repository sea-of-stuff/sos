package uk.ac.standrews.cs.sos.storage;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public enum StorageType {

    LOCAL("local"),
    NETWORK("network"),
    AWS_S3("aws_s3");

    private final String text;

    StorageType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public static StorageType getEnum(String value) {
        for(StorageType v : values())
            if(v.toString().equalsIgnoreCase(value)) return v;
        throw new IllegalArgumentException();
    }
}

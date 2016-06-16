package uk.ac.standrews.cs.sos.node;

public enum ROLE {
    CLIENT((byte) 0b0001),
    STORAGE((byte) 0b0010),
    COORDINATOR((byte) 0b0100);

    public final byte mask;

    ROLE(final byte mask) {
        this.mask = mask;
    }

    @Override
    public String toString() {
        return Byte.toString(mask);
    }

}

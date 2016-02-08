package uk.ac.standrews.cs.sos.model.implementations.identity;

import uk.ac.standrews.cs.sos.model.interfaces.identity.IdentityToken;

import java.util.Objects;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class IdentityTokenImpl implements IdentityToken {

    private long tokey;

    private IdentityTokenImpl(long tokey) {
        this.tokey = tokey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdentityTokenImpl that = (IdentityTokenImpl) o;
        return tokey == that.tokey;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tokey);
    }

    public long getTokey() {
        return tokey;
    }

    public IdentityToken next() {
        return new IdentityTokenImpl(tokey+1);
    }
}




package uk.ac.standrews.cs.sos.model.index;

import java.util.Objects;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LuceneKey {

    private String key;

    public LuceneKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LuceneKey luceneKey = (LuceneKey) o;
        return Objects.equals(key, luceneKey.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}

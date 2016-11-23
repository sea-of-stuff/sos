package uk.ac.standrews.cs.sos.node.directory.database;

import java.util.Objects;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class DatabaseType {

    private final String key;

    public DatabaseType(String key) {
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
        DatabaseType databaseType = (DatabaseType) o;
        return Objects.equals(key, databaseType.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}

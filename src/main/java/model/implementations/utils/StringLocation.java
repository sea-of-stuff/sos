package model.implementations.utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class StringLocation extends Location {

    private Path path;

    public StringLocation(String location) {
        path = Paths.get(location);
    }

    @Override
    public Path getLocationPath() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringLocation that = (StringLocation) o;
        return Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}

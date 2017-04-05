package uk.ac.standrews.cs.sos.model.manifests.builders;

import uk.ac.standrews.cs.sos.interfaces.model.CompoundType;
import uk.ac.standrews.cs.sos.interfaces.model.Content;

import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CompoundBuilder {

    private CompoundType type;
    private Set<Content> contents;


    public Set<Content> getContents() {
        return contents;
    }

    public CompoundBuilder setContents(Set<Content> contents) {
        this.contents = contents;

        return this;
    }

    public CompoundType getType() {
        return type;
    }

    public CompoundBuilder setType(CompoundType type) {
        this.type = type;

        return this;
    }
}

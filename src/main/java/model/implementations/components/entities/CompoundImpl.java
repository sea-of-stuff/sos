package model.implementations.components.entities;

import model.interfaces.components.entities.Compound;
import model.interfaces.components.entities.Union;

import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CompoundImpl implements Compound {

    Collection<Union> components;

    public CompoundImpl(Collection<Union> components) {
        this.components = components;
    }

    @Override
    public Collection<Union> getContents() {
        return null;
    }
}

package uk.ac.standrews.cs.sos.git_to_sos.interfaces;

import java.util.HashMap;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Tree extends Entity {

    HashMap<String, Entity> getContents();
    void addContent(String name, Entity entity);
}

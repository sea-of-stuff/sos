package uk.ac.standrews.cs.sos.git_to_sos;

import java.util.List;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Tree extends Entity {

    List<Entity> getContents();
    void addContent(Entity entity);
}

package uk.ac.standrews.cs.sos.git_to_sos;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class EntityImpl implements Entity {

    private String id;

    public EntityImpl(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }
}

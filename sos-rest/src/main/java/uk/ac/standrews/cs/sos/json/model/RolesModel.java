package uk.ac.standrews.cs.sos.json.model;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RolesModel {

    private boolean isAgent;
    private boolean isStorage;
    private boolean isDDS;
    private boolean isNDS;
    private boolean isMCS;

    public boolean isAgent() {
        return isAgent;
    }

    public void setAgent(boolean agent) {
        isAgent = agent;
    }

    public boolean isStorage() {
        return isStorage;
    }

    public void setStorage(boolean storage) {
        isStorage = storage;
    }

    public boolean isDDS() {
        return isDDS;
    }

    public void setDDS(boolean DDS) {
        isDDS = DDS;
    }

    public boolean isNDS() {
        return isNDS;
    }

    public void setNDS(boolean NDS) {
        isNDS = NDS;
    }

    public boolean isMCS() {
        return isMCS;
    }

    public void setMCS(boolean MCS) {
        isMCS = MCS;
    }
}

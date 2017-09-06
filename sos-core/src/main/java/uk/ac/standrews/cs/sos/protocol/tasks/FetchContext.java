package uk.ac.standrews.cs.sos.protocol.tasks;

import uk.ac.standrews.cs.sos.protocol.Task;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class FetchContext extends Task {

    @Override
    public void performAction() {
        // Fetch context definition from CMS
    }

    @Override
    public String serialize() {
        return null;
    }

    @Override
    public Task deserialize(String json) throws IOException {
        return null;
    }
}

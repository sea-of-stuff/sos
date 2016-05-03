package uk.ac.standrews.cs.utility;

import uk.ac.standrews.cs.sos.model.implementations.utils.Content;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUID;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUIDsha1;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Helper {

    public static Content getContent(String inputContent) {
        Content content;
        boolean hasLabel = inputContent.contains(":");
        if (hasLabel) {
            String[] contentParts = inputContent.split(":");
            GUID guid = new GUIDsha1(contentParts[1]);
            content = new Content(contentParts[0], guid);
        } else {
            GUID guid = new GUIDsha1(inputContent);
            content = new Content(guid);
        }

        return content;
    }
}

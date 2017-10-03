package uk.ac.standrews.cs.sos.impl.context.examples;

import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.impl.context.BasePredicate;
import uk.ac.standrews.cs.sos.impl.context.CommonPredicates;

import java.util.Collections;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ReferencePredicate extends BasePredicate {

    public ReferencePredicate(JsonNode code, long maxAge) {
        super(code, maxAge);
    }

    @Override
    public boolean test(IGUID guid) {

        return CommonPredicates.ContentTypePredicate(guid, Collections.singletonList("image/jpeg"));
    }
}

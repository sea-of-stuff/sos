package uk.ac.standrews.cs.sos.experiments.protocol;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSURLException;
import uk.ac.standrews.cs.sos.impl.protocol.SOSURL;
import uk.ac.standrews.cs.sos.model.Node;

import java.net.URL;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
class ExperimentURL extends SOSURL {

    static URL EXPERIMENT_TRIGGER_PREDICATE(Node node, IGUID context) throws SOSURLException {

        String url = buildURLBase(node) +
                "experiment/cms/guid/" + context.toMultiHash() + "/predicate";

        return makeURL(url);
    }

    static URL DISABLE_REST(Node node) throws SOSURLException {

        String url = buildURLBase(node) +
                "experiment/rest/disable";

        return makeURL(url);
    }

    static URL ENABLE_REST(Node node) throws SOSURLException {

        String url = buildURLBase(node) +
                "experiment/rest/enable";

        return makeURL(url);
    }

    static URL DELETE_ALL_ATOMS(Node node) throws SOSURLException {

        String url = buildURLBase(node) +
                "experiment/atoms";

        return makeURL(url);
    }
}

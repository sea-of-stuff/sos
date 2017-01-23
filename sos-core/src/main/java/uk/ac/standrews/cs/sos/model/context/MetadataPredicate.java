package uk.ac.standrews.cs.sos.model.context;

import uk.ac.standrews.cs.sos.interfaces.manifests.Asset;

import java.util.function.Predicate;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class MetadataPredicate implements Predicate<Asset> {

    public MetadataPredicate() {

    }

    @Override
    public boolean test(Asset o) {
        return false;
    }
}

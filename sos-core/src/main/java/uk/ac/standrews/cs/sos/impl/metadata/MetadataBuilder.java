package uk.ac.standrews.cs.sos.impl.metadata;

import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.ManifestBuilder;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class MetadataBuilder extends ManifestBuilder {

    private Data data;

    public MetadataBuilder setData(Data data) {
        this.data = data;

        return this;
    }

    public Data getData() {
        return data;
    }
}

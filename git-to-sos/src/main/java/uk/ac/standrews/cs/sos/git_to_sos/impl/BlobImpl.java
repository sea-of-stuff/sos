package uk.ac.standrews.cs.sos.git_to_sos.impl;

import uk.ac.standrews.cs.sos.git_to_sos.interfaces.Blob;

import java.io.InputStream;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class BlobImpl extends EntityImpl implements Blob {

    private InputStream data;

    public BlobImpl(String id, InputStream inputStream) {
        super(id);

        this.data = inputStream;
    }

    @Override
    public InputStream getData() {
        return data;
    }
}

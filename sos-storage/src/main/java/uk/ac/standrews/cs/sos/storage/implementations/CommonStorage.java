package uk.ac.standrews.cs.sos.storage.implementations;

import uk.ac.standrews.cs.sos.storage.interfaces.Directory;
import uk.ac.standrews.cs.sos.storage.interfaces.Storage;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class CommonStorage implements Storage {

    public static final String DATA_DIRECTORY_NAME = "data";
    public static final String MANIFESTS_DIRECTORY_NAME = "manifests";
    public static final String TEST_DATA_DIRECTORY_NAME = "test_data";

    protected Directory root;
    protected boolean isImmutable;

    public CommonStorage(boolean isImmutable) {
        this.isImmutable = isImmutable;
    }

    @Override
    public boolean isImmutable() {
        return isImmutable;
    }

    @Override
    public Directory getRoot() {
        return root;
    }

    @Override
    public Directory getDataDirectory() {
        return createDirectory(DATA_DIRECTORY_NAME);
    }

    @Override
    public Directory getManifestDirectory() {
        return createDirectory(MANIFESTS_DIRECTORY_NAME);
    }

    @Override
    public Directory getTestDirectory() {
        return createDirectory(TEST_DATA_DIRECTORY_NAME);
    }

}

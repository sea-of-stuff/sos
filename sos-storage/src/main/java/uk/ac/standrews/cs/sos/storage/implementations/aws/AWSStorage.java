package uk.ac.standrews.cs.sos.storage.implementations.aws;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import uk.ac.standrews.cs.sos.storage.data.Data;
import uk.ac.standrews.cs.sos.storage.interfaces.Directory;
import uk.ac.standrews.cs.sos.storage.interfaces.File;
import uk.ac.standrews.cs.sos.storage.interfaces.Storage;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AWSStorage implements Storage {

    private static final String DATA_DIRECTORY_NAME = "data";
    private static final String MANIFESTS_DIRECTORY_NAME = "manifests";
    private static final String TEST_DATA_DIRECTORY_NAME = "test_data";

    private AmazonS3 s3Client;
    private String bucketName;
    private Directory root;
    private boolean isImmutable;

    public AWSStorage(String bucketName, boolean isImmutable) {
        s3Client = new AmazonS3Client();

        boolean bucketExist = s3Client.doesBucketExist(bucketName);
        if (!bucketExist) {
            s3Client.createBucket(bucketName);
        }
        this.bucketName = bucketName;
        root = new AWSDirectory(s3Client, bucketName);

        this.isImmutable = isImmutable;
    }

    public AWSStorage(String accessKeyId, String secretAccessKey) {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKeyId, secretAccessKey);
        s3Client = new AmazonS3Client();
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
        return createDirectory(DATA_DIRECTORY_NAME); // FIXME - this is common code with Filebasestorage!
    }

    @Override
    public Directory getManifestDirectory() {
        return createDirectory(MANIFESTS_DIRECTORY_NAME);
    }

    @Override
    public Directory getTestDirectory() {
        return createDirectory(TEST_DATA_DIRECTORY_NAME);
    }

    @Override
    public Directory createDirectory(Directory parent, String name) {
        return new AWSDirectory(s3Client, bucketName, parent, name, isImmutable);
    }

    @Override
    public Directory createDirectory(String name) {
        return new AWSDirectory(s3Client, bucketName, root, name, isImmutable);
    }

    @Override
    public File createFile(Directory parent, String filename) {
        return null;
    }

    @Override
    public File createFile(Directory parent, String filename, Data data) {
        return new AWSFile(s3Client, bucketName, parent, filename, data, isImmutable);
    }
}

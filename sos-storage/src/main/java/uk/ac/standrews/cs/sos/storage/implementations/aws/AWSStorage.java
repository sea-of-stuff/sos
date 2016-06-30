package uk.ac.standrews.cs.sos.storage.implementations.aws;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import uk.ac.standrews.cs.sos.storage.data.Data;
import uk.ac.standrews.cs.sos.storage.implementations.CommonStorage;
import uk.ac.standrews.cs.sos.storage.interfaces.Directory;
import uk.ac.standrews.cs.sos.storage.interfaces.File;
import uk.ac.standrews.cs.sos.storage.interfaces.Storage;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AWSStorage extends CommonStorage implements Storage {

    private AmazonS3 s3Client;
    private String bucketName;

    public AWSStorage(String bucketName, boolean isImmutable) {
        super(isImmutable);

        s3Client = new AmazonS3Client();
        createAndSetBucket(bucketName);
        createRoot();
    }

    public AWSStorage(String accessKeyId, String secretAccessKey, String bucketName, boolean isImmutable) {
        super(isImmutable);

        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKeyId, secretAccessKey);
        s3Client = new AmazonS3Client(awsCreds);
        createAndSetBucket(bucketName);
        createRoot();
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

    private void createAndSetBucket(String bucketName) {
        boolean bucketExist = s3Client.doesBucketExist(bucketName);
        if (!bucketExist) {
            s3Client.createBucket(bucketName);
        }
        this.bucketName = bucketName;
    }

    private void createRoot() {
        root = new AWSDirectory(s3Client, bucketName);
    }
}

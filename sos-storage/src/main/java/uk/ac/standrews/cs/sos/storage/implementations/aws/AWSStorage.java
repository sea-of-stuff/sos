package uk.ac.standrews.cs.sos.storage.implementations.aws;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import uk.ac.standrews.cs.sos.storage.data.Data;
import uk.ac.standrews.cs.sos.storage.exceptions.BindingAbsentException;
import uk.ac.standrews.cs.sos.storage.exceptions.DestroyException;
import uk.ac.standrews.cs.sos.storage.exceptions.PersistenceException;
import uk.ac.standrews.cs.sos.storage.exceptions.StorageException;
import uk.ac.standrews.cs.sos.storage.implementations.CommonStorage;
import uk.ac.standrews.cs.sos.storage.interfaces.Directory;
import uk.ac.standrews.cs.sos.storage.interfaces.File;
import uk.ac.standrews.cs.sos.storage.interfaces.Storage;

import java.io.IOException;

/**
 * AWSStorage abstracts the AWS S3 complexity. In doing so, it is possible to use
 * AWS as any normal data storage.
 *
 *
 * TODO - check if files are cached
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AWSStorage extends CommonStorage implements Storage {

    private static final Region DEFAULT_REGION = Region.getRegion(Regions.EU_WEST_1);
    private static final int KEYS_PER_ITERATION = 20;

    private AmazonS3 s3Client;
    private String bucketName;
    private Region region = DEFAULT_REGION;

    public AWSStorage(String bucketName, boolean isImmutable) throws StorageException {
        super(isImmutable);

        try {
            s3Client = new AmazonS3Client();
            s3Client.setRegion(region);
            createAndSetBucket(bucketName);

            createRoot();
            createSOSDirectories();
        } catch (PersistenceException | IOException e) {
            throw new StorageException(e);
        }
    }

    public AWSStorage(String accessKeyId, String secretAccessKey, String bucketName, boolean isImmutable) throws StorageException {
        super(isImmutable);

        try {
            BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKeyId, secretAccessKey);
            s3Client = new AmazonS3Client(awsCreds);
            s3Client.setRegion(region);
            createAndSetBucket(bucketName);

            createRoot();
            createSOSDirectories();
        } catch (PersistenceException | IOException e) {
            throw new StorageException(e);
        }
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
        return new AWSFile(s3Client, bucketName, parent, filename, isImmutable);
    }

    @Override
    public File createFile(Directory parent, String filename, Data data) {
        return new AWSFile(s3Client, bucketName, parent, filename, data, isImmutable);
    }

    @Override
    public void destroy() throws DestroyException {

        try {
            final ListObjectsV2Request req = new ListObjectsV2Request()
                    .withBucketName(bucketName)
                    .withMaxKeys(KEYS_PER_ITERATION);
            ListObjectsV2Result result;
            do {
                result = s3Client.listObjectsV2(req);

                for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
                    root.remove(objectSummary.getKey());
                }
                req.setContinuationToken(result.getNextContinuationToken());
            } while (result.isTruncated() == true);

            s3Client.deleteBucket(bucketName);
        } catch (BindingAbsentException e) {
            throw new DestroyException(e);
        }
    }

    private void createAndSetBucket(String bucketName) throws StorageException {
        try {
            boolean bucketExist = s3Client.doesBucketExist(bucketName);
            if (!bucketExist) {
                s3Client.createBucket(bucketName);
            }
            this.bucketName = bucketName;
        } catch (AmazonServiceException ase) {
            throw new StorageException(ase);
        } catch (AmazonClientException ace) {
            throw new StorageException(ace);
        }
    }

    private void createRoot() {
        root = new AWSDirectory(s3Client, bucketName);
    }

}

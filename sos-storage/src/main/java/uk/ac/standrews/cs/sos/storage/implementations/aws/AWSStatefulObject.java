package uk.ac.standrews.cs.sos.storage.implementations.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import uk.ac.standrews.cs.sos.storage.exceptions.PersistenceException;
import uk.ac.standrews.cs.sos.storage.interfaces.Directory;
import uk.ac.standrews.cs.sos.storage.interfaces.StatefulObject;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class AWSStatefulObject implements StatefulObject {

    private static final int RESOURCE_NOT_FOUND = 404;

    protected AmazonS3 s3Client;
    protected String bucketName;
    protected Directory logicalParent;
    protected String name;
    protected GetObjectRequest getObjectRequest;
    protected boolean isImmutable;

    public AWSStatefulObject(AmazonS3 s3Client, String bucketName,
                             Directory parent, String name, boolean isImmutable) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.logicalParent = parent;
        this.name = name;
        this.isImmutable = isImmutable;

        String objectPath = getPathname();
        getObjectRequest = new GetObjectRequest(bucketName, objectPath);
    }

    public AWSStatefulObject(AmazonS3 s3Client, String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    @Override
    public Directory getParent() {
        return logicalParent;
    }

    @Override
    public boolean exists() {

        try (S3Object s3Object = s3Client.getObject(getObjectRequest)) {
            boolean objectExist = s3Object != null;

            return objectExist;
        } catch (AmazonS3Exception e) {
            if (e.getStatusCode() == RESOURCE_NOT_FOUND) {
                return false;
            }
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public abstract String getPathname();

    @Override
    public long lastModified() {

        try (S3Object s3Object = s3Client.getObject(getObjectRequest)) {
            ObjectMetadata metadata = s3Object.getObjectMetadata();

            Date date = metadata.getLastModified();
            return date.getTime();
        } catch (IOException e) {
            e.printStackTrace();
        }

       return 0;
    }

    @Override
    public File toFile() {
        File file = null;
        s3Client.getObject(getObjectRequest, file);
        return file;
    }

    @Override
    public abstract void persist() throws PersistenceException;
}

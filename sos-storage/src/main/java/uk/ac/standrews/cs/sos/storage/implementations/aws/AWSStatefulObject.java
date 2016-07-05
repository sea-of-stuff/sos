package uk.ac.standrews.cs.sos.storage.implementations.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.NullInputStream;
import uk.ac.standrews.cs.sos.storage.data.Data;
import uk.ac.standrews.cs.sos.storage.exceptions.PersistenceException;
import uk.ac.standrews.cs.sos.storage.interfaces.Directory;
import uk.ac.standrews.cs.sos.storage.interfaces.StatefulObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class AWSStatefulObject implements StatefulObject {

    protected static final int RESOURCE_NOT_FOUND = 404;
    private static final String TMP_FILE_PREFIX = "aws";
    private static final String TMP_FILE_SUFFIX = ".tmp";

    protected AmazonS3 s3Client;
    protected String bucketName;
    protected Directory logicalParent;
    protected String name;
    protected Data data;
    protected GetObjectRequest getObjectRequest;
    protected boolean isImmutable;
    protected boolean persisted;

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

    public AWSStatefulObject(AmazonS3 s3Client, String bucketName,
                             Directory parent, String name, Data data,
                             boolean isImmutable) {
        this(s3Client, bucketName, parent, name, isImmutable);
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
    public File toFile() throws IOException {
        final File tempFile = File.createTempFile(TMP_FILE_PREFIX, TMP_FILE_SUFFIX);
        tempFile.deleteOnExit();
        try (FileOutputStream output = new FileOutputStream(tempFile)) {
            InputStream input = s3Client.getObject(getObjectRequest).getObjectContent();
            IOUtils.copy(input, output);
        }
        return tempFile;
    }

    @Override
    public void persist() throws PersistenceException {
        if (isImmutable && persisted) {
            return;
        }

        try (InputStream inputStream = getInputStream()) {

            String objectPath = getPathname();
            ObjectMetadata metadata = getObjectMetadata();

            PutObjectRequest putObjectRequest =
                    new PutObjectRequest(bucketName, objectPath, inputStream, metadata);
            s3Client.putObject(putObjectRequest);

            persisted = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected InputStream getInputStream() throws IOException {
        return data != null ? data.getInputStream() : new NullInputStream(0);
    }

    protected ObjectMetadata getObjectMetadata() {
        long contentLength = data != null ? data.getSize() : 0;
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(contentLength);

        return metadata;
    }
}

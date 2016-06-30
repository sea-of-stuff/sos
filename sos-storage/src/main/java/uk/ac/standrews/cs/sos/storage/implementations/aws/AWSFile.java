package uk.ac.standrews.cs.sos.storage.implementations.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import uk.ac.standrews.cs.sos.storage.data.Data;
import uk.ac.standrews.cs.sos.storage.exceptions.DataException;
import uk.ac.standrews.cs.sos.storage.exceptions.PersistenceException;
import uk.ac.standrews.cs.sos.storage.interfaces.Directory;
import uk.ac.standrews.cs.sos.storage.interfaces.File;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AWSFile extends AWSStatefulObject implements File {

    private Data data;
    private boolean persisted; // TODO - use this!

    public AWSFile(AmazonS3 s3Client, String bucketName, Directory parent, String name, boolean isImmutable) {
        super(s3Client, bucketName, parent, name, isImmutable);
    }

    public AWSFile(AmazonS3 s3Client, String bucketName, Directory parent, String name, Data data, boolean isImmutable) {
        super(s3Client, bucketName, parent, name, isImmutable);
        this.data = data;
    }

    @Override
    public String getPathname() {
        return logicalParent.getPathname() + name;
    }

    @Override
    public void persist() throws PersistenceException {
        String objectPath = getPathname();

        System.out.println("path: " + objectPath);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(data.getSize());

        System.out.println("meta: " + metadata.toString());
        try {
            PutObjectRequest putObjectRequest =
                    new PutObjectRequest(bucketName, objectPath, data.getInputStream(), metadata);
            s3Client.putObject(putObjectRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setData(Data data) throws DataException {
        if (!persisted) {
            this.data = data;
        } else {
            throw new DataException("Could not set data for file " + getPathname());
        }
    }

    @Override
    public Data getData() throws DataException {
        return null; // TODO - I am not sure what I should return here!
    }
}

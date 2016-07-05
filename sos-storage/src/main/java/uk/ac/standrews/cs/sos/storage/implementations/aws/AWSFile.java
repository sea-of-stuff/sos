package uk.ac.standrews.cs.sos.storage.implementations.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.S3Object;
import uk.ac.standrews.cs.sos.storage.data.Data;
import uk.ac.standrews.cs.sos.storage.data.InputStreamData;
import uk.ac.standrews.cs.sos.storage.exceptions.DataException;
import uk.ac.standrews.cs.sos.storage.interfaces.Directory;
import uk.ac.standrews.cs.sos.storage.interfaces.File;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AWSFile extends AWSStatefulObject implements File {

    public AWSFile(AmazonS3 s3Client, String bucketName, Directory parent,
                   String name, boolean isImmutable) {
        super(s3Client, bucketName, parent, name, isImmutable);

        if (exists()) {
            retrieveData();

            if (isImmutable) {
                this.persisted = true;
            } else {
                this.persisted = false;
            }
        }

    }

    public AWSFile(AmazonS3 s3Client, String bucketName, Directory parent,
                   String name, Data data, boolean isImmutable) {
        super(s3Client, bucketName, parent, name, data, isImmutable);

        if (isImmutable && exists()) {
            this.persisted = true;
        } else {
            this.persisted = false;
            this.data = data;
        }
    }

    @Override
    public boolean exists() {

        try (S3Object s3Object = s3Client.getObject(getObjectRequest)) {
            boolean objectExists = s3Object != null;
            updateData(s3Object, objectExists);

            return objectExists;
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
    public String getPathname() {
        return logicalParent.getPathname() + name;
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
        return data;
    }

    private void retrieveData() {
        try (S3Object s3Object = s3Client.getObject(getObjectRequest)) {
            boolean objectExists = s3Object != null;
            updateData(s3Object, objectExists);

        } catch (AmazonS3Exception e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateData(S3Object s3Object, boolean objectExists) {
        if (objectExists) {
            if (isImmutable && !persisted) {
                data = new InputStreamData(s3Object.getObjectContent());
            } else {
                data = new InputStreamData(s3Object.getObjectContent());
            }
        }
    }
}

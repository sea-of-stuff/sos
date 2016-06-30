package uk.ac.standrews.cs.sos.storage.implementations.aws;

import com.amazonaws.services.s3.AmazonS3;
import uk.ac.standrews.cs.sos.storage.exceptions.BindingAbsentException;
import uk.ac.standrews.cs.sos.storage.exceptions.PersistenceException;
import uk.ac.standrews.cs.sos.storage.interfaces.Directory;
import uk.ac.standrews.cs.sos.storage.interfaces.File;
import uk.ac.standrews.cs.sos.storage.interfaces.StatefulObject;

import java.util.Iterator;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AWSDirectory extends AWSStatefulObject implements Directory {

    public AWSDirectory(AmazonS3 s3Client, String bucketName,
                        Directory parent, String name, boolean isImmutable) {
        super(s3Client, bucketName, parent, name, isImmutable);
    }

    public AWSDirectory(AmazonS3 s3Client, String bucketName) {
        super(s3Client, bucketName);
    }

    @Override
    public String getPathname() {
        if (logicalParent == null) {
            return  ""; // TODO - not sure if this should return the bucket?
        } else if (name == null || name.isEmpty()) {
            return logicalParent.getPathname() + "/";
        } else {
            return logicalParent.getPathname() + name + "/";
        }
    }

    @Override
    public StatefulObject get(String name) throws BindingAbsentException {
        return null;
    }

    @Override
    public boolean contains(String name) {
        return false;
    }

    @Override
    public void addSOSFile(File file, String name) {

    }

    @Override
    public void addSOSDirectory(Directory directory, String name) {

    }

    @Override
    public void remove(String name) throws BindingAbsentException {

    }

    @Override
    public Iterator<StatefulObject> getIterator() {
        return null;
    }

    @Override
    public void persist() throws PersistenceException {
        // Cannot have empty directories in AWS S3, since everything is actually a file
    }
}

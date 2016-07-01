package uk.ac.standrews.cs.sos.storage.implementations.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import uk.ac.standrews.cs.sos.storage.exceptions.BindingAbsentException;
import uk.ac.standrews.cs.sos.storage.exceptions.PersistenceException;
import uk.ac.standrews.cs.sos.storage.implementations.NameObjectBindingImpl;
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
        File file = new AWSFile(s3Client, bucketName, this, name, isImmutable);
        return file.exists();
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
    public Iterator<NameObjectBindingImpl> getIterator() {
        return new DirectoryIterator();
    }

    @Override
    public void persist() throws PersistenceException {
        // Cannot have empty directories in AWS S3, since everything is actually a file
    }

    private class DirectoryIterator implements Iterator<NameObjectBindingImpl> {

        private static final int OBJECTS_PER_REQUESTS = 2;
        private static final String DELIMITER = "/";

        private ObjectListing listing;
        private Iterator<S3ObjectSummary> summary;
        private String marker = null;

        public DirectoryIterator() {
            ListObjectsRequest objectsRequest = getListObjectRequest(null);
            listing = s3Client.listObjects(objectsRequest);
            summary = listing.getObjectSummaries().iterator();
        }

        private ListObjectsRequest getListObjectRequest(String marker) {
            ListObjectsRequest objectsRequest =
                    new ListObjectsRequest(bucketName,
                            getPathname(),
                            marker,
                            DELIMITER,
                            OBJECTS_PER_REQUESTS /* Not sure how this should be set
                        * check ObjectListing.getNextMarker
                        */);

            return objectsRequest;
        }

        @Override
        public boolean hasNext() {
            boolean next = summary.hasNext();

            if (!next) {
                marker = listing.getNextMarker();

                if (marker == null) {
                    next = false;
                } else {
                    ListObjectsRequest objectsRequest = getListObjectRequest(marker);
                    listing = s3Client.listObjects(objectsRequest);
                    summary = listing.getObjectSummaries().iterator();
                    next = true;
                }
            }

            return next;
        }

        @Override
        public NameObjectBindingImpl next() {

            if(!hasNext()) {
                return null;
            }

            S3ObjectSummary object = summary.next();

            System.out.println(object.getKey());

            // TODO - create stateful objects from S3Object
            return null;
        }
    }
}

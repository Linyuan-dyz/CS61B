package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.join;

public class Blob implements Serializable {
    /** The blobs directory. */
    public static final File blobs = join(Repository.OBJECTS_DIR, "blobs");

    //originalFile refers to the file to be stored.
    private File originalFile;
    //blobName refers to the file in blobsFile.
    private File blobName;
    //content refers to the file content as String.
    private String content;
    //path is the arg that is passed into function as the path to the file.
    private String path;
    //blobID refers to the sha1 sequence of the file
    //it needs filePath and fileseq to consist the blobID
    private String blobID;

    //return the filePath
    public String getPath() {
        return path;
    }
    //return the file f by the giving filePath.
    private File getOriginalFile() {
        return originalFile;
    }
    //return the file content as string
    private String getContent() {
        return content;
    }
    //return the bolbID
    public String getBlobID() {
        return blobID;
    }
    public File getBlobName() {
        return blobName;
    }

    /*create a new blob and create the correspond file*/
    //if the original file doesn't exist, put "" into content.
    public Blob(String path) {
        this.path = path;
        this.originalFile = Utils.join(path);
        if (originalFile.exists()) {
            this.content = Utils.readContentsAsString(originalFile);
            this.blobID = Utils.sha1(path, content);
        } else {
            this.content = "";
            this.blobID = "";
        }
        this.blobName = new File(blobs, blobID);
    }

    /*save the correspone blob file in the blobs*/
    public void saveBlob() {
        Utils.writeObject(blobName, this);
    }
}

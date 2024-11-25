package gitlet;

import java.io.File;
import java.io.Serializable;

public class Blob implements Serializable {

    static final File blobsFile = Utils.join(Commit.objectFile, "blobs");

    //fileNameFromPath refers to the file to be stored.
    private File fileNameFromPath;

    //blobFileName refers to the file in blobsFile.
    private File blobFileName;

    //fileseq refers to the file content as String.
    private String fileSeq;

    //filePath is the arg that is passed into function as the path to the file.
    private String filePath;

    //blobID refers to the sha1 sequence of the file
    //it needs filePath and fileseq to consist the blobID
    private String blobID;

    //return the filePath
    public String getFilePath() {
        return filePath;
    }

    //return the file f by the giving filePath.
    private File getFileNameFromPath(String filePath) {
        return Utils.join(filePath);
    }

    //return the file content as string
    private String getFileSeq() {
        return Utils.readContentsAsString(fileNameFromPath);
    }

    //return the bolbID
    public String getBlobID() {
        return Utils.sha1(filePath, fileSeq);
    }

    public File getBlobFileName() {
        return Utils.join(blobsFile, blobID);
    }

    //get the file to make the construct function
    public Blob(String filePath) {
        this.filePath = filePath;
        this.fileNameFromPath = getFileNameFromPath(filePath);
        this.fileSeq = getFileSeq();
        this.blobID = getBlobID();
        this.blobFileName = new File(blobsFile, blobID);
    }


    public void updateBlobAndAddStage() {
        Utils.writeObject(blobFileName, this);
        Add newAdd = new Add(this);
        newAdd.saveAdd();
    }

    public static Blob getBlobFromBlobID(String blobID) {
        File blobFileName = Utils.join(blobsFile, blobID);
        Blob blob = Utils.readObject(blobFileName, Blob.class);
        return blob;
    }
}

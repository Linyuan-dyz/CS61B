package gitlet;

import java.io.File;
import java.io.Serializable;

public class Blob implements Serializable {

    static final File blobsFile = Utils.join(".object", "blobs");

    //fileNameFromPath refers to the file to be stored.
    private File fileNameFromPath;

    //blobFileName refers to the file in blobsFile.
    private File blobFileName;

    //fileContent refers to the file content as byte[], which is the result of serializing.
    private byte[] fileContent;

    //fileseq refers to the file content as String.
    private String fileseq;

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
    private File getfileNameFromPath(String filePath) {
        File f = Utils.join(filePath);
        return f;
    }

    //return the file content by fileName
    private byte[] getfileContent() {
        byte[] fc = Utils.serialize(fileNameFromPath);
        return fc;
    }

    //return the file content as string
    private String getFileseq() {
        String fs = Utils.readContentsAsString(fileNameFromPath);
        return fs;
    }

    //return the bolbID
    public String getBlobID() {
        String id = Utils.sha1(filePath, fileseq);
        return id;
    }

    private File getBlobFileName() {
        File blobFileName = Utils.join(blobsFile, blobID);
        return blobFileName;
    }

    //get the file to make the construct function
    public Blob(String filePath) {
        this.filePath = filePath;
        this.fileNameFromPath = getfileNameFromPath(filePath);
        this.blobID = getBlobID();
        this.blobFileName = getBlobFileName();
        this.fileContent = getfileContent();
        this.fileseq = getFileseq();
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

package gitlet;

import java.io.File;
import java.util.TreeMap;

public class Add {
    static final File addStage = Utils.join(".gitlet", "addStage");

    //use a TreeMap to cast the path to the file's blobID.
    private TreeMap<String, String> pathToBlobID = new TreeMap<>();

    public Add() {

    }

    /** put blob into pathToBlobID and write the TreeMap into the file.
     *if the path doesn't exit, then add it into TreeMap,
     *if the ID is equal to the ID in commit, do nothing,
     *otherwise, overwrite the bolbID.
     */
    public Add(Blob blob) {
        String newPath = blob.getFilePath();
        String newBolbID = blob.getBlobID();

        Commit masterCommit = Repository.getMasterCommit();

        if (pathToBlobID.get(newPath) == null) {
            pathToBlobID.put(newPath, newBolbID);
        } else if (pathToBlobID.get(newPath) == masterCommit.getTreeMap().get(newPath)){
            return;
        } else {
            pathToBlobID.replace(newPath, newBolbID);
        }
    }

    public void saveAdd() {
        Utils.writeContents(addStage, pathToBlobID);
    }

    public void clear() {
        pathToBlobID.clear();
        Utils.writeContents(addStage, pathToBlobID);
    }


}

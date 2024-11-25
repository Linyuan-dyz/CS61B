package gitlet;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;

public class Remove {
    static final File removeStage = Utils.join(Repository.GITLET_DIR, "removeStage");
    static final File removeFile = new File(removeStage, "removeFile");

    //use a TreeMap to cast the path to the file's blobID.
    private TreeMap<String, String> pathToBlobID = new TreeMap<>();

    /** Firstly, figure out whether blob is in the front commit, if it does,
     *  add it into removeFile, and delete its local file. if it doesn't,
     *  check whether it is in the addFile, if it does,
     *  remove it from the addFile. if it doesn't,
     *  print the error message "No reason to remove the file.", if it does,
    * */
    public Remove(Blob blob) {
        String newPath = blob.getFilePath();
        String newBolbID = blob.getBlobID();
        File newBlobFileName = blob.getBlobFileName();

        TreeMap<String, String> returnTreeMap = new TreeMap<>();
        if (removeFile.exists()) {
            returnTreeMap = Utils.readObject(removeFile, TreeMap.class);
        }

        Commit masterCommit = Repository.getMasterCommit();

        TreeMap<String, String> addTreeMap = Repository.getAddPathToBlobID();
        TreeMap<String, String> commitTreeMap = masterCommit.getTreeMap();



        if (!commitTreeMap.isEmpty() && commitTreeMap.get(newPath).equals(newBolbID)) {
            returnTreeMap.put(newPath, newBolbID);
            newBlobFileName.delete();
        } else {
            if (addTreeMap.get(newPath) == newBolbID) {
                addTreeMap.remove(newPath);
                Add.saveAddAfterDelete(addTreeMap);
            } else {
                System.out.println("No reason to remove the file.");
            }
        }
    }

    public void saveRemove() {
        Utils.writeObject(removeFile, pathToBlobID);
    }
}

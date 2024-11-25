package gitlet;

import java.io.File;
import java.util.TreeMap;

public class Add {
    static final File addStage = Utils.join(Repository.GITLET_DIR, "addStage");
    static final File addFile = new File(addStage, "addFile");

    //use a TreeMap to cast the path to the file's blobID.
    private TreeMap<String, String> pathToBlobID = new TreeMap<>();

    /**judge whether the addFile exists, if it does, continue from the previous TreeMap.
     *put blob into pathToBlobID and write the TreeMap into the file.
     *if the path doesn't exit, then add it into TreeMap,
     *if the ID is equal to the ID in commit, do nothing,
     *otherwise, overwrite the bolbID.
     */
    public Add(Blob blob) {
        String newPath = blob.getFilePath();
        String newBolbID = blob.getBlobID();

        TreeMap<String, String> returnTreeMap = new TreeMap<>();
        if (addFile.exists()) {
            returnTreeMap = Utils.readObject(addFile, TreeMap.class);
        }

        Commit masterCommit = Repository.getMasterCommit();

        TreeMap<String, String> commitTreeMap = masterCommit.getTreeMap();

        if (!commitTreeMap.isEmpty() && commitTreeMap.containsValue(newBolbID)) {
            this.pathToBlobID = returnTreeMap;
            return;
        }

        if (returnTreeMap.get(newPath) == null) {
            returnTreeMap.put(newPath, newBolbID);
        } else {
            returnTreeMap.replace(newPath, newBolbID);
        }

        this.pathToBlobID = returnTreeMap;
    }

    public void saveAdd() {
        Utils.writeObject(addFile, pathToBlobID);
    }

    public static void saveAddAfterDelete(TreeMap newMap) {
        Utils.writeObject(addFile, newMap);
    }

}

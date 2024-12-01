package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.TreeMap;

import static gitlet.Utils.join;

public class Add implements Serializable {
    /** The addStage directory. */
    public static final File ADDSTAGE = join(Repository.STAGES_DIR, "addStage");
    public static final File ADDFILE = new File(ADDSTAGE, "addFile");

    private TreeMap<String, String> pathToID = new TreeMap<>();

    public Add() {
        this.pathToID = new TreeMap<>();
    }

    /**judge whether the addFile exists, if it does, continue from the previous TreeMap.
     *put blob into pathToBlobID and write the TreeMap into the file.
     *if the path doesn't exit, then add it into TreeMap,
     *if the ID is equal to the ID in commit, do nothing,
     *otherwise, overwrite the bolbID.
     */
    public Add(Blob newBlob) {
        String newPath = newBlob.getPath();
        String newID = newBlob.getBlobID();

        TreeMap newRemoveTree = Repository.getRemoveTree();
        if (newRemoveTree.containsValue(newID)) {
            newRemoveTree.remove(newPath, newID);
            Remove newRemove = new Remove(newRemoveTree);
            newRemove.saveRemove();
            return;
        }

        //get the masterCommit tree, and judge whether it contains newPath and newBlobID.
        TreeMap commitTree = Repository.getMasterCommit().getTreeMap();
        if (!commitTree.isEmpty()
                && commitTree.get(newPath) != null
                && commitTree.get(newPath).equals(newID)) {
            return;
        }
        //continue from here.
        TreeMap<String, String> originalTreeMap = new TreeMap<>();
        if (ADDFILE.exists()) {
            originalTreeMap = Utils.readObject(ADDFILE, TreeMap.class);
        }

        if (originalTreeMap.get(newPath) == null) {
            originalTreeMap.put(newPath, newID);
        } else {
            originalTreeMap.replace(newPath, newID);
        }

        this.pathToID = originalTreeMap;
    }

    public Add(TreeMap newTree) {
        this.pathToID = newTree;
    }

    public void saveAdd() {
        Utils.writeObject(ADDFILE, this.pathToID);
    }
}

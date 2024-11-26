package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.TreeMap;

import static gitlet.Utils.join;

public class Remove implements Serializable {
    /** The removeStage directory. */
    public static final File removeStage = join(Repository.STAGES_DIR, "removeStage");
    public static final File removeFile = new File(removeStage, "removeFile");

    //use a TreeMap to cast the path to the file's blobID.
    private TreeMap<String, String> pathToID = new TreeMap<>();

    public Remove() {
        this.pathToID = new TreeMap<>();
    }

    /** Firstly, figure out whether blob is in the front commit, if it does,
     *  add it into removeFile, and delete its local file. if it doesn't,
     *  check whether it is in the addFile, if it does,
     *  remove it from the addFile. if it doesn't,
     *  print the error message "No reason to remove the file.", if it does,
     * */
    public Remove(Blob newBlob) {
        String newPath = newBlob.getPath();
        String newID = newBlob.getBlobID();

        TreeMap<String, String> originalTreeMap = new TreeMap<>();
        if (removeFile.exists()) {
            originalTreeMap = Utils.readObject(removeFile, TreeMap.class);
        }

        //get the masterCommit tree, and judge whether it contains newPath and newBlobID.
        TreeMap commitTree = Repository.getMasterCommit().getTreeMap();

        if (!commitTree.isEmpty() && commitTree.get(newPath) != null && (commitTree.get(newPath).equals(newID) || newID.equals(""))) {
            if (newID.equals("")) {
                newID = (String) commitTree.get(newPath);
            }
            if (newBlob.getBlobName().exists()) {
                newBlob.getBlobName().delete();
            }
            this.pathToID.put(newPath, newID);
            return;
        }
        //continue from here.

        TreeMap addTreeMap = Repository.getAddTree();
        if (addTreeMap.containsValue(newID)) {
            addTreeMap.remove(newPath, newID);
            Add addAfterRemove = new Add(addTreeMap);
            addAfterRemove.saveAdd();
            if (newBlob.getBlobName().exists()) {
                newBlob.getBlobName().delete();
            }
        } else {
            System.out.println("No reason to remove the file.");
        }

        this.pathToID = originalTreeMap;
    }

    public void saveRemove() {
        Utils.writeObject(removeFile, this.pathToID);
    }
}

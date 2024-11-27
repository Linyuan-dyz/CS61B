package gitlet;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * .gitlet
     *      |-- objects
     *              |-- commit
     *              |-- blob
     *      |-- refs
     *              |--headsFile
     *                  |-- masterFile
     *      |-- stages
     *              |-- addStage
     *              |-- removeStage
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The objects directory. */
    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");
    /** The refs directory. */
    public static final File REFS_DIR = join(GITLET_DIR, "refs");
    /** The stages directory. */
    public static final File STAGES_DIR = join(GITLET_DIR, "stages");
    /* TODO: fill in the rest of this class. */

    public static void setUpPresistance() {
        CWD.mkdir();
        GITLET_DIR.mkdir();
        OBJECTS_DIR.mkdir();
        Commit.commits.mkdir();
        Blob.blobs.mkdir();
        REFS_DIR.mkdir();
        Head.headsFile.mkdir();
        Head.masterFile.mkdir();
        STAGES_DIR.mkdir();
        Add.addStage.mkdir();
        Remove.removeStage.mkdir();
    }

    public static Head getMaster() {
        String[] masterContent = Head.masterFile.list();
        String masterName = masterContent[0];
        return Utils.readObject(Utils.join(Head.masterFile, masterName), Head.class);
    }

    public static Commit getMasterCommit() {
        Head master = getMaster();
        return Utils.readObject(Utils.join(Commit.commits, master.getCommitID()), Commit.class);
    }
    public static Head getBranch(String branchName) {
        return Utils.readObject(Utils.join(Head.headsFile, branchName), Head.class);
    }

    public static Commit getCommitFromCommitID(String commitID) {
        File cf = Utils.join(Commit.commits, commitID);
        return Utils.readObject(cf, Commit.class);
    }

    public static TreeMap<String, String> getAddTree() {
        TreeMap<String, String> emptyMap = new TreeMap<>();
        if (!Add.addFile.exists()) {
            return emptyMap;
        }
        TreeMap<String, String> newTreeMap = Utils.readObject(Add.addFile, TreeMap.class);
        return newTreeMap;
    }

    public static TreeMap<String, String> getRemoveTree() {
        TreeMap<String, String> emptyMap = new TreeMap<>();
        if (!Remove.removeFile.exists()) {
            return emptyMap;
        }
        TreeMap<String, String> newTreeMap = Utils.readObject(Remove.removeFile, TreeMap.class);
        return newTreeMap;
    }

    public static Blob getBlobFromBlobID(String blobID) {
        File blobFileName = Utils.join(Blob.blobs, blobID);
        return Utils.readObject(blobFileName, Blob.class);
    }

    public static void makeInit() {
        File gitletDir = Utils.join(CWD, ".gitlet");
        if (gitletDir.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        setUpPresistance();
        Commit init = new Commit();
        init.saveCommit();
        Head master = new Head(init);
        master.saveInMaster();
    }

    public static void makeAdd(String path) {
        File f = Utils.join(path);
        if (!f.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        Blob newBlob = new Blob(path);
        newBlob.saveBlob();
        Add newAdd = new Add(newBlob);
        newAdd.saveAdd();
    }

    public static void makeCommit(String message) {
        TreeMap addTree =  getAddTree();
        TreeMap removeTree = getRemoveTree();
        if (addTree.isEmpty() && removeTree.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        Commit newCommit = new Commit(message);
        newCommit.saveCommit();
        Commit.cleanAddFile();
        Commit.cleanRemoveFile();
        Head originalMaster = getMaster();
        Head master = new Head(originalMaster.getBranchName(), newCommit);
        master.saveInMaster();
    }

    public static void makeRemove(String path) {
        Blob newBlob = new Blob(path);
        Remove newRemove = new Remove(newBlob);
        newRemove.saveRemove();
    }

    //print the single log message of current commit.
    public static void printLog(Commit currentCommit) {
        System.out.println("===");
        System.out.println("commit " + currentCommit.getCommitID());
        System.out.println("Date: " + currentCommit.getCommitDate());
        System.out.println(currentCommit.getCommitMessage());
        System.out.printf("\n");
    }

    //print all log message by sequence of all commits.
    public static void printAllLog() {
        Commit masterCommit = getMasterCommit();
        printLog(masterCommit);
        Commit currentCommit;
        for(String currentCommitID: masterCommit.getParent()) {
            currentCommit = getCommitFromCommitID(currentCommitID);
            printLog(currentCommit);
        }
    }

    public static void find(String message) {
        Commit masterCommit = getMasterCommit();
        Commit currentCommit;
        boolean flag = false;
        if (masterCommit.getCommitMessage().equals(message)) {
            System.out.printf(masterCommit.getCommitID() + "\n");
            flag = true;
        }
        for(String currentCommitID : masterCommit.getParent()) {
            currentCommit = getCommitFromCommitID(currentCommitID);
            if (currentCommit.getCommitMessage().equals(message)) {
                System.out.printf(currentCommit.getCommitID() + "\n");
                flag = true;
            }
        }
        if (!flag) {
            System.out.println("Found no commit with that message.");
            System.exit(0);
        }
    }

    //finished the front 3 items, hasn't finished the last two sections.
    public static void status() {
        //problems here, Head.setMaster() neeed to finish.
        System.out.println("=== Branches ===");
        String[] dir = Head.masterFile.list();
        System.out.println("*" + dir[0]);
        for(String headName : Utils.plainFilenamesIn(Head.headsFile)) {
            System.out.println(headName);
        }
        System.out.printf("\n");
        System.out.println("=== Staged Files ===");
        File addFile = Utils.join(Add.addFile);
        File removeFile = Utils.join(Remove.removeFile);
        if (addFile.exists()) {
            TreeMap<String, String> addTreeMap = Utils.readObject(Add.addFile, TreeMap.class);
            Collection addCollection = addTreeMap.keySet();
            Iterator addIter = addCollection.iterator();
            while (addIter.hasNext()) {
                System.out.println(addIter.next());
            }
        }
        System.out.printf("\n");
        System.out.println("=== Removed Files ===");
        if (removeFile.exists()) {
            TreeMap<String, String> removeTreeMap = Utils.readObject(Remove.removeFile, TreeMap.class);
            Collection removeCollection = removeTreeMap.keySet();
            Iterator removeIter = removeCollection.iterator();
            while (removeIter.hasNext()) {
                System.out.println(removeIter.next());
            }
        }
        System.out.printf("\n");
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.printf("\n");
        System.out.println("=== Untracked Files ===");
        System.out.printf("\n");
    }

    public static void createBranch(String brancheName) {
        if (Utils.join(Head.headsFile, brancheName).exists() || Utils.join(Head.masterFile, brancheName).exists()) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        Commit masterCommit = getMasterCommit();
        Head newHead = new Head(brancheName, masterCommit);
        newHead.saveInHeads();
    }

    public static void removeBranch(String branchName) {
        if (getMaster().getBranchName().equals(branchName)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        boolean flag = false;
        Collection c = Utils.plainFilenamesIn(Head.headsFile);
        Iterator headIter = c.iterator();
        while (headIter.hasNext()) {
            if (headIter.next().equals(branchName)) {
                Utils.join(Head.headsFile, branchName).delete();
                flag = true;
            }
        }
        if (!flag) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
    }

    //Takes the version of the file as it exists in the head commit and puts it in the working directory
    //if the file isn't in the commit tree, error,
    //otherwise, get the blobID and find out the blob, write the content into the original file path.
    public static void checkoutFile(String fileName) {
        Commit masterCommit = getMasterCommit();
        TreeMap masterCommitTree = masterCommit.getTreeMap();
        if (!masterCommitTree.containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        String fileBlobID = (String) masterCommitTree.get(fileName);
        Blob fileBlob = getBlobFromBlobID(fileBlobID);
        Utils.writeContents(Utils.join(fileName), fileBlob.getContentAsByte());
    }

    public static void checkoutFileWithCommitID(String targetCommitID, String fileName) {
        File targetCommitFile = Utils.join(Commit.commits, targetCommitID);
        if (!targetCommitFile.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        Commit targetCommit = getCommitFromCommitID(targetCommitID);
        TreeMap targetCommitTree = targetCommit.getTreeMap();
        if (!targetCommitTree.containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        String fileBlobID = (String) targetCommitTree.get(fileName);
        Blob fileBlob = getBlobFromBlobID(fileBlobID);
        Utils.writeContents(Utils.join(fileName), fileBlob.getContentAsByte());
    }

    public static void checkoutBranch(String branchName) {
        if (getMaster().getBranchName().equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        if (!Utils.join(Head.headsFile, branchName).exists()) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        if (!getAddTree().isEmpty() || CWD.length() > 1) {
            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
            System.exit(0);
        }
        //delete add, then write new.(might be wrong idea?)
        Collection c = Utils.plainFilenamesIn(CWD);
        Iterator CWDIter = c.iterator();
        while (CWDIter.hasNext()) {
            Utils.join((String) CWDIter.next()).delete();
        }
        Head newHead = getBranch(branchName);
        newHead.setMaster();
        Commit newCommit = getCommitFromCommitID(newHead.getCommitID());
        TreeMap newCommitTree = newCommit.getTreeMap();
        Collection newBlobIDs = newCommitTree.values();
        Iterator newBlobID = newBlobIDs.iterator();
        while (newBlobID.hasNext()) {
            Blob newBlob = getBlobFromBlobID((String) newBlobID.next());
            Utils.writeContents(Utils.join(CWD, newBlob.getPath()), newBlob.getContentAsByte());
        }
        Commit.cleanAddFile();
        Commit.cleanRemoveFile();
    }

}

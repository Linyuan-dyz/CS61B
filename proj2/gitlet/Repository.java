package gitlet;

import java.io.File;
import java.util.*;

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
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /* TODO: fill in the rest of this class. */
    /*
    * repository structure:
    * .gitlet --
    *       | --object  √
    *               | --commit  √
    *               | --blobs   √
    *       | --refs    √
    *               | --heads   √
    *                   | --master  √
    *       | --addStage        √
    *       | --removeStage     √
    *       | -- HEAD
    * */
    private static void setupPersistence(){
        GITLET_DIR.mkdir();
        Add.addStage.mkdir();
        Commit.objectFile.mkdir();
        Commit.commitFile.mkdir();
        Remove.removeStage.mkdir();
        Blob.blobsFile.mkdir();
        Head.refsFile.mkdir();
        Head.headsFile.mkdir();
        Head.masterFile.mkdir();
    }

    //get the correspond commit by its commitID.
    public static Commit getCommitFromCommitID(String commitID) {
        File cf = Utils.join(Commit.commitFile, commitID);
        Commit retCommit = Utils.readObject(cf, Commit.class);
        return retCommit;
    }

    //assume that there is a head in the master file
    public static File getMasterFileName() {
        String[] dir = Head.masterFile.list();
        return Utils.join(Head.masterFile, dir[0]);
    }

    public static Head getMasterHead() {
        File masterFile = getMasterFileName();
        Head masterHead = Utils.readObject(masterFile, Head.class);
        return masterHead;
    }

    //get the master commit that the master pointer point to.
    public static Commit getMasterCommit() {

        //get the master pointer
        File masterFileName = Utils.join(getMasterFileName());

        //get the commitID that the master pointer point to.
        String commitID = Utils.readObject(masterFileName, Head.class).getCommitID();

        //get the commit the commitID refers to.
        Commit masterCommit = getCommitFromCommitID(commitID);

        Collection c = masterCommit.getTreeMap().values();
        Iterator iter = c.iterator();
        while (iter.hasNext())  {
            System.out.println(iter.next());
        }

        return masterCommit;
    }

    //get the branch commit that the master pointer point to.
    public static Commit getBranchCommit(String branchName) {

        //get the master pointer
        File branchFileName = Utils.join(Head.getBranchFileName(branchName));

        //get the commitID that the master pointer point to.
        String commitID = Utils.readObject(branchFileName, Head.class).getCommitID();

        //get the commit the commitID refers to.
        Commit branchCommit = getCommitFromCommitID(commitID);

        return branchCommit;
    }

    public static TreeMap<String, String> getAddPathToBlobID() {
        TreeMap<String, String> emptyMap = new TreeMap<>();
        if (!Add.addFile.exists()) {
            return emptyMap;
        }
        TreeMap<String, String> newTreeMap = Utils.readObject(Add.addFile, TreeMap.class);
        return newTreeMap;
    }

    public static TreeMap<String, String> getAllRemovePathToBlobID() {
        TreeMap<String, String> emptyMap = new TreeMap<>();
        if (!Remove.removeFile.exists()) {
            return emptyMap;
        }
        TreeMap<String, String> newTreeMap = Utils.readObject(Remove.removeFile, TreeMap.class);
        return newTreeMap;
    }

    public static void makeInit() {
        File gitletDir = Utils.join(CWD, ".gitlet");
        if (gitletDir.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        setupPersistence();
        Commit init = new Commit();
        init.makeCommit();
        Head newHead = new Head(init);
        newHead.saveHeadNotMaster();
        newHead.setMaster("master");

    }

    public static void makeAdd(String fileName) {
        File f = Utils.join(fileName);
        if (!f.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        Blob newBlob = new Blob(fileName);
        newBlob.updateBlobAndAddStage();
    }

    /** create a new commit with the given message.
     *  get the master head name, and use this name & new commit to create a new head.
     *  save this new head in the headsFile
     *  change it into new masterHead (including deleting previous master head and deleting the head in the headsFile)
     *  ATTENTION: commit doesn't change the head's name, it's still the old head, just change the commit.
     * */
    public static void makeCommit(String message) {
        if (getAddPathToBlobID().isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        Commit newCommit = new Commit(message);
        newCommit.makeCommit();

        Head masterHead = getMasterHead();
        String masterHeadName = masterHead.getBranchName();
        Head newHead = new Head(masterHeadName, newCommit);
        newHead.saveHeadNotMaster();
        newHead.setMaster(masterHeadName);
    }

    public static void makeRemove(String fileName) {
        File f = Utils.join(fileName);
        if (!f.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        Blob newBlob = new Blob(fileName);
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
        Commit headCommit = getMasterCommit();
        printLog(headCommit);
        Commit currentCommit;
        for(String currentCommitID: headCommit.getParent()) {
            currentCommit = getCommitFromCommitID(currentCommitID);
            printLog(currentCommit);
        }
    }

    public static void find(String message) {
        Commit headCommit = getMasterCommit();
        Commit currentCommit;
        Boolean flag = false;
        if (headCommit.getCommitMessage().equals(message)) {
            System.out.printf(headCommit.getCommitID() + "\n");
            flag = true;
        }
        for(String currentCommitID : headCommit.getParent()) {
            currentCommit = getCommitFromCommitID(currentCommitID);
            if (currentCommit.getCommitMessage().equals(message)) {
                System.out.printf(currentCommit.getCommitID() + "\n");
                flag = true;
            }
        }
        if (!flag) {
            System.out.println("Found no commit with that message.");
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
            Collection addCollection = addTreeMap.values();
            Iterator addIter = addCollection.iterator();
            while (addIter.hasNext()) {
                System.out.println(Blob.getBlobFromBlobID((String)addIter.next()).getFilePath());
            }
        }
        System.out.printf("\n");
        System.out.println("=== Removed Files ===");
        if (removeFile.exists()) {
            TreeMap<String, String> removeTreeMap = Utils.readObject(Remove.removeFile, TreeMap.class);
            Collection removeCollection = removeTreeMap.values();
            Iterator removeIter = removeCollection.iterator();
            while (removeIter.hasNext()) {
                System.out.println(Blob.getBlobFromBlobID((String)removeIter.next()).getFilePath());
            }
        }
        System.out.printf("\n");
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.printf("\n");
        System.out.println("=== Untracked Files ===");
        System.out.printf("\n");
    }

    public static void createBranch(String branchName) {
        if (Utils.join(Head.headsFile, branchName).exists() || Utils.join(Head.masterFile, branchName).exists()) {
            System.out.println("A branch with that name already exists.");
        }
        Commit headCommit = getMasterCommit();
        Head newBranch = new Head(branchName, headCommit);
        newBranch.saveHeadNotMaster();
    }

    public static void deleteBranch(String branchName) {
        boolean flag = false;
        File masterHeadFile = getMasterFileName();
        Head masterHead = Utils.readObject(masterHeadFile, Head.class);
        String masterName = masterHead.getBranchName();
        if (masterName == branchName) {
            System.out.println("Cannot remove the current branch.");
        } else {
            for(String headName : Utils.plainFilenamesIn(Head.headsFile)) {
                if (headName == branchName) {
                    File targetFile = Utils.join(Head.headsFile, branchName);
                    //Utils.restrictedDelete(targetFile);
                    targetFile.delete();
                    flag = true;
                }
            }
            if (!flag) {
                System.out.println("A branch with that name does not exist.");
            }
        }
    }
}

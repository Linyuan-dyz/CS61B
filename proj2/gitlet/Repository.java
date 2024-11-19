package gitlet;

import java.io.File;
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

    //get the master commit that the master pointer point to.
    public static Commit getMasterCommit() {

        //get the master pointer
        File masterFileName = Utils.join(Head.masterFile);

        //get the commitID that the master pointer point to.
        String commitID = Utils.readObject(masterFileName, Head.class).getCommitID();

        //get the commit the commitID refers to.
        Commit masterCommit = Commit.getCommitFromCommitID(commitID);

        return masterCommit;
    }

    public static TreeMap<String, String> getAllPathToBlobID() {
        TreeMap<String, String> newTreeMap = Utils.readObject(Add.addStage, TreeMap.class);
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
        Head newHead = new Head(init);
        newHead.saveHead();
        init.makeCommit();
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

    public static void makeCommit(String message) {
        if (getAllPathToBlobID().isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        if (message == null) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        } else {
            Commit newCommit = new Commit(message);
            Head newHead = new Head(newCommit);
            newHead.saveHead();
            newCommit.makeCommit();
        }
    }
}

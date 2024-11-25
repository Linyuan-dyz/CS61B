package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     * 1.log message  √
     * 2.commit date  √
     * 3.blobID (haven't finish the class yet, it point to the blobs to indicate versions)
     * 4.reference to parent commit (just need a one-direction arrow, which enables to trace back,
     *                                  rather than proceed to the future version)  √
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */
    static final File objectFile = Utils.join(Repository.GITLET_DIR, "object");
    static final File commitFile = Utils.join(objectFile, "commit");

    /** The message of this Commit. */
    private String message;

    /* TODO: fill in the rest of this class. */

    //commit date
    private Date date;

    //standerTime for sha1 to calculate
    private String standerTime;

    //commitID
    private String commitID;

    //reference to parent commit
    private List<String> parent = new LinkedList<>();

    //use a TreeMap to cast the path to the file's blobID.
    private TreeMap<String, String> pathToBlobID;

    //use commitFileName to indicate which file to point, in order to find the specific file.
    //commitFileName is consisted of ".commit" and the unique commitID.
    private File commitFileName;

    //return the commit message.
    public String getCommitMessage() {
        return message;
    }

    //return the commit date(Date).
    public Date getDate() {
        return date;
    }

    //return the commit time(UTC).
    public String getCommitDate() {
        return standerTime;
    }

    //return the parent of a commit.
    public List<String> getParent() {
        return parent;
    }

    //return the TreeMap in Commit.
    public TreeMap getTreeMap() {
        return pathToBlobID;
    }

    //return the specific file.
    public File getCommitFileName() {
        return Utils.join(commitFile, commitID);
    }

    //return the commitID of a commit.
    public String getCommitID() {
        return Utils.sha1(message, standerTime, pathToBlobID.toString(), parent.toString());
    }

    //return the updated parent, every time the new commit is implemented, the parent adds it's commitID.
    private List<String> updateParent() {
        Commit lastMasterCommit = Repository.getMasterCommit();
        List<String> newParent = lastMasterCommit.getParent();
        newParent.addFirst(lastMasterCommit.getCommitID());
        return newParent;
    }

    //change the date to stander time in order to generate sha1ID
    private static String dateToTimeStamp(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        return dateFormat.format(date);
    }

    private TreeMap<String, String> combineAddAndRemove() {
        TreeMap<String, String> addTree = Repository.getAddPathToBlobID();
        TreeMap<String, String> removeTree = Repository.getAllRemovePathToBlobID();
        Collection removeCollection = removeTree.keySet();
        Iterator removeIter = removeCollection.iterator();
        while (removeIter.hasNext()) {
            addTree.remove(removeIter.next());
        }
        return addTree;
    }

    //the non-argument constructor (init function)
    //bolbID & parent are empty, not null, in case of sha1 error
    public Commit() {
        this.message = "initial commit";
        this.date = new Date(0);
        this.standerTime = dateToTimeStamp(date);
        this.parent = new LinkedList<>();
        this.pathToBlobID = new TreeMap<>();
        this.commitID = getCommitID();
        this.commitFileName = new File(commitFile, commitID);
    }

    //the normal constructor (blobID & parent might need to fix)
    public Commit(String message) {
        this.message = message;
        this.date = new Date();
        this.standerTime = dateToTimeStamp(date);
        this.parent = updateParent();
        this.pathToBlobID = combineAddAndRemove();
        commitID = getCommitID();
        commitFileName = new File(commitFile, commitID);
        cleanAddFile();
        cleanRemoveFile();
    }

    //clean addFile after commit.
    //overwrite addFile with empty treemap.
    public void cleanAddFile() {
        File currentFile = Utils.join(Add.addFile);
        TreeMap<String, String> nullPathToBlobID = new TreeMap<>();
        Utils.writeObject(currentFile, nullPathToBlobID);
    }

    public void cleanRemoveFile() {
        File currentFile = Utils.join(Remove.removeFile);
        TreeMap<String, String> nullPathToBlobID = new TreeMap<>();
        Utils.writeObject(currentFile, nullPathToBlobID);
    }

    //write the commit into commitFileName.
    public void makeCommit() {
        Utils.writeObject(commitFileName, this);
    }

}

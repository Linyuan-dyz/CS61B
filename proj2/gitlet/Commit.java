package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

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
    static final File objectFile = Utils.join(".gitlet", "object");
    static final File commitFile = Utils.join(".object", "commit");

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
    private List<String> parent;

    //use a TreeMap to cast the path to the file's blobID.
    private TreeMap<String, String> pathToBlobID = new TreeMap<>();

    //use commitFileName to indicate which file to point, in order to find the specific file.
    //commitFileName is consisted of ".commit" and the unique commitID.
    private File commitFileName;

    //return the parent of a commit.
    public List<String> getParent() {
        return parent;
    }

    //return the updated parent, every time the new commit is implemented, the parent adds it's commitID.
    private List<String> updateParent() {
        Commit lastMasterCommit = Repository.getMasterCommit();
        List<String> newParent = lastMasterCommit.getParent();
        newParent.addFirst(lastMasterCommit.getCommitID());
        return newParent;
    }

    //return the commitID of a commit.
    public String getCommitID() {
        return generateID();
    }

    //return the TreeMap in Commit.
    public TreeMap getTreeMap() {
        return pathToBlobID;
    }

    //return the specific file.
    public File getCommitFileName() {
        return Utils.join(commitFile, commitID);
    }

    //the non-argument constructor (init function)
    //bolbID & parent are empty, not null, in case of sha1 error
    public Commit() {
        this.message = "initial commit";
        this.date = new Date(0);
        this.standerTime = dateToTimeStamp(date);
        this.commitID = getCommitID();
        this.commitFileName = getCommitFileName();
    }

    //the normal constructor (blobID & parent might need to fix)
    public Commit(String message) {
        this.message = message;
        this.date = new Date();
        this.standerTime = dateToTimeStamp(date);
        this.pathToBlobID = Repository.getAllPathToBlobID();
        List<String> newParent = updateParent();
        this.parent = newParent;
        commitID = generateID();
        commitFileName = Utils.join(commitFile, commitID);
        Add addStageAfterCommit = new Add();
        addStageAfterCommit.clear();
    }

    //get the correspond commit by its commitID.
    public static Commit getCommitFromCommitID(String commitID) {
        File cf = Utils.join(commitFile, commitID);
        Commit retCommit = Utils.readObject(cf, Commit.class);
        return retCommit;
    }

    //change the date to stander time in order to generate sha1ID
    private static String dateToTimeStamp(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        return dateFormat.format(date);
    }

    //gengerate sha1ID
    private String generateID() {
        return Utils.sha1(message, standerTime, pathToBlobID.toString(), parent.toString());
    }

    //write the commit into commitFileName.
    public void makeCommit() {
        Utils.writeObject(commitFileName, this);
    }

}

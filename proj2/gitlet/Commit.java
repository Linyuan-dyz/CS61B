package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.join;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /** The commits directory. */
    public static final File commits = join(Repository.OBJECTS_DIR, "commits");

    /**
     * TODO: add instance variables here.
     * 1.message
     * 2.date
     * 3.standerTime
     * 4.parent
     * 5.pathToBlobID
     * 6.commitID
     * 7.commitName
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */
    /** The message of this Commit. */
    private String message;
    //commit date
    private Date date;
    //standerTime for sha1 to calculate
    private String standerTime;
    //reference to parent commit
    private List<String> parent = new LinkedList<>();
    //use a TreeMap to cast the path to the file's blobID.
    private TreeMap<String, String> pathToBlobID;
    //commitID
    private String commitID;
    //use commitFileName to indicate which file to point, in order to find the specific file.
    private File commitName;

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
    public File getCommitName() {
        return commitName;
    }
    //return the commitID of a commit.
    public String getCommitID() {
        return commitID;
    }

    public Commit() {
        this.message = "initial commit";
        this.date = new Date(0);
        this.standerTime = dateToTimeStamp(date);
        this.parent = new LinkedList<>();
        this.pathToBlobID = new TreeMap<>();
        this.commitID = Utils.sha1(message, standerTime, pathToBlobID.toString(), parent.toString());
        this.commitName = new File(commits, commitID);
    }

    public Commit(String message) {
        this.message = message;
        this.date = new Date();
        this.standerTime = dateToTimeStamp(date);
        this.parent = getMasterCommitParent();
        this.pathToBlobID = combineAddAndRemove();
        this.commitID = Utils.sha1(message, standerTime, pathToBlobID.toString(), parent.toString());
        this.commitName = new File(commits, commitID);
    }

    public void saveCommit() {
        Utils.writeObject(commitName, this);
    }

    public static void cleanAddFile() {
        Add newAdd = new Add();
        newAdd.saveAdd();
    }

    public static void cleanRemoveFile() {
        Remove newRemove = new Remove();
        newRemove.saveRemove();
    }

    //change the date to stander time in order to generate sha1ID
    private static String dateToTimeStamp(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        return dateFormat.format(date);
    }

    private List<String> getMasterCommitParent() {
        Commit MasterCommit = Repository.getMasterCommit();
        List<String> newParent = MasterCommit.getParent();
        newParent.addFirst(MasterCommit.getCommitID());
        return newParent;
    }

    private TreeMap combineAddAndRemove() {
        TreeMap addTree =  Repository.getAddTree();
        TreeMap removeTree = Repository.getRemoveTree();

        Collection c = removeTree.keySet();
        Iterator iter = c.iterator();
        while (iter.hasNext()) {
            addTree.remove(iter.next());
        }
        return addTree;
    }

}

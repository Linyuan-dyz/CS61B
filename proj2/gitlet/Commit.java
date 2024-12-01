package gitlet;


import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.LinkedList;

import static gitlet.Utils.join;

/** Represents a gitlet commit object.
 *
 *  does at a high level.
 *
 *  @author
 */
public class Commit implements Serializable {
    /** The commits directory. */
    public static final File COMMITS = join(Repository.OBJECTS_DIR, "commits");

    /**
     *
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
    private LinkedList<String> parent = new LinkedList<>();
    //use a TreeMap to cast the path to the file's blobID.
    private TreeMap<String, String> pathToBlobID;
    //commitID
    private String commitID;
    //use commitFileName to indicate which file to point, in order to find the specific file.
    private File commitName;
    //the front 8 bits of commitID.
    private String commitID8;
    //the short version of commitName.
    private File shortCommitName;

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
    public LinkedList<String> getParent() {
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
    //return the front 8 commitID.
    public String getCommitID8() {
        return commitID8;
    }
    //return the short version of commitName.
    public File getShortCommitName() {
        return shortCommitName;
    }

    public Commit() {
        this.message = "initial commit";
        this.date = new Date(0);
        this.standerTime = dateToTimeStamp(date);
        this.parent = new LinkedList<>();
        this.pathToBlobID = new TreeMap<>();
        this.commitID = Utils.sha1(message, standerTime,
                pathToBlobID.toString(), parent.toString());
        this.commitName = new File(COMMITS, commitID);
        this.commitID8 = this.commitID.substring(0, 8);
        this.shortCommitName = new File(COMMITS, commitID8);
    }

    public Commit(String message) {
        this.message = message;
        this.date = new Date();
        this.standerTime = dateToTimeStamp(date);
        this.parent = getMasterCommitParent();
        this.pathToBlobID = combineAddAndRemoveAndParent();
        this.commitID = Utils.sha1(message, standerTime,
                pathToBlobID.toString(), parent.toString());
        this.commitName = new File(COMMITS, commitID);
        this.commitID8 = this.commitID.substring(0, 8);
        this.shortCommitName = new File(COMMITS, commitID8);
    }

    public Commit (String message, LinkedList newParent) {
        this.message = message;
        this.date = new Date();
        this.standerTime = dateToTimeStamp(date);
        this.parent = newParent;
        this.pathToBlobID = combineAddAndRemoveAndParent();
        this.commitID = Utils.sha1(message, standerTime,
                pathToBlobID.toString(), parent.toString());
        this.commitName = new File(COMMITS, commitID);
        this.commitID8 = this.commitID.substring(0, 8);
        this.shortCommitName = new File(COMMITS, commitID8);
    }

    public void saveCommit() {
        Utils.writeObject(commitName, this);
    }

    public void saveShortCommit() {
        Utils.writeObject(shortCommitName, this);
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

    private LinkedList<String> getMasterCommitParent() {
        Commit MasterCommit = Repository.getMasterCommit();
        LinkedList<String> newParent = new LinkedList<>();
        newParent.addFirst(MasterCommit.getCommitID());
        return newParent;
    }

    private TreeMap combineAddAndRemoveAndParent() {
        Commit masterCommit = Repository.getMasterCommit();
        TreeMap masterCommitTree = masterCommit.getTreeMap();
        TreeMap addTree =  Repository.getAddTree();
        TreeMap removeTree = Repository.getRemoveTree();

        //combine addTree and previous commitTree.
        Collection cc = addTree.keySet();
        Iterator addTreeKey = cc.iterator();
        while (addTreeKey.hasNext()) {
            String newPath = (String) addTreeKey.next();
            if (!masterCommitTree.containsKey(newPath)) {
                masterCommitTree.put(newPath, addTree.get(newPath));
            } else {
                masterCommitTree.replace(newPath, addTree.get(newPath));
            }
        }
        //combine newCommitTree and removeTree.
        Collection c = removeTree.keySet();
        Iterator iter = c.iterator();
        while (iter.hasNext()) {
            masterCommitTree.remove(iter.next());
        }

        return masterCommitTree;
    }

}

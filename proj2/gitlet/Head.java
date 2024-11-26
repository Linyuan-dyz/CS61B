package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.join;

public class Head implements Serializable {
    /** The headsFile directory. */
    public static final File headsFile = join(Repository.REFS_DIR, "headsFile");
    /** The masterFile directory. */
    public static final File masterFile = join(headsFile, "masterFile");

    private String commitID;
    private String branchName;
    private File headName;
    private File masterName;

    public String getCommitID() {
        return commitID;
    }
    public String getBranchName() {
        return branchName;
    }
    public File getHeadName() {
        return headName;
    }
    public File getMasterName() {
        return masterName;
    }

    public Head(Commit commit) {
        this.commitID = commit.getCommitID();
        this.branchName = "master";
        this.headName = new File(headsFile, branchName);
        this.masterName = new File(masterFile, branchName);
    }

    public Head(String branchName, Commit commit) {
        this.commitID = commit.getCommitID();
        this.branchName = branchName;
        this.headName = new File(headsFile, branchName);
        this.masterName = new File(masterFile, branchName);
    }

    //save the head in headsFile, if it doesn't exist, create it.
    //otherwise, overwrite it.
    public void saveInHeads() {
        Utils.writeObject(headName, this);
    }

    //save the head in masterFile, if it doesn't exist, create it.
    //otherwise, overwrite it.
    public void saveInMaster() {
        Utils.writeObject(masterName, this);
    }

    //assume there is a head in the masterFile, delete then put it into the headsFile.
    //the call the new head saveInMaster.
    public void setMaster() {

        Head master = Repository.getMaster();
        master.getMasterName().delete();
        Utils.writeObject(master.getHeadName(), master);

        this.getHeadName().delete();
        Utils.writeObject(this.getMasterName(), this);;
    }


}

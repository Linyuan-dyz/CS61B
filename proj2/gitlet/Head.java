package gitlet;

import java.io.File;
import java.io.Serializable;

public class Head implements Serializable {

    static final File refsFile = Utils.join(Repository.GITLET_DIR, "refs");
    static final File headsFile = Utils.join(refsFile, "heads");
    static final File masterFile = Utils.join(headsFile, "masterFile");

    private String branchName;

    private String commitID;

    //store the headsFileName in headsFile
    private File headsFileName;

    //get the current head's name
    public String getBranchName() {
        return branchName;
    }

    //get head's commitID.
    public String getCommitID() {
        return commitID;
    }

    //get the head's file name
    public File getHeadsFileName() {
        return headsFileName;
    }

    public Head(Commit commit) {
        this.branchName = "master";
        this.commitID = commit.getCommitID();
        this.headsFileName = new File(headsFile, "master");
    }

    //create a new branch with the given branchName
    public Head(String branchName, Commit commit) {
        this.branchName = branchName;
        this.commitID = commit.getCommitID();
        this.headsFileName = new File(headsFile, branchName);
    }

    //get the given name branch's file.
    //assume the file exist.
    public static File getBranchFileName(String branchName) {
        if (Utils.join(masterFile, branchName).exists()) {
            return Utils.join(masterFile, branchName);
        }
        return Utils.join(headsFile, branchName);
    }

    //get the master's commitID
    public static String getMasterCommitID() {

        //get the master pointer
        File masterFileName = Utils.join(Head.headsFile);

        //get the commitID that the master pointer point to.
        String commitID = Utils.readObject(masterFileName, Head.class).getCommitID();

        return commitID;
    }

    /*if there is nothing in the master file,
        *just delete the target head, create the masterFile, and write it into the masterFile.
     *if there is a master in the master file,
        * firstly, delete current head and write it into the headsFile,
        * then delete the target head and write it into the masterFile.
     */
    public void setMaster(String branchName) {
        String[] whatInMasterFile = masterFile.list();
        if (whatInMasterFile.length > 0) {
            File originalHeadFile = Repository.getMasterFileName();
            Head originalHead = Utils.readObject(originalHeadFile, Head.class);
            Utils.writeObject(originalHead.getHeadsFileName(), originalHead);
            originalHeadFile.delete();
        }
        File targetHeadFile = getBranchFileName(branchName);
        Head targetHead = Utils.readObject(targetHeadFile, Head.class);
        File targetMasterHead = new File(masterFile, targetHead.getBranchName());
        Utils.writeObject(targetMasterHead, targetHead);
        targetHeadFile.delete();
    }

    //write the head into masterFile, if existing, overwrite it.
    public void saveHead() {
        Utils.writeObject(Utils.join(masterFile, this.getBranchName()), this);
    }

    //write the head into headsFile, if existing, overwrite it
    public void saveHeadNotMaster() {
        Utils.writeObject(getHeadsFileName(), this);
    }

}

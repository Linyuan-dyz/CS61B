package gitlet;

import java.io.File;
import java.io.Serializable;

public class Head implements Serializable {

    static final File refsFile = Utils.join(".gitlet", "refs");
    static final File headsFile = Utils.join(".refs", "heads");
    static final File masterFile = Utils.join(".heads", "master");

    private String commitID;

    public Head(Commit commit) {
        commitID = commit.getCommitID();
    }

    //get master's commitID.
    public String getCommitID() {
        return commitID;
    }

    //get the master's commitID
    public static String getMasterCommitID() {

        //get the master pointer
        File masterFileName = Utils.join(Head.masterFile);

        //get the commitID that the master pointer point to.
        String commitID = Utils.readObject(masterFileName, Head.class).getCommitID();

        return commitID;
    }

    //write the head into headFile, if existing, overwrite it.
    public void saveHead() {
        Utils.writeObject(masterFile, commitID);
    }

}
